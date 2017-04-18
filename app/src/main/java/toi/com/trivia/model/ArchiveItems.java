package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akanksha on 21/8/16.
 */
public class ArchiveItems implements Serializable {
    @Expose
    private int status;

    @Expose
    private String message;
    @Expose
    private Result result_single = new Result();

    @Expose
    private String displayMode;
    @Expose
    private List<Games> games = new ArrayList<>();

    public Result getResult_single() {
        return result_single;
    }

    public void setResult_single(Result result_single) {
        this.result_single = result_single;
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

    public String getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public List<Games> getGames() {
        return games;
    }

    public void setGames(List<Games> games) {
        this.games = games;
    }

    public class Games implements Serializable {
        @Expose
        private String date;
        @Expose
        private List<Result> result = new ArrayList<>();

        @Expose

        private String pstatus;
        @Expose
        private int gameRemain;

        public String getPstatus() {
            return pstatus;
        }

        public void setPstatus(String pstatus) {
            this.pstatus = pstatus;
        }

        public int getGameRemain() {
            return gameRemain;
        }

        public void setGameRemain(int gameRemain) {
            this.gameRemain = gameRemain;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<Result> getResult() {
            return result;
        }

        public void setResult(List<Result> result) {
            this.result = result;
        }
    }

    public class Result implements Serializable {

        @Expose
        private String gameId;
        @Expose
        private String title;
        @Expose
        private String ques;
        @Expose
        private String playStatus;

        @Expose
        private String gameDay;


        public String getGameDay() {
            return gameDay;
        }

        public void setGameDay(String gameDay) {
            this.gameDay = gameDay;
        }


        public String getPlayStatus() {
            return playStatus;
        }

        public void setPlayStatus(String playStatus) {
            this.playStatus = playStatus;
        }

        public String getQues() {
            return ques;
        }

        public void setQues(String ques) {
            this.ques = ques;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getGameId() {
            return gameId;
        }

        public void setGameId(String gameId) {
            this.gameId = gameId;
        }
    }
}
