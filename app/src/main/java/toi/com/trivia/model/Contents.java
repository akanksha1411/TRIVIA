package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by akanksha on 21/8/16.
 */
public class Contents implements Serializable {
    @Expose
    private int watchdog;

    @Expose
    private int status;
    @Expose
    private String message;

    @Expose
    private int gameId;

    @Expose
    private String gameType;

    @Expose
    private String resultTime;

    @Expose
    private String answers;

    @Expose
    private User user;
    @Expose
    private Game game;

    @Expose
    private int submitStatus;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
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

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public String getResultTime() {
        return resultTime;
    }

    public void setResultTime(String resultTime) {
        this.resultTime = resultTime;
    }

    public int getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(int submitStatus) {
        this.submitStatus = submitStatus;
    }

    public int getWatchdog() {
        return watchdog;
    }

    public void setWatchdog(int watchdog) {
        this.watchdog = watchdog;
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
        private String gameBonus;
        @Expose
        private String timeBonus;

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
