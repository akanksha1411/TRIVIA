package toi.com.trivia.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.fragments.CategoryPage;
import toi.com.trivia.fragments.QuizScreen;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.RandamisedPojo;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class StartQuiz extends AppCompatActivity implements QuizScreen.OnFragmentInteractionListener, CategoryPage.OnFragmentInteractionListener, TriviaConstants {

    public static List<RandamisedPojo> randamized_ques = new ArrayList<>();

    public static NewGame.GameProperties properties;

    List<NewGame.Options> options = new ArrayList<>();
    //int qid, set_id, isBonus;
    public static AnswersPojo answersPojo;
    ReadPref readPref;
    SavePref savePref;
    int mCurrentPosition = 0;
    public static List<AnswersPojo.Answers> answersList = new ArrayList<>();
    public static DBController dbController;
    public static AppCompatActivity activity;
    String uid, sponsorName = "", sponsorImage = "";
    public static LinearLayout error_bar;
    public static TextView error_txt;

    @Override
    protected void onStart() {

        Log.d("onStart -quiz", "###########################");
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_quiz);
        Log.d("onCreate -quiz", "###########################");
        dbController = new DBController(getApplicationContext());
        activity = this;
        readPref = new ReadPref(getApplicationContext());
        uid = readPref.getUID();
        savePref = new SavePref(getApplicationContext());
        sponsorName = readPref.getSponsorName();

        //set background for the screen

        //current position of the question to be displayed
        mCurrentPosition = readPref.getCurrentPosition();
        savePref.saveIsGameEnded(false);
        Bundle bundle = getIntent().getExtras();
        int pageCalled = 0;
        if (bundle != null) {
            pageCalled = bundle.getInt("pageCalled");
            sponsorImage = bundle.getString("sponsorImage", "");
            if (CommonUtility.chkString(sponsorImage)) {
                sponsorName = bundle.getString("sponsorName", "");
            }

        }
        //    CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.START_QUIZ_ACTIVITY, sponsorName);
        error_bar = (LinearLayout) findViewById(R.id.error_bar);
        error_txt = (TextView) findViewById(R.id.error_bar_title);
        error_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (ResultScreen.context != null) {
                        ResultScreen.context.finish();
                    }
                    error_bar.setVisibility(View.GONE);
                    savePref.isReadyShown(false);
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (CommonUtility.chkString(sponsorImage)) {

            RelativeLayout category_layout = (RelativeLayout) findViewById(R.id.quiz_layout);
            category_layout.setBackground(getBackground(sponsorName));
        } else {
            sponsorName = readPref.getSponsorName();
            CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.START_QUIZ_ACTIVITY, sponsorName);
        }
        answersPojo = new AnswersPojo();
        answersList = new ArrayList<>();
        try {
            properties = dbController.findGameProperties();
            if (properties.getQuesAlgo().equals(QUESTION_ALGO_RANDOM)) {
                randamized_ques = dbController.findRandamizedQuestions(""); //Fetch normal question in random order
            } else {
                randamized_ques = dbController.findFixedQuestions("");//get the question marked as fixedLast
            }
            List<RandamisedPojo> randamized_ques1 = dbController.findRandamizedQuestionsForBonus(""); //Fetch random bonus question
            randamized_ques.addAll(randamized_ques1);

            //pojo to save answers and set on Server
            if (CommonUtility.chkString(String.valueOf(readPref.getCurrentGameId()))) {
                if (pageCalled != GAME_ARCHIVE) {
                    //if the request is made to current game
                    answersPojo.setSetId(String.valueOf(readPref.getCurrentGameId()));
                    answersPojo.setGameId(Integer.parseInt(readPref.getCurrentGameId()));
                } else {
                    //if request for game is from archieve page then archieve game id will be passed
                    answersPojo.setSetId(String.valueOf(readPref.getArchiveGameId()));
                    answersPojo.setGameId(Integer.parseInt(readPref.getArchiveGameId()));
                }
                answersPojo.setUid(Integer.parseInt(readPref.getUID()));//uid of the drupal user saved from registration
                answersPojo.setDeviceType(DEVICE_TYPE);
                answersPojo.setIsLoggedIn(DEFAULT_ZERO);
                answersPojo.setUqid(CommonUtility.getIMEI(getApplicationContext()));
                //Initialize ans pojo with default question data
                Iterator<RandamisedPojo> iterator = randamized_ques.iterator();
                int j = 0;
                while (iterator.hasNext()) {
                    RandamisedPojo next = iterator.next();
                    AnswersPojo.Answers answers = new AnswersPojo().getAnswers();
                    answers.setTimeTaken(0);//set time taken to answer this question
                    int q_id = next.getQ_id();
                    answers.setQuesId(q_id);
                    answers.setPresentedTime(CommonUtility.getCurrentTimeStamp());
                    List<NewGame.Options> options2 = new ArrayList<>();
                    List<NewGame.Options> optionsQ = new ArrayList<>();
                    List<String> optseq = new ArrayList<>();
                    if (properties.getOptionAlgo().equals(QUESTION_ALGO_RANDOM)) {
                        options2 = dbController.findALLOptions(String.valueOf(q_id));
                        List<NewGame.Options> newList = dbController.findCorrectOption(String.valueOf(q_id));

                        if (newList != null) {
                            options2.addAll(newList);
                            Collections.shuffle(options2);
                            optionsQ.addAll(options2);
                        } else {
                            Collections.shuffle(options2);
                            optionsQ.addAll(options2);
                        }
                    } else {
                        options2 = dbController.findALLFixedOptions(String.valueOf(q_id));
                        List<NewGame.Options> newList = dbController.findCorrectOption(String.valueOf(q_id));

                        if (newList != null) {
                            options2.addAll(newList);
                            //Collections.shuffle(options2);
                            optionsQ.addAll(options2);
                        } else {
                            //Collections.shuffle(options2);
                            optionsQ.addAll(options2);
                        }
                    }
                    for (int i = 0; i < optionsQ.size(); i++) {
                        optseq.add(String.valueOf(optionsQ.get(i).getOptId()));
                    }

                    answers.setOptions(optionsQ);
                    answers.setUserOptSeq(CommonUtility.commasSeparatedArray(optseq));
                    answers.setUserQuesSeq(String.valueOf(j));


                    answers.setUserOptId(DEFAULT_ZERO);
                    answers.setqState(TriviaConstants.PRESENTED_STATE);
                    answersList.add(answers);
                    j++;
                }
                answersPojo.setAnswers_list(answersList);
            } else {
                // CommonUtility.showCloseErrorAlert(StartQuiz.activity, TriviaConstants.NEW_GAME_FAILURE);
                StartQuiz.showErrorBar(TriviaConstants.NEW_GAME_FAILURE);
                //do
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        final PublisherAdView mPublisherAdView = (PublisherAdView) findViewById(R.id.publisherAdView);

        CommonUtility.initADs(activity, mPublisherAdView);

        if (savedInstanceState == null) {
            if (CommonUtility.doNogoutTask()) {
                Boolean killed_status = readPref.getIsGameKilled();
                if (!killed_status) {
                    replaceFragmentWithoutHistory(new CategoryPage(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.CATEGORY_PAGE, true, this);
                } else {
                    finish();
                }
            } else {
                replaceFragmentWithoutHistory(new CategoryPage(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.CATEGORY_PAGE, true, this);
            }
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
        if (context != null) {
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
    }

    /**
     * Adds 1st fragment to activity without being added to the history stack
     *
     * @param fragment
     * @param bundleParameterName
     * @param bundleValue
     * @param isUsedBundle
     */
    public static void replaceFragmentHistory(Fragment fragment, String bundleParameterName, int bundleValue,
                                              boolean isUsedBundle, AppCompatActivity context) {
        if (isUsedBundle) {
            Bundle args = new Bundle();
            args.putInt(bundleParameterName, bundleValue);
            fragment.setArguments(args);
        }
        if (context != null) {
            try {
                FragmentTransaction transaction =
                        context.getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
                transaction.replace(R.id.quiz_container, fragment);
                transaction.addToBackStack(null);
                transaction.commitAllowingStateLoss();

            } catch (IllegalStateException e) {
                e.printStackTrace();
                context.finish();
            }
        }
    }

    public static void showErrorBar(String message) {
        error_txt.setText(message);
        error_bar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        Log.d("onbackpressed", "---onbackpressed");
        //Back press show alert to continue to quit
        new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("Alert")
                .setMessage(R.string.quit_msg)
                .setPositiveButton(R.string.continue_, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();

                    }
                }).setNegativeButton(R.string.quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

               /* AnswersPojo pojo = StartQuiz.answersPojo;
                savePref.saveCurrentPosition(0);
                savePref.isReadyShown(false);

                QuizScreen.submitAnswers(pojo);*/
                savePref.saveIsGameKilled(true);
                if (readPref.getCurrentPosition() == 0) {
                    //
                    savePref.saveCurrentPosition(0);
                    savePref.isReadyShown(false);
                    finish();

                    if (TriviaLoader.activity != null) {

                        TriviaLoader.activity.finish();
                    }
                } else {
                    if (StartQuiz.answersPojo != null) {

                        AnswersPojo pojo = StartQuiz.answersPojo;

                        if (pojo.getAnswers_list().size() != 0) {
                            savePref.saveCurrentPosition(0);
                            savePref.isReadyShown(false);
                            QuizScreen.submitAnswers(pojo);
                        } else {

                            finish();
                            if (TriviaLoader.activity != null) {
                                TriviaLoader.activity.finish();
                            }
                        }
                    }
                }


            }
        }).show();
        CommonUtility.fetchArchive(getApplicationContext(), String.valueOf(readPref.getUID()), TriviaConstants.GAME_ARCHIVE);


    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy quiz", "#################");
        savePref.saveCurrentPosition(0);
        savePref.isReadyShown(false);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("ACTION_HOME_RESUMED"));

        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/TOI_TRIVIA", "QuizImages");
        deleteDirectory(mediaStorageDir);
        if (QuizScreen.myCountDownTimer != null) {
            QuizScreen.myCountDownTimer.cancel();
        }

        //refersh archive page
        CommonUtility.fetchArchive(getApplicationContext(), String.valueOf(uid), TriviaConstants.GAME_END);


        super.onDestroy();
    }

    public static NewGame.GameProperties returnGameProperties() {
        return properties;
    }

    public static List<RandamisedPojo> returnRandamizedQues() {
        return randamized_ques;
    }

    public static NewGame.Questions returnQuestions(String q_id) {
        return dbController.findQuestionsForQID(q_id);
    }


    @Override
    public void onQuizSCreenFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCategoryFragmentInteraction(String uri) {

    }

    /**
     * Delete the directory files that contains the images download
     *
     * @param path
     * @return
     */
    public static boolean deleteDirectory(File path) {
        // TODO Auto-generated method stub
        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteDirectory(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return (path.delete());
    }

    /**
     * Converts the category images into drawable icon
     *
     * @param sponsor_name
     * @return the drawable image of the category of the question
     */
    private Drawable getBackground(String sponsor_name) {

        File categoryFile = new File(Environment
                .getExternalStorageDirectory().getPath() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA/SponsorImages/"
                + CommonUtility.replaceSpace(sponsor_name) + ".png");
        Drawable sponsor = null;
        if (categoryFile != null) {
            if (categoryFile.exists()) {
                try {

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(categoryFile.getAbsolutePath(), options);

                    sponsor = new BitmapDrawable(getResources(), bitmap);
                } catch (Exception e) {
                    sponsor = null;
                    e.printStackTrace();
                }
            }

        }
        if (sponsor == null) {
            sponsor = getResources().getDrawable(R.drawable.bg);
        }
        return sponsor;
    }


}
