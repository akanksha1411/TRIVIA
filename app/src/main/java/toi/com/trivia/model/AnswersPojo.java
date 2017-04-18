package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akanksha on 21/8/16.
 */
public class AnswersPojo implements Serializable {

    @Expose
    private int uid;

    @Expose
    private String deviceType;

    @Expose
    private String uqid;

    @Expose
    private int isLoggedIn;

    @Expose
    private int loginType;

    @Expose
    private String loginId;

    @Expose
    private String loginToken;

    @Expose
    private int gameId;

    @Expose
    private String submissionTime;

    @Expose
    private String setId;

    @Expose
    private Answers answers = new Answers();

    @Expose
    private List<Answers> answers_list = new ArrayList<>();


    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public List<Answers> getAnswers_list() {
        return answers_list;
    }

    public void setAnswers_list(List<Answers> answers_list) {
        this.answers_list = answers_list;
    }

    public Answers getAnswers() {
        return answers;
    }

    public void setAnswers(Answers answers) {
        this.answers = answers;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getUqid() {
        return uqid;
    }

    public void setUqid(String uqid) {
        this.uqid = uqid;
    }

    public int getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(int isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public int getLoginType() {
        return loginType;
    }

    public void setLoginType(int loginType) {
        this.loginType = loginType;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }


    public class Answers implements Serializable {

        @Expose
        private int quesId;

        @Expose
        private long timeTaken;

        @Expose
        private int optId;


        @Expose
        private String presentedTime;

        @Expose
        private String userOptSeq;

        @Expose
        private String userQuesSeq;
        @Expose
        private String qState;

        @Expose
        private int userOptId;

        public List<NewGame.Options> getOptions() {
            return options;
        }

        public void setOptions(List<NewGame.Options> options) {
            this.options = options;
        }

        @Expose
        private List<NewGame.Options> options = new ArrayList<>();

        public String getUserQuesSeq() {
            return userQuesSeq;
        }

        public void setUserQuesSeq(String userQuesSeq) {
            this.userQuesSeq = userQuesSeq;
        }

        public String getPresentedTime() {
            return presentedTime;
        }

        public void setPresentedTime(String presentedTime) {
            this.presentedTime = presentedTime;
        }

        public int getUserOptId() {
            return userOptId;
        }

        public void setUserOptId(int userOptId) {
            this.userOptId = userOptId;
        }


        public int getQuesId() {
            return quesId;
        }

        public void setQuesId(int quesId) {
            this.quesId = quesId;
        }

        public long getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(long timeTaken) {
            this.timeTaken = timeTaken;
        }

        public int getOptId() {
            return optId;
        }

        public void setOptId(int optId) {
            this.optId = optId;
        }

        public String getUserOptSeq() {
            return userOptSeq;
        }

        public void setUserOptSeq(String userOptSeq) {
            this.userOptSeq = userOptSeq;
        }

        public String getqState() {
            return qState;
        }

        public void setqState(String qState) {
            this.qState = qState;
        }


    }

   /* public static AnswersPojo buildFromCursor(Cursor results) {

        AnswersPojo item = new AnswersPojo();

        if (!results.isNull(1)) item.uid = results.getInt(1);
        if (!results.isNull(2)) item.setId = results.getString(2);
        if (!results.isNull(3)) item.answers.quesId = results.getInt(3);
        if (!results.isNull(4)) item.answers.qState = results.getString(4);
        if (!results.isNull(6)) item.answers.timeTaken = results.getInt(6);
        if (!results.isNull(7)) item.answers.timeSaved = results.getInt(7);
        if (!results.isNull(8)) item.answers.isCorrect = results.getInt(8);
        if (!results.isNull(9)) item.answers.userPoints = results.getInt(9);
        if (!results.isNull(10)) item.answers.bonusPoints = results.getInt(10);

        return item;
    }

    public static AnswersPojo buildFromCursorAnsOptions(Cursor results) {

        AnswersPojo item = new AnswersPojo();

        if (!results.isNull(1)) item.setId = results.getString(1);
        if (!results.isNull(2)) item.answers.quesId = results.getInt(2);
        if (!results.isNull(3)) item.answers.optId = results.getInt(3);
        if (!results.isNull(5)) item.answers.correctOption = results.getInt(5);

        return item;
    }*/


}
