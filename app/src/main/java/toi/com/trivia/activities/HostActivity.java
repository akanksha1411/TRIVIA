package toi.com.trivia.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.HashMap;

import toi.com.trivia.R;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.fragments.AboutUs;
import toi.com.trivia.fragments.FAQFragment;
import toi.com.trivia.fragments.GameEnd_New;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;
import toi.com.trivia.utility.ui.TriviaLoginView;

/**
 * This will be the main activity that will open every fragment for trivia registration.
 */
public class HostActivity extends AppCompatActivity implements View.OnClickListener, TriviaConstants, AboutUs.OnFragmentInteractionListener {
    private int mUID;
    ReadPref readPref;
    int fragNo = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        readPref = new ReadPref(getApplicationContext());
        mUID = Integer.parseInt(readPref.getUID());
        final String sponsorName = readPref.getSponsorName();
        initToolbar("");
        final PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mPublisherAdView.loadAd(adRequest);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            fragNo = bundle.getInt(TriviaConstants.SCREEN_TYPE);
        }

        //set background for the screen
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.HOST_ACTIVITY, sponsorName);

        initNotification();

        if (savedInstanceState == null) {

            if (fragNo == TriviaConstants.DASHBOARD_SCREEN) {
                setContentView(new TriviaLoginView(getApplicationContext()));
            } else if (fragNo == TriviaConstants.FAQ_SCREEN) {
                initToolbar(getResources().getString(R.string.faq));
                APICalls.fetchFaqQuestions(getApplicationContext(),new HashMap<String, String>());
                replace1stFragment(new FAQFragment(), "", 0, false);

            } else if (fragNo == TriviaConstants.ABOUT_US_SCREEN) {
                initToolbar(getResources().getString(R.string.about_us));

                replace1stFragment(new AboutUs(TriviaConstants.ABOUT_US_SCREEN), "", 0, false);

            } else if (fragNo == TriviaConstants.POLICY_SCREEN) {
                initToolbar(getResources().getString(R.string.privacy_policy));

                replace1stFragment(new AboutUs(TriviaConstants.POLICY_SCREEN), "", 0, false);

            } else if (fragNo == TriviaConstants.GAME_END) {
               // initToolbar(getResources().getString(R.string.result));
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);
                replace1stFragment(new GameEnd_New(), "", 0, false);

            }
        }
    }


    public void initToolbar(String title) {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(title);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arow);
    }


    /**
     * This builds the notifcation section on top of the screen
     * and text are set as per the notification and type set
     */
    public void initNotification() {

        ImageView hide_notification = (ImageView) findViewById(R.id.hide_notification);
        TextView notification_title = (TextView) findViewById(R.id.notification_title);
        TextView notification_action = (TextView) findViewById(R.id.notification_action);
        //todo set text to textviews wrt notification type

        hide_notification.setOnClickListener(this);


    }


    /**
     * Adds 1st fragment to activity without being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     */
    public void replace1stFragment(Fragment fragment, String bundleParameterName, int bundleValue,
                                   boolean isUsedBundle) {
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        try {
            FragmentTransaction transaction =
                    HostActivity.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            //transaction.addToBackStack(null);
            transaction.commit();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.hide_notification) {
            hideNotification();

        }
    }

    /**
     * Adds 1st fragment to activity without being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     */
    public static void replaceFragmentWithoutHistory(Fragment fragment, String bundleParameterName, int bundleValue,
                                                     boolean isUsedBundle, AppCompatActivity context) {
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        try {
            FragmentTransaction transaction =
                    context.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            transaction.replace(R.id.quiz_container, fragment);
            transaction.commitAllowingStateLoss();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void hideNotification() {

        View notification_layout = (View) findViewById(R.id.notification_layout);
        notification_layout.setVisibility(View.GONE);
    }




}
