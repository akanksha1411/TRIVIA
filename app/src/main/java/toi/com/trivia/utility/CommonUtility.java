package toi.com.trivia.utility;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tagmanager.DataLayer;
import com.google.android.gms.tagmanager.TagManager;
import com.til.np.coke.builders.CokeEventData;
import com.til.np.coke.manager.CokeManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toi.com.trivia.R;
import toi.com.trivia.activities.GameArchive;

import toi.com.trivia.activities.Leaderboard_New;
import toi.com.trivia.activities.PrizesActivity;
import toi.com.trivia.activities.ResultScreen;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.api.APIService;
import toi.com.trivia.api.ApiRetroFit;
import toi.com.trivia.fragments.GameEnd_New;
import toi.com.trivia.fragments.MonthlyLeaderboard;
import toi.com.trivia.fragments.WeekLeaderboard;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.HomeItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;

/*
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
*/

/**
 * Created by akanksha on 9/8/16.
 */
public class CommonUtility implements TriviaConstants {

    public static ProgressDialog dialog;
    public static HomeItems homeItems = new HomeItems();

    static PublisherInterstitialAd mInterstitialAd;
    private static final long THRESHOLD_MILLIS_PLAY_NEW_GAME_NON_AD = 1000L;
    private static long lastClickMillisNewGameNonAd;
    private static String ADVERTISING_USER_ID = null;

    /**
     * Start new activity
     *
     * @param context
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     * @param intent
     */
    public static void showActivity(Context context, String bundleParameterName, int bundleValue,
                                    boolean isUsedBundle, Intent intent, Bundle bundle) {

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            intent.putExtras(args);
        }

