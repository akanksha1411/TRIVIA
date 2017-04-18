package com.toi.reader.triviaCode;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.androidprogresslayout.ProgressLayout;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import com.library.util.ImageDownloaderCrossFade;
import com.library.util.PermissionUtil;
import com.sso.library.manager.SSOManager;
import com.sso.library.models.SSOResponse;
import com.sso.library.models.User;
import com.toi.reader.fragments.HomeFragment;
import com.toi.reader.login.activities.LoginSignUpActivity;
import com.urbanairship.UAirship;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toi.com.trivia.activities.GameArchive;
import toi.com.trivia.activities.HostActivity;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.api.APIService;
import toi.com.trivia.api.ApiRetroFit;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.fragments.QuizScreen;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.Contents;
import toi.com.trivia.model.HomeItems;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * Created by Akanksha on 12/1/17.
 * - openLoginPage() - This function is to open TOI login page from Trivia app.
 * <p>
 * - generateTriviaHomeView() -  Get the view object for Trivia Home page i.e the Login Page and Dashboard page
 * <p>
 * - prepareUserBundle() - This returns a bundle with key value pair set from the User object taken from TOI app.
 * This bundle is to be sent with the intent after login is success.
 * These values are then set in the local shared preference for Trivia app.
 * <p>
 * - homeResumedReciver - is a broadcast receiver that will refresh the page as soon as it receives the intent.
 * This checks whether phone permission is given or not
 * 1. if permission given - Hits an api based on senarios mentioned in function registerUserapi()
 * 1.a if first time user - Trivia page is the landing page - UI code in function - initViews()
 * 1.b if old user - Dashboard with Login button is the landing page -UI code in function - initUI()
 * 2. if permission not given - Set permission page is the landing page with set permission button to ask for permission
 * and then land on respective page
 * <p>
 * - submitUserAnswer - This function gets the answer string saved in prefrence when the user ends the game abruptly or net conn gone.
 * The string then converted back to hashmap which is then submited in a background process.
 */
public class TriviaCalls implements TriviaInterface {

