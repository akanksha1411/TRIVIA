package toi.com.trivia.utility.ui;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.androidprogresslayout.ProgressLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toi.com.trivia.R;
import toi.com.trivia.activities.GameArchive;
import toi.com.trivia.activities.HostActivity;
import toi.com.trivia.api.APIService;
import toi.com.trivia.api.ApiRetroFit;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.model.HomeItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * The view that will show the trivia login and Dashboard page
 */
public class TriviaLoginView extends LinearLayout implements View.OnClickListener, TriviaConstants {

    static View view;
    static ReadPref readPref;
    static int UID;
    public static ProgressBar register_loader;
    public static Button play_button;
    int pageShown;

    public static String userName = TriviaConstants.DEFAULT_NAME;

    public static String sponsorName;

    static SavePref savePref;
    static LinearLayout login_lay;
    static LinearLayout rank_box;
    static LinearLayout photo_layout;
    private static int ANIMATION_DURATION = 250;
    static Animation slideUpAnimation;
    Animation slideDownAnimation;

    static HashMap<String, String> fbData = new HashMap<String, String>();
    private static DownloadManager dm;
    public static TextView hours1, hours2, minutes1, minutes2, seconds1, seconds2;
    private static APIService apiService;
    Call<HomeItems> registrationItemsCall;
    private static ApiRetroFit apiRetroFit;
    private static long enqueue;
    static File profilePicture;
    static LinearLayout next_gametime_box;
    BroadcastReceiver receiver;
    static int mCurrentGamePlayed = 0;

    private static final int RC_SIGN_IN = 9001;
    TextView screen_title;
    public static Button play_game, play_button_login, login_register;
    static CircleImageView userImage;
    static Context mContext;
    static Call<HomeItems> dashboardItems;
    public static HomeItems homeItems = new HomeItems();
    public static ProgressLayout progressLayout;
    public static FrameLayout login_layout;
    public static FrameLayout dashboard_layout;
    public static final String ACTION_LOGIN = "ACTION_LOGIN";
    static LinearLayout permission_layout;
    static TextView week_rank, month_rank;
    static ImageView bg1;
    static ImageView bg2, bg3, bg4;
    static String nextArchiveGameId = "";

    public TriviaLoginView(Context context) {
        super(context);
        readPref = new ReadPref(context);
        savePref = new SavePref(context);
        savePref.saveIsLiveCode(false);
        UID = Integer.parseInt(readPref.getUID());
        sponsorName = readPref.getSponsorName();
        this.mContext = context;

        view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dashboard, this, true);
        login_layout = (FrameLayout) view.findViewById(R.id.login_layout);
        progressLayout = (ProgressLayout) view.findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        dashboard_layout = (FrameLayout) view.findViewById(R.id.dashboard_layout);
        permission_layout = (LinearLayout) view.findViewById(R.id.permission_layout);

        bg1 = (ImageView) findViewById(R.id.rotate_bg);
        bg2 = (ImageView) findViewById(R.id.rotate_bg2);
        bg3 = (ImageView) findViewById(R.id.rotate_bg3);
        bg4 = (ImageView) findViewById(R.id.rotate_bg4);

      /*  bg1.startAnimation(rotate);
        bg2.startAnimation(rotate);*/

