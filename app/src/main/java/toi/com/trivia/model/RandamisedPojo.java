package toi.com.trivia.model;

import android.database.Cursor;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by Akanksha on 24/8/16.
 */
public class RandamisedPojo implements Serializable {
    @Expose
    int q_id;
    @Expose
    int is_bonus, set_id;

    public int getSet_id() {
        return set_id;
    }

    public void setSet_id(int set_id) {
        this.set_id = set_id;
    }

    public int getQ_id() {
        return q_id;
    }

    public void setQ_id(int q_id) {
        this.q_id = q_id;
    }

    public int getIs_bonus() {
        return is_bonus;
    }

    public void setIs_bonus(int is_bonus) {
        this.is_bonus = is_bonus;
    }


    public static RandamisedPojo buildFromCursor(Cursor results) {

        RandamisedPojo item = new RandamisedPojo();

        if (!results.isNull(1)) item.set_id = results.getInt(1);

        if (!results.isNull(2)) item.q_id = results.getInt(2);

        if (!results.isNull(3)) item.is_bonus = results.getInt(3);

        return item;
    }
}
