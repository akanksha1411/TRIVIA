package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by akanksha on 21/8/16.
 */
public class ResultItems implements Serializable {

    @Expose
    private int status;

    @Expose
    private String message;
    @Expose
    private String gameId;

    @Expose
    private String deviceType;

    @Expose
    private String indiatimesSsoid;
    @Expose
    private int uqid;

    @Expose
    private String indiatimesSsoidToken;
    @Expose
    private String isCurrentPlayed;
    @Expose
    private String currentGameId;

    @Expose
    private int isLoggedIn;

    @Expose
    private int isWon;
    @Expose
    private int isMobileVerified;

    @Expose
    private int rank;

    @Expose
    private String gameScore;

    @Expose
    private String gameBonus;
    @Expose
    private String timeBonus;
    @Expose
    private int is_played_live;
    @Expose
    private int quesCorrect;

    @Expose
    private int quesTotal;

    @Expose
    private int is_game_live;

    @Expose
    private int totalScore;

    @Expose
    private User user;
    @Expose
    private Game game;

    public int getQuesCorrect() {
        return quesCorrect;
    }

    public void setQuesCorrect(int quesCorrect) {
        this.quesCorrect = quesCorrect;
    }

    public int getQuesTotal() {
        return quesTotal;
    }

    public void setQuesTotal(int quesTotal) {
        this.quesTotal = quesTotal;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getIs_played_live() {
        return is_played_live;
    }

    public void setIs_played_live(int is_played_live) {
        this.is_played_live = is_played_live;
    }

    public int getIs_game_live() {
        return is_game_live;
    }

    public void setIs_game_live(int is_game_live) {
        this.is_game_live = is_game_live;
    }

    public String getIsCurrentPlayed() {
        return isCurrentPlayed;
    }

    public void setIsCurrentPlayed(String isCurrentPlayed) {
        this.isCurrentPlayed = isCurrentPlayed;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }

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

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
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

    public String getIndiatimesSsoidToken() {
        return indiatimesSsoidToken;
    }

    public void setIndiatimesSsoidToken(String indiatimesSsoidToken) {
        this.indiatimesSsoidToken = indiatimesSsoidToken;
    }

    public int getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(int isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public int getIsWon() {
        return isWon;
    }

    public void setIsWon(int isWon) {
        this.isWon = isWon;
    }

    public int getIsMobileVerified() {
        return isMobileVerified;
    }

    public void setIsMobileVerified(int isMobileVerified) {
        this.isMobileVerified = isMobileVerified;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getGameScore() {
        return gameScore;
    }

    public void setGameScore(String gameScore) {
        this.gameScore = gameScore;
    }

    public String getGameBonus() {
        return gameBonus;
    }

    public void setGameBonus(String gameBonus) {
        this.gameBonus = gameBonus;
    }

    public String getTimeBonus() {
        return timeBonus;
    }

    public void setTimeBonus(String timeBonus) {
        this.timeBonus = timeBonus;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public class User implements Serializable {

        @Expose
        private String name;
        @Expose
        private String post;
        @Expose
        private String country;
        @Expose
        private String profile_img;
        @Expose
        private int game_count;
        @Expose
        private int week_rank;
        @Expose
        private int month_rank;
        @Expose
        private int approx_rank;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPost() {
            return post;
        }

        public void setPost(String post) {
            this.post = post;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getProfile_img() {
            return profile_img;
        }

        public void setProfile_img(String profile_img) {
            this.profile_img = profile_img;
        }

        public int getGame_count() {
            return game_count;
        }

        public void setGame_count(int game_count) {
            this.game_count = game_count;
        }

        public int getWeek_rank() {
            return week_rank;
        }

        public void setWeek_rank(int week_rank) {
            this.week_rank = week_rank;
        }

        public int getMonth_rank() {
            return month_rank;
        }

        public void setMonth_rank(int month_rank) {
            this.month_rank = month_rank;
        }

        public int getApprox_rank() {
            return approx_rank;
        }

        public void setApprox_rank(int approx_rank) {
            this.approx_rank = approx_rank;
        }
    }

    public class Game implements Serializable {

        @Expose
        private int isCurrentPlayed;
        @Expose
        private int currentGameId;
        @Expose
        private int nextGameId;
        @Expose
        private String nextGameTime;
        @Expose
        private String nextGameArchiveId;

        @Expose
        private int isShowResult;
        @Expose
        private int resultGameId;
        @Expose
        private int resultMsg;
        @Expose
        private String server_time;
        @Expose
        private String gameScore;
        @Expose
        private String resultTime;

        @Expose
        private String gameBonus;
        @Expose
        private String timeBonus;

        public String getNextGameArchiveId() {
            return nextGameArchiveId;
        }

        public void setNextGameArchiveId(String nextGameArchiveId) {
            this.nextGameArchiveId = nextGameArchiveId;
        }

        public String getResultGameTime() {
            return resultTime;
        }

        public void setResultGameTime(String resultGameTime) {
            this.resultTime = resultGameTime;
        }

        public String getGameScore() {
            return gameScore;
        }

        public void setGameScore(String gameScore) {
            this.gameScore = gameScore;
        }

        public String getGameBonus() {
            return gameBonus;
        }

        public void setGameBonus(String gameBonus) {
            this.gameBonus = gameBonus;
        }

        public String getTimeBonus() {
            return timeBonus;
        }

        public void setTimeBonus(String timeBonus) {
            this.timeBonus = timeBonus;
        }

        public String getServer_time() {
            return server_time;
        }

        public void setServer_time(String server_time) {
            this.server_time = server_time;
        }

        public int getIsCurrentPlayed() {
            return isCurrentPlayed;
        }

        public void setIsCurrentPlayed(int isCurrentPlayed) {
            this.isCurrentPlayed = isCurrentPlayed;
        }

        public int getCurrentGameId() {
            return currentGameId;
        }

        public void setCurrentGameId(int currentGameId) {
            this.currentGameId = currentGameId;
        }

        public int getNextGameId() {
            return nextGameId;
        }

        public void setNextGameId(int nextGameId) {
            this.nextGameId = nextGameId;
        }

        public String getNextGameTime() {
            return nextGameTime;
        }

        public void setNextGameTime(String nextGameTime) {
            this.nextGameTime = nextGameTime;
        }

        public int getIsShowResult() {
            return isShowResult;
        }

        public void setIsShowResult(int isShowResult) {
            this.isShowResult = isShowResult;
        }

        public int getResultGameId() {
            return resultGameId;
        }

        public void setResultGameId(int resultGameId) {
            this.resultGameId = resultGameId;
        }

        public int getResultMsg() {
            return resultMsg;
        }

        public void setResultMsg(int resultMsg) {
            this.resultMsg = resultMsg;
        }
    }


}