    /**
     * openLoginPage()
     * This function is to open TOI login page from Trivia app.
     *
     * @param context
     */
    @Override
    public void openLoginPage(Context context) {
        //CommonUtility.fetchResult(context, uid, resultGameId, openScreen);

        try {
            Intent login = new Intent(context, LoginSignUpActivity.class);
            CommonUtility.showActivity(context, "", 0, false, login, null);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * Get the view object for Trivia Home page i.e the Login Page and Dashboard page
     *
     * @param mContext
     * @return
     */
    @Override
    public TriviaLoginView generateTriviaHomeView(Context mContext) {

        TriviaLoginView loginView = new TriviaLoginView(mContext);

        return loginView;

    }

    @Override
    public Bundle prepareUserBundle(User user) {
        Bundle b = new Bundle();
        b.putString("name", CommonUtility.checkNull(user.getFirstName()));
        b.putString("user_image", CommonUtility.checkNull(user.getImgUrl()));
        b.putString("ssoid", CommonUtility.checkNull(user.getUserId()));
        b.putString("email", CommonUtility.checkNull(user.getEmailId()));
        return b;
    }


    /**
     * The view that will show the trivia login and Dashboard page
     * <p>
     * registerUser(getContext(), new HashMap<String, String>(), String.valueOf(DEFAULT_ZERO), String.valueOf(DEFAULT_ZERO), String.valueOf(DEFAULT_ZERO));
     * <p>
     * <p>
     * 0. Get TOI login status, if user object is there then user is logged in else logged out
     * 1. Check if UID is there
     * 2. If Yes; user is already there either logged in or anon browsing no need to update in user pref
     * 2.a. If TOI login status is yes and reg_status = 0 then call register API with user data - update user data
     * 2.b. If TOI login is yes and reg_status =1 , no action
     * 2.c. If TOI login is no and reg_status =0 , no action
     * 2.d. If TOI login is no and reg_status =1 , Expected logout scenario, update reg_status and uid and call register and update data
     * 3. If No; user is not there and we need to invoke register and save in user pref
     * 3.a. If TOI login is yes, then call register with user information and update data
     * 3.b If TOI lohin id no, then call anon register and update data
     */
    public class TriviaLoginView extends LinearLayout implements TriviaConstants {

        public View view;
        ReadPref readPref;
        int UID;
        public ProgressBar register_loader;
        public Button play_button;
        int pageShown;
        public String userName = TriviaConstants.DEFAULT_NAME;
        public String sponsorName;
        SavePref savePref;
        Call<Contents> newSubmitcall;
        LinearLayout login_lay;
        LinearLayout rank_box;
        LinearLayout photo_layout;
        LinearLayout permission_layout, top_snack_bar;
        TextView screen_title;
        HashMap<String, String> fbData = new HashMap<String, String>();
        private DownloadManager dm;
        public TextView hours1, hours2, minutes1, minutes2, seconds1, seconds2;
        private APIService apiService;
        Call<HomeItems> registrationItemsCall;
        private ApiRetroFit apiRetroFit;
        private long enqueue;
        File profilePicture;
        LinearLayout next_gametime_box;
        BroadcastReceiver receiver;
        DBController dbController;
        int mCurrentGamePlayed = 0;
        public Button play_game, play_button_login, login_register, set_permission;
        CircleImageView userImage;
        Context mContext;
        Call<HomeItems> dashboardItems;
        public HomeItems homeItems = new HomeItems();
        public ProgressLayout progressLayout;
        public FrameLayout login_layout;
        public FrameLayout dashboard_layout;
        public String LoginType;
        public int gameCount;
        public User user1;
        TextView week_rank, month_rank;
        ImageView bg1;
        ImageView bg2, bg3, bg4;
        String nextArchiveGameId = "";
        private DataLayer mDataLayer;
        Boolean isRunning = false;

        BroadcastReceiver homeResumedReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {


                readPref = new ReadPref(context);
                savePref = new SavePref(context);

                if (progressLayout != null) {
                    progressLayout.showProgress();
                }

                if (!checkPhonePermission()) {
                    permission_layout.setVisibility(VISIBLE);
                } else {
                    permission_layout.setVisibility(GONE);
                    registerUserapi(context, fbData);
                }


            }
        };

        BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (context != null) {
                        if (!CommonUtility.haveNetworkConnection(context)) {
                            if (view != null) {
                                Snackbar.make(view, TriviaConstants.No_INTERNET, Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            //submitUserAnswer(context);
                            if (!checkPhonePermission()) {
                                permission_layout.setVisibility(VISIBLE);
                            } else {
                                permission_layout.setVisibility(GONE);
                                registerUserapi(context, fbData);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        };


        public void submitUserAnswer(Context context) {
            ReadPref readPref = new ReadPref(context);
            SavePref savePref = new SavePref(context);
            String mapString = readPref.getUserAnswer();
            // use properties to restore the map
            HashMap<String, String> map2 = new HashMap<String, String>();

            if (mapString.length() != 0) {
                Properties props = new Properties();
                try {
                    props.load(new StringReader(mapString.substring(1, mapString.length() - 1).replace(", ", "\n")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (Map.Entry<Object, Object> e : props.entrySet()) {
                    map2.put((String) e.getKey(), (String) e.getValue());
                }
                map2 = loadMap(mapString);

                savePref.saveUserAnswer("");

                SubmitAnswers(context, map2, 2);//flag=0 for checking request
            }

        }

        public HashMap<String, String> loadMap(String mapString) {
            HashMap<String, String> outputMap = new HashMap<String, String>();
            try {

                JSONObject jsonObject = null;

                jsonObject = new JSONObject(mapString);

                Iterator<String> keysItr = jsonObject.keys();
                while (keysItr.hasNext()) {
                    String k = keysItr.next();
                    String v = (String) jsonObject.get(k);
                    outputMap.put(k, v);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return outputMap;
        }

        public TriviaLoginView(Context context) {
            super(context);

            readPref = new ReadPref(context);
            savePref = new SavePref(context);
            savePref.saveIsLiveCode(true);
            UID = Integer.parseInt(readPref.getUID());
            sponsorName = readPref.getSponsorName();
            LoginType = readPref.getLoginType();
            this.mContext = context;
            gameCount = Integer.parseInt(readPref.getUserGameCount());
            view = LayoutInflater.from(getContext()).inflate(toi.com.trivia.R.layout.fragment_dashboard, this, true);
            login_layout = (FrameLayout) view.findViewById(toi.com.trivia.R.id.login_layout);
            progressLayout = (ProgressLayout) view.findViewById(toi.com.trivia.R.id.progress_layout);
            progressLayout.showProgress();
            dashboard_layout = (FrameLayout) view.findViewById(toi.com.trivia.R.id.dashboard_layout);
            permission_layout = (LinearLayout) view.findViewById(toi.com.trivia.R.id.permission_layout);
            permission_layout.setVisibility(View.GONE);
            screen_title = (TextView) view.findViewById(toi.com.trivia.R.id.screen_title);
            set_permission = (Button) view.findViewById(toi.com.trivia.R.id.set_permission);
            set_permission.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    //ask permission for phone state if user
                    PermissionUtil.checkAllTOIRequiredPermissionsGranted((Activity) mContext, new String[]{android.Manifest.permission.READ_PHONE_STATE});
                }
            });
            if (!checkPhonePermission()) {
                permission_layout.setVisibility(VISIBLE);
            } else {
                permission_layout.setVisibility(GONE);
                registerUserapi(context, fbData);
            }

            String channelId = UAirship.shared().getPushManager().getChannelId();
            if (CommonUtility.chkString(channelId)) {
                Log.d("UA-Channel-- ", channelId);
            }
            UAirship.shared().getPushManager().editTags()
                    .addTag("Trivia")
                    .apply();

            //17 jan for testing gtm
           /* Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {*/
            /*((BaseActivity) mContext).updateAnalyticGtmEvent("sort_reviews", "trivia17jan1", "TEST");
            ((BaseActivity) mContext).updateAnalyticGtmEvent2("Trivia_And_Login", "trivia18janak2", "TEST");
            ((BaseActivity) mContext).updateAnalyticGtmEvent("story_comment_success", "trivia17jan3", "TEST");
            ((BaseActivity) mContext).updateAnalytics("TriviaLoginView" + CommonUtility.formatDate(Long.parseLong(CommonUtility.getCurrentTimeStamp())));
            updateAnalyticGtmEvent("Trivia_And", "trivia app", "trivia app");

            ToiCokeUtils.pushCokeEvent(mContext, "Trivia_And_Login", "TriviaSection", "", "TriviaPath", "Trivia");*/
              /*  }
            }, 100);*/

            bg1 = (ImageView) findViewById(toi.com.trivia.R.id.rotate_bg);
            bg2 = (ImageView) findViewById(toi.com.trivia.R.id.rotate_bg2);
            bg3 = (ImageView) findViewById(toi.com.trivia.R.id.rotate_bg3);
            bg4 = (ImageView) findViewById(toi.com.trivia.R.id.rotate_bg4);

            // makeTriviaDirectory();
            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    try {
                        String action = intent.getAction();
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                            long downloadId = intent.getLongExtra(
                                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(enqueue);

                            if (dm != null) {
                                Cursor c = dm.query(query);

                                if (c.moveToFirst()) {
                                    int columnIndex = c
                                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
                                    if (DownloadManager.STATUS_SUCCESSFUL == c
                                            .getInt(columnIndex)) {

                                        profilePicture = new File(Environment.getExternalStorageDirectory()
                                                .getPath()
                                                + "/Android/data/" + getContext().getPackageName() + "/files/Trivia/"
                                                + "TriviaProfilePicture" + "_" + UID + ".jpg");


                                        File sponsorFile = new File(Environment.getExternalStorageDirectory()
                                                .getPath()
                                                + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/"
                                                + "Sponsor" + ".jpg");


                                        if (sponsorFile != null) {
                                       /* Toast.makeText(context, "sponsor downloaded", Toast.LENGTH_SHORT).show();*/
                                            if (pageShown == LOGIN_SCREEN) {
                                                CommonUtility.initBackground(view, mContext, TriviaConstants.LOGIN_SCREEN, sponsorName);
                                            } else {
                                                CommonUtility.initBackground(view, mContext, TriviaConstants.DASHBOARD_SCREEN, sponsorName);
                                            }
                                        }
                                        if (profilePicture != null) {
                                            if (profilePicture.exists()) {
                                                Log.d("file image", "called");
                                                Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
                                                userImage.setImageBitmap(myBitmap);
                                            }
                                        } else {
                                            Log.d("url image", "called");

                                            Glide.with(mContext)
                                                    .load(fbData.get(PARAM_PROFILE_IMG))
                                                    .asBitmap()
                                                    .placeholder(toi.com.trivia.R.drawable.default_avatar)
                                                    .error(toi.com.trivia.R.drawable.default_avatar)
                                                    .into(userImage);


                                        }


                                    } /*else {


                                    //set background for the screen
                                    CommonUtility.initBackground(view, getActivity().getApplicationContext(), TriviaConstants.DASHBOARD_SCREEN);
                                }*/
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            context.registerReceiver(receiver,
                    new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        }

        private void makeTriviaDirectory() {

            File headFolder = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + mContext.getPackageName(), "TOI_TRIVIA");


            if (!headFolder.exists()) {
                Boolean status = headFolder.mkdirs();
                Log.d("status---", status.toString());
            }


            File mediaStorageDir1 = null, mediaStorageDir2 = null;

            mediaStorageDir1 = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + mContext.getPackageName() + "/TOI_TRIVIA", "QuizImages");

            mediaStorageDir2 = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + mContext.getPackageName() + "/TOI_TRIVIA", "CatImages");

            if (!mediaStorageDir1.exists()) {
                mediaStorageDir1.mkdir();
            }
            if (!mediaStorageDir2.exists()) {
                mediaStorageDir2.mkdir();
            }
            savePref.saveIsDirCreated(true);
        }

        public String getUID() {
            ReadPref readPref = new ReadPref(mContext);
            return readPref.getUID();
        }

        public String getRegStatus() {
            ReadPref readPref = new ReadPref(mContext);
            return readPref.getRegStatus();
        }


        public String getLoginId() {
            ReadPref readPref = new ReadPref(mContext);
            return readPref.getLoginId();
        }


        public void beginPlayingGame() {
            savePref.saveIsGameKilled(false);
            dbController = new DBController(mContext);
            if (pageShown == LOGIN_SCREEN) {
                //if play new game called from trivia login page
                int mCurrentGameId = Integer.parseInt(readPref.getCurrentGameId());
                if (mCurrentGameId != 0) {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Login", "Play new game", TriviaConstants.CLICK, "Trivia_And_Exit_Login");

                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Login", "Play new game", TriviaConstants.CLICK, "Trivia_And_Login");

                    //if current game id id avilable the start game
                    CommonUtility.fetchNewGame(mContext, mCurrentGameId, TriviaConstants.LOGIN_SCREEN);
                } else {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Login", "Play another game", TriviaConstants.CLICK, "Trivia_And_Login");
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Login", "Play another game", TriviaConstants.CLICK, "Trivia_And_Exit_Login");

                    if (CommonUtility.chkString(nextArchiveGameId)) {
                        if (Integer.parseInt(nextArchiveGameId) != 0) {
                            savePref.saveArchieveGameId(nextArchiveGameId);
                            savePref.saveCurrentPosition(0);
                            // savePref.saveResultGameId(String.valueOf(items.getGame().getNextGameArchiveId()));
                            dbController.clearDatabase((AppCompatActivity) mContext, nextArchiveGameId);
                        } else {
                            //another game- Take to archieve
                            Intent intent = new Intent(mContext, GameArchive.class);
                            CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                        }

                    } else {
                        //another game- Take to archieve
                        Intent intent = new Intent(mContext, GameArchive.class);
                        CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                    }
                    //another game- Take to archieve
                    //CommonUtility.fetchArchive(mContext, String.valueOf(getUID()), DASHBOARD_SCREEN);

                }
            } else {
                //if play new called from Dashboard page
                /**
                 *if the mCurrentGamePlayed value is 1 i.e user has played the current game and next game timer
                 is visible then show archieve link else start new game
                 */
                if (mCurrentGamePlayed == DEFAULT_ONE) {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Dashboard", "Play another game", TriviaConstants.CLICK, "Trivia_And_Exit_Dashboard");

                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Play another game", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                    if (CommonUtility.chkString(nextArchiveGameId)) {
                        if (Integer.parseInt(nextArchiveGameId) != 0) {
                            savePref.saveArchieveGameId(nextArchiveGameId);
                            savePref.saveCurrentPosition(0);
                            // savePref.saveResultGameId(String.valueOf(items.getGame().getNextGameArchiveId()));
                            dbController.clearDatabase((AppCompatActivity) mContext, nextArchiveGameId);
                        } else {
                            //another game- Take to archieve
                            Intent intent = new Intent(mContext, GameArchive.class);
                            CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                        }
                    } else {
                        //another game- Take to archieve
                        Intent intent = new Intent(mContext, GameArchive.class);
                        CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                    }

                } else {
                    if (Integer.parseInt(readPref.getCurrentGameId()) != DEFAULT_ZERO) {
                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Dashboard", "Play new game", TriviaConstants.CLICK, "Trivia_And_Exit_Dashboard");

                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Play new game", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                        //call api - new game
                        CommonUtility.fetchNewGame(mContext, Integer.parseInt(readPref.getCurrentGameId()), TriviaConstants.LOGIN_SCREEN);
                    } else {
                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Dashboard", "Play another game", TriviaConstants.CLICK, "Trivia_And_Exit_Dashboard");

                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Play another game", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                        if (CommonUtility.chkString(nextArchiveGameId)) {
                            if (Integer.parseInt(nextArchiveGameId) != 0) {
                                savePref.saveArchieveGameId(nextArchiveGameId);
                                savePref.saveCurrentPosition(0);
                                // savePref.saveResultGameId(String.valueOf(items.getGame().getNextGameArchiveId()));
                                dbController.clearDatabase((AppCompatActivity) mContext, nextArchiveGameId);
                            } else {
                                //another game- Take to archieve
                                Intent intent = new Intent(mContext, GameArchive.class);
                                CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                            }
                        } else {
                            //another game- Take to archieve
                            Intent intent = new Intent(mContext, GameArchive.class);
                            CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                        }
                    }
                }
            }


        }

        public TriviaLoginView(Context context, AttributeSet attrs) {
            super(context, attrs);

        }

        public TriviaLoginView(Context context, AttributeSet attrs, int defStyle) {
            this(context, attrs);

        }


        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            Log.d("onAttachedToWindow", "--called");
            initNetworkStateReciever();
            //register
            LocalBroadcastManager.getInstance(mContext).registerReceiver(homeResumedReciver, new IntentFilter(HomeFragment.ACTION_HOME_RESUMED));


        }


        public void initViews() {
            sponsorName = readPref.getSponsorName();
            if (sponsorName.toLowerCase().equals(TOI_SPONSOR_NAME) || sponsorName.equals("")) {
                final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setInterpolator(new LinearInterpolator());
                //animator.setRepeatMode(ValueAnimator.REVERSE);
                animator.setDuration(30000L);

                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        final float progress = (float) animation.getAnimatedValue();
                        final float width = bg3.getWidth();
                        final float translationX = width * progress;
                        bg3.setTranslationX(translationX);
                        bg4.setTranslationX(translationX - width);

                    }
                });
                animator.start();
            } else {
                hideSpaceAnimation();
                CommonUtility.initBackground(view, mContext, TriviaConstants.LOGIN_SCREEN, sponsorName);
            }

            progressLayout.showContent();

            login_layout.setVisibility(VISIBLE);
            dashboard_layout.setVisibility(GONE);
      /*  top_snack_bar.setVisibility(GONE);*/
            register_loader = (ProgressBar) view.findViewById(toi.com.trivia.R.id.register_loader);
            if (register_loader != null) {
                register_loader.setVisibility(GONE);
            }

            if (!checkPhonePermission()) {
                permission_layout.setVisibility(VISIBLE);
            } else {
                permission_layout.setVisibility(GONE);

            }
            play_button_login = (Button) view.findViewById(toi.com.trivia.R.id.play_button);

            play_button_login.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    beginPlayingGame();
                }
            });


            set_permission = (Button) view.findViewById(toi.com.trivia.R.id.set_permission);
            set_permission.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                   /* boolean isAllowed = PermissionUtil.requestPermissions((BaseActivity) mContext, new String[]{android.Manifest.permission.READ_PHONE_STATE}, PermissionUtil.REQUEST_CODE_GET_PHONE_STATE);
*/
                    PermissionUtil.checkAllTOIRequiredPermissionsGranted((Activity) mContext, new String[]{android.Manifest.permission.READ_PHONE_STATE});
                }
            });


            TextView open_prizes = (TextView) view.findViewById(toi.com.trivia.R.id.open_prizes1);
            open_prizes.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Prizes", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                    //fetch prizes for current game and open prize page
                    CommonUtility.fetchPrizes(mContext, readPref.getCurrentGameId(), DASHBOARD_SCREEN);
                }
            });
            TextView open_about_us = (TextView) view.findViewById(toi.com.trivia.R.id.open_about_us1);
            open_about_us.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "T&C", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                    Bundle bundle = new Bundle();
                    bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.ABOUT_US_SCREEN);
                    Intent startQuiz = new Intent(mContext, HostActivity.class);
                    CommonUtility.showActivity(mContext, "", 0, true, startQuiz, bundle);
                }
            });
            TextView open_faq = (TextView) view.findViewById(toi.com.trivia.R.id.open_faq1);
            open_faq.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "FAQ", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                    Bundle bundle = new Bundle();
                    bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.FAQ_SCREEN);
                    Intent startQuiz = new Intent(mContext, HostActivity.class);
                    CommonUtility.showActivity(mContext, "", 0, true, startQuiz, bundle);
                }
            });
            TextView open_policy = (TextView) view.findViewById(toi.com.trivia.R.id.open_policy1);
            open_policy.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Privacy Policy", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                    Bundle bundle = new Bundle();
                    bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.POLICY_SCREEN);
                    Intent startQuiz = new Intent(mContext, HostActivity.class);
                    CommonUtility.showActivity(mContext, "", 0, true, startQuiz, bundle);
                }
            });
            if (homeItems != null) {
                screen_title = (TextView) view.findViewById(toi.com.trivia.R.id.screen_title);
                screen_title.setText(homeItems.getData().getStitle());
            }
            final ImageView trivia_logo = (ImageView) view.findViewById(toi.com.trivia.R.id.trivia_logo_text);

            Timer timer1 = new Timer();
            timer1.schedule(new TimerTask() {

                @Override
                public void run() {

                    try {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                startFlipAnimation(trivia_logo);
                                try {
                                    //  setButtonTextLogin(Integer.parseInt(readPref.getCurrentGameId()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                private void setButtonTextLogin(int gameid) {


                    if (gameid != DEFAULT_ZERO) {
                        if (mCurrentGamePlayed == DEFAULT_ONE) {
                            //usr has played the current game and also next game timer is visible
                            play_button_login.setText(mContext.getResources().getString(toi.com.trivia.R.string.button_play_another));
                            play_button_login.setEnabled(true);
                        } else {
                            //usr has not played the current game and next game timer is visible.
                            play_button_login.setText(mContext.getResources().getString(toi.com.trivia.R.string.button_play_new));
                            play_button_login.setEnabled(true);


                        }
                    } else {
                        //no game is active right now play from archieve
                        play_button_login.setText(mContext.getResources().getString(toi.com.trivia.R.string.button_play_another));
                        play_button_login.setEnabled(true);
                    }


                }
            }, 0, 3000);

        }

        /**
         * Starts animation 360 degree i.e verticall flipping to logo
         *
         * @param trivia_logo
         */

        private void startFlipAnimation(ImageView trivia_logo) {

            try {
                AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(mContext, toi.com.trivia.R.animator.flipping_animation);
                set.setTarget(trivia_logo);
                set.cancel();
                set.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * Add post parameters for dashboard api call and call api data
         *
         * @param UID
         */
        public void fetchDashboard(int UID) {


            HashMap<String, String> map = new HashMap<>();
            map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
            map.put(PARAM_UQID, CommonUtility.getIMEI(mContext));
            map.put(PARAM_UID, String.valueOf(UID));
            map.put(PARAM_LOGIN_TYPE, "s");
            map.put(PARAM_LOGIN_ID, readPref.getLoginId());
            userDashboard(mContext, map);

        }

        /**
         * Set UI for the Dashboard and set value accordingly
         */
        public void initUI() {
            pageShown = DASHBOARD_SCREEN;
            //
            try {
                sponsorName = readPref.getSponsorName();
                if (sponsorName.toLowerCase().equals(TOI_SPONSOR_NAME) || sponsorName.equals("")) {
                    final ValueAnimator animator = ValueAnimator.ofFloat(0.0f, 1.0f);
                    animator.setRepeatCount(ValueAnimator.INFINITE);
                    animator.setInterpolator(new LinearInterpolator());
                    //animator.setRepeatMode(ValueAnimator.REVERSE);
                    animator.setDuration(30000L);

                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            final float progress = (float) animation.getAnimatedValue();
                            final float width = bg1.getWidth();
                            final float translationX = width * progress;
                            bg1.setTranslationX(translationX);
                            bg2.setTranslationX(translationX - width);

                        }
                    });
                    animator.start();
                } else {
                    hideSpaceAnimation();
                    CommonUtility.initBackground(view, mContext, TriviaConstants.DASHBOARD_SCREEN, sponsorName);

                }

                TextView open_prizes = (TextView) view.findViewById(toi.com.trivia.R.id.open_prizes);
                open_prizes.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //fetch prizes for current game and open prize page
                        CommonUtility.fetchPrizes(mContext, readPref.getCurrentGameId(), DASHBOARD_SCREEN);
                    }
                });
                TextView open_about_us = (TextView) view.findViewById(toi.com.trivia.R.id.open_about_us);
                open_about_us.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.ABOUT_US_SCREEN);
                        Intent startQuiz = new Intent(mContext, HostActivity.class);
                        CommonUtility.showActivity(mContext, "", 0, true, startQuiz, bundle);
                    }
                });
                TextView open_faq = (TextView) view.findViewById(toi.com.trivia.R.id.open_faq);
                open_faq.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.FAQ_SCREEN);
                        Intent startQuiz = new Intent(mContext, HostActivity.class);
                        CommonUtility.showActivity(mContext, "", 0, true, startQuiz, bundle);
                    }
                });
                TextView open_policy = (TextView) view.findViewById(toi.com.trivia.R.id.open_policy);
                open_policy.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.POLICY_SCREEN);
                        Intent startQuiz = new Intent(mContext, HostActivity.class);
                        CommonUtility.showActivity(mContext, "", 0, true, startQuiz, bundle);
                    }
                });

                play_game = (Button) view.findViewById(toi.com.trivia.R.id.play_game_button);

                TextView open_leaderboard = (TextView) view.findViewById(toi.com.trivia.R.id.open_leaderboard);
                open_leaderboard.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Leaderboard", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                        //call leaderboard api and open page
                        CommonUtility.fetchLeaderBoard(mContext, String.valueOf(getUID()), 0, MODE_DAILY, DASHBOARD_SCREEN);
                    }
                });
                TextView open_archive = (TextView) view.findViewById(toi.com.trivia.R.id.open_archive);
                open_archive.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Game Archive", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                        // CommonUtility.fetchArchive(mContext, String.valueOf(getUID()), DASHBOARD_SCREEN);
                        Intent intent = new Intent(mContext, GameArchive.class);
                        CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.GAME_END, true, intent, null);
                    }
                });

                login_register = (Button) view.findViewById(toi.com.trivia.R.id.login_register);
                login_register.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Login", TriviaConstants.CLICK, "Trivia_And_Dashboard");
                        TriviaCalls obj = new TriviaCalls();
                        obj.openLoginPage(mContext);

                    }
                });
                set_permission = (Button) view.findViewById(toi.com.trivia.R.id.set_permission);
                set_permission.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ask permission for phone state if user
                        PermissionUtil.checkAllTOIRequiredPermissionsGranted((Activity) mContext, new String[]{android.Manifest.permission.READ_PHONE_STATE});
                    }
                });


                final TextView user_name = (TextView) view.findViewById(toi.com.trivia.R.id.user_name);
                //set user image into imageview
                userImage = (CircleImageView) view.findViewById(toi.com.trivia.R.id.user_image);

                final String urlImage = readPref.getUserImage();

                next_gametime_box = (LinearLayout) view.findViewById(toi.com.trivia.R.id.next_gametime_box);
                final TextView game_played = (TextView) view.findViewById(toi.com.trivia.R.id.game_played);
                week_rank = (TextView) view.findViewById(toi.com.trivia.R.id.week_rank);
                month_rank = (TextView) view.findViewById(toi.com.trivia.R.id.month_rank);

                login_lay = (LinearLayout) view.findViewById(toi.com.trivia.R.id.login_lay);
                rank_box = (LinearLayout) view.findViewById(toi.com.trivia.R.id.rank_box);
                photo_layout = (LinearLayout) view.findViewById(toi.com.trivia.R.id.photo_layout);
                play_game = (Button) view.findViewById(toi.com.trivia.R.id.play_game_button);
                play_game.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        beginPlayingGame();
                    }
                });


                new Handler().postDelayed(new Runnable() {
                    @Override

                    public void run() {
                        try {
                            //  CommonUtility.initBackground(view, mContext, TriviaConstants.DASHBOARD_SCREEN, sponsorName);


                            HomeItems feed = getDasboardFeed();
                            if (feed.getGame() != null) {

                                // Log.d("getName()----", CommonUtility.checkNull(feed.getUser().getName().toString()));
                                mCurrentGamePlayed = feed.getGame().getIsCurrentPlayed();
                                userName = CommonUtility.checkName(feed.getUser().getName());

                                user_name.setText(readPref.getUserName());

                                // savePref.saveCurrentGameId(CommonUtility.checkNull(String.valueOf(feed.getGame().getCurrentGameId())));//current game id set to preference
                                savePref.saveNextGameId(CommonUtility.checkNull(String.valueOf(feed.getGame().getNextGameId())));
                                userImage.setImageResource(toi.com.trivia.R.drawable.default_avatar);

                                profilePicture = new File(Environment.getExternalStorageDirectory()
                                        .getPath()
                                        + "/Android/data/" + mContext.getPackageName() + "/files/Trivia/"
                                        + "TriviaProfilePicture" + "_" + getUID() + ".jpg");
/*
                            if (profilePicture != null) {
                                if (profilePicture.exists()) {
                                    Log.d("file image", "called");
                                    Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
                                    userImage.setImageBitmap(myBitmap);
                                }
                            } else {*/

                                User user = SSOManager.getInstance().getCurrentUser();
                                Log.d("url image--", feed.getUser().getProfile_img());
                                if (CommonUtility.chkString(feed.getUser().getProfile_img())) {
                                    if (feed.getUser().getProfile_img().contains("facebook")) {
                                   /* Bitmap bitmap = getFacebookProfilePicture(feed.getUser().getProfile_img());
                                    userImage.setImageBitmap(bitmap);*/
                                        new RetrieveFeedTask(feed.getUser().getProfile_img()).execute();
                                    } else {
                                    /*Picasso.with(mContext).load(feed.getUser().getProfile_img()).transform(new CommonUtility.RoundedTransformation(10, 0))
                                            .placeholder(toi.com.trivia.R.drawable.default_avatar)
                                            .error(toi.com.trivia.R.drawable.default_avatar)
                                            .into(userImage);*/
                                        Glide.with(mContext)
                                                .load(feed.getUser().getProfile_img())
                                                .asBitmap()
                                                .placeholder(toi.com.trivia.R.drawable.default_avatar)
                                                .error(toi.com.trivia.R.drawable.default_avatar)
                                                .into(userImage);
                                    }
                                } else {
                                    userImage.setImageResource(toi.com.trivia.R.drawable.default_avatar);
                                }
                                // }

                                if (feed.getGame().getIsShowResult() == DEFAULT_ONE) {
                                    //if (!readPref.getResultGameId().equals(readPref.getResultViewedGameId())) {
                                    //show result notification on top

                                    CommonUtility.initNotification(mContext, view, String.valueOf(getUID()), 1, String.valueOf(feed.getGame().getResultGameId()));
                                    // }
                                } else if (feed.getGame().getIsShowResult() == DEFAULT_ZERO) {
                                    final View result_noti_layout = view.findViewById(toi.com.trivia.R.id.result_noti_layout);
                                    result_noti_layout.setVisibility(View.GONE);
                                }

                                game_played.setText(String.valueOf(feed.getUser().getGame_count()));
                                week_rank.setText(CommonUtility.roundedRankText(String.valueOf(feed.getUser().getWeek_rank())));
                                month_rank.setText(CommonUtility.roundedRankText(String.valueOf(feed.getUser().getMonth_rank())));

                                hours1 = (TextView) view.findViewById(toi.com.trivia.R.id.hours1);
                                minutes1 = (TextView) view.findViewById(toi.com.trivia.R.id.minutes1);
                                seconds1 = (TextView) view.findViewById(toi.com.trivia.R.id.seconds1);
                                hours2 = (TextView) view.findViewById(toi.com.trivia.R.id.hours2);
                                minutes2 = (TextView) view.findViewById(toi.com.trivia.R.id.minutes2);
                                seconds2 = (TextView) view.findViewById(toi.com.trivia.R.id.seconds2);

                                if (!CommonUtility.checkNull(feed.getGame().getNextGameTime()).equals("0")) {
                                    //if next time game is not null then show timer for next game to start

                                    long server_time = Long.parseLong(feed.getGame().getServer_time());

                                    long offset = server_time - (System.currentTimeMillis() / 1000);
                                    long counter_time = Long.valueOf(feed.getGame().getNextGameTime()) - offset;
                                    Log.d("timer---", "current--" + String.valueOf(System.currentTimeMillis() / 1000) + "server--" + server_time + "offset--" + offset + "result time" + feed.getGame().getNextGameTime() + "difference--" + counter_time);
                                    Long nextGameTimeLeft = (counter_time * 1000) - System.currentTimeMillis();
                                    final CounterClass timer = new CounterClass(nextGameTimeLeft, 1000);
                                    //todo uncomment when timer countdown to be inbuild.

                                    if (!isRunning) {
                                        //todo uncomment when timer countdown to be inbuild.
                                        timer.start();
                                        Log.d("timer called", "-------");
                                    }

                                    if (feed.getGame().getIsCurrentPlayed() != 0) {
                                        next_gametime_box.setVisibility(View.VISIBLE);
                                    } else {
                                        //iscurrentplayed is 0
                                        if (feed.getGame().getCurrentGameId() == 0 && Integer.parseInt(feed.getGame().getNextGameTime()) != 0) {
                                            next_gametime_box.setVisibility(View.VISIBLE);
                                        } else {
                                            next_gametime_box.setVisibility(View.GONE);
                                        }
                                    }

                                } else {
                                    next_gametime_box.setVisibility(View.GONE);

                                }

                                setButtonText(feed.getGame().getCurrentGameId());


                                if (user == null) {
                                    //if uid not logged in show estimated rank
                                    setNotLoggedInUI();
                                } else {
                                    //show rank section if user logged in
                                    setLoggedInUI();
                                }

                            }
                            progressLayout.showContent();
                            login_layout.setVisibility(GONE);
                            dashboard_layout.setVisibility(VISIBLE);
                       /* top_snack_bar.setVisibility(GONE);*/
                            if (!checkPhonePermission()) {
                                permission_layout.setVisibility(VISIBLE);
                            } else {
                                permission_layout.setVisibility(GONE);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }
                }, 1500);

            } catch (Exception ee) {
                ee.printStackTrace();

            }
        }

        class RetrieveFeedTask extends AsyncTask<String, Void, Bitmap> {
            String profile_img;

            public RetrieveFeedTask(String profile_img) {
                this.profile_img = profile_img;
            }

            @Override
            protected Bitmap doInBackground(String... strings) {
                ImageDownloaderCrossFade fade = ImageDownloaderCrossFade.getInstance();
                Bitmap bitmap = fade.downloadBitmap(profile_img, 0, 0, mContext);
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                userImage.setImageBitmap(bitmap);
            }
        }


        /**
         * Sections that needs to be shown while user us not logged in.
         */
        public void setNotLoggedInUI() {
            //user not logged in
            login_lay.setVisibility(View.VISIBLE);

            // rank_box.setVisibility(View.GONE);
            photo_layout.setVisibility(View.GONE);
            month_rank.setCompoundDrawablesWithIntrinsicBounds(0, 0, toi.com.trivia.R.drawable.lock_icon, 0);
            month_rank.setText("");
            week_rank.setCompoundDrawablesWithIntrinsicBounds(0, 0, toi.com.trivia.R.drawable.lock_icon, 0);
            week_rank.setText("");

        }

        public void setLoggedInUI() {

            login_lay.setVisibility(View.GONE);
            rank_box.setVisibility(View.VISIBLE);
            month_rank.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            week_rank.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            photo_layout.setVisibility(View.VISIBLE);
        }


        public class CounterClass extends CountDownTimer {
            public CounterClass(long millisInFuture, long countDownInterval) {
                super(millisInFuture, countDownInterval);
            }

            @Override
            public void onFinish() {
                isRunning = false;
                /**
                 *  refresh UI for dashboard as the timer is up for next game to start.
                 */
                fetchDashboard(Integer.parseInt(getUID()));

            }


            @Override
            public void onTick(long millisUntilFinished) {
                isRunning = true;
                long millis = millisUntilFinished;
                //Log.d("secdashboard--", String.valueOf(millisUntilFinished));
                displayTimerTikker(millis);
            }

        }


        private void setButtonText(int gameid) {

            if (gameid != DEFAULT_ZERO) {
                if (mCurrentGamePlayed == DEFAULT_ONE) {
                    //usr has played the current game and also next game timer is visible
                    play_game.setText(mContext.getResources().getString(toi.com.trivia.R.string.button_play_another));

                } else {
                    //usr has not played the current game and next game timer is visible.
                    play_game.setText(mContext.getResources().getString(toi.com.trivia.R.string.button_play_new));

                }
            } else {
                //no game is active right now play from archieve
                play_game.setText(mContext.getResources().getString(toi.com.trivia.R.string.button_play_another));

            }
        }


        /**
         * Display tikker timer left for result display with animation
         *
         * @param millis
         */
        public void displayTimerTikker(long millis) {
            Log.d("timer", String.valueOf(millis));
            try {
                String hours_text = String.format("%02d", TimeUnit.MILLISECONDS.toHours(millis));
                Character[] charhoursArray = CommonUtility.toCharacterArray(hours_text);

                if (!hours1.getText().toString().equals(charhoursArray[0].toString())) {
                    Animation slideUpAnimation = AnimationUtils.loadAnimation(mContext,
                            toi.com.trivia.R.anim.slide_up);
                    hours1.clearAnimation();
                    hours1.startAnimation(slideUpAnimation);
                } else {
                    hours1.clearAnimation();

                }

                if (!hours2.getText().toString().equals(charhoursArray[1].toString())) {
                    Animation slideUpAnimation = AnimationUtils.loadAnimation(mContext,
                            toi.com.trivia.R.anim.slide_up);
                    hours2.clearAnimation();
                    hours2.startAnimation(slideUpAnimation);
                } else {
                    hours2.clearAnimation();

                }
                hours1.setText(charhoursArray[0].toString());
                hours2.setText(charhoursArray[1].toString());

                String mins_text = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
                Character[] charMinsArray = CommonUtility.toCharacterArray(mins_text);
                if (!minutes1.getText().toString().equals(charMinsArray[0].toString())) {
                    Animation slideUpAnimation = AnimationUtils.loadAnimation(mContext,
                            toi.com.trivia.R.anim.slide_up);
                    minutes1.clearAnimation();
                    minutes1.startAnimation(slideUpAnimation);
                } else {
                    minutes1.clearAnimation();

                }

                if (!minutes2.getText().toString().equals(charMinsArray[1].toString())) {
                    Animation slideUpAnimation = AnimationUtils.loadAnimation(mContext,
                            toi.com.trivia.R.anim.slide_up);
                    minutes2.clearAnimation();
                    minutes2.startAnimation(slideUpAnimation);
                } else {
                    minutes2.clearAnimation();

                }

                minutes1.setText(charMinsArray[0].toString());
                minutes2.setText(charMinsArray[1].toString());

                String sec = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                Character[] charObjectArray = CommonUtility.toCharacterArray(sec);

                Animation slideUpAnimation = null;
                if (!seconds1.getText().toString().equals(charObjectArray[0].toString())) {
                    final String sec1 = seconds1.getText().toString();
                    String sec2 = charObjectArray[0].toString();
                    Log.d("seconds 1 ----", "sec1 = " + sec1 + "sec2 =" + sec2);
                    slideUpAnimation = AnimationUtils.loadAnimation(mContext,
                            toi.com.trivia.R.anim.slide_up);
                    //seconds1.clearAnimation();

                    seconds1.startAnimation(slideUpAnimation);
                } else {
                    seconds1.clearAnimation();
                }

                if (!seconds2.getText().toString().equals(charObjectArray[1].toString())) {
                    String sec1 = seconds2.getText().toString();
                    String sec2 = charObjectArray[1].toString();
                    //Log.d("seconds 2 ----", "sec1 = " + sec1 + "sec2 =" + sec2);
                    slideUpAnimation = AnimationUtils.loadAnimation(mContext,
                            toi.com.trivia.R.anim.slide_up);
                    //seconds2.clearAnimation();
                    seconds2.startAnimation(slideUpAnimation);
                } else {
                    seconds2.clearAnimation();

                }
                seconds1.setText(charObjectArray[0].toString());
                seconds2.setText(charObjectArray[1].toString());
                //Log.d("secdashboard--", "[" + charhoursArray[0].toString() + "][" + charhoursArray[1].toString() + "] [" + charMinsArray[0].toString() + "][" + charMinsArray[1].toString() + "]  [" + charObjectArray[0].toString() + "][" + charObjectArray[1].toString() + "]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        /**
         * Download The url passed in respective folder
         *
         * @param Url
         */
        public void downloadProfilePicture(String Url) {
            String file_name = "";
            if (readPref.getLoginType().equals(TriviaConstants.FB_LOGIN_TYPE)) {
                file_name = "TriviaProfilePicture" + "_" + getUID() + ".jpg";
            } else {
                file_name = "TriviaProfilePicture" + "_" + getUID() + ".jpg";
            }
            dm = (DownloadManager) mContext.getSystemService(
                    mContext.getApplicationContext().DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(Url);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            // Restrict the types of networks over
            // which this download may
            // proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE);
            // Set whether this download may proceed
            // over a roaming connection.
            request.setAllowedOverRoaming(false);
            // Set the title of this download, to be
            // displayed in notifications
            // (if enabled).

            request.setTitle(file_name);
            // Set a description of this download,
            // to be displayed in
            // notifications (if enabled)
            request.setDescription("Android Data download using DownloadManager.");

            Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

            if (isSDPresent) {
                File userImgFile = new File(Environment.getExternalStorageDirectory()
                        .getPath()
                        + "/Android/data/" + mContext.getPackageName() + "/files/Trivia/"
                        + file_name);
                if (userImgFile.exists()) {
                    userImgFile.delete();

                }
                File mediaStorageDir = new File(Environment
                        .getExternalStorageDirectory(), "Trivia");
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("Trivia-- ", "Failed to create directory");

                    }
                }

                request.setDestinationInExternalFilesDir(mContext,
                        "/Trivia", file_name);
                enqueue = dm.enqueue(request);
            } /*else {
                CommonUtility.registerUser(getActivity().getApplicationContext(), fbData); //todo uncomment

            }*/


        }


        /**
         * Register User callback
         *
         * @param context
         * @param map
         */
        public void registerUserapi(final Context context, final HashMap<String, String> map) {
            Log.d("registerUser called", "-----------------");
            savePref = new SavePref(context);
            apiRetroFit = new ApiRetroFit();
            apiService = apiRetroFit.getApiService();
            //Initialize map with default param
            map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
            map.put(PARAM_UQID, CommonUtility.getIMEI(context));
            boolean callRegister = false;
            nextArchiveGameId = "";
            //TODO working here
            /**
             *
             * 0. Get TOI login status, if user object is there then user is logged in else logged out
             * 1. Check if UID is there
             * 2. If Yes; user is already there either logged in or anon browsing no need to update in user pref
             * 2.a. If TOI login status is yes and reg_status = 0 then call register API with user data - update user data
             * 2.b. If TOI login is yes and reg_status =1 , no action
             * 2.c. If TOI login is no and reg_status =0 , no action
             * 2.d. If TOI login is no and reg_status =1 , Expected logout scenario, update reg_status and uid and call register and update data
             * 3. If No; user is not there and we need to invoke register and save in user pref
             * 3.a. If TOI login is yes, then call register with user information and update data
             * 3.b If TOI login id no, then call anon register and update data
             */
            // User user1 = SSOManager.getInstance().getCurrentUser();
            // User user1 = SSOManager.getInstance().getCurrentUser();

            SSOManager.getInstance().checkCurrentUser(mContext, new SSOManager.OnSSOProcessed() {

                @Override
                public void onSSOSuccess(User user) {
                    if (user != null) {
                        user1 = user;
                    } else {
                        user1 = null;
                    }

                }

                @Override
                public void onSSOFailure(SSOResponse ssoResponse) {
                    user1 = null;
                }
            });
            boolean toiIsLogin = false;
            String toiLoginId = "";
            if (user1 != null) {
                toiIsLogin = true;
                toiLoginId = user1.getUserId();

            }
            String triviaLoginId = getLoginId();
            if (!triviaLoginId.equals(toiLoginId)) {//if this case occurs that means user has logout from setting and again logged in from setting without comming back to dashboard page
                savePref.logoutClearData();
            }
            String triviaUid = getUID();
            String triviaRegStatus = getRegStatus();


            //Case 3  user is not there and we need to invoke register and save in user pref
            if (Integer.parseInt(triviaUid) == DEFAULT_ZERO) {

                if (toiIsLogin) {//case 3.a
                    map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ONE));
                    map.put(PARAM_LOGIN_ID, toiLoginId);
                    map.put(PARAM_LOGIN_TYPE, "s");
                    map.put(PARAM_UID, String.valueOf(DEFAULT_ZERO));
                    map.put(PARAM_NAME, user1.getFirstName());

                    map.put(PARAM_EMAIL, CommonUtility.checkNull(user1.getEmailId()));

                    map.put("profile_img", user1.getImgUrl());
                    callRegister = true;
                } else {//case 3.bR
                    map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ZERO));
                    if (user1 != null) {
                        map.put(PARAM_EMAIL, CommonUtility.checkNull(user1.getEmailId()));
                    }
                    callRegister = true;
                }

            } else { //Case 2 user is already there either logged in or anon browsing no need to update in user pref

                if (toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ZERO) {//case 2.a
                    //call register API with user data - update user data
                    map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ONE));
                    map.put(PARAM_LOGIN_ID, toiLoginId);
                    map.put(PARAM_LOGIN_TYPE, "s");
                    map.put(PARAM_UID, triviaUid);
                    map.put(PARAM_NAME, user1.getFirstName());
                    map.put(PARAM_EMAIL, CommonUtility.checkNull(user1.getEmailId()));
                    map.put("profile_img", user1.getImgUrl());
                    callRegister = true;
                } else if (toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ONE) {//case 2.b
                    //no action
                    //2.b. If TOI login is yes and reg_status =1 , no action
                    //Do redirect action
                    callRegister = false;
                } else if (!toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ZERO) {//case 2.c
                    //2.c. If TOI login is no and reg_status =0 , no action
                    // no action
                    callRegister = false;
                } else if (!toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ONE) { //case 2.d
                    //2.d. If TOI login is no and reg_status =1 , Expected logout scenario, update reg_status and uid and call register and update data
                    // Expected logout scenario
                    map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ZERO));
                    if (user1 != null) {
                        map.put(PARAM_EMAIL, CommonUtility.checkNull(user1.getEmailId()));
                    }
                    callRegister = true;
                }

            }

            Log.d("Call register--------", String.valueOf(callRegister));
            if (callRegister) {
                registrationItemsCall = apiService.registerUser(map); //Attach parameter
                CommonUtility.printHashmap(map);
                registrationItemsCall.enqueue(new Callback<HomeItems>() { //Call API
                                                  @Override
                                                  public void onResponse(Call<HomeItems> call, Response<HomeItems> response) {

                                                      Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                                      Log.d("response message", response.message());
                                                      if (response.isSuccessful()) {
                                                          try {
                                                              HomeItems items = response.body();
                                                              if (items.getStatus() == SUCCESS_RESPONSE) {
                                                                  //api call is successful
                                                                  homeItems = items;
                                                                  HomeItems.User user = items.getUser();
                                                                  HomeItems.Game game = items.getGame();
                                                                  HomeItems.Data data = items.getData();
                                                                  HomeItems.Sponsor sponsor = items.getSponsor();

                                                                  String data_fire = data.getData_fire();
                                                                  int uid = items.getUid();
                                                                  int game_count = user.getGame_count();
                                                                  int currentGameId = game.getCurrentGameId();
                                                                  int nextGameId = game.getNextGameId();
                                                                  nextArchiveGameId = game.getNextGameArchiveId();
                                                                  HomeItems.Data.ADS ads = items.getData().getAds();

                                                                  savePref.saveAdsUnits(ads.getANS_BACK(), ads.getGAME_START(), ads.getRESULT_OPEN(), ads.getBOT_AD());

                                                                  //save data into local preferences
                                                                  //if do not get ssoid from api set reg status as zero else 1
                                                                  if (items.getSsoId().equals(String.valueOf(DEFAULT_ZERO))) {
                                                                      savePref.saveRegStatus(String.valueOf(DEFAULT_ZERO));
                                                                      savePref.saveLoginId("");//save ssoid in local preference i.e login id for further API calls
                                                                      savePref.isLoggedIn(String.valueOf(DEFAULT_ZERO));
                                                                  } else {
                                                                      savePref.saveRegStatus(String.valueOf(DEFAULT_ONE));
                                                                      savePref.saveLoginId(items.getSsoId());//save ssoid in local preference i.e login id for further API calls
                                                                      savePref.isLoggedIn(String.valueOf(DEFAULT_ONE));
                                                                  }
                                                                  Log.d("SSO-------------", items.getSsoId().toString());
                                                                  savePref.saveUID(String.valueOf(uid));
                                                                  savePref.saveDefaultDate(game.getStime());
                                                                  savePref.saveUserName(CommonUtility.checkName(user.getName()));
                                                                  savePref.saveUserImage(user.getProfile_img());
                                                                  savePref.saveScreenBackground(sponsor.getImg_url());
                                                                  savePref.saveSponsorName(sponsor.getName());
                                                                  savePref.saveUserGameCount(String.valueOf(game_count));
                                                                  savePref.saveUserEmail(CommonUtility.checkName(user.getEmail()));

                                                                  if (game_count == 0) {
                                                                      savePref.isFirstTime(true);
                                                                  } else {
                                                                      savePref.isFirstTime(false);
                                                                  }
                                                                  if (currentGameId == 0) {
                                                                      //no active game
                                                                      savePref.saveCurrentGameId(String.valueOf(currentGameId));

                                                                      if (play_button_login != null) {
                                                                          play_button_login.setEnabled(true);
                                                                      }
                                                                  } else {
                                                                      //current game is active
                                                                      if (play_button_login != null) {

                                                                          play_button_login.setEnabled(true);
                                                                      }

                                                                      savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                                      savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference
                                                                  }


                                                                  //redirect to UI based on game count
                                                                  if (game_count == DEFAULT_ZERO) {
                                                                      pageShown = LOGIN_SCREEN;
                                                                      initViews();
                                                                  } else {
                                                                      pageShown = DASHBOARD_SCREEN;
                                                                      initUI();
                                                                  }

                                                                  processStorageAndSponsor(sponsor.getImg_url(), sponsor.getName(), context, data);
                                                                  Log.d("register download", "---called");
                                                              }
                                                          } catch (Exception e) {
                                                              e.printStackTrace();
                                                          }
                                                      }

                                                      //hide loader icon from page if visible
                                                      if (register_loader != null) {
                                                          register_loader.setVisibility(View.GONE);
                                                      }

                                                  }

                                                  @Override
                                                  public void onFailure(Call<HomeItems> call, Throwable t) {
                                                      t.printStackTrace();
                                                      /*CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                                              ERROR_FAILURE,
                                                              new DialogInterface.OnClickListener() {
                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      registerUserapi(context, map);
                                                                      dialog.dismiss();
                                                                  }
                                                              }, new DialogInterface.OnClickListener() {

                                                                  @Override
                                                                  public void onClick(DialogInterface dialog, int which) {
                                                                      dialog.dismiss();
                                                                  }
                                                              });*/
                                                  }
                                              }

                );

            } else {
                //Call dashboard API
                String mapString = readPref.getUserAnswer();
                if (mapString.length() != 0) {
                    submitUserAnswer(context);
                } else {
                    fetchDashboard(Integer.parseInt(triviaUid));
                }
            }
        }

        /**
         * hides the imagview that are used for space moving animation
         */
        public void hideSpaceAnimation() {
            bg1.setVisibility(View.GONE);
            bg2.setVisibility(View.GONE);
            bg3.setVisibility(View.GONE);
            bg4.setVisibility(View.GONE);

        }

        /**
         * Dashboard User callback
         *
         * @param context
         * @param map
         */

        public void userDashboard(final Context context, final HashMap<String, String> map) {
            Log.d("userDashboard called", "-------------");

            savePref = new SavePref(context);
            apiRetroFit = new ApiRetroFit();
            apiService = apiRetroFit.getApiService();
            dashboardItems = apiService.getDashboard(map);
            CommonUtility.printHashmap(map);

            dashboardItems.enqueue(new Callback<HomeItems>() {
                                       @Override
                                       public void onResponse(Call<HomeItems> call, Response<HomeItems> response) {

                                           Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                           Log.d("response message", response.message());
                                           if (response.isSuccessful()) {
                                               try {
                                                   homeItems = response.body();
                                                   if (homeItems.getStatus() == SUCCESS_RESPONSE) {
                                                       HomeItems.User user = homeItems.getUser();
                                                       HomeItems.Data data = homeItems.getData();
                                                       HomeItems.Sponsor sponsor = homeItems.getSponsor();
                                                       HomeItems.Game game = homeItems.getGame();
                                                       HomeItems.Data.ADS ads = homeItems.getData().getAds();

                                                       savePref.saveAdsUnits(ads.getANS_BACK(), ads.getGAME_START(), ads.getRESULT_OPEN(), ads.getBOT_AD());

                                                       nextArchiveGameId = game.getNextGameArchiveId();
                                                       int uid = homeItems.getUid();
                                                       int game_count = user.getGame_count();
                                                       savePref.saveDefaultDate(game.getStime());
                                                       //save data to preference
                                                       savePref.saveUID(String.valueOf(uid));
                                                       savePref.saveUserName(CommonUtility.checkName(user.getName()));
                                                       savePref.saveUserImage(user.getProfile_img());
                                                       savePref.saveUserGameCount(String.valueOf(game_count));
                                                       savePref.saveScreenBackground(sponsor.getImg_url());
                                                       savePref.saveSponsorName(sponsor.getName());
                                                       savePref.saveUserEmail(CommonUtility.checkName(user.getEmail()));
                                                       if (game_count == 0) {
                                                           savePref.isFirstTime(true);
                                                       } else {
                                                           savePref.isFirstTime(false);
                                                       }
                                                       //todo registration response sent

                                                       mCurrentGamePlayed = game.getIsCurrentPlayed();
                                                       int currentGameId = game.getCurrentGameId();
                                                       int nextGameId = game.getNextGameId();
                                                       int resultGameId = game.getResultGameId();

                                                  /* if (currentGameId == 0) {
                                                       //no active game
                                                       savePref.saveCurrentGameId(String.valueOf(currentGameId));
                                                       // CommonUtility.showMessageAlert(context, NO_GAME_ACTIVE);
                                                   } else {*/
                                                       //current game is active
                                                       savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                       savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference
                                                       savePref.saveResultGameId(String.valueOf(resultGameId));
                                                       //}

                                                       if (homeItems.getSsoId().equals(String.valueOf(DEFAULT_ZERO))) {
                                                           savePref.saveRegStatus(String.valueOf(DEFAULT_ZERO));
                                                           savePref.saveLoginId("");//save ssoid in local preference i.e login id for further API calls
                                                           savePref.isLoggedIn(String.valueOf(DEFAULT_ZERO));
                                                       } else {
                                                           savePref.saveRegStatus(String.valueOf(DEFAULT_ONE));
                                                           savePref.saveLoginId(homeItems.getSsoId());//save ssoid in local preference i.e login id for further API calls
                                                           savePref.isLoggedIn(String.valueOf(DEFAULT_ONE));
                                                       }

                                                       //redirect to UI based on game count
                                                       if (game_count == DEFAULT_ZERO) {
                                                           pageShown = LOGIN_SCREEN;
                                                           initViews();
                                                       } else {
                                                           pageShown = DASHBOARD_SCREEN;
                                                           initUI();
                                                       }

                                                       processStorageAndSponsor(sponsor.getImg_url(), sponsor.getName(), context, data);
                                                       Log.d("dashboard download", "---called");
                                                   }
                                               } catch (Exception e) {
                                                   e.printStackTrace();
                                               }
                                           }


                                       }

                                       @Override
                                       public void onFailure(Call<HomeItems> call, Throwable t) {
                                           t.printStackTrace();
                                          /* CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                                   ERROR_FAILURE,
                                                   new DialogInterface.OnClickListener() {
                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           registerUserapi(context, map);
                                                           dialog.dismiss();
                                                       }
                                                   }, new DialogInterface.OnClickListener() {

                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           dialog.dismiss();
                                                       }
                                                   });*/
                                       }
                                   }

            );


        }

        public HomeItems getDasboardFeed() {
            return homeItems;
        }

        /**
         * Downlaod Sponsor iamge and fireworks images in respective folders
         *
         * @param Url
         * @param name
         * @param type
         */
        public void downloadSponsorPicture(String Url, String name, int type) {
            String file_name = "";
            if (readPref.getLoginType().equals(TriviaConstants.FB_LOGIN_TYPE)) {
                file_name = name + ".jpg";
            } else {
                file_name = name + ".jpg";
            }

            if (type == DEFAULT_ONE) {
                file_name = name + ".gif";
            }
            dm = (DownloadManager) mContext.getSystemService(
                    mContext.DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(Url);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            // Restrict the types of networks over
            // which this download may
            // proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE);
            // Set whether this download may proceed
            // over a roaming connection.
            request.setAllowedOverRoaming(false);
            // Set the title of this download, to be
            // displayed in notifications
            // (if enabled).
            request.setTitle(file_name);
            // Set a description of this download,
            // to be displayed in
            // notifications (if enabled)
            request.setDescription("Android Data download using DownloadManager.");

            Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

            if (isSDPresent) {
                File mediaStorageDir = null;
                if (type == DEFAULT_ZERO) {
                    mediaStorageDir = new File(Environment
                            .getExternalStorageDirectory(), "Trivia/Sponsor");
                } else {
                    mediaStorageDir = new File(Environment
                            .getExternalStorageDirectory(), "Trivia/Others");
                }

                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("Trivia-- ", "Failed to create directory");

                    }
                }
                if (type == DEFAULT_ZERO) {
                    request.setDestinationInExternalFilesDir(mContext,
                            "/Trivia/Sponsor", file_name);
                } else {
                    request.setDestinationInExternalFilesDir(mContext,
                            "/Trivia/Others", file_name);
                }

                enqueue = dm.enqueue(request);
            } /*else {
                CommonUtility.registerUser(getActivity().getApplicationContext(), fbData); //todo uncomment

            }*/


        }

        /**
         * @param Url
         * @param name
         * @param storagePath Trivia/Sponsor Trivia/Others
         */
        public void downloadSponsorPictureNew(String Url, String name, String storagePath) {
            String file_name = name;

            dm = (DownloadManager) mContext.getSystemService(
                    mContext.DOWNLOAD_SERVICE);
            Uri Download_Uri = Uri.parse(Url);
            DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

            // Restrict the types of networks over
            // which this download may
            // proceed.
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI
                    | DownloadManager.Request.NETWORK_MOBILE);
            // Set whether this download may proceed
            // over a roaming connection.
            request.setAllowedOverRoaming(false);
            // Set the title of this download, to be
            // displayed in notifications
            // (if enabled).
            request.setTitle(file_name);
            // Set a description of this download,
            // to be displayed in
            // notifications (if enabled)
            request.setDescription("Android Data download using DownloadManager.");

            Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);


            if (isSDPresent) {

                File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), storagePath);
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("Trivia-- ", "Directoy already exists or failed to create");
                    }
                }
                request.setDestinationInExternalFilesDir(mContext, storagePath, file_name);
                enqueue = dm.enqueue(request);
            } /*else {
                CommonUtility.registerUser(getActivity().getApplicationContext(), fbData); //todo uncomment

            }*/


        }


        public void processStorageAndSponsor(String sponsorUrl, String sponsorName, Context context, HomeItems.Data data) {
            boolean isDirectoryExists = false;
            String defaultImgPath = "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/fireworks.gif";
            File imgFile = new File(Environment.getExternalStorageDirectory().getPath() + defaultImgPath);
            if (!imgFile.exists()) {
                downloadSponsorPictureNew("http://toi.techletsolutions.com/sites/default/files/toifavicon.ico", FIREWORKS_IMAGE + ".gif", "Trivia/Sponsor");
                isDirectoryExists = true;
            }

            if (sponsorUrl == null || sponsorUrl.isEmpty() || sponsorName == null || sponsorName.isEmpty()) {
                //Do nothing
            } else {
                //Check if sponsor image is there in mobile else download sponsor image
                Uri sponsorURI = Uri.parse(sponsorUrl);
                String fileName = sponsorURI.getLastPathSegment();
                int i = fileName.lastIndexOf('.');
                String fileExtension = fileName.substring(i + 1);
                fileExtension = "." + fileExtension;

                File sponsorImgFile = new File(Environment.getExternalStorageDirectory().getPath()
                        + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/" + sponsorName + fileExtension);
                if (!sponsorImgFile.exists()) {
                    downloadSponsorPictureNew(sponsorUrl, sponsorName + fileExtension, "Trivia/Sponsor");
                }
            }

        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            try {
                // Unregister since the activity is about to be closed.
                LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mLoginReceiver);
                mContext.unregisterReceiver(receiver);


                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(homeResumedReciver);
                LocalBroadcastManager.getInstance(mContext).unregisterReceiver(networkStateReceiver);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public boolean checkPhonePermission() {

       /* int permissionCheck = ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck ==  PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }*/

            PackageManager pm = mContext.getPackageManager();
            int hasPerm = pm.checkPermission(
                    android.Manifest.permission.READ_PHONE_STATE,
                    mContext.getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }

        // Our handler for received Intents. This will be called whenever an Intent
        // with an action named "custom-event-name" is broadcasted.
        private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Get extra data included in the Intent
                String message = intent.getStringExtra("user");
                Log.d("receiver", "Got login: " + message);
            }
        };


        protected void initNetworkStateReciever() {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            mContext.registerReceiver(networkStateReceiver, filter);

        }


        /**
         * Fetch new game data and insert into DB
         *
         * @param context
         * @param currentGameId
         * @param showResultflag
         */
        public void SubmitAnswers(final Context context, final HashMap<String, String> currentGameId, final int showResultflag) {
            final ReadPref readPref = new ReadPref(context);
            Log.d("SubmitAnswers called", "-----------------");
            savePref = new SavePref(context);

            apiRetroFit = new ApiRetroFit();
            apiService = apiRetroFit.getApiService();

            newSubmitcall = apiService.submitAnswer(currentGameId);
            CommonUtility.printHashmap(currentGameId);

            newSubmitcall.enqueue(new Callback<Contents>() {
                                      @Override
                                      public void onResponse(Call<Contents> call, Response<Contents> response) {
                                          Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                          Log.d("response message", response.message());
                                          try {
                                              if (response.isSuccessful()) {
                                                  final Contents data = response.body();
                                                  if (data.getStatus() == SUCCESS_RESPONSE) {
                                                      CommonUtility.fetchArchive(context, String.valueOf(readPref.getUID()), TriviaConstants.GAME_ARCHIVE);

                                                     /* if (StartQuiz.properties != null) {
                                                          StartQuiz.properties = new NewGame.GameProperties();
                                                          StartQuiz.randamized_ques.clear();
                                                          StartQuiz.answersPojo = new AnswersPojo();
                                                          StartQuiz.answersList.clear();
                                                      }*/
                                                      //savePref.isFirstTime(false);
                                                      // savePref.isReadyShown(false);
                                                      savePref.saveCurrentGameId("");
                                                      savePref.saveNextGameId("");
                                                      //savePref.saveCurrentPosition(0);
                                                      //  savePref.saveResultGameId("");

                                                      Log.d("Msg submit answer--", String.valueOf(data.getMessage()));
                                                      Log.d("answer--", String.valueOf(data.getAnswers()));
                                                      Log.d("game id answer--", String.valueOf(data.getGameId()));
                                                      Log.d("result time answer--", String.valueOf(data.getResultTime()));

                                                      final Bundle bundle = new Bundle();
                                                      bundle.putSerializable("game_end", data);


                                                      //clear answer string
                                                      savePref.saveUserAnswer("");

                                                      fetchDashboard(Integer.parseInt(getUID()));
                                                      if (QuizScreen.pd != null)
                                                          QuizScreen.pd.dismiss();
                                                  } else {
                                                      //APi
                                                      if (QuizScreen.pd != null)
                                                          QuizScreen.pd.dismiss();
                                                      savePref.saveUserAnswer("");
                                                      // CommonUtility.saveAnswerMap(context, currentGameId);
                                                      //CommonUtility.showRetryErrorAlert(context, data.getMessage());
                                                  }

                                                  if (!CommonUtility.haveNetworkConnection(context)) {
                                                      saveAnswerMap(context, currentGameId);
                                                  } else {
                                                      savePref.saveUserAnswer("");
                                                  }

                                              } else {

                                                  //API Response failed
                                                  if (QuizScreen.pd != null)
                                                      QuizScreen.pd.dismiss();
                                                  savePref.saveUserAnswer("");
                                                  //CommonUtility.showRetryErrorAlert(context, response.message());
                                              }


                                          } catch (Exception e) {
                                              e.printStackTrace();
                                              savePref.saveUserAnswer("");
                                          }
                                      }

                                      @Override
                                      public void onFailure(Call<Contents> call, Throwable t) {
                                          t.printStackTrace();
                                          if (QuizScreen.pd != null)
                                              QuizScreen.pd.dismiss();
                                          saveAnswerMap(context, currentGameId);

                                          //CommonUtility.showRetryErrorAlert(context, TRY_LATER);
                                      }
                                  }

            );
        }

        public void saveAnswerMap(Context context, Map<String, String> inputMap) {
            SavePref savePref = new SavePref(context);
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            savePref.saveUserAnswer(jsonString);
        }

        public void updateAnalyticGtmEvent(String eventName, String eventAction, String eventLabel) {

            Log.d("GTM_TOI", eventName + "+" + eventAction + "+" + eventLabel);
            mDataLayer = TagManager.getInstance(mContext).getDataLayer();
            mDataLayer.pushEvent(eventName, DataLayer.mapOf("EventAction", eventAction, "EventLabel", eventLabel));

        }

    }


}