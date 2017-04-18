package toi.com.trivia.api;


import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
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
import toi.com.trivia.utility.TriviaConstants;

public interface APIService {

    @POST(TriviaConstants.ARCHIVE_URL)
        //POST archives api call
    Call<ArchiveItems> getAllGameArchives(@Body HashMap<String, String> map);

    @POST(TriviaConstants.RESULT_URL)
        //get current game result api call
    Call<ResultItems> getGameResult(@Body HashMap<String, String> map);

    @POST(TriviaConstants.HOME_URL)
        //get current game dashboard api call
    Call<HomeItems> getDashboard(@Body HashMap<String, String> map);

    @POST(TriviaConstants.LEADERBOARD_URL)
        //get user leaderboard api call
    Call<LeaderboardItems> getLeaderboard(@Body HashMap<String, String> map);

    @POST(TriviaConstants.NEW_GAME_URL)
        //get user new game available api call
    Call<NewGame> getNewGame(@Body HashMap<String, String> map);

    @POST(TriviaConstants.PLAYED_GAME_URL)
        //get user played_game details api call
    Call<PlayedGame> getPlayedGame(@Body HashMap<String, String> map);

    @POST(TriviaConstants.PRIZES_URL)
        //get user prizes details api call
    Call<PrizesItems> getPrizesList(@Body HashMap<String, String> map);

    @POST(TriviaConstants.CATEGORY_URL)
        //get category details api call
    Call<CategoriesItems> getAllCategories(@Body HashMap<String, String> map);

    @POST(TriviaConstants.REGISTER_URL)
        //For Register operation
    Call<HomeItems> registerUser(
            @Body HashMap<String, String> map);

    @POST(TriviaConstants.SUBMIT_ANSWER)
        //For Register operation
    Call<Contents> submitAnswer(
            @Body HashMap<String, String> map);

    @POST(TriviaConstants.NOTIFY_RESULT)
        //For Register operation
    Call<NotifyResult> notifyResult(
            @Body HashMap<String, String> map);

    @POST(TriviaConstants.FAQ_URL)
        //For Register operation
    Call<FaqPojo> faqQuestions(
            @Body HashMap<String, String> map);

    @Multipart
    @POST(TriviaConstants.REGISTER_URL)
    Call<HomeItems> uploadFileWithPartMap(
            @PartMap() HashMap<String, String> map,
            @Part MultipartBody.Part file);
}
