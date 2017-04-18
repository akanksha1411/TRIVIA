package toi.com.trivia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import toi.com.trivia.R;
import toi.com.trivia.fragments.PrizesDaily;
import toi.com.trivia.fragments.PrizesMonth;
import toi.com.trivia.fragments.PrizesWeek;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

//import toi.com.trivia.fragments.PrizesDaily;

public class PrizesActivity extends AppCompatActivity implements TriviaConstants, PrizesDaily.OnFragmentInteractionListener, PrizesWeek.OnFragmentInteractionListener, PrizesMonth.OnFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static PrizesDaily prizesDaily = new PrizesDaily();
    public static PrizesWeek prizesWeek = new PrizesWeek();
    public static PrizesMonth prizesMonth = new PrizesMonth();
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    public static AppCompatActivity activity;
    static ReadPref readPref;


    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!CommonUtility.haveNetworkConnection(context)) {
                Snackbar.make(getWindow().getDecorView(), TriviaConstants.No_INTERNET, Snackbar.LENGTH_SHORT).show();

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prizes);
        activity = this;
        //register reciever for network connection change
        initNetworkStateReciever();
        //get data from bundle for prizes
        //Bundle bundle = getIntent().getExtras();
        //prizesItems = (PrizesItems) bundle.getSerializable("data");

        readPref = new ReadPref(getApplicationContext());
        final String sponsorName = readPref.getSponsorName();
        final String currrentGameId = readPref.getCurrentGameId();
        initToolbar();
        //set background
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.ANSWERS_ACTIVITY, sponsorName);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Prizes", "Daily", TriviaConstants.CLICK, "Trivia_And_Prizes");
                } else if (position == 1) {
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Prizes", "Monthly", TriviaConstants.CLICK, "Trivia_And_Prizes");
                } else if (position == 2) {
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Prizes", "Weekly", TriviaConstants.CLICK, "Trivia_And_Prizes");
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

       /* tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Prizes", "Daily", TriviaConstants.CLICK);
                } else if (position == 1) {
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Prizes", "Monthly", TriviaConstants.CLICK);
                } else if (position == 2) {
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Prizes", "Weekly", TriviaConstants.CLICK);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
*/
        //send pageview
        CommonUtility.updateAnalytics(getApplicationContext(), "Prizes");
    }

    /**
     * init receiver for network connction change
     */
    protected void initNetworkStateReciever() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(networkStateReceiver, filter);

    }

    public void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.answers));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arow);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(networkStateReceiver);
        CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Prizes", "Back Press", CLICK, "Trivia_And_ Exit_Prizes");
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            if (position == 0) {
                return PrizesDaily.newInstance(position);
                // return new DailyFragment();
            } else if (position == 1) {
                //return WeekPrizesFragment.newInstance(position);
                return PrizesWeek.newInstance(position);
            } else if (position == 2) {
                //return MonthlyPrizesFragment.newInstance(position);
                return PrizesMonth.newInstance(position);
            } else {

                return PrizesDaily.newInstance(position);

            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.daily);
                case 1:
                    return getResources().getString(R.string.weekly);
                case 2:
                    return getResources().getString(R.string.monthly);
            }
            return null;
        }
    }


}