        if (UID == DEFAULT_ZERO) {
            pageShown = LOGIN_SCREEN;
            //User Registration - when app opens
            HashMap<String, String> fbData = new HashMap<>();
            registerUser(context, fbData, readPref.getRegStatus(), readPref.getIsLoggedIn(), readPref.getUID(), 0); //todo uncomment
            //view = LayoutInflater.from(context).inflate(R.layout.fragment_trivia_login, this, true);
            initViews(context);
        } else {
            pageShown = DASHBOARD_SCREEN;
            // view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dashboard, this, true);

            //final TextView user_others = (TextView) view.findViewById(R.id.user_others);
            TextView open_prizes = (TextView) view.findViewById(R.id.open_prizes);
            open_prizes.setOnClickListener(this);
            TextView open_about_us = (TextView) view.findViewById(R.id.open_about_us);
            open_about_us.setOnClickListener(this);
            TextView open_faq = (TextView) view.findViewById(R.id.open_faq);
            open_faq.setOnClickListener(this);
            TextView open_policy = (TextView) view.findViewById(R.id.open_policy);
            open_policy.setOnClickListener(this);

            play_game = (Button) view.findViewById(R.id.play_game_button);
            play_game.setOnClickListener(this);
            TextView open_leaderboard = (TextView) view.findViewById(R.id.open_leaderboard);
            open_leaderboard.setOnClickListener(this);
            TextView open_archive = (TextView) view.findViewById(R.id.open_archive);
            open_archive.setOnClickListener(this);
            login_register = (Button) view.findViewById(R.id.login_register);
            login_register.setOnClickListener(this);

            fetchDashboard(UID);

        }

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
                                        /*Picasso.with(getContext()).load(fbData.get(PARAM_PROFILE_IMG)).transform(new CommonUtility.RoundedTransformation(10, 0))
                                                .placeholder(R.drawable.default_post_img)
                                                .error(R.drawable.default_post_img)
                                                .into(userImage);*/
                                        Glide.with(getContext())
                                                .load(fbData.get(PARAM_PROFILE_IMG))
                                                .asBitmap()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .placeholder(R.drawable.default_post_img)
                                                .error(R.drawable.default_post_img)
                                                .into(userImage);
                                    }


                                    //todo
                                    registerUser(getContext(), fbData, readPref.getRegStatus(), readPref.getIsLoggedIn(), readPref.getUID(), 1); //todo uncomment


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


   /* public void beginPlayingGame() {
        //  CommonUtility.fetchNewGame(getContext(), Integer.parseInt(readPref.getCurrentGameId()), TriviaConstants.LOGIN_SCREEN);
        if (pageShown == LOGIN_SCREEN) {
            //if play new game called from trivia login page
            int mCurrentGameId = Integer.parseInt(readPref.getCurrentGameId());
            if (mCurrentGameId != 0) {
                //if current game id id avilable the start game
                CommonUtility.fetchNewGame(mContext, mCurrentGameId, TriviaConstants.LOGIN_SCREEN);
            } else {
                //another game- Take to archieve
                //CommonUtility.fetchArchive(mContext, String.valueOf(UID), DASHBOARD_SCREEN);
                Intent intent = new Intent(getContext(), GameArchive.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                CommonUtility.showActivity(getContext(), SCREEN_TYPE, TriviaConstants.DASHBOARD_SCREEN, true, intent, null);
            }
        } else {
            //if play new called from Dashboard page
            */

    /**
     * if the mCurrentGamePlayed value is 1 i.e user has played the current game and next game timer
     * is visible then show archieve link else start new game
     *//*
            if (mCurrentGamePlayed == DEFAULT_ONE) {
                //another game- Take to archieve
                //    CommonUtility.fetchArchive(mContext, String.valueOf(UID), DASHBOARD_SCREEN);
                Intent intent = new Intent(getContext(), GameArchive.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                CommonUtility.showActivity(getContext(), SCREEN_TYPE, TriviaConstants.DASHBOARD_SCREEN, true, intent, null);

            } else {
                if ((readPref.getCurrentGameId() != null && !readPref.getCurrentGameId().trim().isEmpty()) && Integer.parseInt(readPref.getCurrentGameId()) != DEFAULT_ZERO) {
                    //call api - new game
                    CommonUtility.fetchNewGame(mContext, Integer.parseInt(readPref.getCurrentGameId()), TriviaConstants.LOGIN_SCREEN);
                } else {
                    //another game- Take to archieve
                    //CommonUtility.fetchArchive(mContext, String.valueOf(UID), DASHBOARD_SCREEN);
                    Intent intent = new Intent(getContext(), GameArchive.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    CommonUtility.showActivity(getContext(), SCREEN_TYPE, TriviaConstants.DASHBOARD_SCREEN, true, intent, null);
                }
            }
        }


    }*/
    public void beginPlayingGame() {
        savePref.saveIsGameKilled(false);
        savePref.isReadyShown(false);
        //   showAd();
        try {
            DBController dbController = new DBController(mContext);
            if (pageShown == LOGIN_SCREEN) {
                //if play new game called from trivia login page
                int mCurrentGameId = Integer.parseInt(readPref.getCurrentGameId());
                if (mCurrentGameId != 0) {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Login", "Play new game", TriviaConstants.CLICK, "");

                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Login", "Play new game", TriviaConstants.CLICK, "Trivia_And_Login");

                    //if current game id id avilable the start game
                    CommonUtility.fetchNewGame(mContext, mCurrentGameId, TriviaConstants.LOGIN_SCREEN);
                } else {
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Login", "Play another game", TriviaConstants.CLICK, "Trivia_And_Login");
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Login", "Play another game", TriviaConstants.CLICK, "");

                    if (CommonUtility.chkString(nextArchiveGameId)) {
                        savePref.saveArchieveGameId(nextArchiveGameId);
                        savePref.saveCurrentPosition(0);
                        // savePref.saveResultGameId(String.valueOf(items.getGame().getNextGameArchiveId()));
                        dbController.clearDatabase((AppCompatActivity) mContext, nextArchiveGameId);
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
                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Dashboard", "Play another game", TriviaConstants.CLICK, "");

                    CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Play another game", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                    if (CommonUtility.chkString(nextArchiveGameId)) {
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
                    if (Integer.parseInt(readPref.getCurrentGameId()) != DEFAULT_ZERO) {
                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Dashboard", "Play new game", TriviaConstants.CLICK, "");

                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Play new game", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                        //call api - new game
                        CommonUtility.fetchNewGame(mContext, Integer.parseInt(readPref.getCurrentGameId()), TriviaConstants.LOGIN_SCREEN);
                    } else {
                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Dashboard", "Play another game", TriviaConstants.CLICK, "");

                        CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + "Dashboard", "Play another game", TriviaConstants.CLICK, "Trivia_And_Dashboard");

                        if (CommonUtility.chkString(nextArchiveGameId)) {
                            savePref.saveArchieveGameId(nextArchiveGameId);
                            savePref.saveCurrentPosition(0);
                            // savePref.saveResultGameId(String.valueOf(items.getGame().getNextGameArchiveId()));
                            dbController.clearDatabase((AppCompatActivity) mContext, nextArchiveGameId);
                        } else {
                            //another game- Take to archieve
                            Intent intent = new Intent(mContext, GameArchive.class);
                            CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            //another game- Take to archieve
            Intent intent = new Intent(mContext, GameArchive.class);
            CommonUtility.showActivity(mContext, SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
        }
    }


    public void showAd() {
        String adUnit = readPref.getGameStart();
        if (CommonUtility.chkString(adUnit)) {

            //Interstitial ads integration
            final PublisherInterstitialAd mInterstitialAd = new PublisherInterstitialAd(mContext);
            mInterstitialAd.setAdUnitId(adUnit);

            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();
                }

                @Override
                public void onAdClosed() {

                }


                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);

                }
            });
            CommonUtility.requestNewInterstitial(mInterstitialAd, mContext);
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
        if (PARENT_APP.equals("toi")) {
            LocalBroadcastManager.getInstance(mContext).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Toast.makeText(getContext(), "receiver called for login", Toast.LENGTH_SHORT).show();
                }
            }, new IntentFilter(ACTION_LOGIN));
        }

    }

    private void initViews(final Context context) {
        //set background for the screen
        //CommonUtility.initBackground(view, context, TriviaConstants.LOGIN_SCREEN, sponsorName);
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

        play_button_login = (Button) view.findViewById(R.id.play_button);
        play_button_login.setEnabled(false);
        play_button_login.setOnClickListener(this);

        TextView open_prizes1 = (TextView) view.findViewById(R.id.open_prizes1);
        open_prizes1.setOnClickListener(this);
        TextView open_about_us1 = (TextView) view.findViewById(R.id.open_about_us1);
        open_about_us1.setOnClickListener(this);
        TextView open_faq1 = (TextView) view.findViewById(R.id.open_faq1);
        open_faq1.setOnClickListener(this);
        TextView open_policy1 = (TextView) view.findViewById(R.id.open_policy1);
        open_policy1.setOnClickListener(this);
        register_loader = (ProgressBar) view.findViewById(R.id.register_loader);
        final ImageView trivia_logo = (ImageView) view.findViewById(R.id.trivia_logo_text);
      /*  if (homeItems != null) {
            screen_title = (TextView) view.findViewById(toi.com.trivia.R.id.screen_title);
            screen_title.setText(homeItems.getData().getStitle());
        }*/
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
                        play_button_login.setText(mContext.getResources().getString(R.string.button_play_another));
                        play_button_login.setEnabled(true);
                    } else {
                        //usr has not played the current game and next game timer is visible.
                        play_button_login.setText(mContext.getResources().getString(R.string.button_play_new));


                    }
                } else {
                    //no game is active right now play from archieve
                    play_button_login.setText(mContext.getResources().getString(R.string.button_play_another));
                    play_button_login.setEnabled(true);
                }


            }
        }, 0, 3000);

        progressLayout.showContent();




       /* RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5555000);
        rotate.setRepeatCount(100);
        rotate.setInterpolator(new LinearInterpolator());*/

        login_layout.setVisibility(VISIBLE);
        dashboard_layout.setVisibility(GONE);
        permission_layout.setVisibility(GONE);
    }

    /**
     * Starts animation 360 degree i.e verticall flipping to logo
     *
     * @param trivia_logo
     */

    private void startFlipAnimation(ImageView trivia_logo) {

        try {
            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getContext(), R.animator.flipping_animation);
            set.setTarget(trivia_logo);
            set.cancel();
            set.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play_button) {
            //start playgame fetching
            beginPlayingGame();

        } else if (id == R.id.open_prizes) {

            //fetch prizes for current game and open prize page
            CommonUtility.fetchPrizes(getContext(), readPref.getCurrentGameId(), DASHBOARD_SCREEN);
        } else if (id == R.id.open_faq) {
            Bundle bundle = new Bundle();
            bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.FAQ_SCREEN);
            Intent startQuiz = new Intent(getContext(), HostActivity.class);
            CommonUtility.showActivity(getContext().getApplicationContext(), "", 0, true, startQuiz, bundle);
        } else if (id == R.id.open_about_us) {
            Bundle bundle = new Bundle();
            bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.ABOUT_US_SCREEN);
            Intent startQuiz = new Intent(getContext(), HostActivity.class);
            CommonUtility.showActivity(getContext().getApplicationContext(), "", 0, true, startQuiz, bundle);
        } else if (id == R.id.open_policy) {
            Bundle bundle = new Bundle();
            bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.POLICY_SCREEN);
            Intent startQuiz = new Intent(getContext(), HostActivity.class);
            CommonUtility.showActivity(getContext().getApplicationContext(), "", 0, true, startQuiz, bundle);
        } else if (id == R.id.play_game_button) {

            beginPlayingGame();

        } else if (id == R.id.open_leaderboard) {

            //call leaderboard api and open page
            CommonUtility.fetchLeaderBoard((AppCompatActivity) getContext(), String.valueOf(UID), 0, MODE_DAILY, DASHBOARD_SCREEN);

        } else if (id == R.id.open_archive) {
            //CommonUtility.fetchArchive((AppCompatActivity) getContext(), String.valueOf(UID), TriviaConstants.GAME_END);
            Intent intent = new Intent(getContext(), GameArchive.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            CommonUtility.showActivity(getContext(), SCREEN_TYPE, TriviaConstants.GAME_END, true, intent, null);
        } else if (id == R.id.open_prizes1) {

            //fetch prizes for current game and open prize page
            CommonUtility.fetchPrizes(getContext(), readPref.getCurrentGameId(), DASHBOARD_SCREEN);
        } else if (id == R.id.open_faq1) {
            Bundle bundle = new Bundle();
            bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.FAQ_SCREEN);
            Intent startQuiz = new Intent(getContext(), HostActivity.class);
            CommonUtility.showActivity(getContext().getApplicationContext(), "", 0, true, startQuiz, bundle);
        } else if (id == R.id.open_about_us1) {
            Bundle bundle = new Bundle();
            bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.ABOUT_US_SCREEN);
            Intent startQuiz = new Intent(getContext(), HostActivity.class);
            CommonUtility.showActivity(getContext().getApplicationContext(), "", 0, true, startQuiz, bundle);
        } else if (id == R.id.open_policy1) {
            Bundle bundle = new Bundle();
            bundle.putInt(TriviaConstants.SCREEN_TYPE, TriviaConstants.POLICY_SCREEN);
            Intent startQuiz = new Intent(getContext(), HostActivity.class);
            CommonUtility.showActivity(getContext().getApplicationContext(), "", 0, true, startQuiz, bundle);
        } else if (id == R.id.login_register) {
        }
    }

    /**
     * Add post parameters for dashboard api call and call api data
     *
     * @param UID
     */
    public static void fetchDashboard(int UID) {


        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(mContext));
        map.put(PARAM_UID, String.valueOf(UID));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, readPref.getLoginType());
        map.put(PARAM_LOGIN_ID, "133441");
        map.put(PARAM_LOGIN_TOKEN, "85385");

        userDashboard(mContext, map);

    }

    /**
     * Set UI for the Dashboard and set value accordingly
     */
    public static void initUI() {
        // CommonUtility.initBackground(view, mContext, TriviaConstants.DASHBOARD_SCREEN, sponsorName);


        try {
            final TextView user_name = (TextView) view.findViewById(R.id.user_name);


       /* RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(5555000);
        rotate.setRepeatCount(100);
        rotate.setInterpolator(new LinearInterpolator());*/
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

            //set user image into imageview
            userImage = (CircleImageView) view.findViewById(R.id.user_image);

            final String urlImage = readPref.getUserImage();

            next_gametime_box = (LinearLayout) view.findViewById(R.id.next_gametime_box);
            final TextView game_played = (TextView) view.findViewById(R.id.game_played);
            week_rank = (TextView) view.findViewById(R.id.week_rank);
            month_rank = (TextView) view.findViewById(R.id.month_rank);


            login_lay = (LinearLayout) view.findViewById(R.id.login_lay);
            rank_box = (LinearLayout) view.findViewById(R.id.rank_box);
            photo_layout = (LinearLayout) view.findViewById(R.id.photo_layout);

            new Handler().postDelayed(new Runnable() {
                @Override

                public void run() {
                    try {
                        // CommonUtility.initBackground(view, mContext, TriviaConstants.DASHBOARD_SCREEN, sponsorName);
                        savePref.saveIsGameCalled(false);
                        HomeItems feed = getDasboardFeed();
                        if (feed.getGame() != null) {

                            // Log.d("getName()----", CommonUtility.checkNull(feed.getUser().getName().toString()));
                            mCurrentGamePlayed = feed.getGame().getIsCurrentPlayed();
                            userName = CommonUtility.checkName(feed.getUser().getName());
                            // userOthers = CommonUtility.checkNull(feed.getUser().getPost() + " | " + "India");
                            user_name.setText(userName);
                            //user_others.setText(userOthers);
                            // savePref.saveCurrentGameId(CommonUtility.checkNull(String.valueOf(feed.getGame().getCurrentGameId())));//current game id set to preference
                            savePref.saveNextGameId(CommonUtility.checkNull(String.valueOf(feed.getGame().getNextGameId())));
                            userImage.setImageResource(R.drawable.default_avatar);
                            if (profilePicture != null) {
                                if (profilePicture.exists()) {
                                    Log.d("file image", "called");
                                    Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
                                    userImage.setImageBitmap(myBitmap);
                                }
                            } else {
                                Log.d("url image", "called");
                                if (CommonUtility.chkString(feed.getUser().getProfile_img())) {
                                /*    Picasso.with(mContext).load(feed.getUser().getProfile_img()).transform(new CommonUtility.RoundedTransformation(10, 0))
                                            .placeholder(R.drawable.default_post_img)
                                            .error(R.drawable.default_post_img)
                                            .into(userImage);*/

                                    Glide.with(mContext)
                                            .load(feed.getUser().getProfile_img())
                                            .asBitmap()
                                            .placeholder(R.drawable.default_avatar)
                                            .error(R.drawable.default_avatar)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(userImage);
                                } else {
                                    userImage.setImageResource(R.drawable.default_avatar);
                                }
                            }

                            if (feed.getGame().getIsShowResult() == DEFAULT_ONE) {
                                //if (!readPref.getResultGameId().equals(readPref.getResultViewedGameId())) {
                                //show result notification on top

                                CommonUtility.initNotification(mContext, view, String.valueOf(UID), 1, readPref.getResultGameId());
                                // }
                            }
                            game_played.setText(String.valueOf(feed.getUser().getGame_count()));
                            week_rank.setText(String.valueOf(feed.getUser().getWeek_rank()));
                            month_rank.setText(String.valueOf(feed.getUser().getMonth_rank()));

                            hours1 = (TextView) view.findViewById(R.id.hours1);
                            minutes1 = (TextView) view.findViewById(R.id.minutes1);
                            seconds1 = (TextView) view.findViewById(R.id.seconds1);
                            hours2 = (TextView) view.findViewById(R.id.hours2);
                            minutes2 = (TextView) view.findViewById(R.id.minutes2);
                            seconds2 = (TextView) view.findViewById(R.id.seconds2);

                            if (!CommonUtility.checkNull(feed.getGame().getNextGameTime()).equals("0")) {
                                //if next time game is not null then show timer for next game to start
                                //Long nextGameTimeLeft = (Long.valueOf(CommonUtility.checkNull(feed.getGame().getNextGameTime())) * 1000) - System.currentTimeMillis();

                                long server_time = Long.parseLong(feed.getGame().getServer_time());

                                long offset = server_time - (System.currentTimeMillis() / 1000);
                                long counter_time = Long.valueOf(feed.getGame().getNextGameTime()) - offset;
                                Log.d("timer---", "current--" + String.valueOf(System.currentTimeMillis() / 1000) + "server--" + server_time + "offset--" + offset + "result time" + feed.getGame().getNextGameTime() + "difference--" + counter_time);
                                Long nextGameTimeLeft = (counter_time * 1000) - System.currentTimeMillis();
                                final CounterClass timer = new CounterClass(nextGameTimeLeft, 1000);
                                //todo uncomment when timer countdown to be inbuild.
                                timer.start();
                                slideUpAnimation = AnimationUtils.loadAnimation(mContext,
                                        R.anim.slide_up);
                                if (feed.getGame().getIsCurrentPlayed() != 0) {
                                    next_gametime_box.setVisibility(View.VISIBLE);
                                } else {
                                    next_gametime_box.setVisibility(View.GONE);

                                }

                            } else {
                                next_gametime_box.setVisibility(View.GONE);

                            }

                            setButtonText(feed.getGame().getCurrentGameId());
                            if (!readPref.getIsLoggedIn().equals("1")) {
                                //if uid not logged in show estimated rank
                                setNotLoggedInUI();
                            } else {
                                //show rank section if user logged in
                                setLoggedInUI();
                            }

                            TriviaLoginView.progressLayout.showContent();
                            TriviaLoginView.login_layout.setVisibility(GONE);
                            TriviaLoginView.dashboard_layout.setVisibility(VISIBLE);
                            TriviaLoginView.permission_layout.setVisibility(GONE);


                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //  if (feed.getGame().getIsShowResult() == DEFAULT_ONE) {

                    }
                }
            }, 1500);

        } catch (Exception ee) {
            ee.printStackTrace();
            //  if (feed.getGame().getIsShowResult() == DEFAULT_ONE) {

        }
    }

    public static void setNotLoggedInUI() {

        login_lay.setVisibility(View.VISIBLE);
        // rank_box.setVisibility(View.GONE);
        photo_layout.setVisibility(View.GONE);
        month_rank.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_icon, 0);
        month_rank.setText("");
        week_rank.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.lock_icon, 0);
        week_rank.setText("");


    }

    public static void setLoggedInUI() {

        login_lay.setVisibility(View.GONE);
        // rank_box.setVisibility(View.VISIBLE);
        photo_layout.setVisibility(View.VISIBLE);

    }


    public static class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            fetchDashboard(UID);

        }

        @SuppressLint("NewApi")
        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        public void onTick(long millisUntilFinished) {
            displayTimerTikker(millisUntilFinished);
        }

    }

    private static void setButtonText(int gameid) {

        if (gameid != DEFAULT_ZERO) {
            if (mCurrentGamePlayed == DEFAULT_ONE) {
                //usr has played the current game and also next game timer is visible
                play_game.setText(mContext.getResources().getString(R.string.button_play_another));

            } else {
                //usr has not played the current game and next game timer is visible.
                play_game.setText(mContext.getResources().getString(R.string.button_play_new));

            }
        } else {
            //no game is active right now play from archieve
            play_game.setText(mContext.getResources().getString(R.string.button_play_another));

        }
    }


    /**
     * Display tikker timer left for result display with animation
     *
     * @param millis
     */
    public static void displayTimerTikker(long millis) {

        String hours_text = String.format("%02d", TimeUnit.MILLISECONDS.toHours(millis));
        Character[] charhoursArray = CommonUtility.toCharacterArray(hours_text);

        if (!hours1.getText().toString().equals(charhoursArray[0].toString())) {
            hours1.startAnimation(slideUpAnimation);
        } else {
            hours1.clearAnimation();
        }

        if (!hours2.getText().toString().equals(charhoursArray[1].toString())) {
            hours2.startAnimation(slideUpAnimation);
        } else {
            hours2.clearAnimation();
        }
        hours1.setText(charhoursArray[0].toString());
        hours2.setText(charhoursArray[1].toString());

        String mins_text = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)));
        Character[] charMinsArray = CommonUtility.toCharacterArray(mins_text);
        if (!minutes1.getText().toString().equals(charMinsArray[0].toString())) {
            minutes1.startAnimation(slideUpAnimation);
        } else {
            minutes1.clearAnimation();
        }

        if (!minutes2.getText().toString().equals(charMinsArray[1].toString())) {
            minutes2.startAnimation(slideUpAnimation);
        } else {
            minutes2.clearAnimation();
        }

        minutes1.setText(charMinsArray[0].toString());
        minutes2.setText(charMinsArray[1].toString());

        String sec = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        Character[] charObjectArray = CommonUtility.toCharacterArray(sec);
        if (!seconds1.getText().toString().equals(charObjectArray[0].toString())) {
            seconds1.startAnimation(slideUpAnimation);
        } else {
            seconds1.clearAnimation();
        }

        if (!seconds2.getText().toString().equals(charObjectArray[1].toString())) {
            seconds2.startAnimation(slideUpAnimation);
        } else {
            seconds2.clearAnimation();
        }
        seconds1.setText(charObjectArray[0].toString());
        seconds2.setText(charObjectArray[1].toString());
    }

    /**
     * set image to imageview for user
     *
     * @param quiz_image
     * @param url
     */
    private void openImage(final ImageView quiz_image, String url) {
        profilePicture = new File(Environment.getExternalStorageDirectory()
                .getPath()
                + "/Android/data/" + getContext().getPackageName() + "/files/Trivia/"
                + "TriviaProfilePicture" + "_" + UID + ".jpg");
        if (profilePicture.exists()) {
            Log.d("file image", "called");
            Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
            quiz_image.setImageBitmap(myBitmap);
        } else {
            if (CommonUtility.chkString(url)) {
                Log.d("url image", "called");
              /*  Picasso.with(getContext()).load(CommonUtility.checkNull(url)).transform(new CommonUtility.RoundedTransformation(10, 0))
                        .placeholder(R.drawable.default_post_img)
                        .error(R.drawable.default_post_img)
                        .into(quiz_image);*/
                Glide.with(getContext())
                        .load(CommonUtility.checkNull(url))
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_post_img)
                        .error(R.drawable.default_post_img)
                        .into(quiz_image);
            }
        }

    }

    /**
     * Download The url passed in respective folder
     *
     * @param Url
     * @param fbData
     */
    public void downloadProfilePicture(String Url, HashMap<String, String> fbData) {
        String file_name = "";
        if (readPref.getLoginType().equals(TriviaConstants.FB_LOGIN_TYPE)) {
            file_name = "TriviaProfilePicture" + "_" + UID + ".jpg";
        } else {
            file_name = "TriviaProfilePicture" + "_" + UID + ".jpg";
        }
        dm = (DownloadManager) getContext().getSystemService(
                getContext().getApplicationContext().DOWNLOAD_SERVICE);
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

        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

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

            request.setDestinationInExternalFilesDir(getContext(),
                    "/Trivia", file_name);
            enqueue = dm.enqueue(request);
        } /*else {
                CommonUtility.registerUser(getActivity().getApplicationContext(), fbData); //todo uncomment

            }*/


    }

    /**
     * Add post parameters for register and call api for data
     */
    public void registerUser(Context context, HashMap<String, String> fbData, String regStatus, String isLoggedIn, String uid, int type) {
        HashMap<String, String> map = new HashMap<>();
        if (fbData.size() != 0) {
            map.putAll(fbData);
        }

        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_REG_STATUS, regStatus);
        map.put(PARAM_ISLOGGEDIN, isLoggedIn);
        if (!uid.equals("0")) {
            map.put(PARAM_UID, uid);
        }

        registerUserapi(context, map);

    }

    /**
     * Register User callback
     *
     * @param context
     * @param map
     */
    public void registerUserapi(final Context context, HashMap<String, String> map) {
        Log.d("registerUser called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        registrationItemsCall = apiService.registerUser(map);
        CommonUtility.printHashmap(map);
        registrationItemsCall.enqueue(new Callback<HomeItems>() {
                                          @Override
                                          public void onResponse(Call<HomeItems> call, Response<HomeItems> response) {

                                              Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                              Log.d("response message", response.message());
                                              if (response.isSuccessful()) {
                                                  try {
                                                      HomeItems items = response.body();
                                                      homeItems=response.body();
                                                      if (items.getStatus() == SUCCESS_RESPONSE) {
                                                          //just addded for automatically  setting user as logged in
                                                         /* savePref.isLoggedIn("1");
                                                          savePref.saveRegStatus("1");*/
                                                          //api call is successful
                                                          HomeItems.User user = items.getUser();
                                                          HomeItems.Data.ADS ads = items.getData().getAds();

                                                          savePref.saveAdsUnits(ads.getANS_BACK(), ads.getGAME_START(), ads.getRESULT_OPEN(), ads.getBOT_AD());

                                                          int week_rank = user.getWeek_rank();
                                                          int month_rank = user.getMonth_rank();
                                                          int approx_rank = user.getApprox_rank();
                                                          int uid = items.getUid();
                                                          //save uid to preference
                                                          savePref.saveUID(String.valueOf(uid));
                                                          savePref.saveUserName(CommonUtility.checkName(user.getName()));
                                                          savePref.saveUserImage(user.getProfile_img());
                                                          HomeItems.Sponsor sponsor = items.getSponsor();
                                                          savePref.saveScreenBackground(sponsor.getImg_url());
                                                          savePref.saveSponsorName(sponsor.getName());

                                                          int game_count = user.getGame_count();
                                                          savePref.saveUserGameCount(String.valueOf(game_count));
                                                          if (game_count == 0) {
                                                              savePref.isFirstTime(true);
                                                          } else {
                                                              savePref.isFirstTime(false);
                                                          }
                                                          //todo registration response sent
                                                          HomeItems.Game game = items.getGame();
                                                          savePref.saveDefaultDate(game.getStime());
                                                          int currentGameId = game.getCurrentGameId();
                                                          int nextGameId = game.getNextGameId();
                                                          nextArchiveGameId = game.getNextGameArchiveId();
                                                          if (currentGameId == 0) {
                                                              //no active game
                                                              savePref.saveCurrentGameId(String.valueOf(currentGameId));
                                                              // CommonUtility.showMessageAlert(context, NO_GAME_ACTIVE);
                                                              if (TriviaLoginView.play_button_login != null) {
                                                                  TriviaLoginView.play_button_login.setEnabled(false);
                                                              }
                                                          } else {
                                                              if (TriviaLoginView.play_button_login != null) {

                                                                  TriviaLoginView.play_button_login.setEnabled(true);
                                                              }
                                                              //current game is active
                                                              savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                              savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference
                                                          }


                                                          try {
                                                              if (!sponsor.getName().toLowerCase().equals(TOI_SPONSOR_NAME)) {
                                                                  File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                          .getPath()
                                                                          + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/"
                                                                          + sponsor.getName() + ".jpg");
                                                                  if (!imgFile.exists()) {
                                                                      TriviaLoginView.downloadSponsorPicture(sponsor.getImg_url(), sponsor.getName(), 0);
                                                                  }
                                                              }
                                                          } catch (Exception e) {
                                                              e.printStackTrace();
                                                          }
                                                          HomeItems.Data data = items.getData();

                                                          String data_fire = data.getData_fire();
                                                          try {
                                                              if (CommonUtility.chkString(data_fire)) {
                                                                  File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                          .getPath()
                                                                          + "/Android/data/" + context.getPackageName() + "/files/Trivia/Others/"
                                                                          + FIREWORKS_IMAGE + ".gif");
                                                                  if (!imgFile.exists()) {
                                                                      TriviaLoginView.downloadSponsorPicture(data_fire, FIREWORKS_IMAGE, 1);
                                                                  }
                                                              }
                                                          } catch (Exception e) {
                                                              e.printStackTrace();
                                                          }
                                                      }
                                                  } catch (Exception e) {
                                                      e.printStackTrace();
                                                  }
                                              }

                                              if (TriviaLoginView.register_loader != null) {
                                                  TriviaLoginView.register_loader.setVisibility(View.GONE);
                                              }

                                          }

                                          @Override
                                          public void onFailure(Call<HomeItems> call, Throwable t) {
                                              t.printStackTrace();
                                              CommonUtility.showErrorAlert(context, TRY_LATER);
                                          }
                                      }

        );
    }


    /**
     * Register User callback
     *
     * @param context
     * @param map
     *//*
    public void registerUserapi(final Context context, HashMap<String, String> map) {
        Log.d("registerUser called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        registrationItemsCall = apiService.registerUser(map);
        CommonUtility.printHashmap(map);
        registrationItemsCall.enqueue(new Callback<HomeItems>() {
                                          @Override
                                          public void onResponse(Call<HomeItems> call, Response<HomeItems> response) {

                                              Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                              Log.d("response message", response.message());
                                              if (response.isSuccessful()) {
                                                  try {
                                                      HomeItems items = response.body();
                                                      if (items.getStatus() == SUCCESS_RESPONSE) {
                                                          //api call is successful
                                                          HomeItems.User user = items.getUser();

                                                          int week_rank = user.getWeek_rank();
                                                          int month_rank = user.getMonth_rank();
                                                          int approx_rank = user.getApprox_rank();
                                                          int uid = items.getUid();
                                                          //save uid to preference
                                                          savePref.saveUID(String.valueOf(uid));
                                                          savePref.saveUserName(CommonUtility.checkName(user.getName()));
                                                          int game_count = user.getGame_count();

                                                          if (game_count == 0) {
                                                              savePref.isFirstTime(true);
                                                          } else {
                                                              savePref.isFirstTime(false);
                                                          }
                                                          //todo registration response sent
                                                          HomeItems.Game game = items.getGame();

                                                          int currentGameId = game.getCurrentGameId();
                                                          int nextGameId = game.getNextGameId();

                                                          if (currentGameId == 0) {
                                                              //no active game
                                                              CommonUtility.showMessageAlert(context, NO_GAME_ACTIVE);
                                                          } else {
                                                              //current game is active
                                                              savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                              savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference

                                                          }

                                                          setLoggedInUI();


                                                      }
                                                  } catch (Exception e) {
                                                      e.printStackTrace();
                                                  }
                                              }
                                          }

                                          @Override
                                          public void onFailure(Call<HomeItems> call, Throwable t) {
                                              t.printStackTrace();
                                              CommonUtility.showErrorAlert(context, TRY_LATER);
                                          }
                                      }

        );
    }*/


    /**
     * Dashboard User callback
     *
     * @param context
     * @param map
     */
    public static void userDashboard(final Context context, HashMap<String, String> map) {
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
                                                   HomeItems.Data.ADS ads = homeItems.getData().getAds();

                                                   savePref.saveAdsUnits(ads.getANS_BACK(), ads.getGAME_START(), ads.getRESULT_OPEN(), ads.getBOT_AD());

                                                   int week_rank = user.getWeek_rank();
                                                   int month_rank = user.getMonth_rank();
                                                   int approx_rank = user.getApprox_rank();
                                                   int uid = homeItems.getUid();
                                                   //save uid to preference
                                                   savePref.saveUID(String.valueOf(uid));
                                                   savePref.saveUserName(CommonUtility.checkName(user.getName()));
                                                   savePref.saveUserImage(user.getProfile_img());
                                                   savePref.saveUserEmail(CommonUtility.checkName(user.getEmail()));
                                                   int game_count = user.getGame_count();
                                                   savePref.saveUserGameCount(String.valueOf(game_count));

                                                   HomeItems.Sponsor sponsor = homeItems.getSponsor();
                                                   savePref.saveScreenBackground(sponsor.getImg_url());
                                                   savePref.saveSponsorName(sponsor.getName());

                                                   if (game_count == 0) {
                                                       savePref.isFirstTime(true);
                                                   } else {
                                                       savePref.isFirstTime(false);
                                                   }
                                                   //todo registration response sent
                                                   HomeItems.Game game = homeItems.getGame();
                                                   savePref.saveDefaultDate(game.getStime());

                                                   int currentGameId = game.getCurrentGameId();
                                                   int nextGameId = game.getNextGameId();
                                                   int resultGameId = game.getResultGameId();
                                                   nextArchiveGameId = game.getNextGameArchiveId();
                                                   if (currentGameId == 0) {
                                                       //no active game
                                                       savePref.saveCurrentGameId(String.valueOf(currentGameId));
                                                       // CommonUtility.showMessageAlert(context, NO_GAME_ACTIVE);
                                                   } else {
                                                       //current game is active
                                                       savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                       savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference
                                                       savePref.saveResultGameId(String.valueOf(resultGameId));
                                                   }

                                                   if (homeItems.getSsoId().equals(String.valueOf(DEFAULT_ZERO))) {
                                                       savePref.saveRegStatus(String.valueOf(DEFAULT_ZERO));
                                                       savePref.saveLoginId("");//save ssoid in local preference i.e login id for further API calls
                                                       savePref.isLoggedIn(String.valueOf(DEFAULT_ZERO));
                                                   } else {
                                                       savePref.saveRegStatus(String.valueOf(DEFAULT_ONE));
                                                       savePref.saveLoginId(homeItems.getSsoId());//save ssoid in local preference i.e login id for further API calls
                                                       savePref.isLoggedIn(String.valueOf(DEFAULT_ONE));
                                                   }

                                                   //for making user loggin or nonlogged in - set these to 0 or 1
                                                   savePref.isLoggedIn("1");
                                                   savePref.saveRegStatus("1");

                                                   initUI();

                                                   try {
                                                       if (!sponsor.getName().toLowerCase().equals(TOI_SPONSOR_NAME)) {
                                                           File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                   .getPath()
                                                                   + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/"
                                                                   + sponsor.getName() + ".jpg");
                                                           if (!imgFile.exists()) {
                                                               TriviaLoginView.downloadSponsorPicture(sponsor.getImg_url(), sponsor.getName(), 0);
                                                           }
                                                       }
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                   }
                                                   HomeItems.Data data = homeItems.getData();

                                                   String data_fire = data.getData_fire();
                                                   try {
                                                       if (CommonUtility.chkString(data_fire)) {
                                                           File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                   .getPath()
                                                                   + "/Android/data/" + context.getPackageName() + "/files/Trivia/Others/"
                                                                   + FIREWORKS_IMAGE + ".gif");
                                                           if (!imgFile.exists()) {
                                                               TriviaLoginView.downloadSponsorPicture(data_fire, FIREWORKS_IMAGE, 1);
                                                           }
                                                       }
                                                   } catch (Exception e) {
                                                       e.printStackTrace();
                                                   }

                                               }
                                           } catch (Exception e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }

                                   @Override
                                   public void onFailure(Call<HomeItems> call, Throwable t) {
                                       t.printStackTrace();
                                       CommonUtility.showErrorAlert(context, TRY_LATER);
                                   }
                               }

        );


    }

    public static HomeItems getDasboardFeed() {
        return homeItems;
    }

    /**
     * Downlaod Sponsor iamge and fireworks images in respective folders
     *
     * @param Url
     * @param name
     * @param type
     */
    public static void downloadSponsorPicture(String Url, String name, int type) {
        try {
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

            Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            // Unregister since the activity is about to be closed.
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mLoginReceiver);
            mContext.unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
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

}