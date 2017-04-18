package toi.com.trivia.model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akanksha on 21/8/16.
 */
public class NewGame implements Serializable {

    @Expose
    private int status;

    @Expose
    private String message;
    @Expose
    private int gameId;

    @Expose
    private String gameType;

    @Expose
    private Sponsor sponsor;
    @Expose
    private GameProperties gameProperties;

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    public GameProperties getGameProperties() {
        return gameProperties;
    }

    public void setGameProperties(GameProperties gameProperties) {
        this.gameProperties = gameProperties;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public class Sponsor implements Serializable {
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

    public static class GameProperties {

        @Expose
        private int maxMarks;

        @Expose
        private int setId;

        @Expose
        private String quesAlgo;
        @Expose
        private String optionAlgo;

        @Expose
        private int isMinusMarking;

        @Expose
        private String title;
        @Expose
        private int correctMarks;
        @Expose
        private int bonusMarks;
        @Expose
        private int negMarks;

        public int getSetId() {
            return setId;
        }

        public void setSetId(int setId) {
            this.setId = setId;
        }

        private List<Questions> questions = new ArrayList<>();

        public int getMaxMarks() {
            return maxMarks;
        }

        public void setMaxMarks(int maxMarks) {
            this.maxMarks = maxMarks;
        }

        public String getQuesAlgo() {
            return quesAlgo;
        }

        public void setQuesAlgo(String quesAlgo) {
            this.quesAlgo = quesAlgo;
        }

        public String getOptionAlgo() {
            return optionAlgo;
        }

        public void setOptionAlgo(String optionAlgo) {
            this.optionAlgo = optionAlgo;
        }

        public int getIsMinusMarking() {
            return isMinusMarking;
        }

        public void setIsMinusMarking(int isMinusMarking) {
            this.isMinusMarking = isMinusMarking;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getCorrectMarks() {
            return correctMarks;
        }

        public void setCorrectMarks(int correctMarks) {
            this.correctMarks = correctMarks;
        }

        public int getBonusMarks() {
            return bonusMarks;
        }

        public void setBonusMarks(int bonusMarks) {
            this.bonusMarks = bonusMarks;
        }

        public int getNegMarks() {
            return negMarks;
        }

        public void setNegMarks(int negMarks) {
            this.negMarks = negMarks;
        }

        public List<Questions> getQuestions() {
            return questions;
        }

        public void setQuestions(List<Questions> questions) {
            this.questions = questions;
        }


    }


    public static class Questions {

        @Expose
        private int quesId;

        @Expose
        private String optDisplay;
        @Expose
        private String qImage;

        @Expose
        private String qVideo;

        @Expose
        private String qAudio;
        @Expose
        private int isBonus;
        @Expose
        private String title;

        @Expose
        private int catId;
        @Expose
        private String catName;
        @Expose
        private String catImage;

        @Expose
        private int qTough;//0-simple 5 - medium 10-toughest

        @Expose
        private int quesTime;

        public String getCatImage() {
            return catImage;
        }

        public void setCatImage(String catImage) {
            this.catImage = catImage;
        }

        public int getQuesTime() {
            return quesTime;
        }

        public void setQuesTime(int quesTime) {
            this.quesTime = quesTime;
        }

        public int getCatId() {
            return catId;
        }

        public void setCatId(int catId) {
            this.catId = catId;
        }

        public String getCatName() {
            return catName;
        }

        public void setCatName(String catName) {
            this.catName = catName;
        }

        @Expose
        private List<Options> options = new ArrayList<>();

        public int getQuesId() {
            return quesId;
        }

        public void setQuesId(int quesId) {
            this.quesId = quesId;
        }

        public String getOptDisplay() {
            return optDisplay;
        }

        public void setOptDisplay(String optDisplay) {
            this.optDisplay = optDisplay;
        }

        public String getqImage() {
            return qImage;
        }

        public void setqImage(String qImage) {
            this.qImage = qImage;
        }

        public String getqVideo() {
            return qVideo;
        }

        public void setqVideo(String qVideo) {
            this.qVideo = qVideo;
        }

        public String getqAudio() {
            return qAudio;
        }

        public void setqAudio(String qAudio) {
            this.qAudio = qAudio;
        }

        public int getIsBonus() {
            return isBonus;
        }

        public void setIsBonus(int isBonus) {
            this.isBonus = isBonus;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getqTough() {
            return qTough;
        }

        public void setqTough(int qTough) {
            this.qTough = qTough;
        }

        public List<Options> getOptions() {
            return options;
        }

        public void setOptions(List<Options> options) {
            this.options = options;
        }


    }

    public static class Options {

        @Expose
        private int optId;

        @Expose
        private String name;
        @Expose
        private int isCorrect;

        @Expose
        private int seq;

        @Expose
        private int fixLast;

        public int getOptId() {
            return optId;
        }

        public void setOptId(int optId) {
            this.optId = optId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getIsCorrect() {
            return isCorrect;
        }

        public void setIsCorrect(int isCorrect) {
            this.isCorrect = isCorrect;
        }

        public int getSeq() {
            return seq;
        }

        public void setSeq(int seq) {
            this.seq = seq;
        }

        public int getFixLast() {
            return fixLast;
        }

        public void setFixLast(int fixLast) {
            this.fixLast = fixLast;
        }
    }


    public static Questions buildFromCursor(Cursor results) {

        Questions item = new Questions();

        if (!results.isNull(0)) item.quesId = results.getInt(0);
        if (!results.isNull(18)) item.optDisplay = results.getString(18);
        if (!results.isNull(17)) item.qImage = results.getString(17);
        if (!results.isNull(11)) item.qVideo = results.getString(11);
        if (!results.isNull(12)) item.qAudio = results.getString(12);
        if (!results.isNull(0)) item.isBonus = 0;
        if (!results.isNull(1)) item.title = results.getString(1);
        if (!results.isNull(7)) item.catId = results.getInt(7);
        if (!results.isNull(19)) item.catName = results.getString(19);
        if (!results.isNull(20)) item.catImage = results.getString(20);


        return item;
    }


    public static GameProperties buildFromCursorProperties(Cursor results) {

        GameProperties item = new GameProperties();
        if (!results.isNull(2)) item.maxMarks = results.getInt(2);
        if (!results.isNull(4)) item.quesAlgo = results.getString(4);
        if (!results.isNull(5)) item.optionAlgo = results.getString(5);

        if (!results.isNull(7)) item.isMinusMarking = results.getInt(7);
        if (!results.isNull(9)) item.title = results.getString(9);
        if (!results.isNull(13)) item.correctMarks = results.getInt(13);
        if (!results.isNull(15)) item.bonusMarks = results.getInt(15);
        if (!results.isNull(14)) item.negMarks = results.getInt(14);


        return item;
    }

    public static Options buildFromCursorOption(Cursor results) {

        Options item = new Options();

        if (!results.isNull(0)) item.optId = results.getInt(0);
        if (!results.isNull(2)) item.name = results.getString(2);
        if (!results.isNull(3)) item.isCorrect = results.getInt(3);
        if (!results.isNull(0)) item.seq = 0;
        if (!results.isNull(4)) item.fixLast = results.getInt(4);

        return item;
    }

}
