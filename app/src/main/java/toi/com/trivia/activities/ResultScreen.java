package toi.com.trivia.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.github.androidprogresslayout.ProgressLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import toi.com.trivia.R;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.fragments.GameEnd_New;
import toi.com.trivia.fragments.ResultAnnounced;
import toi.com.trivia.model.ResultItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;


public class ResultScreen extends AppCompatActivity implements TriviaConstants, ResultAnnounced.OnFragmentInteractionListener {
    // TODO: Rename parameter arguments, choose names that match


    public static AppCompatActivity context;
    static ReadPref readPref;
    int screenType, time;
    Bundle bundle = new Bundle();
    String UID;
    public static ProgressLayout progressLayout;
    public static PublisherInterstitialAd mInterstitialAd;
    SavePref savePref;

    @Override
    protected void onStart() {
        super.onStart();
        savePref = new SavePref(getApplicationContext());
        bundle = getIntent().getExtras();
        screenType = bundle.getInt(SCREEN_TYPE);

        if (screenType == GAME_ARCHIVE) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Result Announced", "Game Archive", CLICK, "Trivia_And_Entry_Result_Announced ");
        } else if (screenType == GAME_END) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Result Announced", "Game End Page", AUTOLOAD, "Trivia_And_Entry_Result_Announced ");
        } else if (screenType == START_QUIZ_ACTIVITY) {

            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Result Announced", "Answer Submit", AUTOLOAD, "Trivia_And_Entry_Result_Announced ");
        } else if (screenType == DASHBOARD_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Result Announced", "Dashboard", CLICK, "Trivia_And_Entry_Result_Announced ");
        } else if (screenType == NOTIFICATION) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Result Announced", "Notification", CLICK, "Trivia_And_Entry_Result_Announced ");
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readPref = new ReadPref(getApplicationContext());
        UID = readPref.getUID();
        String sponsorName = readPref.getSponsorName();
        context = this;
        setContentView(R.layout.fragment_result_screen);

        progressLayout = (ProgressLayout) findViewById(R.id.progress_layout);
        progressLayout.showProgress();

        PublisherAdView publisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);

        CommonUtility.initADs(context, publisherAdView);

        mInterstitialAd = new PublisherInterstitialAd(context);
        final ReadPref readPref = new ReadPref(context);
        String adUnit = readPref.getResultOpen();
        if (CommonUtility.chkString(adUnit)) {
            //Interstitial ads integration
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
                    Log.d("on ad closed"," from result page");
                }


                @Override
                public void onAdFailedToLoad(int i) {
                    Log.d("FailedAdLoad", "Ad failed to load");
                    super.onAdFailedToLoad(i);

                }
            });
            CommonUtility.requestNewInterstitial(mInterstitialAd, context);
        }
        initToolbar();

    }


    /**
     * Adds 1st fragment to activity without being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     * @param historyFlag
     */
    public static void replace1stFragment(Fragment fragment, String bundleParameterName, int bundleValue,
                                          boolean isUsedBundle, int historyFlag) {

        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        try {
            FragmentTransaction transaction =
                    context.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            //transaction.addToBackStack(null);
            //transaction.commit();
            transaction.commitAllowingStateLoss();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public static void openResultFragment() {

        ResultItems items = APICalls.getResultItems();
        int is_game_live = items.getIs_game_live();
        int is_played_live = items.getIs_played_live();
        /**
         * is_played_live && is_game_live
         * 1. =1 , Game end page
         * 2. =0 , Result page - message screen
         * 3. =1 && =0 , Result page - won screen
         * 4. =0 && =0 , No scene
         */
        if (Integer.parseInt(readPref.getIsLoggedIn()) == DEFAULT_ONE) {
            //if played new game - game end page occurs else result page opens
            if (is_played_live == DEFAULT_ONE && is_game_live == DEFAULT_ONE) {
                //1.
                CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Result Game End", "Quiz End", TriviaConstants.AUTOLOAD, "Trivia_And_Result_Game_End");
                replace1stFragment(new GameEnd_New(items), SCREEN_TYPE, RESULT_SCREEN, true, 1);
            } else if (is_played_live == DEFAULT_ZERO && is_game_live == DEFAULT_ZERO) {
                replace1stFragment(new ResultAnnounced(items), SCREEN_TYPE, RESULT_SCREEN, true, 0);
            } else if (is_played_live == DEFAULT_ONE && is_game_live == DEFAULT_ZERO) {
                replace1stFragment(new ResultAnnounced(items), SCREEN_TYPE, RESULT_SCREEN, true, 0);
            }

        } else {
            CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Result Game End", "Quiz End", TriviaConstants.AUTOLOAD, "Trivia_And_Result_Game_End");
            replace1stFragment(new GameEnd_New(items), SCREEN_TYPE, RESULT_SCREEN, true, 1);
        }


        /*if (Integer.parseInt(readPref.getIsLoggedIn()) == DEFAULT_ONE) {
            if (data != null) {
                if (Long.valueOf(data.getResultTime()) <= System.currentTimeMillis() / 1000) {
                    replace1stFragment(new ResultAnnounced(), SCREEN_TYPE, RESULT_SCREEN, true);
                } else {
                    replace1stFragment(new GameEnd_New(data), SCREEN_TYPE, RESULT_SCREEN, true);
                }
            } else {
                replace1stFragment(new ResultAnnounced(), SCREEN_TYPE, RESULT_SCREEN, true);
            }
        } else {

            replace1stFragment(new GameEnd_New(data), SCREEN_TYPE, RESULT_SCREEN, true);

        }*/
    }


    private void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //GA entry ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Result Announced", BACK_ARROW, TriviaConstants.CLICK, "Trivia_And_Exit_Result_Announced");
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arow);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Log.d("activity result called", "leaderboard");

        //if (requestCode == RESPONSE_LOGIN_CODE) {
        if (resultCode == LOGIN_REQUEST_CODE) {
            if (data != null) {

                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    String name = bundle.getString("name");
                    String user_image = bundle.getString("user_image");
                    String ssoid = bundle.getString("ssoid");

                    Log.d("name", name);
                    CommonUtility.registerUserapi(getApplicationContext(), new HashMap<String, String>(), name, user_image, ssoid, GAME_END);
                }


            }
        }

        //}

    }*/

    @Override
    public void onBackPressed() {
        savePref.saveIsGameKilled(true);
        if (StartQuiz.activity != null) {
            StartQuiz.activity.finish();
        }
        if (TriviaLoader.activity != null) {
            TriviaLoader.activity.finish();
        }
        finish();
        CommonUtility.fetchArchive(getApplicationContext(), String.valueOf(readPref.getUID()), TriviaConstants.GAME_END);
        //GA EXIT ANALYTICS
        CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Result Announced", DEVICE_BACK, TriviaConstants.CLICK, "Trivia_And_Exit_Result_Announced");

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
