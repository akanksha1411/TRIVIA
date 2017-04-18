package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Akanksha on 21/8/16.
 */
public class CategoriesItems implements Serializable {

    @Expose
    private int status;

    @Expose
    private String message;

    @Expose
    private int uid;

    @Expose
    private Category category;


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

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public class Category {

        @Expose
        private int id;

        @Expose
        private String name;

        @Expose
        private String image;

        @Expose
        private int updated;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getUpdated() {
            return updated;
        }

        public void setUpdated(int updated) {
            this.updated = updated;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }


}
