package toi.com.trivia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.androidprogresslayout.ProgressLayout;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.model.PlayedGame;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class AnswersActivity extends AppCompatActivity {

    private static SectionsPagerAdapter mSectionsPagerAdapter;
    public static ProgressLayout progressLayout;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private static ViewPager mViewPager;
    public static PlayedGame playedGame = new PlayedGame();
    static ImageView imageView;
    static ReadPref readPref;
    static Context context;
    static String UID;
    static LinearLayout viewpager_indicator, top_snack_bar;
    public static AppCompatActivity activity;
    static TextView retry;
    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!CommonUtility.haveNetworkConnection(context)) {
                // TSnackbar.make(mViewPager, "No internet connection", TSnackbar.LENGTH_SHORT).show();
                //showOfflineLayout();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        //register reciever for network connection change
        initNetworkStateReciever();
        activity = this;
        readPref = new ReadPref(getApplicationContext());
        UID = readPref.getUID();
        final String sponsorName = readPref.getSponsorName();
        context = getApplicationContext();
        initToolbar();
        //set background
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.ANSWERS_ACTIVITY, sponsorName);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        // top_snack_bar = (LinearLayout) findViewById(R.id.top_snack_bar);
        // retry = (TextView) findViewById(R.id.retry_action);
        progressLayout = (ProgressLayout) findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        viewpager_indicator = (LinearLayout) findViewById(R.id.viewpager_indicators);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                               @Override
                                               public void onPageSelected(int position) {

                                                   for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {
                                                       ImageView imgvw = (ImageView) findViewById(i);

                                                       if (i == position) {

                                                           imgvw.setImageResource(R.drawable.yellow_dot);
                                                       } else {
                                                           imgvw.setImageResource(R.drawable.grey_dot);
                                                       }
                                                   }

                                               }

                                               @Override
                                               public void onPageScrolled(int arg0, float arg1, int arg2) {

                                               }

                                               @Override
                                               public void onPageScrollStateChanged(int arg0) {

                                               }
                                           }

        );

        Bundle b = getIntent().getExtras();
        int gameId = 0;
        if (b != null) {
            gameId = b.getInt("gameId");
        }
        if (AnswersActivity.activity != null) {
            CommonUtility.fetchPlayedGame(gameId, AnswersActivity.activity, UID);
        }
    }

    /**
     * set the data to be accessed by fragment
     */
    public static void initUI() {
        playedGame = APICalls.getPlayedGameAnswers();
        progressLayout.showContent();
        mViewPager.setAdapter(mSectionsPagerAdapter);

        for (int i = 0; i < mViewPager.getAdapter().getCount(); i++) {

            imageView = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, 20);
            layoutParams.gravity = Gravity.CENTER;
            imageView.setLayoutParams(layoutParams);
            imageView.setPadding(0, 0, 5, 0);
            imageView.setId(i);
            imageView.setImageResource(R.drawable.grey_dot);
            if (i == 0) {
                imageView.setImageResource(R.drawable.yellow_dot);
            }
            viewpager_indicator.addView(imageView);

        }

        /*retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // top_snack_bar.setVisibility(View.GONE);
                progressLayout.setVisibility(View.VISIBLE);
                CommonUtility.fetchPlayedGame(Integer.parseInt(readPref.getResultGameId()), context, UID);
            }
        });
*/

    }

    private void showOfflineLayout() {
        // progressLayout.setVisibility(View.GONE);
        //top_snack_bar.setVisibility(View.VISIBLE);
    }

    public void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.answers));
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int position;

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
            View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
           /* TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));*/
            position = getArguments().getInt(ARG_SECTION_NUMBER);
            initUI(rootView);
            return rootView;
        }

        public void initUI(View view) {
            try {

                TextView quiz_question = (TextView) view.findViewById(R.id.quiz_question);
                TextView answer_no = (TextView) view.findViewById(R.id.answer_no);
                final ImageView quiz_image = (ImageView) view.findViewById(R.id.quiz_image);
                List<PlayedGame.Questions> questions = playedGame.getGameProperties().getQuestions();

                quiz_question.setText(CommonUtility.checkNull(questions.get(position).getTitle()));//set question title
                answer_no.setText(String.valueOf(position + 1));
                List<PlayedGame.Options> options = playedGame.getGameProperties().getQuestions().get(position).getOptions();


                LinearLayout options_list = (LinearLayout) view.findViewById(R.id.list_option_layout);//List view for option
                //   GridView options_grid = (GridView) view.findViewById(R.id.options_grid);//grid view for option
                LinearLayout options_grid = (LinearLayout) view.findViewById(R.id.grid_option_layout);//List view for option
                //if image is present show grid options and if without image then show list options
                //if long question without image - Grid option should come
                //set adapter to listview or gridview to display options for each question
                if (questions.get(position).getqImage() != null) {
                    if (questions.get(position).getqImage().length() != 0) {
                        //image visible if not null- image present
                        if (questions.get(position).getTitle().length() >= 90) {
                            quiz_question.setTextSize(18);
                        }
                        quiz_image.setVisibility(View.VISIBLE);
                       /* Picasso.with(getActivity()).load(CommonUtility.checkNull(questions.get(position).getqImage())).transform(new CommonUtility.RoundedTransformation(10, 0))
                                .placeholder(R.drawable.default_post_img)
                                .error(R.drawable.default_post_img)
                                .into(quiz_image);*/
                        if (context != null) {
                            Glide.with(getActivity())
                                    .load(CommonUtility.checkNull(questions.get(position).getqImage()))
                                    .asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .placeholder(R.drawable.default_post_img)
                                    .error(R.drawable.default_post_img)
                                    .into(quiz_image);
                        }
                        if (options != null) {
                            // OptionsGridAdapter adapter = new OptionsGridAdapter(getActivity(), options, qid, isBonus);
                            // options_grid.setAdapter(adapter);
                            setGridOptionUI(view, options, questions.get(position).getSelectedOption());
                            options_list.setVisibility(View.GONE);
                            options_grid.setVisibility(View.VISIBLE);
                        }
                    } else {
                        //image not present -
                        if (questions.get(position).getTitle().length() >= 90) {
                            //more than 90 characters
                            quiz_question.setTextSize(20);
                            quiz_question.setPadding(0, 0, 0, 20);
                        } else {
                            quiz_question.setTextSize(24);
                            quiz_question.setPadding(0, 0, 0, 15);
                        }
                        if (options != null) {
                            //   OptionsListAdapter adapter = new OptionsListAdapter(getActivity(), options, qid, isBonus);
                            //  options_list.setAdapter(adapter);

                            setListOptionUI(view, options, questions.get(position).getSelectedOption());
                            options_list.setVisibility(View.VISIBLE);
                            options_grid.setVisibility(View.GONE);
                        }
                        //image hidden as image is not present
                        quiz_image.setVisibility(View.GONE);

                    }
                } else {
                    //image not present list view options to be shown
                    if (questions.get(position).getTitle().length() >= 90) {
                        //more than 90 characters
                        quiz_question.setTextSize(20);
                        quiz_question.setPadding(0, 0, 0, 20);
                    } else {
                        quiz_question.setTextSize(24);
                        quiz_question.setPadding(0, 0, 0, 15);
                    }
                    if (options != null) {
                        //   OptionsListAdapter adapter = new OptionsListAdapter(getActivity(), options, qid, isBonus);
                        //  options_list.setAdapter(adapter);

                        setListOptionUI(view, options, questions.get(position).getSelectedOption());
                        options_list.setVisibility(View.VISIBLE);
                        options_grid.setVisibility(View.GONE);
                    }
                    //image hidden as image is not present
                    quiz_image.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        public void setGridOptionUI(View view, final List<PlayedGame.Options> options, String selectedOption) {

            final TextView opt1 = (TextView) view.findViewById(R.id.option_grid1);
            final TextView opt2 = (TextView) view.findViewById(R.id.option_grid2);
            final TextView opt3 = (TextView) view.findViewById(R.id.option_grid3);
            final TextView opt4 = (TextView) view.findViewById(R.id.option_grid4);

            opt1.setText(CommonUtility.checkNull(options.get(0).getName()));
            opt2.setText(CommonUtility.checkNull(options.get(1).getName()));
            opt3.setText(CommonUtility.checkNull(options.get(2).getName()));
            opt4.setText(CommonUtility.checkNull(options.get(3).getName()));

            //set green color if answer is correct and user user has selected the correct answer
            if (options.get(0).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(0).getOptId() == Integer.parseInt(selectedOption)) {
                setCorrectAnswerBackground(opt1, 0, view, Integer.parseInt(selectedOption));
            } else if (options.get(1).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(1).getOptId() == Integer.parseInt(selectedOption)) {
                setCorrectAnswerBackground(opt2, 1, view, Integer.parseInt(selectedOption));
            } else if (options.get(2).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(2).getOptId() == Integer.parseInt(selectedOption)) {
                setCorrectAnswerBackground(opt3, 2, view, Integer.parseInt(selectedOption));
            } else if (options.get(3).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(3).getOptId() == Integer.parseInt(selectedOption)) {
                setCorrectAnswerBackground(opt4, 3, view, Integer.parseInt(selectedOption));
            }

            if (options.get(0).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setCorrectAnswerBackground(opt1, 0, view, Integer.parseInt(selectedOption));
            } else if (options.get(1).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setCorrectAnswerBackground(opt2, 1, view, Integer.parseInt(selectedOption));
            } else if (options.get(2).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setCorrectAnswerBackground(opt3, 2, view, Integer.parseInt(selectedOption));
            } else if (options.get(3).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setCorrectAnswerBackground(opt4, 3, view, Integer.parseInt(selectedOption));
            }

            if (options.get(0).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(0).getOptId() == Integer.parseInt(selectedOption)) {
                setRedBackground(opt1, 0, view);
            } else if (options.get(1).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(1).getOptId() == Integer.parseInt(selectedOption)) {
                setRedBackground(opt2, 1, view);
            } else if (options.get(2).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(2).getOptId() == Integer.parseInt(selectedOption)) {
                setRedBackground(opt3, 2, view);
            } else if (options.get(3).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(3).getOptId() == Integer.parseInt(selectedOption)) {
                setRedBackground(opt4, 3, view);
            }


        }

        private void setRedBackground(TextView textView, int position, View v) {

            if (position == 0) {
                textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_red_top_left));

            }

            if (position == 1) {
                textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_red_top_right));

            }

            if (position == 2) {
                textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_red_bottom_left));

            }
            if (position == 3) {
                textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_red_bottom_right));

            }
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(v.getResources().getColor(R.color.white));


        }

        private void setCorrectAnswerBackground(TextView textView, int position, View v, int selectedOptions) {

            if (position == 0) {
                if (selectedOptions != TriviaConstants.DEFAULT_ZERO) {
                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_green_top_left));
                    textView.setTextColor(v.getResources().getColor(R.color.white));
                } else {
                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_top_left));
                    textView.setTextColor(v.getResources().getColor(R.color.black));
                }
            }

            if (position == 1) {
                if (selectedOptions != TriviaConstants.DEFAULT_ZERO) {
                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_green_top_right));
                    textView.setTextColor(v.getResources().getColor(R.color.white));
                } else {
                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_top_right));
                    textView.setTextColor(v.getResources().getColor(R.color.black));
                }
            }

            if (position == 2) {
                if (selectedOptions != TriviaConstants.DEFAULT_ZERO) {

                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_green_bottom_left));
                    textView.setTextColor(v.getResources().getColor(R.color.white));
                } else {
                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_bottom_left));
                    textView.setTextColor(v.getResources().getColor(R.color.black));
                }
            }
            if (position == 3) {
                if (selectedOptions != TriviaConstants.DEFAULT_ZERO) {
                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_green_bottom_right));
                    textView.setTextColor(v.getResources().getColor(R.color.white));
                } else {
                    textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow_bottom_right));
                    textView.setTextColor(v.getResources().getColor(R.color.black));
                }
            }
            textView.setTypeface(Typeface.DEFAULT_BOLD);


        }

        public void setListOptionUI(View view, final List<PlayedGame.Options> options, String selectedOption) {

            final TextView opt1 = (TextView) view.findViewById(R.id.option_text1);
            final TextView opt2 = (TextView) view.findViewById(R.id.option_text2);
            final TextView opt3 = (TextView) view.findViewById(R.id.option_text3);
            final TextView opt4 = (TextView) view.findViewById(R.id.option_text4);


            opt1.setText(CommonUtility.checkNull(options.get(0).getName()));
            opt2.setText(CommonUtility.checkNull(options.get(1).getName()));
            opt3.setText(CommonUtility.checkNull(options.get(2).getName()));
            opt4.setText(CommonUtility.checkNull(options.get(3).getName()));

            if (options.get(0).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(0).getOptId() == Integer.parseInt(selectedOption)) {
                setGreenListBackground(opt1, 0, view, Integer.parseInt(selectedOption));
            } else if (options.get(1).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(1).getOptId() == Integer.parseInt(selectedOption)) {
                setGreenListBackground(opt2, 1, view, Integer.parseInt(selectedOption));
            } else if (options.get(2).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(2).getOptId() == Integer.parseInt(selectedOption)) {
                setGreenListBackground(opt3, 2, view, Integer.parseInt(selectedOption));
            } else if (options.get(3).getIsCorrect() == TriviaConstants.DEFAULT_ONE && options.get(3).getOptId() == Integer.parseInt(selectedOption)) {
                setGreenListBackground(opt4, 3, view, Integer.parseInt(selectedOption));
            }

            if (options.get(0).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setGreenListBackground(opt1, 0, view, Integer.parseInt(selectedOption));
            } else if (options.get(1).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setGreenListBackground(opt2, 1, view, Integer.parseInt(selectedOption));
            } else if (options.get(2).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setGreenListBackground(opt3, 2, view, Integer.parseInt(selectedOption));
            } else if (options.get(3).getIsCorrect() == TriviaConstants.DEFAULT_ONE) {
                setGreenListBackground(opt4, 3, view, Integer.parseInt(selectedOption));
            }

            if (options.get(0).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(0).getOptId() == Integer.parseInt(selectedOption)) {
                setRedListBackground(opt1, 0, view);
            } else if (options.get(1).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(1).getOptId() == Integer.parseInt(selectedOption)) {
                setRedListBackground(opt2, 1, view);
            } else if (options.get(2).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(2).getOptId() == Integer.parseInt(selectedOption)) {
                setRedListBackground(opt3, 2, view);
            } else if (options.get(3).getIsCorrect() != TriviaConstants.DEFAULT_ONE && options.get(3).getOptId() == Integer.parseInt(selectedOption)) {
                setRedListBackground(opt4, 3, view);
            }


        }

        private void setRedListBackground(TextView textView, int i, View v) {
            textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_red));
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setTextColor(v.getResources().getColor(R.color.white));

        }

        private void setGreenListBackground(TextView textView, int i, View v, int selectedOption) {
            if (selectedOption != TriviaConstants.DEFAULT_ZERO) {
                textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_green));
                textView.setTextColor(v.getResources().getColor(R.color.white));
            } else {
                textView.setBackground(v.getResources().getDrawable(R.drawable.rounded_button_yellow));
                textView.setTextColor(v.getResources().getColor(R.color.black));
            }
            textView.setTypeface(Typeface.DEFAULT_BOLD);


        }


    }

    @Override
    protected void onDestroy() {

        unregisterReceiver(networkStateReceiver);
        super.onDestroy();
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
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            int n = playedGame.getGameProperties().getQuestions().size();

            return n;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ReadPref readPref = new ReadPref(context);
        String adUnit = readPref.getAnsBack();
        if (CommonUtility.chkString(adUnit)) {
            //if slot has ad unit then show ad
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
                    closeResult();
                }

                private void closeResult() {

                    finish();
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    closeResult();
                }
            });
            CommonUtility.requestNewInterstitial(mInterstitialAd, context);
        } else {
            finish();
        }
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
