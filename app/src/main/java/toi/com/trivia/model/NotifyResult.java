package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Akanksha on 27/1/17.
 */
public class NotifyResult {
    @Expose
    private int status;

    @Expose
    private String message;

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
}
