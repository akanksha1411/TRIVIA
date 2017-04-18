package toi.com.trivia.utility;

/**
 * Created by AKanksha on 9/8/16.
 */
public interface TriviaConstants {


    /**
     * Screens numbers
     */
    int LOGIN_SCREEN = 0;
    int PHONE_NUMBER_SCREEN = 1;
    int OTP_SCREEN = 2;
    int DASHBOARD_SCREEN = 3;
    int HOST_ACTIVITY = 4;
    int ANSWERS_ACTIVITY = 5;
    int GAME_ARCHIVE = 6;
    int LEADERBOARD = 7;
    int RESULT_SCREEN = 8;
    int QUESTION_SCREEN = 9;
    int CATEGORY_PAGE = 10;
    int START_QUIZ_ACTIVITY = 11;
    int GAME_END = 12;
    int NOTIFICATION = 13;
    int FAQ_SCREEN = 14;
    int ABOUT_US_SCREEN = 15;
    int POLICY_SCREEN = 16;
    int RESULT_OUT_SCREEN = 17;
    int GAME_LOADER = 18;
    int PRIZES = 19;
    /**
     * API response status
     */
    int SUCCESS_RESPONSE = 1;
    int FAILURE_RESPONSE = 0;

    /**
     * Alert messages
     */
    String TRY_LATER = "No internet connection found. Game data will sync once online.";
    String NO_GAME_ACTIVE = "There is no active game. Try again Later....";
    String No_INTERNET = "No internet connection found. Game data will sync once online.";
    String RESULT_NOT_OUT = "Cannot open this result, try again later....";
    String PRIZES_NOT_OUT = "Prizes list for current game not available now";
    String NEW_GAME_FAILURE = "Some error occurred, please try again...";
    String ERROR_FAILURE = "Request time out, please retry...";
    String ANS_RECORDED = "Your answer is recorded";
    /**
     * Preference keys
     */
    String BACKGROUND_SCREEN = "background";
    String NOTIFICATION_TITLE = "notificationTitle";
    String READY_SHOWN = "readyShown";
    String CURRENT_GAME_ID = "currentGameId";
    String NEXT_GAME_ID = "nextGameId";
    String IS_FIRST_TIME = "isFirstTime";
    String CURRENT_POSITION = "currentPosition";
    String SELECTED_OPTION = "selectedOption";
    String REFERENCE_UIDS = "referenceUids";
    String REG_STATUS = "regStatus";
    String RESULT_GAME_ID = "resultGameId";
    String SPONSOR_NAME = "sponsorName";
    String ARCHIEVE_GAME_ID = "archieveGameId";
    String RESULT_VIEWED_GAME_ID = "resultViewedGameId";
    String BACK_PRESS_TIME = "backPressTime";
    String IS_LIVE_CODE = "isLiveCode";
    String USER_ANSWER = "user_answer";
    String IS_DIR_CREATED = "is_dir_created";
    String IS_GAME_ENDED = "is_game_ended";
    String LAST_ARCHIVE_POINTER = "last_archive_pointer";
    String USER_EMAIL = "user_email";
    String IS_GAME_CALLED = "is_game_called";
    String IS_GAME_KILLED = "is_game_killed";
    String LEADERBOARD_DATE = "default_date";
    /**
     * Default values
     */
    String DEFAULT_NAME = "";
    int DEFAULT_ZERO = 0;
    int DEFAULT_ONE = 1;
    String SCREEN_TYPE = "screen_type";
    String PREF_NAME = "trivia";
    String DEVICE_TYPE = "A";
    String DEFAULT_LOGIN_TYPE = "s";
    String CATEGORY_PAGE_TITLE = "Question #";
    String PRESENTED_STATE = "2";
    String ANSWERED_STATE = "8";
    String WRONG_POINTS = "0";
    int OPTION_COUNT = 4;
    String FB_LOGIN_TYPE = "F";
    String GOOGLE_LOGIN_TYPE = "G";
    String Archive_Text_Qcount = "Ques.";
    String MODE_DAILY = "D";
    String MODE_WEEKLY = "W";
    String MODE_MONTHLY = "M";
    String MODE_QUIZ = "Q";
    String TOI_SPONSOR_NAME = "toi";
    String FIREWORKS_IMAGE = "fireworks";
    String PARENT_APP = "toi";
    String LOGIN_FB_TYPE = "facebook";
    String LOGIN_GOOGLE_TYPE = "googleplus";
    String QUESTION_ALGO_RANDOM = "R";
    String QUESTION_ALGO_FIXED = "F";
    String ARCHIVE_LIST_COUNT = "32";
    String SPACE = " ";

