package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akanksha on 21/8/16.
 */
public class PrizesItems implements Serializable {

    @Expose
    private int status;

    @Expose
    private String message;

    @Expose
    private List<Prizes> prizes = new ArrayList<>();

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

    public List<Prizes> getPrizes() {
        return prizes;
    }

    public void setPrizes(List<Prizes> prizes) {
        this.prizes = prizes;
    }


    public static class Prizes implements Serializable {

        @Expose
        private String title;

        @Expose
        private List<Prizesss> prizesss = new ArrayList<>();

        @Expose
        private String key;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Prizesss> getPrizesss() {
            return prizesss;
        }

        public void setPrizesss(List<Prizesss> prizesss) {
            this.prizesss = prizesss;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

    }

    public class Prizesss implements Serializable {

        @Expose
        private String rank;

        @Expose
        private String name;

        @Expose
        private String image;

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getname() {
            return name;
        }

        public void setname(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

}
