package toi.com.trivia.api;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import toi.com.trivia.activities.AnswersActivity;
import toi.com.trivia.activities.GameArchive;

import toi.com.trivia.activities.Leaderboard_New;
import toi.com.trivia.activities.PrizesActivity;
import toi.com.trivia.activities.ResultScreen;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.fragments.FAQFragment;
import toi.com.trivia.fragments.MonthlyLeaderboard;
import toi.com.trivia.fragments.PrizesDaily;
import toi.com.trivia.fragments.PrizesMonth;
import toi.com.trivia.fragments.PrizesWeek;
import toi.com.trivia.fragments.QuizScreen;
import toi.com.trivia.fragments.WeekLeaderboard;
import toi.com.trivia.model.AnswersPojo;
import toi.com.trivia.model.ArchiveItems;
import toi.com.trivia.model.CategoriesItems;
import toi.com.trivia.model.Contents;
import toi.com.trivia.model.FaqPojo;
import toi.com.trivia.model.HomeItems;
import toi.com.trivia.model.LeaderboardItems;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.NotifyResult;
import toi.com.trivia.model.PlayedGame;
import toi.com.trivia.model.PrizesItems;
import toi.com.trivia.model.ResultItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;
import toi.com.trivia.utility.ui.TriviaLoginView;

//import toi.com.trivia.fragments.PrizesDaily;

/**
 * Created by Akanksha on 22/8/16.
 */
public class APICalls implements TriviaConstants {

    private static APIService apiService;

    private static ApiRetroFit apiRetroFit;
    static Call<HomeItems> registrationItemsCall;
    static Call<HomeItems> dashboardItems;
    static Call<ResultItems> resultItemsCall;
    static Call<PrizesItems> prizesItemsCall;
    static Call<CategoriesItems> categoriesItemsCall;
    static Call<PlayedGame> playedGameCall;
    static Call<ArchiveItems> archiveItemsCall;
    static Call<NotifyResult> notifyResultCall;
    static Call<FaqPojo> faqPojoCall;
    static Call<LeaderboardItems> leaderboardItemsCall;
    static Call<NewGame> newGameCall;
    static Call<Contents> newSubmitcall;
    public static HomeItems homeItems = new HomeItems();
    static SavePref savePref;
    static LeaderboardItems leaderboardItemses, weeklyLeaderboard, monthlyLeaderboard;
    public static DBController dbController;
    public static ArchiveItems archiveItems = new ArchiveItems();
    public static ResultItems resultItems = new ResultItems();
    public static PlayedGame playedGame = new PlayedGame();
    public static PrizesItems prizesItems = new PrizesItems();
    public static PrizesItems prizesItemsWeekly = new PrizesItems();
    public static PrizesItems prizesItemsMonthy = new PrizesItems();
    public static List<FaqPojo.Faq> faqList = new ArrayList<>();
    public static PrizesItems.Prizes prizes = new PrizesItems.Prizes();
    // public static HashMap<String,List<PrizesItems.Prizes>> prizesItemList = new HashMap<>();
    public static HashMap<String, List<PrizesItems.Prizesss>> prizesItemList = new HashMap<>();

