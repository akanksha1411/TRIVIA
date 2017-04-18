package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akanksha on 21/8/16.
 */
public class LeaderboardItems implements Serializable {

    @Expose
    private int status;

    @Expose
    private String message;

    @Expose
    private int isLoggedIn;
    @Expose
    private String indiatimesSsoid;
    @Expose
    private int uqid;

    @Expose
    private int uid;

    @Expose
    private String loginType;

    @Expose
    private String loginId;

    @Expose
    private String loginToken;


    @Expose
    private String indiatimesSsoidToken;

    @Expose
    private String gameId;

    @Expose
    private String deviceType;

    @Expose
    private String mode;//Mode of the leaderboard to be displayed. Daily/Weekly/Monthly/Single Quiz (D/W/M/Q)
    @Expose
    private String sdate;
    @Expose
    private String edate;
    @Expose
    private int isMobileVerified;


    @Expose
    private String isData;
    @Expose
    private Rankings rankingsItems = new Rankings();

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }


    @Expose
    private List<Rankings> rankings = new ArrayList<>();
    @Expose
    private List<Myrank> myrank = new ArrayList<>();
    @Expose
    private List<Quizzes> quizzes = new ArrayList<>();

    public Rankings getRankingsItems() {
        return rankingsItems;
    }

    public void setRankingsItems(Rankings rankingsItems) {
        this.rankingsItems = rankingsItems;
    }

    public String getIsData() {
        return isData;
    }

    public void setIsData(String isData) {
        this.isData = isData;
    }

    public List<Quizzes> getQuizzes() {
        return quizzes;
    }

    public void setQuizzes(List<Quizzes> quizzes) {
        this.quizzes = quizzes;
    }

    public List<Myrank> getMyrank() {
        return myrank;
    }

    public void setMyrank(List<Myrank> myrank) {
        this.myrank = myrank;
    }

    public List<Rankings> getRankings() {
        return rankings;
    }

    public void setRankings(List<Rankings> rankings) {
        this.rankings = rankings;
    }

    @Expose
    private int qid;//Quiz Id. In case quiz is is passed then details of single quiz will be fetched out. It will neglect the date range.

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(int isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public String getIndiatimesSsoid() {
        return indiatimesSsoid;
    }

    public void setIndiatimesSsoid(String indiatimesSsoid) {
        this.indiatimesSsoid = indiatimesSsoid;
    }

    public int getUqid() {
        return uqid;
    }

    public void setUqid(int uqid) {
        this.uqid = uqid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
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

    public String getIndiatimesSsoidToken() {
        return indiatimesSsoidToken;
    }

    public void setIndiatimesSsoidToken(String indiatimesSsoidToken) {
        this.indiatimesSsoidToken = indiatimesSsoidToken;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSdate() {
        return sdate;
    }

    public void setSdate(String sdate) {
        this.sdate = sdate;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public int getIsMobileVerified() {
        return isMobileVerified;
    }

    public void setIsMobileVerified(int isMobileVerified) {
        this.isMobileVerified = isMobileVerified;
    }

    public int getQid() {
        return qid;
    }

    public void setQid(int qid) {
        this.qid = qid;
    }


    public class Rankings implements Serializable {

        @Expose
        private int uid;
        @Expose
        private String rank;
        @Expose
        private String name;
        @Expose
        private String score;
        @Expose
        private String imgurl;

        @Expose
        private int iswon;
        @Expose
        private String pimgurl;
        @Expose
        private String pname;

        public int getIswon() {
            return iswon;
        }

        public void setIswon(int iswon) {
            this.iswon = iswon;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getPimgurl() {
            return pimgurl;
        }

        public void setPimgurl(String pimgurl) {
            this.pimgurl = pimgurl;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }


    }

    public class Myrank implements Serializable {

        @Expose
        private int uid;
        @Expose
        private int rank;
        @Expose
        private String name;
        @Expose
        private String score;

        @Expose
        private String imgurl;
        @Expose
        private int iswon;
        @Expose
        private String pimgurl;
        @Expose
        private String pname;

        public int getIswon() {
            return iswon;
        }

        public void setIswon(int iswon) {
            this.iswon = iswon;
        }

        public String getPimgurl() {
            return pimgurl;
        }

        public void setPimgurl(String pimgurl) {
            this.pimgurl = pimgurl;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }



        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }


        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getRank() {
            return rank;
        }

        public void setRank(int rank) {
            this.rank = rank;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

    }


    public class Quizzes implements Serializable {

        @Expose
        private int id;
        @Expose
        private String name;

        public int getQid() {
            return id;
        }

        public void setQid(int qid) {
            this.id = qid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
