package toi.com.trivia.activities;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.androidprogresslayout.ProgressLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import toi.com.trivia.R;
import toi.com.trivia.adapters.LeaderboardAdapter;
import toi.com.trivia.adapters.MonthAdapter;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.fragments.MonthlyLeaderboard;
import toi.com.trivia.fragments.WeekLeaderboard;
import toi.com.trivia.model.LeaderboardItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;
import toi.com.trivia.utility.ui.NonScrollListView;
import toi.com.trivia.utility.ui.WeekCalenderView;

public class Leaderboard_New extends AppCompatActivity implements WeekCalenderView.OnFragmentInteractionListener, View.OnClickListener, TriviaConstants {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    public static LeaderboardItems items;
    public static ViewPager mViewPager;
    CalendarView week_calender;
    public static View week_view, month_view;
    public static ListView months_list;
    int UID;
    public static String[] months_new = new String[12];
    public static AppCompatActivity activity;
    public static RelativeLayout prizes_view;
    public static FrameLayout img;
    public static ImageView close_prize_view, winner_prize_img, winner_prize_rank;
    public static TextView winner_name, winner_prize_name;
    public static String mDailyStartDate, mWeekStartDate, mMonthStartDate;/*= String.valueOf(System.currentTimeMillis() / 1000);*/
    public static String mDailyPrevious, mWeekPrevious, mMonthPrevious;/*= String.valueOf(System.currentTimeMillis() / 1000);*/
    public static String mMode = MODE_DAILY;
    public static String selectedQuizId = "0", default_date;
    public static Date monthSelectedValue;
    public static TextView selected_week, selected_month;
    static ReadPref readPref;
    public static int selectedMonthPos = 0;
    public static MonthAdapter monthAdapter;
    SavePref savePref;
    int weekNo = 0, weekYear = 0, mSelectedWeekYear, mSelectedWeekMonth, mSelectedWeekDay, mSelectedWeek;
    String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!CommonUtility.haveNetworkConnection(context)) {
                Snackbar.make(getWindow().getDecorView(), TriviaConstants.No_INTERNET, Snackbar.LENGTH_SHORT).show();

            }

        }
    };


    @Override
    protected void onStart() {
        super.onStart();

        Bundle b = getIntent().getExtras();
        int screenType = b.getInt(SCREEN_TYPE);

        if (screenType == DASHBOARD_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Leaderboard", "Dashboard", CLICK, "Trivia_And_Entry_Leaderboard");
        } else if (screenType == NOTIFICATION) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Leaderboard", "Notification", CLICK, "Trivia_And_Entry_Leaderboard");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        readPref = new ReadPref(getApplicationContext());
        savePref = new SavePref(getApplicationContext());
        UID = Integer.parseInt(readPref.getUID());
        default_date = readPref.getDefaultDate();
        mDailyStartDate = default_date;
        mWeekStartDate = default_date;
        mMonthStartDate = default_date;
        mDailyPrevious = default_date;
        mWeekPrevious = default_date;
        mMonthPrevious = default_date;
        final String sponsorName = readPref.getSponsorName();
        //initialise toolbar Ui
        initToolbar();
        activity = this;
        //getting data from Bundle
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            items = (LeaderboardItems) bundle.getSerializable("data");
        } else {
            items = new LeaderboardItems();
        }
        //register reciever for network connection change
        initNetworkStateReciever();
        //set background
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.LEADERBOARD, sponsorName);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //set the Parent Ui for Leaderboard
        initParentView();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                               @Override
                                               public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                                               }

                                               @Override
                                               public void onPageSelected(int position) {
                                                   if (position == 0) {

                                                       //GA ANALYTICS
                                                       CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Leaderboard", "Daily", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
                                                   } else if (position == 1) {

                                                       //GA ANALYTICS
                                                       CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Leaderboard", "Monthly", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
                                                   } else if (position == 2) {
                                                       //GA ANALYTICS


                                                       CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + "Leaderboard", "Weekly", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
                                                   }

                                               }

                                               @Override
                                               public void onPageScrollStateChanged(int state) {

                                               }
                                           }

        );

        //send pageview
        CommonUtility.updateAnalytics(getApplicationContext(), "Leaderboard");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.leaderboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.open_prizes) {
            //fetch prizes for current game and open prize page
            CommonUtility.fetchPrizes(getApplicationContext(), readPref.getCurrentGameId(), TriviaConstants.LEADERBOARD);
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Prizes", "Leaderboard", TriviaConstants.CLICK, "Trivia_And_Entry_Leaderboard");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkStateReceiver);
        if (null != GameArchive.activity) {
            GameArchive.activity.finish();
        }
        if (null != ResultScreen.context) {
            ResultScreen.context.finish();
        }
    }

    /**
     * Set the view for Leaderboard
     */
    public void initParentView() {

        week_view = (View) findViewById(R.id.week_view);
        month_view = (View) findViewById(R.id.month_view);
        week_calender = (CalendarView) findViewById(R.id.week_calender);

        TextView cancel_week = (TextView) findViewById(R.id.cancel_week);
        TextView cancel_month = (TextView) findViewById(R.id.cancel_month);
        TextView ok_month = (TextView) findViewById(R.id.ok_month);
        TextView ok_week = (TextView) findViewById(R.id.ok_week);
        TextView selected_month_year = (TextView) findViewById(R.id.selected_month_year);
        TextView selected_week_year = (TextView) findViewById(R.id.selected_week_year);
        selected_month = (TextView) findViewById(R.id.selected_month);
        selected_week = (TextView) findViewById(R.id.selected_week);
        prizes_view = (RelativeLayout) findViewById(R.id.prizes_view);
        close_prize_view = (ImageView) findViewById(R.id.close_prize_view);
        winner_prize_img = (ImageView) findViewById(R.id.winner_prize_img);
        winner_name = (TextView) findViewById(R.id.winner_name);
        winner_prize_name = (TextView) findViewById(R.id.winner_prize_name);
        winner_prize_rank = (ImageView) findViewById(R.id.winner_prize_rank);
        img = (FrameLayout) findViewById(R.id.img);

        close_prize_view.setOnClickListener(this);
        cancel_month.setOnClickListener(this);
        cancel_week.setOnClickListener(this);
        ok_month.setOnClickListener(this);
        ok_week.setOnClickListener(this);

        Calendar cal = Calendar.getInstance();

        cal.setTimeInMillis(Long.valueOf(Leaderboard_New.mWeekStartDate) * 1000);
        int year = cal.get(Calendar.YEAR);
        int MONTH = cal.get(Calendar.MONTH);
        int DAY = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(year, MONTH, DAY);
        int week_no = cal.get(Calendar.WEEK_OF_YEAR);
        String mStart = CommonUtility.getStartWeek(week_no, year);
        String mEnd = CommonUtility.getEndOFWeek(week_no, year);

        //selected week to be shown inside the popup selector
        selected_week.setText(CommonUtility.formatDate(Long.parseLong(mStart)) + "-" + CommonUtility.formatDate(Long.parseLong(mEnd)));
        week_calender.setMaxDate(System.currentTimeMillis() + 3600000);
        week_calender.setMinDate(Long.valueOf("1470009600000"));//min date set to 1st august 2016
        if (cal.getFirstDayOfWeek() == Calendar.SUNDAY) {//if the default first day of calender is sunday then change it as Monday
            week_calender.setFirstDayOfWeek(Calendar.MONDAY);
        }
        week_calender.setDate(Long.parseLong(Leaderboard_New.mDailyStartDate) * 1000);
        week_calender.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year,
                                            int monthOfYear, int dayOfMonth) {

                week_calender.setSelectedDateVerticalBar(R.color.yellow);

                Calendar cal = Calendar.getInstance();
                cal.set(year, monthOfYear, dayOfMonth);
                mSelectedWeekYear = year;
                mSelectedWeekMonth = monthOfYear;
                mSelectedWeekDay = cal.get(Calendar.DATE);
                weekNo = cal.get(Calendar.WEEK_OF_YEAR);
                weekYear = cal.get(Calendar.YEAR);
                mWeekStartDate = CommonUtility.getStartWeek(weekNo, year);
                //  mWeekStartDate = CommonUtility.getEndOFWeek(weekNo, year);
             /*   Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                mSelectedStartDate = String.valueOf(date.getTime() / 1000);
                mSelectedEndDate = String.valueOf(date.getTime() / 1000);
                weekYear = cal.get(Calendar.YEAR);*/


            }

        });
        months_list = (ListView) findViewById(R.id.months_list);
        final int selectedMonth = cal.get(Calendar.MONTH);

        for (int i = 0; i < 12; i++) {
            Calendar cal_temp = Calendar.getInstance();
            cal_temp.add(Calendar.MONTH, -i);
            final int month = cal_temp.get(Calendar.MONTH);
            final int yr = cal_temp.get(Calendar.YEAR);

            months_new[i] = months[month] + TriviaConstants.SPACE + yr;
        }
        /*for (int i = 0; i <= selectedMonth; i++) {
            months_new[i] = months[i];
        }*/
        monthAdapter = new MonthAdapter(getApplicationContext(),
                android.R.layout.simple_list_item_1, months_new);

        months_list.setAdapter(monthAdapter);
        months_list.setSelectionAfterHeaderView();
        months_list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        months_list.setSelection(0);
        String month_name = new SimpleDateFormat("MMMM").format(cal.getTime());

        for (int i = 0; i < Leaderboard_New.months_new.length; i++)
            if (Leaderboard_New.months_new[i].contains(month_name))
                MONTH = i;
        selectedMonthPos = MONTH;
        selected_month_year.setText(String.valueOf(cal.get(Calendar.YEAR)));
        selected_week_year.setText(String.valueOf(cal.get(Calendar.YEAR)));
        //selected month to be shown inside the popup selector

        selected_month.setText(months_new[selectedMonthPos].toString());

        months_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                months_list.setSelection(position);
                Calendar cal = Calendar.getInstance();
                selectedMonthPos = position;
                String[] split = months_new[position].split(" ");
                String monthSubString = split[0];
                String yearSubString = split[1];
                int n = Arrays.asList(months).indexOf(monthSubString);
                cal.set(Calendar.MONTH, n);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.YEAR, Integer.parseInt(yearSubString));
                Date monthStart = cal.getTime();
                monthSelectedValue = monthStart;
                mMonthStartDate = String.valueOf(monthStart.getTime() / 1000);
                monthAdapter.notifyDataSetChanged();
                // }
            }
        });
    }

    public void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.leaderboard));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arow);
    }

    /**
     * leaderboard call for daily tab
     *
     * @param context
     * @param uid
     */
    public static void fetchLeaderBoard(Context context, String uid) {
        String ntw = CommonUtility.getNetworkType(context);
        ReadPref readPref = new ReadPref(context);
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(uid));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());

        map.put(PARAM_MODE, mMode);//Mode of the leaderboard to be displayed. Daily/Weekly/Monthly/Single Quiz (D/W/M/Q)
        map.put(PARAM_START_DATE, mDailyStartDate);
        map.put(PARAM_END_DATE, mDailyStartDate);
        map.put(PARAM_NETWORK, ntw);
        //3rd parameters is 1 if the call is made from leaderboard i.e will only refersh adapter
        if (mMode.equals(MODE_QUIZ)) {
            map.put(PARAM_QUIZ_ID, selectedQuizId);
        }

        APICalls.fetchLeaderBoard((AppCompatActivity) context, map, 1, mMode);


    }

    public static void openPrizesView(int Uid, String pname, String name, String url, String rank, Context baseContext) {
        if (Leaderboard_New.prizes_view != null) {
            Leaderboard_New.winner_prize_name.setText(pname);
            Leaderboard_New.winner_name.setText(name);

            int user_rank = Integer.parseInt(rank);
            //Leaderboard.winner_prize_img.setImageBitmap(url);


            if (CommonUtility.chkString(url)) {


                switch (user_rank) {
                    case 1:
                        LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                        winner_prize_rank.setImageResource(R.drawable.rank_1);
                        Leaderboard_New.winner_prize_rank.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                        winner_prize_rank.setImageResource(R.drawable.rank_2);
                        Leaderboard_New.winner_prize_rank.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                        winner_prize_rank.setImageResource(R.drawable.rank_3);
                        Leaderboard_New.winner_prize_rank.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                        Leaderboard_New.winner_prize_rank.setVisibility(View.INVISIBLE);

                        break;
                    case 5:
                        LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                        Leaderboard_New.winner_prize_rank.setVisibility(View.INVISIBLE);
                        break;
                    case 6:
                        LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                        Leaderboard_New.winner_prize_rank.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        Leaderboard_New.winner_prize_name.setText(pname);
                        Leaderboard_New.winner_name.setText(name);
                        Leaderboard_New.winner_prize_rank.setVisibility(View.INVISIBLE);

                        break;

                }
            }
                /*if (user_rank == 1) {
                    LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                    winner_prize_rank.setImageResource(R.drawable.rank_1);
                    Leaderboard_New.winner_prize_rank.setVisibility(View.VISIBLE);

                } else if (user_rank == 2) {
                    LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                    winner_prize_rank.setImageResource(R.drawable.rank_2);
                } else if (user_rank == 3) {
                    LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                    winner_prize_rank.setImageResource(R.drawable.rank_3);
                } else if (user_rank == 4) {
                    LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                    Leaderboard_New.winner_prize_rank.setVisibility(View.INVISIBLE);
                } else if (user_rank == 5) {
                    LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                    Leaderboard_New.winner_prize_rank.setVisibility(View.INVISIBLE);
                } else if (user_rank == 6) {
                    LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);
                    Leaderboard_New.winner_prize_rank.setVisibility(View.INVISIBLE);
                }


                // LeaderboardAdapter.loadImageWithCallback(baseContext, url, winner_prize_img);


            } else {
                Leaderboard_New.winner_prize_name.setText(pname);
                Leaderboard_New.winner_name.setText(name);
                Leaderboard_New.prizes_view.setVisibility(View.VISIBLE);
            }*/
            Leaderboard_New.prizes_view.setVisibility(View.VISIBLE);

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onClick(View v) {

        readPref = new ReadPref(getApplicationContext());
        UID = Integer.parseInt(readPref.getUID());
        Calendar calendar = Calendar.getInstance();

        int id = v.getId();
        if (id == R.id.cancel_month) {

            selectedMonthPos = 0;
            mMonthStartDate = readPref.getDefaultDate();

            MonthlyLeaderboard.progressLayout.showProgress();
            MonthlyLeaderboard.fetchLeaderBoard(getApplicationContext(), String.valueOf(UID), 1, TriviaConstants.MODE_MONTHLY);
            MonthlyLeaderboard.datepicker.setText(months_new[0].toString());
            selected_month.setText(months_new[0].toString());
            months_list.setSelection(2);
            monthAdapter.notifyDataSetChanged();
            month_view.setVisibility(View.GONE);
        } else if (id == R.id.ok_month) {
            if (monthSelectedValue != null) {
                mMonthStartDate = String.valueOf(monthSelectedValue.getTime() / 1000);
            }
            MonthlyLeaderboard.progressLayout.showProgress();
            MonthlyLeaderboard.fetchLeaderBoard(getApplicationContext(), String.valueOf(UID), 1, TriviaConstants.MODE_MONTHLY);
            MonthlyLeaderboard.datepicker.setText(months_new[selectedMonthPos].toString());
            selected_month.setText(months_new[selectedMonthPos].toString());
            month_view.setVisibility(View.GONE);

        } else if (id == R.id.cancel_week) {
            week_calender.setDate(Long.valueOf(readPref.getDefaultDate()) * 1000);
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(Long.valueOf(readPref.getDefaultDate()) * 1000);
            weekNo = cal.get(Calendar.WEEK_OF_YEAR);
            weekYear = cal.get(Calendar.YEAR);
            mWeekStartDate = readPref.getDefaultDate();

            WeekLeaderboard.progressLayout.showProgress();
            WeekLeaderboard.datepicker.setText(CommonUtility.formatDate(Long.parseLong(CommonUtility.getStartWeek(weekNo, weekYear))) + "-" + CommonUtility.formatDate(Long.parseLong(CommonUtility.getEndOFWeek(weekNo, weekYear))));
            WeekLeaderboard.fetchLeaderBoard(getApplicationContext(), String.valueOf(UID), 1, TriviaConstants.MODE_WEEKLY);
            selected_week.setText(CommonUtility.formatDate(Long.parseLong(CommonUtility.getStartWeek(weekNo, weekYear))) + "-" + CommonUtility.formatDate(Long.parseLong(CommonUtility.getEndOFWeek(weekNo, weekYear))));
            week_view.setVisibility(View.GONE);
        } else if (id == R.id.ok_week) {


            int day = calendar.getFirstDayOfWeek();
            if (day == Calendar.SUNDAY) {
                Calendar cal = Calendar.getInstance();
                cal.set(mSelectedWeekYear, mSelectedWeekMonth, mSelectedWeekDay);
                weekNo = cal.get(Calendar.WEEK_OF_YEAR);

                mSelectedWeek = cal.get(Calendar.DAY_OF_WEEK);
                if (mSelectedWeek == Calendar.SUNDAY) {
                    weekNo = weekNo - 1;
                }
            }

            if (mSelectedWeekYear != 0) {
                mWeekStartDate = CommonUtility.getStartWeek(weekNo, mSelectedWeekYear);

                WeekLeaderboard.datepicker.setText(CommonUtility.formatDate(Long.parseLong(CommonUtility.getStartWeek(weekNo, weekYear))) + "-" + CommonUtility.formatDate(Long.parseLong(CommonUtility.getEndOFWeek(weekNo, weekYear))));
                WeekLeaderboard.fetchLeaderBoard(getApplicationContext(), String.valueOf(UID), 1, TriviaConstants.MODE_WEEKLY);
                selected_week.setText(CommonUtility.formatDate(Long.parseLong(CommonUtility.getStartWeek(weekNo, weekYear))) + "-" + CommonUtility.formatDate(Long.parseLong(CommonUtility.getEndOFWeek(weekNo, weekYear))));
                WeekLeaderboard.progressLayout.showProgress();

            }
            week_view.setVisibility(View.GONE);
        } else if (id == R.id.close_prize_view) {
            prizes_view.setVisibility(View.GONE);
        }

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, TriviaConstants, GoogleApiClient.OnConnectionFailedListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private DatePickerDialog fromDatePickerDialog;
        static int position;
        static String UID;
        static ReadPref readPref;
        static SavePref savePref;
        public static LinearLayout lock_leaderboard;
        public static LeaderboardAdapter adapter;
        public static ProgressLayout progressLayout;
        public static NonScrollListView leaderboard_list;
        static TextView datepicker;
        static List<LeaderboardItems.Rankings> rankingsList = new ArrayList<>();
        static TextView default_quiz;
        static PopupMenu popupMenu;
        static Context context;
        Button login_register;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            LinearLayout rootView = new LinearLayout(getActivity()); // for example
            inflater.inflate(R.layout.fragment_leaderboard, rootView, true);
            savePref = new SavePref(getActivity().getApplicationContext());
            readPref = new ReadPref(getActivity().getApplicationContext());
            UID = readPref.getUID();
            context = getActivity();
            position = getArguments().getInt(ARG_SECTION_NUMBER);
            progressLayout = (ProgressLayout) rootView.findViewById(R.id.progress_layout);
            if (progressLayout != null) {
                progressLayout.showProgress();
            }
            leaderboard_list = (NonScrollListView) rootView.findViewById(R.id.leaderboard_list);
            items = new LeaderboardItems();
            adapter = new LeaderboardAdapter(getActivity(), rankingsList, UID);
            leaderboard_list.setAdapter(adapter);
            default_quiz = (TextView) rootView.findViewById(R.id.default_quiz);
            lock_leaderboard = (LinearLayout) rootView.findViewById(R.id.lock_leaderboard);

            login_register = (Button) rootView.findViewById(R.id.login_register);
            //login_register.setOnClickListener(this);

            login_register.setOnClickListener(new View.OnClickListener() {
                                                  public void onClick(View v) {
                                                      if (readPref.getIsLiveCode()) {
                                                          Leaderboard_New.mMode = MODE_DAILY;
                                                          try {
                                                              final Intent login = new Intent(Intent.ACTION_VIEW);
                                                              String implClassName = context.getResources().getString(R.string.login_class);
                                                              login.setClassName(context, implClassName);

                                                              //CommonUtility.showActivity(context, "", 0, false, login, null);
                                                              ((AppCompatActivity) context).startActivityForResult(login, RESPONSE_LOGIN_CODE);
                                                          } catch (Exception e) {
                                                              e.printStackTrace();
                                                          }
                                                      }
                                                  }
                                              }

            );

            setDateTimeField();
            initDatePicker(rootView);
            //initUI();
            Leaderboard_New.selectedQuizId = "0";
            Leaderboard_New.mMode = MODE_DAILY;
            //Leaderboard_New.selectedMonthPos = 0;
            fetchLeaderBoard((AppCompatActivity) getActivity(), String.valueOf(UID));
            if (readPref.getIsLoggedIn().equals(String.valueOf(TriviaConstants.DEFAULT_ONE))) {
                //if the user is logged in lock UI should hide.
                lock_leaderboard.setVisibility(View.GONE);
            }

            if (!readPref.getRegStatus().equals("1")) {
                lock_leaderboard.setVisibility(View.VISIBLE);
            } else {
                lock_leaderboard.setVisibility(View.GONE);
            }
            return rootView;
        }


        @Override
        public void onStart() {
            super.onStart();


        }

        @Override
        public void onDestroy() {

            super.onDestroy();


        }

        @Override
        public void onStop() {
            super.onStop();

        }


        /**
         * Show UI for the leaderboard daily page
         */

        public static void initUI() {
            UID = readPref.getUID();
            List<LeaderboardItems.Myrank> myranking = new ArrayList<>();
            List<LeaderboardItems.Quizzes> itemsQuizzes = new ArrayList<>();
            items = new LeaderboardItems();
            //get data items from APi call
            items = APICalls.returnLeaderboardData();
            selectedQuizId = "0";
            if (items != null) {
                //selectedQuizId is the id of game whose leaderboard is being shown and should be selected when page opens
                selectedQuizId = items.getGameId();
            }
            if (items != null) {
                rankingsList = items.getRankings();
                myranking = items.getMyrank();
                itemsQuizzes = items.getQuizzes();

            }
            //prepare the final display list for leaderboard
            List<LeaderboardItems.Rankings> list = checkNullRank(rankingsList, myranking);
            rankingsList.clear();
            rankingsList.addAll(list);
            //refresh the leaderboard adapter with new data added or changed
            adapter.updateItem(rankingsList);
            adapter.notifyDataSetChanged();
            popupMenu = new PopupMenu(context, default_quiz);
            default_quiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupMenu.show();
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Leaderboard", "QuizDropdown", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
                }
            });

            if (itemsQuizzes.size() != 0) {
                default_quiz.setVisibility(View.VISIBLE);
                for (int i = 0; i < itemsQuizzes.size(); i++) {
                    // popupMenu.getMenu().add("QUIZ " + String.valueOf(i));
                    popupMenu.getMenu().add(1, itemsQuizzes.get(i).getQid(), i, "QUIZ " + TriviaConstants.SPACE + String.valueOf(itemsQuizzes.size() - i));
                    if (itemsQuizzes.get(i).getQid() == Integer.parseInt(selectedQuizId)) {
                        default_quiz.setText(context.getResources().getString(R.string.quiz_caps) + String.valueOf(itemsQuizzes.size() - i));
                    }
                }
            } else {
                default_quiz.setVisibility(View.GONE);
            }
            // popupMenu.inflate(R.menu.all_quiz_menu);

            popupMenu.setOnMenuItemClickListener(
                    new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();
                            selectedQuizId = String.valueOf(id);
                            default_quiz.setText(item.getTitle());

                            datepicker.setText(CommonUtility.formatDate(Long.valueOf(mDailyStartDate)));
                            mMode = TriviaConstants.MODE_QUIZ;
                            progressLayout.showProgress();
                            fetchLeaderBoard(context, UID);
                            return true;
                        }
                    });


            if (position == 1 || position == 2) {
                default_quiz.setVisibility(View.GONE);
            }
            //if reg_status is 0 then user is non_logged in thus show lock leaderboard screen
            if (!readPref.getRegStatus().equals("1")) {
                lock_leaderboard.setVisibility(View.VISIBLE);
            } else {
                lock_leaderboard.setVisibility(View.GONE);
            }

        }

        /**
         * Prepare a Listview with top 5 rankers and my self rank at button with yellow background
         *
         * @param rankings
         * @param myranking
         * @return
         */
        private static List<LeaderboardItems.Rankings> checkNullRank(List<LeaderboardItems.Rankings> rankings, List<LeaderboardItems.Myrank> myranking) {
            List<LeaderboardItems.Rankings> temp = new ArrayList<>();

            LeaderboardItems.Rankings tmp = new LeaderboardItems().getRankingsItems();
            tmp.setImgurl("");
            tmp.setUid(0);
            tmp.setScore("0");
            tmp.setName("-");
            LeaderboardItems.Rankings rankings1 = new LeaderboardItems().getRankingsItems();
            boolean iranked = false;
            try {

                if (myranking.size() != 0) {
                    rankings1.setImgurl(CommonUtility.checkNull(myranking.get(0).getImgurl()));
                    rankings1.setName(CommonUtility.checkNull(myranking.get(0).getName()));
                    rankings1.setScore(String.valueOf(myranking.get(0).getScore()));
                    rankings1.setIswon(myranking.get(0).getIswon());
                    rankings1.setPname(myranking.get(0).getPname());
                    rankings1.setPimgurl(myranking.get(0).getPimgurl());
                    int rank = myranking.get(0).getRank();
                    if (rank == 0) {
                        rankings1.setRank("-");
                    } else {
                        rankings1.setRank(String.valueOf(rank));
                    }
                    rankings1.setUid(myranking.get(0).getUid());
                    //temp.add(rankings1);
                } /*else {

                    rankings1.setImgurl(readPref.getUserImage());
                    rankings1.setUid(Integer.parseInt(readPref.getUID()));
                    rankings1.setScore("0");
                    rankings1.setName(readPref.getUserName());
                    rankings1.setRank("-");
                    //temp.add(tmp1);

                }*/

                if (rankings.size() == 0) {
                    for (int i = 0; i <= 5; i++) {
                        LeaderboardItems.Rankings tmp1 = new LeaderboardItems().getRankingsItems();
                        tmp1.setImgurl("");
                        tmp1.setUid(0);
                        tmp1.setScore("0");
                        tmp1.setName("-");
                        tmp1.setRank(String.valueOf(i + 1));
                        temp.add(tmp1);
                    }
                } else {
                    for (int i = 0; i <= 5; i++) {
                        if (i < rankings.size()) {

                            if (Integer.parseInt(rankings.get(i).getRank()) == i + 1) {
                                temp.add(i, rankings.get(i));
                                if (rankings1.getUid() == rankings.get(i).getUid()) {
                                    iranked = true;
                                }
                            }
                        } else {
                            LeaderboardItems.Rankings tmp1 = new LeaderboardItems().getRankingsItems();
                            tmp1.setImgurl("");
                            tmp1.setUid(0);
                            tmp1.setScore("0");
                            tmp1.setName("-");
                            tmp1.setRank(String.valueOf(i + 1));
                            temp.add(tmp1);

                        }
                    }

                }

                if (!iranked) {
                    if (CommonUtility.chkString(rankings1.getRank())) {
                        temp.set(5, rankings1);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return temp;
        }


        @Override
        public void onResume() {

            super.onResume();
            // months_list.setSelection(selectedMonthPos);

        }

        @Override
        public void onPause() {
            super.onPause();

        }


        private void initDatePicker(View view) {

            datepicker = (TextView) view.findViewById(R.id.default_date);
            datepicker.setText(CommonUtility.formatDate(Long.valueOf(mDailyStartDate)));
            datepicker.setOnClickListener(this);
        }

        /**
         * set the time in date picker.
         */

        private void setDateTimeField() {

            final Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, 0);
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(Long.valueOf(readPref.getDefaultDate()) * 1000);
            int year1 = c.get(Calendar.YEAR);
            int month1 = c.get(Calendar.MONTH);
            int day1 = c.get(Calendar.DAY_OF_MONTH);

            fromDatePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                            Date date = new Date(year - 1900, monthOfYear, dayOfMonth);
                            mDailyPrevious = mDailyStartDate;
                            mDailyStartDate = String.valueOf(date.getTime() / 1000);
                            Log.d("mDailyStartDate--", String.valueOf(mDailyStartDate));
                            PlaceholderFragment.datepicker.setText(CommonUtility.formatDate(Long.valueOf(mDailyStartDate)));
                            mMode = MODE_DAILY;
                            progressLayout.showProgress();
                            fetchLeaderBoard((AppCompatActivity) getContext(), UID);
                            LeaderboardAdapter.removed = false;
                        }

                    }, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));


            fromDatePickerDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    Calendar c = Calendar.getInstance();
                    c.setTimeInMillis(Long.valueOf(readPref.getDefaultDate()) * 1000);
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    fromDatePickerDialog.updateDate(year, month, day);

                    Date date = new Date(year - 1900, month, day);
                    mDailyStartDate = String.valueOf(date.getTime() / 1000);


                    Log.d("mDailyStartDate--", String.valueOf(mDailyStartDate));

                    PlaceholderFragment.datepicker.setText(CommonUtility.formatDate(Long.valueOf(mDailyStartDate)));
                    mMode = MODE_DAILY;
                    progressLayout.showProgress();
                    fetchLeaderBoard((AppCompatActivity) getContext(), UID);
                    LeaderboardAdapter.removed = false;


                }
            });
            fromDatePickerDialog.updateDate(year1, month1, day1);
            fromDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            fromDatePickerDialog.getDatePicker().setMinDate(Long.valueOf("1470011400000"));//min date set to 1st august 2016
        }


        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.default_date) {
                if (position == 0) {
                    fromDatePickerDialog.show();
                } else if (position == 1) {
                    week_view.setVisibility(View.VISIBLE);
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Leaderboard", "Week selector", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
                } else if (position == 2) {
                    Calendar calendar = Calendar.getInstance();
                    final int selectedMonth = calendar.get(Calendar.MONTH);
                    selectedMonthPos = selectedMonth;
                    months_list.setSelection(selectedMonthPos);
                    monthAdapter.notifyDataSetChanged();
                    months_list.smoothScrollToPosition(selectedMonthPos);

                    month_view.setVisibility(View.VISIBLE);
                    //GA ANALYTICS
                    CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Leaderboard", "Month selector", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
                }

                //GA ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Leaderboard", "Datepicker", TriviaConstants.CLICK, "Trivia_And_Leaderboard");

            } else if (id == R.id.login_register) {
                if (null != GameArchive.activity) {
                    GameArchive.activity.finish();
                }
                if (null != ResultScreen.context) {
                    ResultScreen.context.finish();
                }
                getActivity().finish();
                //GA ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Leaderboard", "Login", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
            }
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        }
    }


    @Override
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
                    String email = bundle.getString("email");

                    Log.d("name", name);
                    CommonUtility.registerUserapi(activity.getApplicationContext(), new HashMap<String, String>(), name, user_image, ssoid, email, LEADERBOARD, "", mMode);


                    if (mMode.equals(MODE_DAILY)) {
                        if (Leaderboard_New.PlaceholderFragment.progressLayout != null) {
                            Leaderboard_New.PlaceholderFragment.lock_leaderboard.setVisibility(View.GONE);
                            Leaderboard_New.PlaceholderFragment.progressLayout.showProgress();

                        }
                    } else if (mMode.equals(MODE_WEEKLY)) {
                        if (WeekLeaderboard.progressLayout != null) {
                            WeekLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                            WeekLeaderboard.progressLayout.showProgress();

                        }
                    } else if (mMode.equals(MODE_MONTHLY)) {
                        if (MonthlyLeaderboard.progressLayout != null) {
                            MonthlyLeaderboard.lock_leaderboard.setVisibility(View.GONE);
                            MonthlyLeaderboard.progressLayout.showProgress();


                        }
                    }
                }
                Map<String, String> dataMap = new HashMap<String, String>();

            }
        }

        //}

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
            switch (position) {
                case 0:
                    return PlaceholderFragment.newInstance(position);
                case 1:
                    return WeekLeaderboard.newInstance(position);
                case 2:
                    return MonthlyLeaderboard.newInstance(position);

            }
            return null;
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

    @Override
    public void onBackPressed() {

        if (null != GameArchive.activity) {
            GameArchive.activity.finish();
        }
        if (null != ResultScreen.context) {
            ResultScreen.context.finish();
        }
        finish();
        //GA entry ANALYTICS
        CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Leaderboard", DEVICE_BACK, TriviaConstants.CLICK, "Trivia_And_Exit_Leaderboard");
        //onback press set the static variable to defaults
        mMode = MODE_DAILY;
        default_date = readPref.getDefaultDate();
        mDailyStartDate = default_date;
        mWeekStartDate = default_date;
        mMonthStartDate = default_date;
        Calendar calendar = Calendar.getInstance();
        final int selectedMonth = calendar.get(Calendar.MONTH);
        selectedMonthPos = selectedMonth;
    }

    /**
     * init receiver for network connction change
     */

    protected void initNetworkStateReciever() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(networkStateReceiver, filter);

    }

}
