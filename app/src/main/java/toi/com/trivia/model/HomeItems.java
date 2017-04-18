package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Akanksha on 21/8/16.
 */
public class HomeItems implements Serializable {

    @Expose
    private int status;

    @Expose
    private String message;

    @Expose
    private String ssoId;

    @Expose
    private int isLoggedIn;

    @Expose
    private String indiatimesSsoid;

    @Expose
    private int uqid;


    @Expose
    private int uid;

    @Expose
    private String deviceType;

    @Expose
    private String indiatimesSsoidToken;

    @Expose
    private int isMobileVerified;

    @Expose
    private Sponsor sponsor;

    @Expose
    private User user;

    @Expose
    private Data data;

    @Expose
    private String name;

    @Expose
    private String post;

    @Expose
    private String country;

    @Expose
    private String profile_img;

    @Expose
    private int gamePlayed;

    @Expose
    private int weeklyRank;

    @Expose
    private int monthlyRank;

    @Expose
    private int estimateRank;

    @Expose
    private int gameId;

    @Expose
    private int nextGameId;

    @Expose
    private int showResult;

    @Expose
    private int showResultId;


    @Expose
    private Game game;

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public String getIndiatimesSsoidToken() {
        return indiatimesSsoidToken;
    }

    public void setIndiatimesSsoidToken(String indiatimesSsoidToken) {
        this.indiatimesSsoidToken = indiatimesSsoidToken;
    }

    public int getIsMobileVerified() {
        return isMobileVerified;
    }

    public void setIsMobileVerified(int isMobileVerified) {
        this.isMobileVerified = isMobileVerified;
    }


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

    public int getGamePlayed() {
        return gamePlayed;
    }

    public void setGamePlayed(int gamePlayed) {
        this.gamePlayed = gamePlayed;
    }

    public int getWeeklyRank() {
        return weeklyRank;
    }

    public void setWeeklyRank(int weeklyRank) {
        this.weeklyRank = weeklyRank;
    }

    public int getMonthlyRank() {
        return monthlyRank;
    }

    public void setMonthlyRank(int monthlyRank) {
        this.monthlyRank = monthlyRank;
    }

    public int getEstimateRank() {
        return estimateRank;
    }

    public void setEstimateRank(int estimateRank) {
        this.estimateRank = estimateRank;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getNextGameId() {
        return nextGameId;
    }

    public void setNextGameId(int nextGameId) {
        this.nextGameId = nextGameId;
    }

    public int getShowResult() {
        return showResult;
    }

    public void setShowResult(int showResult) {
        this.showResult = showResult;
    }

    public int getShowResultId() {
        return showResultId;
    }

    public void setShowResultId(int showResultId) {
        this.showResultId = showResultId;
    }


    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

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

    public class Sponsor {
        @Expose
        private String name;
        @Expose
        private String img_url;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg_url() {
            return img_url;
        }

        public void setImg_url(String img_url) {
            this.img_url = img_url;
        }
    }

    public class User {

        @Expose
        private String name;
        @Expose
        private String post;
        @Expose
        private String country;
        @Expose
        private String profile_img;
        @Expose
        private String email;
        @Expose
        private int game_count;
        @Expose
        private int week_rank;
        @Expose
        private int month_rank;
        @Expose
        private int approx_rank;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

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

    public class Game {

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
        private String stime;


        public String getStime() {
            return stime;
        }

        public void setStime(String stime) {
            this.stime = stime;
        }

        public String getNextGameArchiveId() {
            return nextGameArchiveId;
        }

        public void setNextGameArchiveId(String nextGameArchiveId) {
            this.nextGameArchiveId = nextGameArchiveId;
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

    public class Data {
        @Expose
        private String data_fire;
        @Expose
        private String stitle;
        @Expose
        private ADS ADS = new ADS();

        public String getStitle() {
            return stitle;
        }

        public void setStitle(String stitle) {
            this.stitle = stitle;
        }

        public String getData_fire() {
            return data_fire;
        }

        public void setData_fire(String data_fire) {
            this.data_fire = data_fire;
        }

        public ADS getAds() {
            return ADS;
        }

        public void setAds(ADS ads) {
            this.ADS = ads;
        }

        public class ADS {

            @Expose
            private String BOT_AD;
            @Expose
            private String ANS_BACK;
            @Expose
            private String GAME_START;
            @Expose
            private String RESULT_OPEN;

            public String getBOT_AD() {
                return BOT_AD;
            }

            public void setBOT_AD(String BOT_AD) {
                this.BOT_AD = BOT_AD;
            }

            public String getANS_BACK() {
                return ANS_BACK;
            }

            public void setANS_BACK(String ANS_BACK) {
                this.ANS_BACK = ANS_BACK;
            }

            public String getGAME_START() {
                return GAME_START;
            }

            public void setGAME_START(String GAME_START) {
                this.GAME_START = GAME_START;
            }

            public String getRESULT_OPEN() {
                return RESULT_OPEN;
            }

            public void setRESULT_OPEN(String RESULT_OPEN) {
                this.RESULT_OPEN = RESULT_OPEN;
            }
        }
    }
}