        if (isUsedBundle) {
            if (bundle != null) {
                intent.putExtras(bundle);
            }
        }
        try {
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
                    context, R.anim.slide_in_left,
                    R.anim.slide_out_right).toBundle();
            context.startActivity(intent, bndlanimation);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Start new activity with no history
     *
     * @param context
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     * @param intent
     */
    public static void showActivityNoHistory(Context context, String bundleParameterName, int bundleValue,
                                             boolean isUsedBundle, Intent intent, Bundle bundle) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            intent.putExtras(args);
        }

        if (isUsedBundle) {
            if (bundle != null) {
                intent.putExtras(bundle);
            }
        }
        try {
            Bundle bndlanimation = ActivityOptions.makeCustomAnimation(
                    context, R.anim.slide_in_left,
                    R.anim.slide_out_right).toBundle();
            context.startActivity(intent, bndlanimation);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * @param bitmap
     * @param pixels
     * @return
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Set background for App as default or from URL
     */
    public static void initBackground(View view, Context context, int type, String sponsorName) {

        if (!sponsorName.toLowerCase().equals(TOI_SPONSOR_NAME) && !sponsorName.equals("")) {


            File sponsorFile = new File(Environment.getExternalStorageDirectory()
                    .getPath()
                    + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/"
                    + sponsorName + ".jpg");
            if (sponsorFile != null) {
                if (sponsorFile.exists()) {
                    try {

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(sponsorFile.getAbsolutePath(), options);
                        //Bitmap bitmap = getBitmapFromURL(sponsorImg);
                        Drawable sponsor = new BitmapDrawable(context.getResources(), bitmap);

                        //   BitmapDrawable sponsor = new BitmapDrawable(context.getResources(), b);
                        switch (type) {
                            case TriviaConstants.HOST_ACTIVITY:
                                LinearLayout main_layout = (LinearLayout) view.findViewById(R.id.main_layout);
                                main_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.ANSWERS_ACTIVITY:
                                LinearLayout answers_layout = (LinearLayout) view.findViewById(R.id.answers_layout);
                                answers_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.RESULT_SCREEN:
                                FrameLayout result_layout = (FrameLayout) view.findViewById(R.id.result_layout);
                                result_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.GAME_ARCHIVE:
                                LinearLayout game_archive_layout = (LinearLayout) view.findViewById(R.id.game_archive_layout);
                                game_archive_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.LEADERBOARD:
                                LinearLayout leaderboard_layout = (LinearLayout) view.findViewById(R.id.leaderboard_layout);
                                leaderboard_layout.setBackground(sponsor);
                                break;

                            case TriviaConstants.START_QUIZ_ACTIVITY:
                                RelativeLayout quiz_layout = (RelativeLayout) view.findViewById(R.id.quiz_layout);
                                quiz_layout.setBackground(sponsor);
                                //    quiz_layout.setBackground(gifFromResource);
                                break;
                            case TriviaConstants.LOGIN_SCREEN:
                                FrameLayout login_layout = (FrameLayout) view.findViewById(R.id.login_layout);
                                login_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.GAME_END:
                                RelativeLayout game_end = (RelativeLayout) view.findViewById(R.id.game_end_result);
                                game_end.setBackground(sponsor);
                                break;
                            case TriviaConstants.DASHBOARD_SCREEN:
                                FrameLayout dashboard_layout = (FrameLayout) view.findViewById(R.id.dashboard_layout);
                                dashboard_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.GAME_LOADER:
                                LinearLayout loader_layout = (LinearLayout) view.findViewById(R.id.loader_layout);
                                loader_layout.setBackground(sponsor);
                                break;
                            case TriviaConstants.CATEGORY_PAGE:
                                RelativeLayout category_layout = (RelativeLayout) view.findViewById(R.id.category_layout);
                                category_layout.setBackground(sponsor);
                                break;
                        }
                    } catch (Exception e) {
                        setDefaultBackground(view, context, type);
                        e.printStackTrace();
                    }

                } else {
                    setDefaultBackground(view, context, type);
                }
            } else {
                setDefaultBackground(view, context, type);

            }
        } else {
            setDefaultBackground(view, context, type);

        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * sets the default TOI theme to apps as background if sponsor not present
     *
     * @param view
     * @param context
     * @param type
     */
    public static void setDefaultBackground(View view, Context context, int type) {
        //   GifDrawable gifFromResource = null;
        try {

            //gifFromResource = new GifDrawable(context.getResources(), R.drawable.home_animation);

            Drawable gifFromResource = ContextCompat.getDrawable(context, R.drawable.bg);

            switch (type) {
                case TriviaConstants.HOST_ACTIVITY:
                    LinearLayout main_layout = (LinearLayout) view.findViewById(R.id.main_layout);
                    main_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.ANSWERS_ACTIVITY:
                    LinearLayout answers_layout = (LinearLayout) view.findViewById(R.id.answers_layout);
                    answers_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.RESULT_SCREEN:
                    FrameLayout result_layout = (FrameLayout) view.findViewById(R.id.result_layout);
                    result_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.GAME_ARCHIVE:
                    LinearLayout game_archive_layout = (LinearLayout) view.findViewById(R.id.game_archive_layout);
                    game_archive_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.LEADERBOARD:
                    LinearLayout leaderboard_layout = (LinearLayout) view.findViewById(R.id.leaderboard_layout);
                    leaderboard_layout.setBackground(gifFromResource);
                    break;

                case TriviaConstants.START_QUIZ_ACTIVITY:
                    RelativeLayout quiz_layout = (RelativeLayout) view.findViewById(R.id.quiz_layout);
                    quiz_layout.setBackground(context.getResources().getDrawable(R.drawable.bg));
                    //    quiz_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.LOGIN_SCREEN:
                    FrameLayout login_layout = (FrameLayout) view.findViewById(R.id.login_layout);
                    login_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.GAME_END:
                    RelativeLayout game_end = (RelativeLayout) view.findViewById(R.id.game_end_result);
                    game_end.setBackground(gifFromResource);
                    break;
                case TriviaConstants.DASHBOARD_SCREEN:
                    FrameLayout dashboard_layout = (FrameLayout) view.findViewById(R.id.dashboard_layout);
                    dashboard_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.GAME_LOADER:
                    LinearLayout loader_layout = (LinearLayout) view.findViewById(R.id.loader_layout);
                    loader_layout.setBackground(gifFromResource);
                    break;
                case TriviaConstants.CATEGORY_PAGE:
                    RelativeLayout category_layout = (RelativeLayout) view.findViewById(R.id.category_layout);
                    category_layout.setBackground(gifFromResource);
                    break;
               /* Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
                quiz_image.setImageBitmap(myBitmap);*/

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get device country iso code from configurations
     *
     * @param context
     */
    public static String getDeviceCountry(Context context) {
        String locale = context.getResources().getConfiguration().locale.getDisplayCountry();

        return locale;

    }

    /**
     * Get IMEI no for the device to be sent as UQID for trivia
     *
     * @param context
     * @return
     */
    public static String getIMEI(final Context context) {
        /*String imei = "";
        try {
            TelephonyManager mngr = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
            imei = mngr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

       /* final String[] advId = {""};
        final String[] adId = new String[1];
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    adId[0] = adInfo != null ? adInfo.getId() : null;
                    Log.d("AD ID ---", adId[0]);
                    advId[0] = adId[0];
                    Log.d("ADV ---", adId[0]);
                    // Use the advertising id
                } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
                    // Error handling if needed
                }
            }
        });*/
        return /*getAdvertisingId(context)*/"50a7b32d-bc17-4784-b078-556a1ef400ec";
    }

    public static String getAdvertisingId(final Context context) {

        if (null == ADVERTISING_USER_ID) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        com.google.android.gms.ads.identifier.AdvertisingIdClient.Info adInfo = com.google.android.gms.ads.identifier.AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
                        ADVERTISING_USER_ID = adInfo.getId();
                        Log.d("ADVERTISING_USER_ID ---", ADVERTISING_USER_ID);

                    } catch (IOException | NullPointerException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                        ADVERTISING_USER_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                        Log.d("ADVER_USER_ID exception", ADVERTISING_USER_ID);
                    }
                }
            }).start();
        }
        return ADVERTISING_USER_ID;
    }


    /***
     * convert comma separated string to staring list
     *
     * @param commaSeparedString
     * @return
     */
    public static List<String> convertCommaStringToList(String commaSeparedString) {
        List<String> items =
                new ArrayList<String>(Arrays.asList(commaSeparedString.split(",")));

        return items;
    }

    /**
     * Alert to be shown when api calls fails or some error occurs
     *
     * @param context
     */
    public static void showErrorAlert(final Context context, final String message) {

        try {
          /*  new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            if (!message.equals(TriviaConstants.TRY_LATER)) {
                                //finish the activity opened on okk click
                                ((AppCompatActivity) context).finish();
                            } else {
                                dialog.dismiss();
                            }

                        }
                    }).show();*/

           /* SnackbarManager.show(
                    com.nispok.snackbar.Snackbar.with(context) // context
                            .text(message) // text to display
                            .actionLabel("Ok")
                            .duration(10000)
                            .color(context.getResources().getColor(R.color.black_transparent))
                            .textColor(context.getResources().getColor(R.color.white))// action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                    snackbar.dismiss();
                                }
                            }) // action button's ActionClickListener
            );*/
            View rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);

            Snackbar.make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Snackbar to be shown when api calls fails or some error occurs
     * with close button
     *
     * @param context
     */
    public static void showCloseErrorAlert(final AppCompatActivity context, final String message) {
        final SavePref savePref = new SavePref(context);
        try {
          /*  new AlertDialog.Builder(context)
                    .setCancelable(false)
                    .setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            if (!message.equals(TriviaConstants.TRY_LATER)) {
                                //finish the activity opened on okk click
                                ((AppCompatActivity) context).finish();
                            } else {
                                dialog.dismiss();
                            }

                        }
                    }).show();*/

           /* SnackbarManager.show(
                    com.nispok.snackbar.Snackbar.with(context) // context
                            .text(message) // text to display
                            .actionLabel("Ok")
                            .duration(10000)
                            .color(context.getResources().getColor(R.color.black_transparent))
                            .textColor(context.getResources().getColor(R.color.white))// action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                    snackbar.dismiss();
                                }
                            }) // action button's ActionClickListener
            );*/
            View rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);

            // Snackbar.make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();

            final Snackbar snackbar = Snackbar.make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);

                   /* .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (StartQuiz.answersPojo != null) {
                                AnswersPojo pojo = StartQuiz.answersPojo;
                                savePref.saveCurrentPosition(0);
                                savePref.isReadyShown(false);

                                QuizScreen.submitAnswers(pojo);
                            }
                        }
                    });*/
            snackbar.setAction("CLOSE", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (ResultScreen.context != null) {
                            ResultScreen.context.finish();
                        }
                        savePref.isReadyShown(false);
                        if (context instanceof GameArchive) {
                            snackbar.dismiss();
                        } else {
                            if (context != null) {
                                context.finish();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                            /*if (StartQuiz.answersPojo != null) {
                                AnswersPojo pojo = StartQuiz.answersPojo;
                                savePref.saveCurrentPosition(0);
                                savePref.isReadyShown(false);

                                QuizScreen.submitAnswers(pojo);
                            }*/
                }
            });

// Changing message text color
            snackbar.setActionTextColor(Color.RED);

// Changing action button text color

            snackbar.show();

           /* SnackbarManager.show(
                    Snackbar.with(context) // context
                            .text(message) // text to display
                            .actionLabel("RETRY") // action button label
                            .actionListener(new ActionClickListener() {
                                @Override
                                public void onActionClicked(Snackbar snackbar) {
                                    if (StartQuiz.answersPojo != null) {
                                        AnswersPojo pojo = StartQuiz.answersPojo;
                                        savePref.saveCurrentPosition(0);
                                        savePref.isReadyShown(false);

                                        QuizScreen.submitAnswers(pojo);
                                    }
                                }
                            }) // action button's ActionClickListener
                    , ((AppCompatActivity) context));*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void showAlertRetryCancel(Context context, String title, String message,
                                            DialogInterface.OnClickListener positiveButtonClickListener,
                                            DialogInterface.OnClickListener negativeButtonClickListener) {
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

            builder.setTitle(title);
            builder.setMessage(message);

            builder.setCancelable(false).setPositiveButton(R.string.retry, positiveButtonClickListener)
                    .setNegativeButton(R.string.cancel, negativeButtonClickListener);

            android.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Alert to be shown when api calls fails or some error occurs
     *
     * @param context
     */
    public static void showMessageAlert(final Context context, String message) {

        try {

            View rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(rootView.findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * replace with blank if data not present
     *
     * @param text
     * @return
     */
    public static String checkNull(String text) {
        if (text != null) {
            if (text == null || text.length() == 0 || text.equals("null")) {
                text = "";
            }
        } else

        {
            text = "";
        }

        return text;
    }

    /**
     * replace with blank if data not present
     *
     * @param text
     * @return
     */
    public static String getDefaultZero(String text) {
        if (text != null) {
            if (text == null || text.length() == 0 || text.equals("null")) {
                text = "0";
            }
        } else

        {
            text = "0";
        }

        return text;
    }


    /**
     * replace with Anonymous if data not present
     *
     * @param text
     * @return
     */
    public static String checkName(String text) {

        if (text.length() == 0 || text.equals("null") || text == null) {
            text = TriviaConstants.DEFAULT_NAME;
        }

        return text;
    }

    /**
     * Get Comma separated String from list
     *
     * @param selected
     * @return
     */
    public static String commasSeparatedArray(List<String> selected) {
        // The string builder used to construct the string
        StringBuilder commaSepValueBuilder = new StringBuilder();

        // Looping through the list
        for (int i = 0; i < selected.size(); i++) {
            // append the value into the builder
            commaSepValueBuilder.append(selected.get(i));

            // if the value is not the last element of the list
            // then append the comma(,) as well
            if (i != selected.size() - 1) {
                commaSepValueBuilder.append(", ");
            }
        }

        return commaSepValueBuilder.toString();
    }

    /**
     * Show ad in the ad container passed
     *
     * @param mPublisherAdView
     */
    public static void initADs(Context context, final PublisherAdView mPublisherAdView) {
/*
        ReadPref readPref = new ReadPref(context);
        String adUnit = readPref.getBotAd();
       // if (CommonUtility.chkString(adUnit)) {
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
            //final PublisherAdView mPublisherAdView = new PublisherAdView(context);

            //mPublisherAdView.setAdSizes(AdSize.SMART_BANNER);
           */
/*
            if (readPref.getIsLiveCode()) {
                mPublisherAdView.setAdUnitId(adUnit);
            } else {
                mPublisherAdView.setAdUnitId(context.getResources().getString(R.string.banner_ad_unit_id));
            }
            view.addView(mPublisherAdView);*//*

            mPublisherAdView.loadAd(adRequest);
            mPublisherAdView.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    mPublisherAdView.setVisibility(View.GONE);

                    super.onAdClosed();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    mPublisherAdView.setVisibility(View.GONE);

                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLeftApplication() {
                    super.onAdLeftApplication();
                }

                @Override
                public void onAdOpened() {
                    mPublisherAdView.setVisibility(View.VISIBLE);
                    super.onAdOpened();
                }

                @Override
                public void onAdLoaded() {
                    mPublisherAdView.setVisibility(View.VISIBLE);

                    super.onAdLoaded();
                }
            });
*/

       /* } else {

        }*/


        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);
        mPublisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                //  mPublisherAdView.setVisibility(View.GONE);
                mPublisherAdView.setVisibility(View.VISIBLE);
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int i) {
                // mPublisherAdView.setVisibility(View.GONE);

                super.onAdFailedToLoad(i);
            }

            @Override
            public void onAdLeftApplication() {
                mPublisherAdView.setVisibility(View.VISIBLE);
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                mPublisherAdView.setVisibility(View.VISIBLE);
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded() {
                mPublisherAdView.setVisibility(View.VISIBLE);

                super.onAdLoaded();
            }
        });

    }

    /**
     * @param str
     * @return date format of timestamp
     */
    public static String formatTime(long str) {
        DateFormat df = new SimpleDateFormat("dd MMM, h a");
        Long ltym = Long.valueOf(str);
        Date date = new Date(ltym * 1000);
        TimeZone t = TimeZone.getTimeZone("Asia/Calcutta");
        df.setTimeZone(t);
        // String formattedDate = new
        // SimpleDateFormat("mm/dd/yy hh:mm:ss").format(date);
        String reportDate = df.format(date);

        System.out.println("json:" + str + "  , reportDate:" + reportDate);
        return reportDate;
    }


    /**
     * @param str
     * @return date format of timestamp
     */
    public static String formatDate(long str) {

        DateFormat df = new SimpleDateFormat("dd MMM yy");

        Long ltym = Long.valueOf(str);
        Date date = new Date(ltym * 1000);
        TimeZone t = TimeZone.getTimeZone("Asia/Calcutta");
        df.setTimeZone(t);
        // String formattedDate = new
        // SimpleDateFormat("mm/dd/yy hh:mm:ss").format(date);
        String reportDate = df.format(date);

        System.out.println("json:" + str + "  , reportDate:" + reportDate);
        return reportDate;
    }

    /**
     * String to time stamp
     *
     * @param str_date
     * @return
     */
    public static Long getTimestamp(String str_date) {

        Timestamp timestamp = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            Date parsedDate = dateFormat.parse(str_date);
            timestamp = new java.sql.Timestamp(parsedDate.getTime());
            Long d = timestamp.getTime();
            Long d1 = timestamp.getTime();
        } catch (Exception e) {//this generic but you can control another types of exception
            e.printStackTrace();
        }

        return timestamp.getTime();
    }

    /**
     * Convert string to char array
     *
     * @param s
     * @return
     */
    public static Character[] toCharacterArray(String s) {

        if (s == null) {
            return null;
        }

        int len = s.length();
        Character[] array = new Character[len];
        for (int i = 0; i < len; i++) {
            array[i] = Character.valueOf(s.charAt(i));
        }

        return array;
    }

    /**
     * Check network connection available or not
     *
     * @param context
     * @return
     */
    public static boolean haveNetworkConnection(Context context) {


        ConnectivityManager mCM = null;
        if (mCM == null) {
            mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo netInfo = mCM.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();


    }


    /**
     * This builds the notification section on top of the screen
     * and text are set as per the notification and type set
     *
     * @param applicationContext
     * @param notificationType
     * @param resultGameId
     */
    public static void initNotification(final Context applicationContext, View view, final String uid, int notificationType, final String resultGameId) {

        final SavePref savePref = new SavePref(applicationContext);
        switch (notificationType) {
            case 1:
                //result notification
                ImageView hide_notification = (ImageView) view.findViewById(R.id.hide_notification);
                TextView notification_title = (TextView) view.findViewById(R.id.notification_title);
                notification_title.setText(R.string.notification_title);
                TextView notification_action = (TextView) view.findViewById(R.id.notification_action);

                final View result_noti_layout = view.findViewById(R.id.result_noti_layout);


                result_noti_layout.setVisibility(View.VISIBLE);

                //todo set text to textviews wrt notification type
                notification_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonUtility.updateAnalyticGtmEvent(applicationContext, GA_PREFIX + "Dashboard", "See Results", TriviaConstants.CLICK, "Trivia_And_Dashboard");
                        CommonUtility.notifyResult(applicationContext, uid, resultGameId);
                        savePref.saveLastResultGameId(resultGameId);
                        //action
                        fetchResult(applicationContext, uid, resultGameId, TriviaConstants.NOTIFICATION);

                        result_noti_layout.setVisibility(View.GONE);
                    }
                });

                //todo set text to textviews wrt notification type
                result_noti_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePref.saveLastResultGameId(resultGameId);
                        CommonUtility.notifyResult(applicationContext, uid, resultGameId);
                        //action
                        fetchResult(applicationContext, uid, resultGameId, TriviaConstants.NOTIFICATION);

                        result_noti_layout.setVisibility(View.GONE);
                    }
                });
                hide_notification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        result_noti_layout.setVisibility(View.GONE);
                        savePref.saveLastResultGameId(resultGameId);
                        CommonUtility.notifyResult(applicationContext, uid, resultGameId);
                    }
                });
                break;

        }


    }


    /**
     * Make call to fetch new game data
     *
     * @param mCurrentGameId
     */
    public static void fetchPlayedGame(int mCurrentGameId, Context context, String UID) {
        String ntw = getNetworkType(context);
        ReadPref readPref = new ReadPref(context);

        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_GAME_ID, String.valueOf(mCurrentGameId)); // current game id stored in preference from home Api
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(UID));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());
        map.put(PARAM_NETWORK, ntw);
        APICalls.fetchPlayedGame(context, map);

    }

    /**
     * fetch result for the game id passed
     *
     * @param context
     * @param UID
     * @param resultGameId
     * @param openScreen
     */
    public static void fetchResult(final Context context, final String UID, final String resultGameId, int openScreen) {
        final ReadPref readPref = new ReadPref(context);

       /*
        String adUnit = readPref.getResultOpen();

        Intent intent = new Intent(context, ResultScreen.class);
        CommonUtility.showActivity(context, TriviaConstants.SCREEN_TYPE, TriviaConstants.GAME_ARCHIVE, true, intent, null);
        if (CommonUtility.chkString(adUnit)) {
            //Interstitial ads integration
            final PublisherInterstitialAd mInterstitialAd = new PublisherInterstitialAd(context);

            mInterstitialAd.setAdUnitId(adUnit);

            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mInterstitialAd.show();

                }

                @Override
                public void onAdClosed() {
                    //on ads closed the game will start
                    openResult();
                }

                private void openResult() {


                    if (ResultScreen.progressLayout != null) {
                        ResultScreen.progressLayout.showProgress();
                    }
                    HashMap<String, String> map = new HashMap<>();
                    map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
                    map.put(PARAM_UQID, CommonUtility.getIMEI(context));
                    map.put(PARAM_UID, String.valueOf(UID));
                    map.put(PARAM_ISLOGGEDIN, "1");
                    map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
                    map.put(PARAM_LOGIN_ID, readPref.getLoginId());
                    map.put(PARAM_GAME_ID, resultGameId);

                    if (ResultScreen.context != null) {
                        APICalls.userResult(ResultScreen.context, map);
                    } else {
                        APICalls.userResult(context, map);
                    }

                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    openResult();
                }
            });
            requestNewInterstitial(mInterstitialAd, context);

        } else {*/
        String ntw = getNetworkType(context);
        Intent intent = new Intent(context, ResultScreen.class);
        CommonUtility.showActivity(context, TriviaConstants.SCREEN_TYPE, TriviaConstants.GAME_ARCHIVE, true, intent, null);
        if (ResultScreen.progressLayout != null) {
            ResultScreen.progressLayout.showProgress();
        }
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(UID));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());
        map.put(PARAM_GAME_ID, resultGameId);
        map.put(PARAM_NETWORK, ntw);
        APICalls.userResult(context, map);
        // }
    }

    public static void requestNewInterstitial(PublisherInterstitialAd mInterstitialAd, Context context) {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice(CommonUtility.getIMEI(context))
                .build();

        mInterstitialAd.loadAd(adRequest);

    }

    /**
     * Add post parameters for fetching prizes from api
     */
    public static void fetchPrizes(Context context, String gameId, int screenType) {

        Intent intent = new Intent(context, PrizesActivity.class);
        CommonUtility.showActivity(context, SCREEN_TYPE, screenType, true, intent, null);

     /*   HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_GAME_ID, gameId); // A FOR ANDROID
        map.put(PARAM_MODE, "D"); // A FOR ANDROID
        APICalls.triviaPrizes(context, map);*/

    }

    /**
     * Make call to fetch new game data
     *
     * @param mCurrentGameId
     * @param pageCalled
     */

    public static void fetchNewGame(final Context context, final int mCurrentGameId, final int pageCalled) {
        final String ntw = getNetworkType(context);
        final ReadPref readPref = new ReadPref(context);
        String adUnit = readPref.getGameStart();
        Log.d("adUnit- Game start", adUnit);

        if (readPref.isGameCalled() == false) {
            final SavePref savePref = new SavePref(context);
            savePref.saveIsGameCalled(true); //Is game called set to true as soon as game processing starts
            int screenSize = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
            if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {
                adUnit = "/7176/TOI_App_Android/TOI_APP_AOS_Trivia/TOI_APP_AOS_Trivia_MREC";
            }
            if (CommonUtility.chkString(adUnit)) {

                //Interstitial ads integration
                final PublisherInterstitialAd mInterstitialAd = new PublisherInterstitialAd(context);
                mInterstitialAd.setAdUnitId(adUnit);
                mInterstitialAd.setAppEventListener(new AppEventListener() {
                    @Override
                    public void onAppEvent(String s, String s1) {
                        Log.d(s, "----" + s1);
                    }
                });
                mInterstitialAd.setAdListener(new AdListener() {

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mInterstitialAd.show();
                    }

                    @Override
                    public void onAdClosed() {
                        //on ads closed the game will start
                        openGame();
                    }


                    private void openGame() {
                        savePref.saveIsGameCalled(false);
                        createProgressDialog(context);
                        HashMap<String, String> map = new HashMap<>();
                        map.put(PARAM_GAME_ID, String.valueOf(mCurrentGameId)); // current game id stored in preference from home Api
                        map.put(PARAM_NETWORK, ntw);
                        APICalls.fetchNewGame(context, map, pageCalled);
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        Log.d("FailedAdLoad", "Ad failed to load");
                        openGame();
                    }
                });
                requestNewInterstitial(mInterstitialAd, context);


            } else { //If not ad unit is there
                long now = SystemClock.elapsedRealtime();
                if (now - lastClickMillisNewGameNonAd > THRESHOLD_MILLIS_PLAY_NEW_GAME_NON_AD) {
                    createProgressDialog(context);
                    HashMap<String, String> map = new HashMap<>();
                    map.put(PARAM_GAME_ID, String.valueOf(mCurrentGameId)); // current game id stored in preference from home Api
                    map.put(PARAM_NETWORK, ntw);
                    APICalls.fetchNewGame(context, map, pageCalled);
                    savePref.saveIsGameCalled(false);
                }
                lastClickMillisNewGameNonAd = now;

            }
        }
    }


    public static ProgressDialog createProgressDialog(Context mContext) {
        try {
            dialog = new ProgressDialog(mContext);
            dialog.show();
            dialog.setCancelable(false);
            dialog.setMessage("Loading...");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dialog;
    }


    /**
     * Fetch leaderboard daily data
     *
     * @param context
     * @param uid
     * @param isrefreshaction
     * @param mode
     * @param screenType
     */
    public static void fetchLeaderBoard(Context context, String uid, int isrefreshaction, String mode, int screenType) {
        String ntw = getNetworkType(context);
        ReadPref readPref = new ReadPref(context);
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(uid));
        map.put(PARAM_ISLOGGEDIN, String.valueOf(DEFAULT_ONE));
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());
        map.put(PARAM_MODE, mode);//Mode of the leaderboard to be displayed. Daily/Weekly/Monthly/Single Quiz (D/W/M/Q)
        map.put(PARAM_START_DATE, readPref.getDefaultDate());
        map.put(PARAM_END_DATE, readPref.getDefaultDate());
        map.put(PARAM_NETWORK, ntw);

        // APICalls.fetchLeaderBoard(context, map, isrefreshaction, mode);

        Intent intent = new Intent(context, Leaderboard_New.class);
        CommonUtility.showActivity(context, SCREEN_TYPE, screenType, true, intent, null);

    }

    /**
     * hits api call
     *
     * @param context
     * @param uid
     * @param screenType
     */
    public static void fetchArchive(Context context, String uid, int screenType) {
        String ntw = getNetworkType(context);
        if (GameArchive.progressLayout != null) {
            GameArchive.progressLayout.showProgress();
        }
        ReadPref readPref = new ReadPref(context);

        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(uid));
        map.put(PARAM_ISLOGGEDIN, String.valueOf(DEFAULT_ONE));
        map.put(PARAM_LOGIN_TYPE, TriviaConstants.DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());

        map.put(PARAM_FROM_INDEX, "1");
        map.put(PARAM_COUNT, ARCHIVE_LIST_COUNT);
        map.put(PARAM_NETWORK, ntw);

        APICalls.fetchGameArchive(context, map);


    }

    /**
     * Add post parameters for register and call api for data
     */
    public static void registerUser(Context context, HashMap<String, String> fbData, String regStatus, String isLoggedIn, String uid, int type) {
        String ntw = getNetworkType(context);
        HashMap<String, String> map = new HashMap<>();
        if (fbData.size() != 0) {
            map.putAll(fbData);
        }

        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_REG_STATUS, regStatus);
        map.put(PARAM_ISLOGGEDIN, isLoggedIn);
        map.put(PARAM_NETWORK, ntw);
        if (!uid.equals("0")) {
            map.put(PARAM_UID, uid);
        }

        APICalls.registerUser(context, map, type);

    }

    /**
     * Mark result notification as seen
     *
     * @param context
     * @param uid
     * @param qid
     */
    public static void notifyResult(Context context, String uid, String qid) {
        String ntw = getNetworkType(context);
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_UID, uid);
        map.put(PARAM_QUIZ_ID, qid);
        map.put(PARAM_NETWORK, ntw);
        APICalls.notifyResult(context, map);

    }


    /**
     * reference uids
     *
     * @return
     */
    public static Map<String, String> getLoggedInReferences() {
        Map<String, String> map = new HashMap<>();
        map.put("1001", "A_218325482282679");
        map.put("1002", "A_365748863369875");
        map.put("1003", "A_517164593862456");
        map.put("1004", "A_362190292030111");
        map.put("1005", "A_214189993431624");

        return map;


    }

    /**
     * print hashmap values sent in api
     *
     * @param map
     */
    public static void printHashmap(Map<String, String> map) {
        try {
            for (String name : map.keySet()) {

                String key = name.toString();
                String value = map.get(name).toString();
                System.out.println(key + " " + value);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get start and end date from calender
     *
     * @param enterWeek
     * @param enterYear
     */
    public static String getStartWeek(int enterWeek, int enterYear) {
        //enterWeek is week number
        //enterYear is year
        long start = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calendar.set(Calendar.YEAR, enterYear);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy"); // PST`
        Date startDate = calendar.getTime();
        String startDateInStr = formatter.format(startDate);
        System.out.println("...date..." + startDateInStr);

        //if the calender first day of week is sunday then add 1 day to it.
        if (calendar.getFirstDayOfWeek() == Calendar.SUNDAY) {
            start = (startDate.getTime() / 1000) + 86400;
        } else {
            start = startDate.getTime() / 1000;
        }
        // start = startDate.getTime() / 1000;


        // calendar.add(Calendar.DATE, 6);
      /*  Date enddate = calendar.getTime();
        String endDaString = formatter.format(enddate);
        System.out.println("...date..." + endDaString);*/

        return String.valueOf(start);
    }

    /**
     * Get start and end date from calender
     *
     * @param enterWeek
     * @param enterYear
     */
    public static String getEndOFWeek(int enterWeek, int enterYear) {
        //enterWeek is week number
        //enterYear is year
        long end = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.WEEK_OF_YEAR, enterWeek);
        calendar.set(Calendar.YEAR, enterYear);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy"); // PST`
     /*   Date startDate = calendar.getTime();
        String startDateInStr = formatter.format(startDate);
        System.out.println("...date..." + startDateInStr);*/

        calendar.add(Calendar.DATE, 6);
        Date enddate = calendar.getTime();
        String endDaString = formatter.format(enddate);
        System.out.println("...date..." + endDaString);
        if (calendar.getFirstDayOfWeek() == Calendar.SUNDAY) {
            end = enddate.getTime() / 1000 + 86400;
        } else {
            end = enddate.getTime() / 1000;
        }
        // end = enddate.getTime() / 1000;


        return String.valueOf(end);
    }


    /**
     * validation in edit text to check space null and enter
     *
     * @param text
     * @return
     */
    public static Boolean chkString(String text) {

        if (TextUtils.isEmpty(text) || text.length() == 0 || text.equals(" ")
                || text == null || text.equals("\n") || text.equals("null")) {

            return false;
        } else if (text.length() > 0 && text.startsWith(" ")) {
            return false;

        } else {
            return true;
        }

    }


    /**
     * Updates analytics GTM events
     *
     * @param eventName
     * @param eventAction
     * @param eventLabel
     */
    public static void updateAnalyticGtmEvent(final Context mContext, final String eventName, final String eventAction, final String eventLabel, String gaTag) {

       /* Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {*/
        pushCokeEvent(mContext, eventAction, eventName, "", eventName, "");
        Log.d("GTM_TRIVIA--", gaTag + "+" + eventName + "+" + eventAction + "+" + eventLabel);
        DataLayer mDataLayer = TagManager.getInstance(mContext).getDataLayer();
        mDataLayer.pushEvent(gaTag, DataLayer.mapOf("EventCategory", eventName, "EventAction", eventAction, "EventLabel", eventLabel));
        //  }
      /*  });
        thread.start();*/

    }

    public static void updateAnalytics(final Context mContext, final String analyticsText) {

       /* Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {*/
        Log.d("GTM_TRIVIA--", analyticsText);
        DataLayer mDataLayer = TagManager.getInstance(mContext).getDataLayer();
        mDataLayer.pushEvent("openScreen", DataLayer.mapOf("screenName", GA_PREFIX + FRONT_SLASH + analyticsText));
        //  }
      /*  });
        thread.start();*/

    }

    /**
     * Get current time stamp
     *
     * @return
     */
    public static String getCurrentTimeStamp() {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }


    public static void registerUserapi(final Context context, HashMap<String, String> map, String name, String userImage, String toiSsoId, String email, final int pageCalled, final String gameId, final String leaderboardMode) {
        final String ntw = getNetworkType(context);
        APIService apiService;
        Call<HomeItems> registrationItemsCall;
        ApiRetroFit apiRetroFit;
        Log.d("registerUser called", "-----------------");
        final SavePref savePref = new SavePref(context);
        final ReadPref readPref = new ReadPref(context);

        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        //Initialize map with default param
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        boolean callRegister = false;

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


        boolean toiIsLogin = false;
        String toiLoginId = "";
        if (CommonUtility.chkString(toiSsoId)) {
            toiIsLogin = true;
            toiLoginId = toiSsoId;

        }
        String triviaLoginId = readPref.getLoginId();
        if (!triviaLoginId.equals(toiLoginId)) {//if this case occurs that means user has logout from setting and again logged in from setting without comming back to dashboard page
            savePref.logoutClearData();
        }
        String triviaUid = readPref.getUID();
        String triviaRegStatus = readPref.getRegStatus();


        //Case 3  user is not there and we need to invoke register and save in user pref
        if (Integer.parseInt(triviaUid) == DEFAULT_ZERO) {

            if (toiIsLogin) {//case 3.a
                map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ONE));
                map.put(PARAM_LOGIN_ID, toiLoginId);
                map.put(PARAM_LOGIN_TYPE, "s");
                map.put(PARAM_UID, String.valueOf(DEFAULT_ZERO));
                map.put(PARAM_NAME, name);
                map.put(PARAM_EMAIL, CommonUtility.checkNull(email));
                map.put("profile_img", userImage);
                callRegister = true;
            } else {//case 3.bR
                map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ZERO));
                map.put(PARAM_EMAIL, CommonUtility.checkNull(email));
                callRegister = true;
            }

        } else { //Case 2 user is already there either logged in or anon browsing no need to update in user pref

            if (toiIsLogin && Integer.parseInt(triviaRegStatus) == DEFAULT_ZERO) {//case 2.a
                //call register API with user data - update user data
                map.put(PARAM_REG_STATUS, String.valueOf(DEFAULT_ONE));
                map.put(PARAM_LOGIN_ID, toiLoginId);
                map.put(PARAM_LOGIN_TYPE, "s");
                map.put(PARAM_UID, triviaUid);
                map.put(PARAM_NAME, name);
                map.put(PARAM_EMAIL, CommonUtility.checkNull(email));

                map.put("profile_img", userImage);
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
                map.put(PARAM_EMAIL, CommonUtility.checkNull(email));

                callRegister = true;
            }

        }

        Log.d("Call register--------", String.valueOf(callRegister));
        if (callRegister) {
            registrationItemsCall = apiService.registerUser(map);
            final String oldUId = map.get(PARAM_UID);//Attach parameter
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
                                                              if (game_count == 0) {
                                                                  savePref.isFirstTime(true);
                                                              } else {
                                                                  savePref.isFirstTime(false);
                                                              }
                                                              if (currentGameId == 0) {
                                                                  //no active game
                                                                  savePref.saveCurrentGameId(String.valueOf(currentGameId));


                                                              } else {
                                                                  //current game is active

                                                                  savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                                  savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference
                                                              }

                                                              if (pageCalled == LEADERBOARD) {
                                                                  if (Leaderboard_New.PlaceholderFragment.lock_leaderboard != null) {
                                                                      Leaderboard_New.PlaceholderFragment.lock_leaderboard.setVisibility(View.GONE);
                                                                  }
                                                                  if (WeekLeaderboard.lock_leaderboard != null) {
                                                                      WeekLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                                                                  }
                                                                  if (MonthlyLeaderboard.lock_leaderboard != null) {
                                                                      MonthlyLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                                                                  }
                                                                  //if oldUid i.e anonymous user uid and uid i.e logged in user uid is not same - call update leaderboard
                                                                  //with new user uid data
                                                                  if (!oldUId.equals(uid)) {

                                                                      if (Leaderboard_New.PlaceholderFragment.progressLayout != null) {
                                                                          Leaderboard_New.PlaceholderFragment.progressLayout.showProgress();
                                                                      }

                                                                      HashMap<String, String> map = new HashMap<>();
                                                                      map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
                                                                      map.put(PARAM_UQID, CommonUtility.getIMEI(context));
                                                                      map.put(PARAM_UID, String.valueOf(uid));
                                                                      map.put(PARAM_ISLOGGEDIN, String.valueOf(DEFAULT_ONE));
                                                                      map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
                                                                      map.put(PARAM_LOGIN_ID, readPref.getLoginId());
                                                                      map.put(PARAM_MODE, MODE_DAILY);
                                                                      //Mode of the leaderboard to be displayed. Daily/Weekly/Monthly/Single Quiz (D/W/M/Q)
                                                                      map.put(PARAM_START_DATE, readPref.getDefaultDate());
                                                                      map.put(PARAM_END_DATE, readPref.getDefaultDate());
                                                                      map.put(PARAM_NETWORK, ntw);
                                                                      //   if (leaderboardMode.equals(MODE_DAILY)) {

                                                                      APICalls.fetchLeaderBoard(context, map, 1, MODE_DAILY);
                                                                      //  } else if (leaderboardMode.equals(MODE_WEEKLY)) {
                                                                      // APICalls.fetchLeaderBoardWeekly(context, map, 1, MODE_WEEKLY);
                                                                      //    } else if (leaderboardMode.equals(MODE_MONTHLY)) {
                                                                      // APICalls.fetchLeaderBoardMonthly(context, map, 1, MODE_MONTHLY);
                                                                      //   } else {
                                                                      // APICalls.fetchLeaderBoard(context, map, 1, leaderboardMode);
                                                                      //    }


                                                                      WeekLeaderboard.fetchLeaderBoard(context, String.valueOf(uid), 1, MODE_WEEKLY);

                                                                      MonthlyLeaderboard.fetchLeaderBoard(context, String.valueOf(uid), 1, MODE_MONTHLY);
                                                                      //CommonUtility.fetchLeaderBoard(context,String.valueOf(uid),1,MODE_DAILY,TriviaConstants.LEADERBOARD);
                                                                  }
                                                              } else if (pageCalled == GAME_END) {
                                                                  if (oldUId.equals(uid)) {
                                                                      if (GameEnd_New.non_logged_in != null) {
                                                                          GameEnd_New.non_logged_in.setVisibility(View.GONE);
                                                                      }
                                                                      if (GameEnd_New.logged_in != null) {
                                                                          GameEnd_New.logged_in.setVisibility(View.VISIBLE);
                                                                      }
                                                                      if (GameEnd_New.user_name != null) {
                                                                          GameEnd_New.user_name.setText(CommonUtility.checkName(readPref.getUserName()));
                                                                      }
                                                                      if (GameEnd_New.profile_img != null) {
                                                                          GameEnd_New.openImage(GameEnd_New.profile_img, readPref.getUserImage());
                                                                      }
                                                                      if (GameEnd_New.game_end_result != null) {
                                                                          GameEnd_New.game_end_result.setBackground(context.getResources().getDrawable(R.drawable.bg));
                                                                      }
                                                                  } else {
                                                                      if (ResultScreen.progressLayout != null) {
                                                                          ResultScreen.progressLayout.showProgress();
                                                                      }
                                                                      CommonUtility.fetchResult(context, String.valueOf(uid), gameId, TriviaConstants.GAME_END);
                                                                  }

                                                              }

                                                              /*//downloading process for sponsor and fireworks
                                                              try {
                                                                  //sponsor image
                                                                  if (!sponsor.getName().toLowerCase().equals(TOI_SPONSOR_NAME)) {
                                                                      File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                              .getPath()
                                                                              + "/Android/data/" + context.getPackageName() + "/files/Trivia/Sponsor/"
                                                                              + sponsor.getName() + ".jpg");


                                                                      if (!imgFile.exists()) {
                                                                          downloadSponsorPicture(sponsor.getImg_url(), sponsor.getName(), 0);
                                                                      }
                                                                  }
                                                              } catch (Exception e) {
                                                                  e.printStackTrace();
                                                              }

                                                              try {//for fireworks
                                                                  if (CommonUtility.chkString(data_fire)) {
                                                                      File imgFile = new File(Environment.getExternalStorageDirectory()
                                                                              .getPath()
                                                                              + "/Android/data/" + context.getPackageName() + "/files/Trivia/Others/"
                                                                              + FIREWORKS_IMAGE + ".gif");
                                                                      if (!imgFile.exists()) {
                                                                          downloadSponsorPicture(data_fire, FIREWORKS_IMAGE, 1);
                                                                      }
                                                                  }
                                                              } catch (Exception e) {
                                                                  e.printStackTrace();
                                                              }*/
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

        } else {

            if (pageCalled == LEADERBOARD) {
                if (Leaderboard_New.PlaceholderFragment.lock_leaderboard != null) {
                    Leaderboard_New.PlaceholderFragment.lock_leaderboard.setVisibility(View.GONE);
                }
                if (WeekLeaderboard.lock_leaderboard != null) {
                    WeekLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                }
                if (MonthlyLeaderboard.lock_leaderboard != null) {
                    MonthlyLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                }
            } else if (pageCalled == GAME_END) {
                if (GameEnd_New.non_logged_in != null) {
                    GameEnd_New.non_logged_in.setVisibility(View.GONE);
                }
                if (GameEnd_New.logged_in != null) {
                    GameEnd_New.logged_in.setVisibility(View.VISIBLE);
                }
                if (GameEnd_New.user_name != null) {
                    GameEnd_New.user_name.setText(CommonUtility.checkName(readPref.getUserName()));
                }
                if (GameEnd_New.profile_img != null) {
                    GameEnd_New.openImage(GameEnd_New.profile_img, readPref.getUserImage());
                }
                if (GameEnd_New.game_end_result != null) {
                    GameEnd_New.game_end_result.setBackground(context.getResources().getDrawable(R.drawable.bg));
                }

            }

        }
    }

    /**
     * convert back string to hashmap
     *
     * @param mapString
     * @return
     */
    public static HashMap<String, String> loadAnswerMap(String mapString) {
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

    /**
     * saves the answers post parameter map into SharedPrefrence.
     *
     * @param context
     * @param inputMap
     */
    public static void saveAnswerMap(AppCompatActivity context, Map<String, String> inputMap) {
        SavePref savePref = new SavePref(context);
        JSONObject jsonObject = new JSONObject(inputMap);
        String jsonString = jsonObject.toString();
        savePref.saveUserAnswer(jsonString);

    }

    /**
     * Makes the post parameter map for submit answers
     *
     * @param context
     * @param data
     * @return
     */
    public static HashMap<String, String> makeAnswerMap(Context context, AnswersPojo data) {
        ReadPref readPref = new ReadPref(context);

        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(StartQuiz.activity));
        map.put(PARAM_UID, readPref.getUID()); // A FOR ANDROID
        map.put(PARAM_ISLOGGEDIN, String.valueOf(DEFAULT_ONE));
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_GAME_ID, String.valueOf(data.getGameId())); // A FOR ANDROID
        map.put(PARAM_SUBMIT_TIME, String.valueOf((System.currentTimeMillis() / 1000)));
        map.put(PARAM_LOGIN_ID, String.valueOf(readPref.getLoginId()));

        JSONArray answers = new JSONArray();
        if (data.getAnswers_list() != null) {
            for (int i = 0; i < data.getAnswers_list().size(); i++) {
                JSONObject jobj = new JSONObject();
                AnswersPojo.Answers ans = data.getAnswers_list().get(i);
                try {
                    jobj.put(PARAM_QUES_ID, ans.getQuesId());
                    jobj.put(PARAM_TIME_TAKEN, ans.getTimeTaken());
                    jobj.put(PARAM_OPTION_ID, ans.getUserOptId());
                    jobj.put(PARAM_PRESENTED_TIME, ans.getPresentedTime());
                    jobj.put(PARAM_USER_OPT_SEQ, ans.getUserOptSeq());
                    jobj.put(PARAM_QUES_STATE, ans.getqState());
                    jobj.put(PARAM_QUES_SEQ, ans.getUserQuesSeq());
                    answers.put(i, jobj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        map.put(PARAM_ANSWERS, String.valueOf(answers));
        Log.d("answers--", String.valueOf(answers));
        return map;
    }


    /**
     * replace space in string with underscore
     *
     * @param string
     * @return
     */
    public static String replaceSpace(String string) {
        String new_string = "";
        try {
            new_string = string.replaceAll(" ", "_");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_string;
    }

/*
    public static void initGoogleTagManager(Context context) {

        TagManager tagManager = TagManager.getInstance(context);
        tagManager.setVerboseLoggingEnabled(true);
//            PendingResult<ContainerHolder> pending = tagManager.loadContainerPreferNonDefault("GTM-MJKMSJ", R.raw.gtm_mjkmsj);
        PendingResult<ContainerHolder> pending = tagManager.loadContainerPreferNonDefault(context.getResources().getString(R.string.GTM_CONTAINER_ID), R.raw.gtm_kflxmm);
        pending.setResultCallback(new ResultCallback<ContainerHolder>() {
            @Override
            public void onResult(ContainerHolder containerHolder) {
                ContainerHolderSingleton.setContainerHolder(containerHolder);

                Log.d("containerLoaded", "true");

            }
        }, 2, TimeUnit.SECONDS);

    }
*/


    public static void pushCokeEvent(Context context, String eventName, String section, String webUrl, String navigationPath, String feedUrl) {

        try {
            CokeManager cokeManager = CokeManager.getInstance();
            CokeEventData.CokeEventDataBuilder builder = (CokeEventData.CokeEventDataBuilder) cokeManager.newEventBuilder();
            builder.setMevent(eventName)
                    .setAcategory(section)
                    .setAUrl(webUrl)
                    .setScreen(navigationPath)
                    .setFUrl(feedUrl);
            cokeManager.trackEvent(builder);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for device have nogout Os
     *
     * @return true if yes
     */
    public static boolean doNogoutTask() {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= 24) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Round off and show the ranks of user more than 10000
     *
     * @param user_rank
     * @return
     */
    public static String roundedRankText(String user_rank) {
        String rank = "-";
        if (!user_rank.equals(rank)) {
            int mUserRank = Integer.parseInt(user_rank);
            if (Integer.parseInt(user_rank) > 9999) {

                if (mUserRank >= 10000 && mUserRank <= 99999) {

                    int newRank = mUserRank / 1000;
                    double rounded = (double) Math.round(newRank * 10) / 10;
                    rank = String.valueOf((int) rounded) + "K";
                } else if (mUserRank >= 100000 && mUserRank <= 9999999) {
                    int newRank = mUserRank / 100000;
                    double rounded = (double) Math.round(newRank * 10) / 10;
                    rank = String.valueOf((int) rounded) + "L";

                } else if (mUserRank > 9999999) {
                    int newRank = mUserRank / 10000000;
                    double rounded = (double) Math.round(newRank * 10) / 10;
                    rank = String.valueOf((int) rounded) + "Cr";

                }
                Log.d("K category new rank--", rank);
                return rank;
            } else {
                rank = String.valueOf(user_rank);
            }
        } else {
            // if(user_rank!=0){
            rank = String.valueOf(user_rank);
            // }

        }

        return rank;
    }

    /**
     * display respective trophy assigned to user for particular rank
     *
     * @param trophy_imageview
     * @param is_won
     * @param loggedUserUid
     * @param mDatasetUid
     * @param user_rank
     */
    public static void showTrophyImage(ImageView trophy_imageview, int is_won, int loggedUserUid, int mDatasetUid, int user_rank) {
        if (loggedUserUid != mDatasetUid) {
            //if loggedUserUid is not qual to row uid then show normal trophy icons
            if (is_won == 1) {
                switch (user_rank) {
                    case 1:

                        trophy_imageview.setImageResource(R.drawable.trophy_gold);
                        break;
                    case 2:
                        trophy_imageview.setImageResource(R.drawable.silver_trophy);

                        break;
                    case 3:
                        trophy_imageview.setImageResource(R.drawable.bronze_trophy);

                        break;
                    case 4:
                        trophy_imageview.setImageResource(R.drawable.trophy_gen);

                        break;
                    case 5:
                        trophy_imageview.setImageResource(R.drawable.trophy_gen);

                        break;
                    case 6:
                        trophy_imageview.setImageResource(R.drawable.trophy_gen);

                        break;
                }
            } else {
                trophy_imageview.setImageResource(R.drawable.trophy_trans);

            }
        } else {
            //logged user leaderboard row

            if (is_won == 1) {
                switch (user_rank) {
                    case 1:

                        trophy_imageview.setImageResource(R.drawable.trophy_gold_black);
                        break;
                    case 2:
                        trophy_imageview.setImageResource(R.drawable.silver_trophy_black);

                        break;
                    case 3:
                        trophy_imageview.setImageResource(R.drawable.bronze_trophy_black);

                        break;
                    case 4:
                        trophy_imageview.setImageResource(R.drawable.black_gen_trophy);

                        break;
                    case 5:
                        trophy_imageview.setImageResource(R.drawable.black_gen_trophy);

                        break;
                    case 6:
                        trophy_imageview.setImageResource(R.drawable.black_gen_trophy);

                        break;
                }
            } else {
                trophy_imageview.setImageResource(R.drawable.trophy_trans);

            }

        }
    }

    public static String getNetworkType(Context context) {
        int type = 0;
        if (context != null) {
            TelephonyManager mTelephonyManager = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            int networkType = mTelephonyManager.getNetworkType();

            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    return "2g";

                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    /**
                     From this link https://goo.gl/R2HOjR ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                     EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.

                     Where CDMA2000 https://goo.gl/1y10WI .CDMA2000 is a family of 3G[1] mobile technology standards for sending voice,
                     data, and signaling data between mobile phones and cell sites.
                     */
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                case TelephonyManager.NETWORK_TYPE_EHRPD:
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    //Log.d("Type", "3g");
                    //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                    //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                    //Some cases are added after  testing(real) in device with 3g enable data
                    //and speed also matters to decide 3g network type
                    //http://goo.gl/bhtVT
                    return "3g";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    //No specification for the 4g but from wiki
                    //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                    //https://goo.gl/9t7yrR
                    return "4g";
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                    return "wifi";
                default:
                    return "Notfound";
            }
        }
        return "Notfound";
    }

}