    /**
     * Register User callback
     *
     * @param context
     * @param map
     */
    public static void registerUser(final Context context, final HashMap<String, String> map, final int type) {
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
                                                          //just addded for automatically  setting user as logged in
                                                       /*   savePref.isLoggedIn("1");
                                                          savePref.saveRegStatus("1");*/
                                                          //api call is successful
                                                          HomeItems.User user = items.getUser();

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

                                                          int currentGameId = game.getCurrentGameId();
                                                          int nextGameId = game.getNextGameId();

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

                                                          if (type == 1) {
                                                              CommonUtility.fetchLeaderBoard(context, String.valueOf(uid), 0, MODE_DAILY, DASHBOARD_SCREEN);
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
                                              CommonUtility.showAlertRetryCancel(GameArchive.activity, ALERT_TITLE,
                                                      ERROR_FAILURE,
                                                      new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              registerUser(context, map, type);
                                                              dialog.dismiss();
                                                          }
                                                      }, new DialogInterface.OnClickListener() {

                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              dialog.dismiss();
                                                          }
                                                      });
                                          }
                                      }

        );
    }

    /**
     * Fetch new game data and insert into DB
     *
     * @param context
     * @param currentGameId
     * @param pageCalled
     */
    public static void fetchNewGame(final Context context, final HashMap<String, String> currentGameId, final int pageCalled) {
        dbController = new DBController(context);
        dbController.clearDatabase2((AppCompatActivity) context, currentGameId.get(PARAM_GAME_ID));
        Log.d("fetchNewGame called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        newGameCall = apiService.getNewGame(currentGameId);
        CommonUtility.printHashmap(currentGameId);
        newGameCall.enqueue(new Callback<NewGame>() {
            @Override
            public void onResponse(Call<NewGame> call, Response<NewGame> response) {
                Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                Log.d("response message", response.message());
                if (response.isSuccessful()) {
                    if (response.body().getStatus() != DEFAULT_ZERO) {
                        NewGame items = response.body();
                        //  CommonUtility.replaceFragment(new Dashboard(), "", 0, false, (AppCompatActivity) context);

                        dbController.updateGameProperties(items, pageCalled);
                        dbController.updateERE_QUES_SET_QUES(items);
                    } else {
                        if (CommonUtility.dialog != null) {
                            CommonUtility.dialog.dismiss();
                        }
                        CommonUtility.showErrorAlert((AppCompatActivity) context, NEW_GAME_FAILURE);
                    }
                } else {
                    CommonUtility.showErrorAlert((AppCompatActivity) context, NEW_GAME_FAILURE);
                }
            }

            @Override
            public void onFailure(Call<NewGame> call, Throwable t) {
                t.printStackTrace();
                if (CommonUtility.dialog != null) {
                    CommonUtility.dialog.dismiss();
                }
                CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                        ERROR_FAILURE,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fetchNewGame(context, currentGameId, pageCalled);
                                dialog.dismiss();
                            }
                        }, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }
        });


    }

    /**
     * Fetch new game data and insert into DB
     *
     * @param context
     * @param currentGameId
     */
    public static void fetchArchivePLayGame(final Context context, HashMap<String, String> currentGameId) {
        Log.d("fetchNewGame called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();

        newGameCall = apiService.getNewGame(currentGameId);
        CommonUtility.printHashmap(currentGameId);
        newGameCall.enqueue(new Callback<NewGame>() {
            @Override
            public void onResponse(Call<NewGame> call, Response<NewGame> response) {
                Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                Log.d("response message", response.message());
                if (response.isSuccessful()) {
                    NewGame items = response.body();

                    Intent startQuiz = new Intent(context, StartQuiz.class);
                    CommonUtility.showActivity(context, "", 0, false, startQuiz, null);
                }
            }

            @Override
            public void onFailure(Call<NewGame> call, Throwable t) {
                t.printStackTrace();

            }
        });


    }

    /**
     * API calls for Submit answer to server
     * showResultflag - this shows whether to redirect to result page or not i.e means called at time f game end or elsewhere
     *
     * @param context
     * @param currentGameId
     * @param showResultflag
     */
    public static void SubmitAnswers(final AppCompatActivity context, final HashMap<String, String> currentGameId, final int showResultflag) {
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

                                                  if (StartQuiz.properties != null) {
                                                      StartQuiz.properties = new NewGame.GameProperties();
                                                      StartQuiz.randamized_ques.clear();
                                                      StartQuiz.answersPojo = new AnswersPojo();
                                                      StartQuiz.answersList.clear();
                                                  }
                                                  savePref.isFirstTime(false);
                                                  savePref.isReadyShown(false);
                                                  savePref.saveCurrentGameId("");
                                                  savePref.saveNextGameId("");
                                                  savePref.saveCurrentPosition(0);
                                                  //  savePref.saveResultGameId("");

                                                  Log.d("Msg submit answer--", String.valueOf(data.getMessage()));
                                                  Log.d("answer--", String.valueOf(data.getAnswers()));
                                                  Log.d("game id answer--", String.valueOf(data.getGameId()));
                                                  Log.d("result time answer--", String.valueOf(data.getResultTime()));

                                                  final Bundle bundle = new Bundle();
                                                  bundle.putSerializable("game_end", data);

                      /*  //Interstitial ads integration
                        final PublisherInterstitialAd mInterstitialAd = new PublisherInterstitialAd(context);
                        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.interstitial_ad_unit_id));

                        mInterstitialAd.setAdListener(new AdListener() {

                            @Override
                            public void onAdLoaded() {
                                super.onAdLoaded();
                                mInterstitialAd.show();
                            }

                            @Override
                            public void onAdClosed() {
                                //on ads closed the game will start

                                if (Long.valueOf(data.getResultTime()) <= System.currentTimeMillis() / 1000) {
                                    CommonUtility.fetchResult(context, readPref.getUID(), String.valueOf(data.getGameId()), TriviaConstants.RESULT_SCREEN);

                                } else {
                                    openGameEnd();
                                }


                            }

                            private void openGameEnd() {

                                Fragment fragment = new GameEnd();
                                if (context != null) {
                                    try {
                                        FragmentTransaction transaction =
                                                ((StartQuiz) context).getSupportFragmentManager().beginTransaction();
                                        fragment.setArguments(bundle);
                                        transaction.replace(R.id.quiz_container, fragment);

                                        transaction.commitAllowingStateLoss();

                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                        ((StartQuiz) context).finish();
                                        CommonUtility.showErrorAlert(context, "Answers not submitted...error occurred");
                                    }
                                }
                            }

                            @Override
                            public void onAdFailedToLoad(int i) {
                                super.onAdFailedToLoad(i);
                                if (Long.valueOf(data.getResultTime()) <= System.currentTimeMillis()) {
                                    CommonUtility.fetchResult(context, readPref.getUID(), String.valueOf(data.getGameId()), TriviaConstants.RESULT_SCREEN);

                                } else {
                                    openGameEnd();
                                }

                            }
                        });
                        CommonUtility.requestNewInterstitial(mInterstitialAd, context);*/
                                                  //clear answer string
                                                  savePref.saveUserAnswer("");

                                                  if (showResultflag == 1) {//
                                                      CommonUtility.fetchResult(context, readPref.getUID(), String.valueOf(data.getGameId()), TriviaConstants.RESULT_SCREEN);
                                                  } else if (showResultflag == 2) {

                                                  }
                                                  if (QuizScreen.pd != null)
                                                      QuizScreen.pd.dismiss();
                                              } else {
                                                  //APi response status is 0
                                                  if (QuizScreen.pd != null)
                                                      QuizScreen.pd.dismiss();
                                                  savePref.saveUserAnswer("");
                                                  if (QuizScreen.myCountDownTimer != null) {
                                                      QuizScreen.myCountDownTimer.cancel();
                                                  }

                                                  if(QuizScreen.options_list!=null){
                                                      enableDisableView(QuizScreen.options_list,false);
                                                  }
                                                  if(QuizScreen.options_grid!=null){
                                                      enableDisableView(QuizScreen.options_grid,false);
                                                  }
                                                  StartQuiz.showErrorBar(data.getMessage());
                                                  //CommonUtility.showCloseErrorAlert(context, data.getMessage());
                                              }

                                              if (!CommonUtility.haveNetworkConnection(context)) {
                                                  CommonUtility.saveAnswerMap(context, currentGameId);
                                              } else {
                                                  savePref.saveUserAnswer("");
                                              }
                                              //if (showResultflag == 0) {
                                              //}
                                          } else {

                                              //API Response failed
                                              if (QuizScreen.pd != null)
                                                  QuizScreen.pd.dismiss();
                                              savePref.saveUserAnswer("");
                                              if (QuizScreen.myCountDownTimer != null) {
                                                  QuizScreen.myCountDownTimer.cancel();
                                              }
                                              if(QuizScreen.options_list!=null){
                                                  enableDisableView(QuizScreen.options_list,false);
                                              }
                                              if(QuizScreen.options_grid!=null){
                                                  enableDisableView(QuizScreen.options_grid,false);
                                              }
                                              StartQuiz.showErrorBar(response.message());

                                             // CommonUtility.showCloseErrorAlert(context, response.message());
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

                                      if (QuizScreen.myCountDownTimer != null) {
                                          QuizScreen.myCountDownTimer.cancel();
                                      }
                                      if(QuizScreen.options_list!=null){
                                          enableDisableView(QuizScreen.options_list,false);
                                      }
                                      if(QuizScreen.options_grid!=null){
                                          enableDisableView(QuizScreen.options_grid,false);
                                      }
                                      CommonUtility.saveAnswerMap(context, currentGameId);
                                      CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                              ERROR_FAILURE,
                                              new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      SubmitAnswers(context, currentGameId, showResultflag);
                                                      dialog.dismiss();
                                                      if (QuizScreen.myCountDownTimer != null) {
                                                          QuizScreen.myCountDownTimer.cancel();
                                                      }
                                                  }
                                              }, new DialogInterface.OnClickListener() {

                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      dialog.dismiss();

                                                      if (ResultScreen.context != null) {
                                                          ResultScreen.context.finish();
                                                      }
                                                     /* if (StartQuiz.activity != null) {
                                                          StartQuiz.activity.finish();
                                                      }*/
                                                      if (context != null) {
                                                          context.finish();
                                                      }
                                                  }
                                              });
                                  }
                              }

        );
    }

    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }

    /**
     * Get leaderboard data callback
     *
     * @param context
     * @param map
     * @param isRefreshAdapter
     * @param mode
     */

    public static void fetchLeaderBoard(final Context context, final HashMap<String, String> map, final int isRefreshAdapter, final String mode) {
        Log.d("fetchLeaderBoard called", "-----------------");

        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        leaderboardItemsCall = apiService.getLeaderboard(map);
        CommonUtility.printHashmap(map);

        leaderboardItemsCall.enqueue(new Callback<LeaderboardItems>() {
                                         @Override
                                         public void onResponse(Call<LeaderboardItems> call, Response<LeaderboardItems> response) {

                                             Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                             Log.d("response message", response.message());
                                             leaderboardItemses = new LeaderboardItems();
                                             if (response.isSuccessful()) {
                                                 LeaderboardItems items = response.body();
                                                 if (items.getStatus() == SUCCESS_RESPONSE) {
                                                     /*Leaderboard.PlaceholderFragment.adapter.updateItem(new ArrayList<LeaderboardItems.Rankings>());
                                                     Leaderboard.PlaceholderFragment.adapter.notifyDataSetChanged();*/

                                                     leaderboardItemses = new LeaderboardItems();


                                                   /*  Bundle bundle = new Bundle();
                                                     bundle.putSerializable("data", items);*/

                                                    /* if (isRefreshAdapter == DEFAULT_ZERO) {
                                                         leaderboardItemses = items;

                                                     } else {*/

                                                     List<LeaderboardItems.Rankings> rankingsList = new ArrayList<>();
                                                     List<LeaderboardItems.Myrank> myranking = new ArrayList<>();
                                                     if (items != null) {
                                                         rankingsList = items.getRankings();
                                                         myranking = items.getMyrank();
                                                     }


                                                     leaderboardItemses = items;
                                                     Leaderboard_New.PlaceholderFragment.initUI();
                                                     //  Leaderboard_New.PlaceholderFragment.adapter.updateItem(rankingsList);
                                                     //Leaderboard_New.PlaceholderFragment.adapter.notifyDataSetChanged();

                                                     //}
                                                     if (Leaderboard_New.PlaceholderFragment.progressLayout != null) {
                                                         Leaderboard_New.PlaceholderFragment.progressLayout.showContent();
                                                     }
                                                 } else {
                                                     CommonUtility.showMessageAlert(context, items.getMessage());

                                                 }
                                             } else {
                                                 ((AppCompatActivity) context).finish();
                                             }
                                         }

                                         @Override
                                         public void onFailure(Call<LeaderboardItems> call, Throwable t) {
                                             t.printStackTrace();
                                             CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                                     ERROR_FAILURE,
                                                     new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             fetchLeaderBoard(context, map, isRefreshAdapter, mode);
                                                             dialog.dismiss();
                                                         }
                                                     }, new DialogInterface.OnClickListener() {

                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             dialog.dismiss();
                                                             if (Leaderboard_New.activity != null) {
                                                                 Leaderboard_New.activity.finish();
                                                             }
                                                         }
                                                     });
                                         }
                                     }

        );
    }

    /**
     * Get leaderboard data callback
     *
     * @param context
     * @param map
     * @param isRefreshAdapter
     * @param mode
     */
    public static void fetchLeaderBoardMonthly(final Context context, final HashMap<String, String> map, final int isRefreshAdapter, final String mode) {
        Log.d("fetchLeaderBoard called", "-----------------");

        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        leaderboardItemsCall = apiService.getLeaderboard(map);
        CommonUtility.printHashmap(map);

        leaderboardItemsCall.enqueue(new Callback<LeaderboardItems>() {
                                         @Override
                                         public void onResponse(Call<LeaderboardItems> call, Response<LeaderboardItems> response) {

                                             Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                             Log.d("response message", response.message());
                                             leaderboardItemses = new LeaderboardItems();
                                             if (response.isSuccessful()) {
                                                 LeaderboardItems items = response.body();
                                                 if (items.getStatus() == SUCCESS_RESPONSE) {

                                                    /* MonthlyLeaderboard.monthlyAdapter.updateItem(new ArrayList<LeaderboardItems.Rankings>());
                                                     MonthlyLeaderboard.monthlyAdapter.notifyDataSetChanged();*/
                                                     monthlyLeaderboard = new LeaderboardItems();

                                                   /*  Bundle bundle = new Bundle();
                                                     bundle.putSerializable("data", items);*/

                                                     if (isRefreshAdapter == DEFAULT_ZERO) {
                                                         monthlyLeaderboard = items;
                                                         Intent intent = new Intent(context, Leaderboard_New.class);
                                                         CommonUtility.showActivity(context, "screen", 0, false, intent, null);
                                                     } else {

                                                         List<LeaderboardItems.Rankings> rankingsList = new ArrayList<>();
                                                         List<LeaderboardItems.Myrank> myranking = new ArrayList<>();
                                                         if (items != null) {
                                                             rankingsList = items.getRankings();
                                                             myranking = items.getMyrank();
                                                         }


                                                         monthlyLeaderboard = items;
                                                         // MonthlyLeaderboard.monthlyAdapter.notifyDataSetChanged();
                                                         // Leaderboard_New.mMonthStartDate = String.valueOf(System.currentTimeMillis() / 1000);

                                                         MonthlyLeaderboard.initUI();
                                                     }
                                                     if (MonthlyLeaderboard.progressLayout != null) {
                                                         MonthlyLeaderboard.progressLayout.showContent();
                                                     }
                                                 } else {
                                                     CommonUtility.showMessageAlert(context, items.getMessage());

                                                 }

                                             } else {
                                                 ((AppCompatActivity) context).finish();
                                             }
                                         }

                                         @Override
                                         public void onFailure(Call<LeaderboardItems> call, Throwable t) {
                                             t.printStackTrace();
                                             CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                                     ERROR_FAILURE,
                                                     new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             fetchLeaderBoardMonthly(context, map, isRefreshAdapter, mode);
                                                             dialog.dismiss();
                                                         }
                                                     }, new DialogInterface.OnClickListener() {

                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             dialog.dismiss();
                                                             if (Leaderboard_New.activity != null) {
                                                                 Leaderboard_New.activity.finish();
                                                             }
                                                         }
                                                     });
                                         }
                                     }

        );
    }


    /**
     * Get leaderboard data callback
     *
     * @param context
     * @param map
     * @param isRefreshAdapter
     * @param mode
     */
    public static void fetchLeaderBoardWeekly(final Context context, final HashMap<String, String> map, final int isRefreshAdapter, final String mode) {
        Log.d("fetchLeaderBoard called", "------------");

        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        leaderboardItemsCall = apiService.getLeaderboard(map);
        CommonUtility.printHashmap(map);

        leaderboardItemsCall.enqueue(new Callback<LeaderboardItems>() {
                                         @Override
                                         public void onResponse(Call<LeaderboardItems> call, Response<LeaderboardItems> response) {

                                             Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                             Log.d("response message", response.message());

                                             if (response.isSuccessful()) {
                                                 LeaderboardItems items = response.body();
                                                 if (items.getStatus() == SUCCESS_RESPONSE) {
                                                    /* WeekLeaderboard.weeklyAdapter.updateItem(new ArrayList<LeaderboardItems.Rankings>());
                                                     WeekLeaderboard.weeklyAdapter.notifyDataSetChanged();*/
                                                     weeklyLeaderboard = new LeaderboardItems();

                                                   /*  Bundle bundle = new Bundle();
                                                     bundle.putSerializable("data", items);*/

                                                     if (isRefreshAdapter == DEFAULT_ZERO) {
                                                         weeklyLeaderboard = items;
                                                         Intent intent = new Intent(context, Leaderboard_New.class);
                                                         CommonUtility.showActivity(context, "screen", 0, false, intent, null);
                                                     } else {

                                                         List<LeaderboardItems.Rankings> rankingsList = new ArrayList<>();
                                                         List<LeaderboardItems.Myrank> myranking = new ArrayList<>();
                                                         if (items != null) {
                                                             rankingsList = items.getRankings();
                                                             myranking = items.getMyrank();
                                                         }


                                                         weeklyLeaderboard = items;


                                                         // WeekLeaderboard.weeklyAdapter.notifyDataSetChanged();
                                                         WeekLeaderboard.initUI();
                                                     }
                                                     if (WeekLeaderboard.progressLayout != null) {

                                                         WeekLeaderboard.progressLayout.showContent();
                                                     }
                                                 } else {
                                                     CommonUtility.showMessageAlert(context, items.getMessage());

                                                 }
                                             } else {
                                                 ((AppCompatActivity) context).finish();
                                             }
                                         }

                                         @Override
                                         public void onFailure(Call<LeaderboardItems> call, Throwable t) {
                                             t.printStackTrace();
                                             CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                                     ERROR_FAILURE,
                                                     new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             fetchLeaderBoardWeekly(context, map, isRefreshAdapter, mode);
                                                             dialog.dismiss();
                                                         }
                                                     }, new DialogInterface.OnClickListener() {

                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             dialog.dismiss();
                                                             if (Leaderboard_New.activity != null) {
                                                                 Leaderboard_New.activity.finish();
                                                             }
                                                         }
                                                     });
                                         }
                                     }

        );
    }

    public static LeaderboardItems returnLeaderboardData() {
        return leaderboardItemses;
    }

    public static LeaderboardItems returnWeekLeaderboardData() {
        return weeklyLeaderboard;
    }

    public static LeaderboardItems returnMonthLeaderboardData() {
        return monthlyLeaderboard;
    }


    /**
     * REsult User callback
     *
     * @param context
     * @param map
     */
    public static void userResult(final Context context, final HashMap<String, String> map) {
        Log.d("userResult called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        resultItemsCall = apiService.getGameResult(map);
        CommonUtility.printHashmap(map);

        resultItemsCall.enqueue(new Callback<ResultItems>() {
                                    @Override
                                    public void onResponse(Call<ResultItems> call, Response<ResultItems> response) {

                                        Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                        Log.d("response message", response.message());
                                        if (response.isSuccessful()) {
                                            try {
                                                ResultItems items = response.body();
                                                if (items.getStatus() == SUCCESS_RESPONSE) {
                                                    boolean isGameCalled = false;
                                                    //api call is successful
                                                    //open result page
                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("data", items);
                                                    resultItems = items;

                                                    ResultScreen.openResultFragment();

                                                } else {
                                                    CommonUtility.showMessageAlert(context, RESULT_NOT_OUT);

                                                }

                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } else {

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultItems> call, Throwable t) {
                                        t.printStackTrace();
                                        CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                                ERROR_FAILURE,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        userResult(context, map);
                                                        dialog.dismiss();
                                                    }
                                                }, new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if (ResultScreen.context != null) {
                                                            ResultScreen.context.finish();
                                                        }
                                                    }
                                                });
                                    }
                                }

        );


    }

    public static ResultItems getResultItems() {
        return resultItems;
    }

    /**
     * Prizes callback
     *
     * @param context
     * @param map
     */
    public static void triviaPrizes(final Context context, final HashMap<String, String> map) {
        String mode = "";
        Log.d("triviaPrizes called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        prizesItemsCall = apiService.getPrizesList(map);
        CommonUtility.printHashmap(map);
        mode = map.get(PARAM_MODE);
        final String finalMode = mode;
        prizesItemsCall.enqueue(new Callback<PrizesItems>() {

                                    @Override
                                    public void onResponse(Call<PrizesItems> call, Response<PrizesItems> response) {

                                        Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                        Log.d("response message", response.message());
                                        if (response.isSuccessful()) {
                                            try {
                                                PrizesItems items = response.body();


                                                if (items.getStatus() == SUCCESS_RESPONSE) {


                                                    //api call is successful
                                                    if (finalMode.equals(MODE_DAILY)) {
                                                        prizesItems = items;
                                                    } else if (finalMode.equals(MODE_WEEKLY)) {
                                                        prizesItemsWeekly = items;
                                                    } else if (finalMode.equals(MODE_MONTHLY)) {
                                                        prizesItemsMonthy = items;
                                                    }

                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("data", items);


                                                    //api call is successful
                                                    if (finalMode.equals(MODE_DAILY)) {
                                                        PrizesDaily.initUI();
                                                    } else if (finalMode.equals(MODE_WEEKLY)) {
                                                        PrizesWeek.initUI();
                                                    } else if (finalMode.equals(MODE_MONTHLY)) {
                                                        PrizesMonth.initUI();
                                                    }

                                                } else {
                                                    CommonUtility.showMessageAlert(context, NEW_GAME_FAILURE);

                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }


                                    @Override
                                    public void onFailure(Call<PrizesItems> call, Throwable t) {
                                        t.printStackTrace();
                                        CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                                ERROR_FAILURE,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        triviaPrizes(context, map);
                                                        dialog.dismiss();
                                                    }
                                                }, new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                        if (PrizesActivity.activity != null) {
                                                            PrizesActivity.activity.finish();
                                                        }
                                                    }
                                                });
                                    }
                                }

        );


    }

    public static PrizesItems getPrizesItems() {
        return prizesItems;
    }

    public static PrizesItems getPrizesItemsWeekly() {
        return prizesItemsWeekly;
    }

    public static PrizesItems getPrizesItemsMonthy() {
        return prizesItemsMonthy;
    }


    /**
     * Categories callback
     *
     * @param context
     * @param map
     */
    public static void fetchCategories(final Context context, HashMap<String, String> map) {
        Log.d("fetchCategories called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        categoriesItemsCall = apiService.getAllCategories(map);
        CommonUtility.printHashmap(map);

        categoriesItemsCall.enqueue(new Callback<CategoriesItems>() {
                                        @Override
                                        public void onResponse(Call<CategoriesItems> call, Response<CategoriesItems> response) {

                                            Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                            Log.d("response message", response.message());
                                            if (response.isSuccessful()) {
                                                try {
                                                    CategoriesItems items = response.body();
                                                    if (items.getStatus() == SUCCESS_RESPONSE) {


                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CategoriesItems> call, Throwable t) {
                                            t.printStackTrace();
                                            CommonUtility.showErrorAlert(context, TRY_LATER);
                                        }
                                    }

        );


    }


    /**
     * PlayedGame  callback
     *
     * @param context
     * @param map
     */
    public static void fetchPlayedGame(final Context context, final HashMap<String, String> map) {
        Log.d("fetchPlayedGame called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        playedGameCall = apiService.getPlayedGame(map);
        CommonUtility.printHashmap(map);

        playedGameCall.enqueue(new Callback<PlayedGame>() {
                                   @Override
                                   public void onResponse(Call<PlayedGame> call, Response<PlayedGame> response) {

                                       Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                       Log.d("response message", response.message());
                                       if (response.isSuccessful()) {
                                           try {
                                               PlayedGame items = response.body();
                                               if (items.getStatus() == SUCCESS_RESPONSE) {

                                                   //api call is successful

                                                   Bundle bundle = new Bundle();
                                                   bundle.putSerializable("data", items);
                                                   playedGame = items;

                                                   AnswersActivity.initUI();
                                               }
                                           } catch (Exception e) {
                                               e.printStackTrace();
                                           }
                                       }
                                   }

                                   @Override
                                   public void onFailure(Call<PlayedGame> call, Throwable t) {
                                       t.printStackTrace();
                                       CommonUtility.showAlertRetryCancel(context, ALERT_TITLE,
                                               ERROR_FAILURE,
                                               new DialogInterface.OnClickListener() {
                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       fetchPlayedGame(context, map);
                                                       dialog.dismiss();
                                                   }
                                               }, new DialogInterface.OnClickListener() {

                                                   @Override
                                                   public void onClick(DialogInterface dialog, int which) {
                                                       dialog.dismiss();
                                                       if (AnswersActivity.activity != null) {
                                                           AnswersActivity.activity.finish();
                                                       }
                                                   }
                                               });
                                   }
                               }

        );


    }

    public static PlayedGame getPlayedGameAnswers() {
        return playedGame;
    }

    /**
     * Game Archive callback
     *
     * @param context
     * @param map
     */
    public static void fetchGameArchive(final Context context, final HashMap<String, String> map) {
        Log.d("fetchGameArchive called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        archiveItemsCall = apiService.getAllGameArchives(map);
        CommonUtility.printHashmap(map);

        archiveItemsCall.enqueue(new Callback<ArchiveItems>() {
                                     @Override
                                     public void onResponse(Call<ArchiveItems> call, Response<ArchiveItems> response) {

                                         Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                         Log.d("response message", response.message());
                                         if (response.isSuccessful()) {
                                             try {
                                                 ArchiveItems items = response.body();
                                                 if (items.getStatus() == SUCCESS_RESPONSE) {
                                                     archiveItems = new ArchiveItems();
                                                     //api call is successful
                                                     archiveItems = items;
                                                     Bundle bundle = new Bundle();
                                                     bundle.putSerializable("data", items);
                                                     //todo answes screen open
                                                     GameArchive.initUI();

                                                 }
                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }
                                         }
                                     }

                                     @Override
                                     public void onFailure(Call<ArchiveItems> call, Throwable t) {
                                         t.printStackTrace();
                                         if (GameArchive.activity != null) {
                                             CommonUtility.showAlertRetryCancel(GameArchive.activity, ALERT_TITLE,
                                                     ERROR_FAILURE,
                                                     new DialogInterface.OnClickListener() {
                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             fetchGameArchive(context, map);
                                                             dialog.dismiss();
                                                         }
                                                     }, new DialogInterface.OnClickListener() {

                                                         @Override
                                                         public void onClick(DialogInterface dialog, int which) {
                                                             dialog.dismiss();
                                                             if (GameArchive.activity != null) {
                                                                 GameArchive.activity.finish();
                                                             }
                                                         }
                                                     });
                                         }

                                     }
                                 }

        );


    }

    public static ArchiveItems getArchiveItems() {
        return archiveItems;
    }


    public static void notifyResult(final Context context, final HashMap<String, String> map) {
        Log.d("notifyResult called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        notifyResultCall = apiService.notifyResult(map);
        CommonUtility.printHashmap(map);

        notifyResultCall.enqueue(new Callback<NotifyResult>() {
                                     @Override
                                     public void onResponse(Call<NotifyResult> call, Response<NotifyResult> response) {

                                         Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                         Log.d("response message", response.message());
                                         if (response.isSuccessful()) {
                                             try {
                                                 NotifyResult items = response.body();
                                                 if (items.getStatus() == SUCCESS_RESPONSE) {
                                                     //todo

                                                 }
                                             } catch (Exception e) {
                                                 e.printStackTrace();
                                             }
                                         }
                                     }

                                     @Override
                                     public void onFailure(Call<NotifyResult> call, Throwable t) {
                                         t.printStackTrace();


                                     }
                                 }

        );


    }

    public static void fetchFaqQuestions(final Context context, final HashMap<String, String> map) {
        Log.d("notifyResult called", "-----------------");
        savePref = new SavePref(context);
        apiRetroFit = new ApiRetroFit();
        apiService = apiRetroFit.getApiService();
        faqPojoCall = apiService.faqQuestions(map);
        CommonUtility.printHashmap(map);

        faqPojoCall.enqueue(new Callback<FaqPojo>() {
                                @Override
                                public void onResponse(Call<FaqPojo> call, Response<FaqPojo> response) {

                                    Log.d("response.isSuccess", String.valueOf(response.isSuccessful()));
                                    Log.d("response message", response.message());
                                    if (response.isSuccessful()) {
                                        try {
                                            FaqPojo items = response.body();
                                            if (items.getStatus() == SUCCESS_RESPONSE) {

                                                faqList = items.getFaq();
                                                FAQFragment.initUI();

                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<FaqPojo> call, Throwable t) {
                                    t.printStackTrace();


                                }
                            }

        );


    }

    public static List<FaqPojo.Faq> getfaqList() {
        return faqList;
    }
}
