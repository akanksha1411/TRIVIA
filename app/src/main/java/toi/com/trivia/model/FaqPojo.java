package toi.com.trivia.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pranav.shankar1 on 22/02/17.
 */
public class FaqPojo {

    @Expose
    private int status;
    @Expose
    private String message;

    private List<Faq> faq=new ArrayList<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Faq> getFaq() {
        return faq;
    }

    public void setFaq(List<Faq> faq) {
        this.faq = faq;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Faq implements Serializable {

        @Expose
        private String ques;
        @Expose
        private String ans;

        public String getQues() {
            return ques;
        }

        public void setQues(String ques) {
            this.ques = ques;
        }

        public String getAns() {
            return ans;
        }

        public void setAns(String ans) {
            this.ans = ans;
        }
    }

}
