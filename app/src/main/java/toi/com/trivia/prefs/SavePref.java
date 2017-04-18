package toi.com.trivia.prefs;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import toi.com.trivia.utility.TriviaConstants;

/**
 * Created by akanksha on 9/8/16.
 */
public class SavePref {

    static boolean result = false;
    static String TAG = "SavePref";
    String myprefs = TriviaConstants.PREF_NAME;
    int mode = Activity.MODE_PRIVATE;
    Context ctx;
    private SharedPreferences prefs;

    public SavePref(Context ctx) {
        this.ctx = ctx;
        prefs = this.ctx.getSharedPreferences(myprefs, mode);
    }

    static void printlog() {
        Log.i(TAG, Boolean.toString(result));
    }


    /**
     * Save screen background url for complete application
     *
     * @param res
     */
    public void saveScreenBackground(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.BACKGROUND_SCREEN, res);

        result = editor.commit();
        printlog();
    }

    /**
     * Save Leaderboard default date for complete application
     *
     * @param res
     */
    public void saveDefaultDate(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.LEADERBOARD_DATE, res);

        result = editor.commit();
        printlog();
    }


    /**
     * Save sponsor name for complete application
     *
     * @param res
     */
    public void saveSponsorName(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.SPONSOR_NAME, res);

        result = editor.commit();
        printlog();
    }

    /**
     * Save back press time i.e when user pressed game and quiz stoped
     *
     * @param res
     */
    public void saveBackPressTime(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.BACK_PRESS_TIME, res);

        result = editor.commit();
        printlog();
    }

    /**
     * Save whether directory is present or not
     *
     * @param res
     */
    public void saveIsDirCreated(Boolean res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(TriviaConstants.IS_DIR_CREATED, res);

        result = editor.commit();
        printlog();
    }


    /**
     * Save screen background url for complete application
     *
     * @param res
     */
    public void saveUserGameCount(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_COUNT, res);

        result = editor.commit();
        printlog();
    }


    /**
     * Save screen background url for complete application
     *
     * @param res
     */
    public void saveUserEmail(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.USER_EMAIL, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves the notification title to be displayed on in app notification
     *
     * @param res
     */
    public void saveNotificationTitle(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.NOTIFICATION_TITLE, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves player name
     *
     * @param res
     */
    public void saveUserName(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_NAME, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves player name
     *
     * @param res
     */
    public void saveUserImage(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_PROFILE_IMG, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves player unique device id
     *
     * @param res
     */
    public void saveUQID(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_UQID, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves loginType corresponding login ID
     *
     * @param res
     */
    public void saveLoginId(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_LOGIN_ID, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves UID FOR player
     *
     * @param res
     */
    public void saveUID(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_UID, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves login token
     *
     * @param res
     */
    public void saveLoginToken(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_LOGIN_TOKEN, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves current active game id
     *
     * @param res
     */
    public void saveCurrentGameId(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.CURRENT_GAME_ID, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves archieve game id
     *
     * @param res
     */
    public void saveArchieveGameId(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.ARCHIEVE_GAME_ID, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves last result declared game id
     *
     * @param res
     */
    public void saveLastResultGameId(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.RESULT_VIEWED_GAME_ID, res);

        result = editor.commit();
        printlog();
    }


    /**
     * saves next game id
     *
     * @param res
     */
    public void saveNextGameId(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.NEXT_GAME_ID, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves next game id
     *
     * @param res
     */
    public void saveResultGameId(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.RESULT_GAME_ID, res);

        result = editor.commit();
        printlog();
    }


    /**
     * is true if the user is logged in through any medium
     *
     * @param res
     */
    public void isLoggedIn(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_ISLOGGEDIN, res);

        result = editor.commit();
        printlog();
    }

    /**
     * is true if the user is first time opening in through any medium
     *
     * @param res
     */
    public void isFirstTime(Boolean res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(TriviaConstants.IS_FIRST_TIME, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves selection option of the question
     *
     * @param res
     */
    public void saveSelectedOption(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.SELECTED_OPTION, res);

        result = editor.commit();
        printlog();
    }

    /**
     * is true if the user is first time opening in through any medium
     *
     * @param res
     */
    public void saveRegStatus(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.REG_STATUS, res);

        result = editor.commit();
        printlog();
    }


    /**
     * Login type of user f=Facebook, s=SSO, t=twitter.
     * In case isLoggedIn flat is on this field is mandatory.
     * In case user is connected with SSO pass SSO s, in case we connect with facebook pass f.
     *
     * @param res
     */
    public void saveLoginType(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_LOGIN_TYPE, res);

        result = editor.commit();
        printlog();
    }

    /**
     * Is set to true when ready button is pressed by the user. Shown only for first time.
     *
     * @param res
     */
    public void isReadyShown(Boolean res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(TriviaConstants.READY_SHOWN, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves current quiz question position
     *
     * @param res
     */
    public void saveCurrentPosition(int res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(TriviaConstants.CURRENT_POSITION, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves value for build is with toi or sdk
     *
     * @param res
     */
    public void saveIsLiveCode(Boolean res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(TriviaConstants.IS_LIVE_CODE, res);

        result = editor.commit();
        printlog();
    }


    /**
     * saves current quiz question position
     *
     * @param res
     */
    public void saveReferenceUids(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.REFERENCE_UIDS, res);

        result = editor.commit();
        printlog();
    }


    /**
     * saves current quiz question position
     *
     * @param res
     */
    public void saveReferenceIMEI(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.REFERENCE_UIDS, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves the flag if the quiz has ended or not
     *
     * @param res
     */
    public void saveIsGameEnded(Boolean res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putBoolean(TriviaConstants.IS_GAME_ENDED, res);

        result = editor.commit();
        printlog();
    }

    /**
     * saves current user answered map
     *
     * @param res
     */
    public void saveUserAnswer(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.USER_ANSWER, res);

        result = editor.commit();
        printlog();
    }


    /**
     * saves the last selected position of archive list
     *
     * @param res
     */
    public void saveLastArchivePointer(String res) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.LAST_ARCHIVE_POINTER, res);

        result = editor.commit();
        printlog();
    }


    /**
     * Save ads unit ids for app slots
     *
     * @param ans_back
     * @param game_start
     * @param result_open
     * @param bot_ad
     */
    public void saveAdsUnits(String ans_back, String game_start, String result_open, String bot_ad) {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.ANS_BACK, ans_back);
        editor.putString(TriviaConstants.GAME_START, game_start);
        editor.putString(TriviaConstants.RESULT_OPEN, result_open);
        editor.putString(TriviaConstants.BOT_AD, bot_ad);

        result = editor.commit();
        printlog();
    }

    /**
     * Clear necessary pref for logout to happen
     */
    public void logoutClearData() {
        result = false;

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(TriviaConstants.PARAM_UID, "0");
        editor.putString(TriviaConstants.PARAM_LOGIN_ID, "0");
        editor.putString(TriviaConstants.PARAM_PROFILE_IMG, "");
        editor.putString(TriviaConstants.PARAM_NAME, "");
        editor.putString(TriviaConstants.PARAM_COUNT, "0");
        editor.putString(TriviaConstants.REG_STATUS, "0");
        editor.putBoolean(TriviaConstants.IS_FIRST_TIME, true);
        editor.putString(TriviaConstants.LAST_ARCHIVE_POINTER, "0");
        editor.putBoolean(TriviaConstants.IS_GAME_CALLED, false);
        editor.putBoolean(TriviaConstants.IS_GAME_KILLED, false);
        editor.putBoolean(TriviaConstants.IS_GAME_ENDED, false);
        //editor.clear();
        result = editor.commit();
        printlog();
    }

    public void saveIsGameCalled(boolean res) {
        result = false;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(TriviaConstants.IS_GAME_CALLED, res);
        result = editor.commit();
        Log.d("Pranav","IS_GAME_CALLED set to ["+ res + "]");
        printlog();
    }

    public void saveIsGameKilled(boolean res) {
        result = false;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(TriviaConstants.IS_GAME_KILLED, res);
        result = editor.commit();
        printlog();
    }

}
