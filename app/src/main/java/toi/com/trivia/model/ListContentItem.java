package toi.com.trivia.model;


import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class ListContentItem {

    public static List<NewGame.Questions> buildFromCursor(Cursor results) {

        List<NewGame.Questions> items = new ArrayList<>();

        results.moveToFirst();
        while (results.isAfterLast() == false) {
            items.add(NewGame.buildFromCursor(results));


            results.moveToNext();
        }

        return items;
    }

    public static List<RandamisedPojo> buildFromCursorNewGame(Cursor results) {

        List<RandamisedPojo> items = new ArrayList<>();

        results.moveToFirst();
        while (results.isAfterLast() == false) {
            items.add(RandamisedPojo.buildFromCursor(results));


            results.moveToNext();
        }

        return items;
    }

    public static List<NewGame.Options> buildFromCursorOptions(Cursor results) {

        List<NewGame.Options> items = new ArrayList<>();

        results.moveToFirst();
        while (results.isAfterLast() == false) {
            items.add(NewGame.buildFromCursorOption(results));


            results.moveToNext();
        }

        return items;
    }




    /*public static List<AnswersPojo> buildfromcursorAnswers(Cursor results) {

        List<AnswersPojo> items = new ArrayList<>();

        results.moveToFirst();
        while (results.isAfterLast() == false) {
            items.add(AnswersPojo.buildFromCursor(results));


            results.moveToNext();
        }

        return items;
    }

    public static List<AnswersPojo> buildFromCursorAnswersOptions(Cursor results) {

        List<AnswersPojo> items = new ArrayList<>();

        results.moveToFirst();
        while (results.isAfterLast() == false) {
            items.add(AnswersPojo.buildFromCursorAnsOptions(results));


            results.moveToNext();
        }

        return items;
    }*/
}