    /**
     * Api URLS
     */

    String ARCHIVE_URL = "trivia/archive";
    String RESULT_URL = "trivia/result";
    String HOME_URL = "trivia/home";
    String LEADERBOARD_URL = "trivia/leaderboard";
    String NEW_GAME_URL = "trivia/new_game";
    String PLAYED_GAME_URL = "trivia/played_game";
    String PRIZES_URL = "trivia/prizes";
    String CATEGORY_URL = "trivia/category";
    String REGISTER_URL = "users/register";
    String SUBMIT_ANSWER = "trivia/submit";
    String NOTIFY_RESULT = "trivia/notify_result";
    String FAQ_URL = "trivia/faq";

    /**
     * Database Tables names
     */
    String QUESTION_SET_TABLE = " ere_ques_set ";
    String QUESTION_SET_QUES_TABLE = " ere_ques_set_ques ";
    String QUESTIONS_TABLE = " ere_question ";
    String QUESTIONS_OPTION_TABLE = " ere_question_option ";
    String USER_ANS_TABLE = " ere_user_ans";
    String USER_ANS_OPTION_TABLE = " ere_user_ans_option ";


    /**
     * Parameters for post requests
     */
    String PARAM_DEVICE_TYPE = "deviceType";
    String PARAM_UQID = "uqid";
    String PARAM_LOGIN_TYPE = "loginType";
    String PARAM_LOGIN_ID = "loginId";
    String PARAM_LOGIN_TOKEN = "loginToken";
    String PARAM_NAME = "name";
    String PARAM_USER_POST = "post";
    String PARAM_COUNTRY = "country";
    String PARAM_PROFILE_IMG = "image";
    String PARAM_REG_STATUS = "regStatus";
    String PARAM_UID = "uid";
    String PARAM_ISLOGGEDIN = "isLoggedIn";
    String PARAM_GAME_ID = "gameId";
    String PARAM_SUBMIT_TIME = "submissionTime";
    String PARAM_ANSWERS = "answers";
    String PARAM_QUES_ID = "quesId";
    String PARAM_TIME_TAKEN = "timeTaken";
    String PARAM_OPTION_ID = "optId";
    String PARAM_PRESENTED_TIME = "qpresentedtime";
    String PARAM_USER_OPT_SEQ = "userOptSeq";
    String PARAM_QUES_STATE = "qState";
    String PARAM_QUES_SEQ = "userQuesSeq";
    String PARAM_MODE = "mode";
    String PARAM_START_DATE = "sdate";
    String PARAM_END_DATE = "edate";
    String PARAM_QUIZ_ID = "qid";
    String PARAM_COUNT = "count";
    String PARAM_FROM_INDEX = "fromIndex";
    String PARAM_EMAIL = "email";
    String PARAM_NETWORK = "ntw";

    /**
     * GA tags used
     */

    String CLICK = "Click";
    String ENTRY = "Entry ";
    String EXIT = "Exit ";
    String DEVICE_BACK = "Device Back Button";
    String BACK_ARROW = "Back arrow";
    String AUTOLOAD = "Autoload";

    int RESPONSE_LOGIN_CODE = 3597;
    int LOGIN_REQUEST_CODE = 9001;
    String GA_PREFIX = "TriviaAnd ";
    String ALERT_TITLE = "Alert";
    /**
     * Ads Slots name
     */

    String ANS_BACK = "ANS_BACK";
    String GAME_START = "GAME_START";
    String RESULT_OPEN = "RESULT_OPEN";
    String BOT_AD = "BOT_AD";
    String FRONT_SLASH="/";


}
