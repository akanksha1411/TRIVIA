package toi.com.trivia.fragments;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.androidprogresslayout.ProgressLayout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toi.com.trivia.R;

import toi.com.trivia.activities.Leaderboard_New;
import toi.com.trivia.adapters.LeaderboardMonthlyAdapter;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.api.APIService;
import toi.com.trivia.api.ApiRetroFit;
import toi.com.trivia.model.HomeItems;
import toi.com.trivia.model.LeaderboardItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;
import toi.com.trivia.utility.ui.NonScrollListView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MonthlyLeaderboard extends Fragment implements View.OnClickListener, TriviaConstants, GoogleApiClient.OnConnectionFailedListener {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private DatePickerDialog fromDatePickerDialog;
    static int position;
    String UID;
    public static Context mContext;
    static ReadPref readPref;
    private APIService apiService;
    Call<HomeItems> registrationItemsCall;
    private ApiRetroFit apiRetroFit;
    SavePref savePref;
    private long enqueue;
    private DownloadManager dm;
    BroadcastReceiver receiver;
    File profilePicture;
    public static LinearLayout lock_leaderboard;
    public static LeaderboardMonthlyAdapter monthlyAdapter;
    public static LeaderboardItems weekItems = new LeaderboardItems();
    static HashMap<String, String> fbData = new HashMap<String, String>();

    public static TextView datepicker;
    public static ProgressLayout progressLayout;
    static List<LeaderboardItems.Rankings> rankingsList = new ArrayList<>();
    static TextView default_quiz;
    Button login_register;

    public MonthlyLeaderboard() {
    }


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MonthlyLeaderboard newInstance(int sectionNumber) {
        MonthlyLeaderboard fragment = new MonthlyLeaderboard();
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
        position = getArguments().getInt(ARG_SECTION_NUMBER);
        mContext = getActivity();
        progressLayout = (ProgressLayout) rootView.findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        NonScrollListView leaderboard_list = (NonScrollListView) rootView.findViewById(R.id.leaderboard_list);

        default_quiz = (TextView) rootView.findViewById(R.id.default_quiz);
        lock_leaderboard = (LinearLayout) rootView.findViewById(R.id.lock_leaderboard);
        login_register = (Button) rootView.findViewById(R.id.login_register);
        login_register.setOnClickListener(this);
        datepicker = (TextView) rootView.findViewById(R.id.default_date);
        datepicker.setOnClickListener(this);
        monthlyAdapter = new LeaderboardMonthlyAdapter(getActivity(), rankingsList, UID);
        leaderboard_list.setAdapter(monthlyAdapter);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(Leaderboard_New.mMonthStartDate) * 1000);
        int year = cal.get(Calendar.YEAR);
        int MONTH = cal.get(Calendar.MONTH);
        int DAY = cal.get(Calendar.DAY_OF_MONTH);
        String month_name = new SimpleDateFormat("MMMM").format(cal.getTime());
        for (int i = 0; i < Leaderboard_New.months_new.length; i++)
            if (Leaderboard_New.months_new[i].contains(month_name))
                MONTH = i;

        if (MonthlyLeaderboard.datepicker != null) {
            MonthlyLeaderboard.datepicker.setText(Leaderboard_New.months_new[MONTH].toString());
            Leaderboard_New.selected_month.setText(Leaderboard_New.months_new[MONTH].toString());
            //Leaderboard_New.monthAdapter.notifyDataSetChanged();
        }
        /**
         * Broadcast receiver to receive the intent after download process is complete
         */
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

                          /*  Toast.makeText(getApplicationContext(), "downloaded",
                                    Toast.LENGTH_SHORT).show();
*/
                                    if (readPref.getLoginType().equals(FB_LOGIN_TYPE)) {
                                        profilePicture = new File(Environment.getExternalStorageDirectory()
                                                .getPath()
                                                + "/Android/data/" + getActivity().getApplicationContext().getPackageName() + "/files/Trivia/"
                                                + "SamarthProfilePicture" + "_" + UID + ".jpg");

                                    } else {
                                        profilePicture = new File(Environment.getExternalStorageDirectory()
                                                .getPath()
                                                + "/Android/data/" + getActivity().getApplicationContext().getPackageName() + "/files/Trivia/"
                                                + "SamarthProfilePicture" + "_" + UID + ".jpg");
                                    }

                                    //todo
                                    registerUser(getActivity().getApplicationContext(), fbData, readPref.getRegStatus(), readPref.getIsLoggedIn(), readPref.getUID(), 1); //todo uncomment


                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        getActivity().registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        //initUI();
        Leaderboard_New.selectedQuizId = "0";
        //Leaderboard_New.selectedMonthPos = 0;
        fetchLeaderBoard((AppCompatActivity) getActivity(), String.valueOf(UID), 1, TriviaConstants.MODE_MONTHLY);
        if (readPref.getIsLoggedIn().equals(String.valueOf(TriviaConstants.DEFAULT_ONE))) {
            MonthlyLeaderboard.lock_leaderboard.setVisibility(View.GONE);
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
     * Add post parameters for register and call api for data
     */
    public void registerUser(Context context, HashMap<String, String> fbData, String regStatus, String isLoggedIn, String uid, int type) {
        String ntw = CommonUtility.getNetworkType(context);
        HashMap<String, String> map = new HashMap<>();
        if (fbData.size() != 0) {
            map.putAll(fbData);
        }

        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_REG_STATUS, regStatus);
        map.put(PARAM_LOGIN_TYPE, readPref.getLoginType());
        map.put(PARAM_ISLOGGEDIN, isLoggedIn);
        map.put(PARAM_NETWORK, ntw);
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
                                                          savePref.saveRegStatus("1");
                                                          savePref.isLoggedIn("1");
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
                                                              // CommonUtility.showMessageAlert(context, NO_GAME_ACTIVE);
                                                          } else {
                                                              //current game is active
                                                              savePref.saveCurrentGameId(String.valueOf(currentGameId));//save game id to preference
                                                              savePref.saveNextGameId(String.valueOf(nextGameId));//save next game id to preference

                                                          }

                                                          lock_leaderboard.setVisibility(View.GONE);
                                                          //TODAY
                                                          // TriviaLoginView.setLoggedInUI();
                                                              /*initUI(getView(), itemsQuizzes);*/

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


    public static void initUI() {


        List<LeaderboardItems.Myrank> myranking = new ArrayList<>();
        List<LeaderboardItems.Quizzes> itemsQuizzes = new ArrayList<>();

        weekItems = new LeaderboardItems();
        rankingsList.clear();
        weekItems = APICalls.returnMonthLeaderboardData();
        if (weekItems != null) {
            rankingsList = weekItems.getRankings();
            myranking = weekItems.getMyrank();
            itemsQuizzes = weekItems.getQuizzes();
        }


        List<LeaderboardItems.Rankings> list = checkNullRank(rankingsList, myranking);
        rankingsList.clear();
        rankingsList.addAll(list);
        if (monthlyAdapter != null) {
            monthlyAdapter.updateItem(rankingsList);
            monthlyAdapter.notifyDataSetChanged();
        }

        if (position == 1 || position == 2) {
            if (default_quiz != null) {
                default_quiz.setVisibility(View.GONE);
            }
        }
        if (readPref != null) {
            if (!readPref.getIsLoggedIn().equals("1")) {
                lock_leaderboard.setVisibility(View.VISIBLE);
            } else {
                lock_leaderboard.setVisibility(View.GONE);
            }
        }

    }

    private static List<LeaderboardItems.Rankings> checkNullRank(List<LeaderboardItems.Rankings> rankings, List<LeaderboardItems.Myrank> myranking) {
        List<LeaderboardItems.Rankings> temp = new ArrayList<>();

        LeaderboardItems.Rankings tmp = new LeaderboardItems().getRankingsItems();
        tmp.setImgurl("");
        tmp.setUid(0);
        tmp.setScore("0");
        tmp.setName("-");
        int maxNo = 5;

        boolean iranked = false;
        try {
            LeaderboardItems.Rankings rankings1 = new LeaderboardItems().getRankingsItems();
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
            }/* else {

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * api call for Month leaderboard
     *
     * @param context
     * @param uid
     * @param i
     * @param mode
     */
    public static void fetchLeaderBoard(Context context, String uid, int i, String mode) {
        String ntw = CommonUtility.getNetworkType(context);
        ReadPref readPref = new ReadPref(context);
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_DEVICE_TYPE, DEVICE_TYPE); // A FOR ANDROID
        map.put(PARAM_UQID, CommonUtility.getIMEI(context));
        map.put(PARAM_UID, String.valueOf(uid));
        map.put(PARAM_ISLOGGEDIN, "1");
        map.put(PARAM_LOGIN_TYPE, DEFAULT_LOGIN_TYPE);
        map.put(PARAM_LOGIN_ID, readPref.getLoginId());

        map.put(PARAM_MODE, mode);//Mode of the leaderboard to be displayed. Daily/Weekly/Monthly/Single Quiz (D/W/M/Q)
        map.put(PARAM_START_DATE, Leaderboard_New.mMonthStartDate);
        map.put(PARAM_END_DATE, Leaderboard_New.mMonthStartDate);
        map.put(PARAM_NETWORK, ntw);
        //3rd parameters is 1 if the call is made from leaderboard i.e will only refersh adapter


        APICalls.fetchLeaderBoardMonthly(context, map, 1, mode);

    }


    @Override
    public void onResume() {
        super.onResume();

        getActivity().registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().unregisterReceiver(receiver);
    }

    public void downloadProfilePicture(String Url, HashMap<String, String> fbData) {
        String file_name = "";
        if (readPref.getLoginType().equals(TriviaConstants.FB_LOGIN_TYPE)) {
            file_name = "TriviaProfilePicture" + "_" + UID + ".jpg";
        } else {
            file_name = "TriviaProfilePicture" + "_" + UID + ".jpg";
        }
        dm = (DownloadManager) getActivity().getSystemService(
                getActivity().getApplicationContext().DOWNLOAD_SERVICE);
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

            File mediaStorageDir = new File(Environment
                    .getExternalStorageDirectory(), "Trivia");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("Trivia-- ", "Failed to create directory");

                }
            }

            request.setDestinationInExternalFilesDir(getActivity().getApplicationContext(),
                    "/Trivia", file_name);
            enqueue = dm.enqueue(request);
        } /*else {
                CommonUtility.registerUser(getActivity().getApplicationContext(), fbData); //todo uncomment

            }*/


    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.default_date) {

            Leaderboard_New.month_view.setVisibility(View.VISIBLE);

        } else if (id == R.id.login_register) {
            if (readPref.getIsLiveCode()) {
                // Open Toi Login page
                Leaderboard_New.mMode = MODE_MONTHLY;
                try {
                    final Intent login = new Intent(android.content.Intent.ACTION_VIEW);
                    String implClassName = getResources().getString(R.string.login_class);
                    login.setClassName(mContext, implClassName);

                    //CommonUtility.showActivity(context, "", 0, false, login, null);
                    startActivityForResult(login, RESPONSE_LOGIN_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getActivity(), "Leaderboard", "Login", TriviaConstants.CLICK, "Trivia_And_Leaderboard");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}