package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akanksha on 21/8/16.
 */
public class PlayedGame implements Serializable {

    @Expose
    private int status;

    @Expose
    private String message;
    @Expose
    private int gameId;

    @Expose
    private String gameType;
    @Expose
    private GameProperties gameProperties;

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

    public class GameProperties implements Serializable {

        @Expose
        private int maxMarks;

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

        @Expose
        private int gameRank;
        @Expose
        private long timeTaken;
        @Expose
        private long submissionTime;

        private List<Questions> questions=new ArrayList<>();

        public long getTimeTaken() {
            return timeTaken;
        }

        public void setTimeTaken(long timeTaken) {
            this.timeTaken = timeTaken;
        }

        public long getSubmissionTime() {
            return submissionTime;
        }

        public void setSubmissionTime(long submissionTime) {
            this.submissionTime = submissionTime;
        }

        public int getGameRank() {
            return gameRank;
        }

        public void setGameRank(int gameRank) {
            this.gameRank = gameRank;
        }

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


    public class Questions implements Serializable {

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
        private int qTough;//0-simple 5 - medium 10-toughest

        @Expose
        private int bonusPoints;
        @Expose
        private long timeTaken;


        @Expose
        private String selectedOption;

        public String getSelectedOption() {
            return selectedOption;
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;
        }


        @Expose
        private List<Options> options=new ArrayList<>();

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

    public class Options implements Serializable {

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


}
