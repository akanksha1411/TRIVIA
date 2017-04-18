package toi.com.trivia.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import toi.com.trivia.utility.TriviaConstants;

/**
 * Created by akanksha on 9/8/16.
 */
public class ReadPref {

    String myprefs = TriviaConstants.PREF_NAME;
    int mode = Activity.MODE_PRIVATE;
    boolean result = false;
    String TAG = "ReadPref";
    Context ctx;
    String res = "";
    private SharedPreferences prefs;
    String defaultString = "";
    int defaultInt = 0;
    boolean defaultBool = false;

    public ReadPref(Context ctx) {
        this.ctx = ctx;
        prefs = this.ctx.getSharedPreferences(myprefs, mode);
    }

    public String getScreenBackground() {
        res = "";
        res = prefs.getString(TriviaConstants.BACKGROUND_SCREEN, defaultString);

        return res;
    }

    public Boolean getIsLiveCode() {
        Boolean res = false;
        res = prefs.getBoolean(TriviaConstants.IS_LIVE_CODE, res);

        return res;
    }

    public String getRegStatus() {
        res = "";
        res = prefs.getString(TriviaConstants.REG_STATUS, String.valueOf(defaultInt));

        return res;
    }

    public Boolean getIsGameKilled() {
        Boolean res = false;
        res = prefs.getBoolean(TriviaConstants.IS_GAME_KILLED, res);

        return res;
    }

    public String getNotificationTitle() {
        res = "";
        res = prefs.getString(TriviaConstants.NOTIFICATION_TITLE, defaultString);

        return res;
    }

    public String getUserName() {
        res = "";
        res = prefs.getString(TriviaConstants.PARAM_NAME, TriviaConstants.DEFAULT_NAME);

        return res;
    }

    public String getUserImage() {
        res = "";
        res = prefs.getString(TriviaConstants.PARAM_PROFILE_IMG, "");

        return res;
    }

    public String getUQID() {
        res = "";
        res = prefs.getString(TriviaConstants.PARAM_UQID, defaultString);

        return res;
    }

    public String getLoginType() {
        res = "";
        res = prefs.getString(TriviaConstants.PARAM_LOGIN_TYPE, "F");

        return res;
    }

    public String getLoginId() {
        res = "";
        res = prefs.getString(TriviaConstants.PARAM_LOGIN_ID, defaultString);

        return res;
    }

    public String getResultViewedGameId() {
        res = "";
        res = prefs.getString(TriviaConstants.RESULT_VIEWED_GAME_ID, String.valueOf(defaultInt));

        return res;
    }


    public String getLoginToken() {
        res = "";
        res = prefs.getString(TriviaConstants.PARAM_LOGIN_TOKEN, defaultString);

        return res;
    }

    public String getUID() {
        res = "";
        res = prefs.getString(TriviaConstants.PARAM_UID, String.valueOf("0"));

        return res;
    }

    public String getDefaultDate() {
        res = "";
        res = prefs.getString(TriviaConstants.LEADERBOARD_DATE, String.valueOf(System.currentTimeMillis() / 1000));

        return res;
    }

    public String getSelectedOption() {
        res = "";
        res = prefs.getString(TriviaConstants.SELECTED_OPTION, String.valueOf(0));

        return res;
    }

    public String getUserAnswer() {
        res = "";
        res = prefs.getString(TriviaConstants.USER_ANSWER, defaultString);

        return res;
    }

    public String getUserEmail() {
        res = "";
        res = prefs.getString(TriviaConstants.USER_EMAIL, defaultString);

        return res;
    }

    public String getCurrentGameId() {
        String res = "0";
        res = prefs.getString(TriviaConstants.CURRENT_GAME_ID, "0"/*hardcoded value to be removed when API starts*/);

        return res;
    }

    public Boolean getIsDirCreated() {
        Boolean res = false;
        res = prefs.getBoolean(TriviaConstants.IS_DIR_CREATED, res/*hardcoded value to be removed when API starts*/);

        return res;
    }

    public String getArchiveGameId() {
        String res = "0";
        res = prefs.getString(TriviaConstants.ARCHIEVE_GAME_ID, "0"/*hardcoded value to be removed when API starts*/);

        return res;
    }


    public String getResultGameId() {
        res = "0";
        res = prefs.getString(TriviaConstants.RESULT_GAME_ID, "0"/*hardcoded value to be removed when API starts*/);

        return res;
    }

    public String getNextGameId() {
        res = "";
        res = prefs.getString(TriviaConstants.NEXT_GAME_ID, String.valueOf(defaultInt));

        return res;
    }


    public String getIsLoggedIn() {
        res = prefs.getString(TriviaConstants.PARAM_ISLOGGEDIN, String.valueOf(defaultInt));

        return res;
    }

    public String getUserGameCount() {
        res = prefs.getString(TriviaConstants.PARAM_COUNT, String.valueOf(defaultInt));

        return res;
    }


    public String getSponsorName() {
        res = prefs.getString(TriviaConstants.SPONSOR_NAME, String.valueOf(defaultString));

        return res;
    }

    public String getBackPressTime() {
        res = prefs.getString(TriviaConstants.BACK_PRESS_TIME, String.valueOf(defaultString));

        return res;
    }

    public Boolean getIsFirstTime() {
        Boolean res = false;
        res = prefs.getBoolean(TriviaConstants.IS_FIRST_TIME, true);

        return res;
    }

    public Boolean getIsReadyShown() {
        Boolean res = false;
        res = prefs.getBoolean(TriviaConstants.READY_SHOWN, false);

        return res;
    }

    public int getCurrentPosition() {
        int res = 0;
        res = prefs.getInt(TriviaConstants.CURRENT_POSITION, defaultInt /*hardcoded value to be removed when API starts*/);

        return res;
    }

    public String getReferenceUids() {
        res = "";
        res = prefs.getString(TriviaConstants.CURRENT_POSITION, res /*hardcoded value to be removed when API starts*/);

        return res;
    }

    public String getAnsBack() {
        res = "";
        res = prefs.getString(TriviaConstants.ANS_BACK, defaultString);

        return res;
    }

    public String getLastArchivePointer() {
        res = "";
        res = prefs.getString(TriviaConstants.LAST_ARCHIVE_POINTER, String.valueOf(defaultInt));

        return res;
    }

    public Boolean getIsGameEnded() {
        Boolean res = false;
        res = prefs.getBoolean(TriviaConstants.IS_GAME_ENDED, res);

        return res;
    }

    public String getGameStart() {
        res = "";
        res = prefs.getString(TriviaConstants.GAME_START, defaultString);

        return res;
    }

    public String getResultOpen() {
        res = "";
        res = prefs.getString(TriviaConstants.RESULT_OPEN, defaultString);

        return res;
    }

    public String getBotAd() {
        res = "";
        res = prefs.getString(TriviaConstants.BOT_AD, defaultString);

        return res;
    }

    public Boolean isGameCalled() {
        boolean isGame;
        isGame = prefs.getBoolean(TriviaConstants.IS_GAME_CALLED, defaultBool);
        Log.d("Pranav","IS_GAME_CALLED returned as  ["+ isGame + "]");

        return isGame;
    }


}
