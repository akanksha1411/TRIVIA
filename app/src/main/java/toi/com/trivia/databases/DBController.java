package toi.com.trivia.databases;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import toi.com.trivia.activities.TriviaLoader;
import toi.com.trivia.model.ListContentItem;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.RandamisedPojo;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class DBController extends SQLiteOpenHelper implements TriviaConstants {

    public static final String DATABASE_NAME = "trivia.db";
    private static final int DATABASE_VERSION = 1;
    private Context mContext;


    public String TAG = "DBController--";

    public DBController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        //  onCreate(getWritableDatabase());
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e(TAG, "::Create Database:");

        // TODO Auto-generated method stub
       /* db.execSQL(
                "create table contacts " +
                        "(id integer primary key, name text,phone text,email text, street text,place text)"
        );*/
        db.execSQL("CREATE TABLE " + QUESTION_SET_TABLE + "(" +
                "  id bigint(20) NOT NULL," +
                "  set_type char(1) NOT NULL ," +
                "  max_marks decimal(10,2) NOT NULL ," +
                "  exam_time int(11) NOT NULL ," +
                "  ques_algo char(1) NOT NULL ," +
                "  opt_algo char(1) NOT NULL," +
                "  level int(2) DEFAULT '5' ," +
                "  minus_marking int(1) DEFAULT '0' ," +
                "  exam_end_time int(11) NOT NULL ," +
                "  title varchar(255) DEFAULT '' ," +
                "  coach_id int(10) DEFAULT '0' ," +
                "  credits int(10) DEFAULT '0' ," +
                "  detail text," +
                "  correct_marks float(10,2) DEFAULT '1.00'," +
                "  negative_marks float(10,2) DEFAULT '0.00'," +
                "  bonus_marks float(10,2) DEFAULT '1.00'," +
                "  image_url varchar(256) DEFAULT NULL ," +
                "  is_top char(1) DEFAULT 'N'," +
                "  meta_title varchar(128) DEFAULT '' ," +
                "  meta_description varchar(512) DEFAULT '' ," +
                "  sample_question varchar(256) DEFAULT '' ," +
                "  pdf_link text ," +
                "  deleted int(1) NOT NULL DEFAULT '0' ," +
                "  created int(11) DEFAULT NULL ," +
                "  updated int(11) NOT NULL ," +
                "  update_by int(11) NOT NULL DEFAULT '1' ," +
                "  PRIMARY KEY (id)" +
                ") ");

        db.execSQL("CREATE TABLE " + QUESTIONS_TABLE + " (" +
                "  id bigint(20) NOT NULL ," +
                "  question text NOT NULL ," +
                "  q_type char(1) NOT NULL DEFAULT 'M' ," +
                "  is_factual int(1) NOT NULL DEFAULT '1' ," +
                "  tags text ," +
                "  q_explain text ," +
                "  priority int(3) DEFAULT '0' ," +
                "  cat_id int(10) NOT NULL ," +
                "  question_type char(1) DEFAULT 'T' ," +
                "  parent_id bigint(20) DEFAULT '0' ," +
                "  q_link text ," +
                "  video varchar(1024) DEFAULT NULL ," +
                "  audio varchar(1024) DEFAULT NULL ," +
                "  deleted int(1) NOT NULL DEFAULT '0'," +
                "  created int(11) NOT NULL ," +
                "  updated int(11) NOT NULL ," +
                "  update_by int(10) NOT NULL DEFAULT '1' ," +
                "  image varchar(1024) DEFAULT NULL, " +
                "  opt_disp_mode varchar(32) DEFAULT 'SEQ_COL'," +
                "  cat_name TEXT," +
                "  cat_image TEXT," +
                "  PRIMARY KEY (id)" +
                ")");

        db.execSQL("CREATE TABLE " + QUESTION_SET_QUES_TABLE + " (" +
                "  id bigint(20) NOT NULL ," +
                "  set_id bigint(20)  NOT NULL ," +
                "  q_id bigint(20)  NOT NULL ," +
                "  is_bonus int(1) NOT NULL DEFAULT '0' ," +
                "  priority int(3) DEFAULT '0'," +
                "  deleted int(1) NOT NULL DEFAULT '0' ," +
                "  created int(11) DEFAULT NULL ," +
                "  updated int(11) NOT NULL ," +
                "  update_by int(11) NOT NULL DEFAULT '1' ," +
                "  PRIMARY KEY (id)," +
                "  FOREIGN KEY (q_id) REFERENCES ere_question (id) ON DELETE NO ACTION," +
                "  FOREIGN KEY (set_id) REFERENCES ere_ques_set (id) ON DELETE NO ACTION" +
                ")");

        db.execSQL("CREATE TABLE " + QUESTIONS_OPTION_TABLE + " (" +
                "  id bigint(20)  NOT NULL ," +
                "  q_id bigint(20)  NOT NULL ," +
                "  q_option text NOT NULL ," +
                "  option_correct int(1) NOT NULL DEFAULT '0' ," +
                "  fix_last int(1) DEFAULT '0' ," +
                "  deleted int(1) NOT NULL DEFAULT '0' ," +
                "  created int(11) NOT NULL ," +
                "  updated int(11) NOT NULL ," +
                "  update_by int(11) NOT NULL DEFAULT '1' ," +
                "  PRIMARY KEY (id)," +
                "  FOREIGN KEY (q_id) REFERENCES ere_question (id) ON DELETE NO ACTION" +
                ")");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS contacts");
        onCreate(db);
    }


    /*
  *
   * Clear THe database

  */
    public boolean clearDatabase(AppCompatActivity context, String gameId) {
        Log.e(TAG, "::clearDatabase:");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();

            String sql1 = "DELETE FROM " + QUESTIONS_TABLE;
            String sql2 = "DELETE FROM " + QUESTION_SET_TABLE;
            String sql3 = "DELETE FROM " + QUESTIONS_OPTION_TABLE;
            String sql4 = "DELETE FROM " + QUESTION_SET_QUES_TABLE;
            String[] statements = new String[]{sql1, sql2, sql3, sql4};

            for (String sql : statements) {
                db.execSQL(sql);
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, " failure clearDatabase " + e.getMessage());
            return false;
        } finally {
            Log.d(TAG, " clearDatabase endTransaction");
            db.endTransaction();
            //start playing
            CommonUtility.fetchNewGame(context, Integer.parseInt(gameId), TriviaConstants.GAME_ARCHIVE);

        }
        //  db.close();
        return true;
    }


    /*
  *
   * Clear THe database

  */
    public boolean clearDatabase2(AppCompatActivity context, String gameId) {
        Log.e(TAG, "::clearDatabase:");
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransactionNonExclusive();

            String sql1 = "DELETE FROM " + QUESTIONS_TABLE;
            String sql2 = "DELETE FROM " + QUESTION_SET_TABLE;
            String sql3 = "DELETE FROM " + QUESTIONS_OPTION_TABLE;
            String sql4 = "DELETE FROM " + QUESTION_SET_QUES_TABLE;
            String[] statements = new String[]{sql1, sql2, sql3, sql4};

            for (String sql : statements) {
                db.execSQL(sql);
            }


            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, " failure clearDatabase " + e.getMessage());
            return false;
        } finally {
            Log.d(TAG, " clearDatabase endTransaction");
            db.endTransaction();


        }
        db.close();
        return true;
    }

    /*
   *
    * Get questions
    *
    * @param category_id
    * @return

   */
    public NewGame.Questions findQuestionsForQID(String ques_id) {
        SQLiteDatabase db = this.getWritableDatabase();


        try {

            String query = "SELECT * FROM " + QUESTIONS_TABLE + " WHERE id = ? ";

            Cursor results = db.rawQuery(query, new String[]{
                    ques_id
            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findProductsForCategory:" + results.toString());
                return NewGame.buildFromCursor(results);
            } finally {
                results.close();
            }
        } finally {
            //closeDB(db);
        }
    }


    /*
  *
   * Get questions
   *
   * @param category_id
   * @return

  */
    public String findCorrectOptionId(String quesId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String query = "SELECT id FROM " + QUESTIONS_OPTION_TABLE + " WHERE q_id = ? AND option_correct = 1";

            Cursor results = db.rawQuery(query, new String[]{
                    quesId
            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findProductsForCategory:" + results.toString());
                return results.getString(0).toString();
            } finally {
                results.close();
            }
        } finally {
            // closeDB(db);
        }
    }


    /*
   *
    * Get questions
    *
    * @param category_id
    * @return

   */
    public List<NewGame.Options> findALLOptions(String quesId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            String query = "SELECT * FROM " + QUESTIONS_OPTION_TABLE + " WHERE q_id = ? AND option_correct == 0 ORDER BY RANDOM() LIMIT 3";

            Cursor results = db.rawQuery(query, new String[]{
                    quesId
            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findALLOptions:" + results.toString());
                return ListContentItem.buildFromCursorOptions(results);
            } finally {
                results.close();
            }
        } finally {
            //  closeDB(db);
        }
    }

    /*
   *
    * Get questions
    *
    * @param category_id
    * @return

   */
    public List<NewGame.Options> findALLFixedOptions(String quesId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            String query = "SELECT * FROM " + QUESTIONS_OPTION_TABLE + " WHERE q_id = ? AND option_correct == 0 LIMIT 3";

            Cursor results = db.rawQuery(query, new String[]{
                    quesId
            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findALLOptions:" + results.toString());
                return ListContentItem.buildFromCursorOptions(results);
            } finally {
                results.close();
            }
        } finally {
            //  closeDB(db);
        }
    }


    /*
   *
    * Get questions
    *
    * @param category_id
    * @return

   */
    public List<NewGame.Questions> findALLQuestions() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String query = "SELECT * FROM " + QUESTIONS_TABLE;

            Cursor results = db.rawQuery(query, new String[]{
            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findALLOptions:" + results.toString());
                return ListContentItem.buildFromCursor(results);
            } finally {
                results.close();
            }
        } finally {
            //closeDB(db);
        }
    }


    /*
  *
   * Get questions
   *
   * @param category_id
   * @return

  */
    public List<NewGame.Options> findCorrectOption(String quesId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String query = "SELECT * FROM " + QUESTIONS_OPTION_TABLE + " WHERE q_id = ? AND option_correct == 1 LIMIT 1 ";

            Cursor results = db.rawQuery(query, new String[]{
                    quesId
            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findALLOptions:" + results.toString());
                return ListContentItem.buildFromCursorOptions(results);
            } finally {
                results.close();
            }
        } finally {
            // closeDB(db);
        }
    }


    /*
    *
     * Get questions
     *
     * @param category_id
     * @return

    */
    public List<RandamisedPojo> findRandamizedQuestions(String category_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String query = "SELECT * FROM " + QUESTION_SET_QUES_TABLE + " WHERE is_bonus == 0 ORDER BY RANDOM()";

            Cursor results = db.rawQuery(query, new String[]{

            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findProductsForCategory:" + results.toString());
                return ListContentItem.buildFromCursorNewGame(results);
            } finally {
                results.close();
            }
        } finally {
            // closeDB(db);
        }
    }

    /*
    *
     * Get questions
     *
     * @param category_id
     * @return

    */
    public List<RandamisedPojo> findFixedQuestions(String category_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String query = "SELECT * FROM " + QUESTION_SET_QUES_TABLE + " WHERE is_bonus == 0 ";

            Cursor results = db.rawQuery(query, new String[]{

            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findProductsForCategory:" + results.toString());
                return ListContentItem.buildFromCursorNewGame(results);
            } finally {
                results.close();
            }
        } finally {
            // closeDB(db);
        }
    }

    /*
   *
    * Get questions
    *
    * @param category_id
    * @return

   */
    public List<RandamisedPojo> findRandamizedQuestionsForBonus(String category_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String query = "SELECT * FROM " + QUESTION_SET_QUES_TABLE + " WHERE is_bonus == 1 ";

            Cursor results = db.rawQuery(query, new String[]{

            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findProductsForCategory:" + results.toString());
                return ListContentItem.buildFromCursorNewGame(results);
            } finally {
                results.close();
            }
        } finally {
            // closeDB(db);
        }
    }

/*

*
 *Get Quiz properties
 *
 * @param letter
 * @return

*/

    public NewGame.GameProperties findGameProperties() {
        SQLiteDatabase db = this.getWritableDatabase();

        try {

            String query = "SELECT * FROM " + QUESTION_SET_TABLE;
            Cursor results = db.rawQuery(query, new String[]{

            });

            try {
                if (results.moveToFirst() == false)
                    return null;
                Log.i("TAG", "::findProductsForCategory:" + results.toString());
                return NewGame.buildFromCursorProperties(results);
            } finally {
                results.close();
            }
        } finally {
            // closeDB(db);
        }
    }


    /**
     * Get Categiries from Database
     *
     * @return
     *//*

    public List<CategoryItem> getCategories() {

        if (dbIsInWALMode) {
            openDBtoWrite();
        } else {
            openDB();
        }
        try {

            String query = "SELECT * FROM category";

            Cursor results = db.rawQuery(query, new String[]{});
            List<CategoryItem> temp = new ArrayList<>();
            try {
                if (results.moveToFirst() == false)
                    return null;
                return ListCategoryItem.buildFromCursor(results);

            } finally {
                results.close();
            }
        } finally {
            closeDB();
        }
    }
*/

    /**
     * Inserts the game properties for quiz to start
     *
     * @param data       New Game data feed
     * @param pageCalled
     * @return
     */
    public boolean updateGameProperties(NewGame data, int pageCalled) {
        Log.e(TAG, "::updateGameProperties:");
        SQLiteDatabase db = this.getWritableDatabase();

        try {
//            db.beginTransaction();
            db.beginTransactionNonExclusive();
            String query = "INSERT OR REPLACE INTO" + QUESTION_SET_TABLE + " VALUES (?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "? )";
            SQLiteStatement insert = db.compileStatement(query);
            //  for (int i = 0; i < data.size(); i++) {
            //      item = data.get(i);
            NewGame.GameProperties gameProperties = data.getGameProperties();
            String parameters[] = new String[]{Integer.toString(gameProperties.getSetId()), ""/*COMMENT 'D daily,M monthly,U usergen'*/,
                    String.valueOf(gameProperties.getMaxMarks())/*COMMENT '10'*/,
                    String.valueOf(/*gameProperties.getQuestions().get(0).getQuesTime()*/0)/*COMMENT 'exam time'*/,
                    CommonUtility.checkNull(gameProperties.getQuesAlgo())/*COMMENT 'R random/ F fixed'*/,
                    CommonUtility.checkNull(gameProperties.getOptionAlgo())/* COMMENT 'R random/ F fixed'*/,
                    String.valueOf(5)/* DEFAULT '5' COMMENT 'toughness level 1-10'*/,
                    Integer.toString(gameProperties.getIsMinusMarking())/*DEFAULT '0' COMMENT '1 = yes, 0= no'*/,
                    Integer.toString(10)/*COMMENT 'exam end time in minutes'*/,
                    gameProperties.getTitle()/*DEFAULT '' COMMENT 'Question set title'*/,
                    String.valueOf(0)/*DEFAULT '0' COMMENT 'Question sponsored by company'*/,
                    String.valueOf(0)/* DEFAULT '0' COMMENT 'Credit required to participate in the test'*/,
                    ""/*COMMENT 'Detail of question set'*/,
                    String.valueOf(gameProperties.getCorrectMarks())/*DEFAULT '1.00'*/,
                    String.valueOf(gameProperties.getNegMarks())/*DEFAULT '0.00'*/,
                    String.valueOf(gameProperties.getBonusMarks())/* DEFAULT '1.00'*/,
                    ""/*DEFAULT NULL COMMENT 'Image '*/, "N"/*is_top - DEFAULT 'N'*/, "", "", "", "", "", "", "", ""
            };

            insert.bindAllArgsAsStrings(parameters);
            insert.execute();
            //} //for loop close
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, " failure updateGameProperties " + e.getMessage());
            return false;
        } finally {
            Log.d(TAG, " endTransaction updateGameProperties");
            db.endTransaction();
            //start next insert transaction for questions
            updateQuizQuestions(data, pageCalled);

        }
        closeDB(db);
        return true;
    }

    /**
     * Inserts Questions and inserts questions options
     *
     * @param data
     * @param pageCalled
     * @return
     */
    public boolean updateQuizQuestions(NewGame data, int pageCalled) {
        Log.e(TAG, "::updateQuizQuestions:");
        SQLiteDatabase db = this.getWritableDatabase();


        try {
//            db.beginTransaction();
            db.beginTransactionNonExclusive();
            String query = "INSERT OR REPLACE INTO" + QUESTIONS_TABLE + " VALUES (?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, " +
                    "?, ?, ?, ? ,?)";
            SQLiteStatement insert = db.compileStatement(query);
            List<NewGame.Questions> questions = data.getGameProperties().getQuestions();

            for (int i = 0; i < questions.size(); i++) {
                NewGame.Questions item = questions.get(i);
              /*  String video = "";
                if (i == 1) {
                    video = "http://cdnbakmi.kaltura.com/p/303932/sp/30393200/serveFlavor/flavorId/0_hcx2qvpl/name/0_hcx2qvpl.mp4";
                }*/

                String parameters[] = new String[]{Integer.toString(item.getQuesId()), CommonUtility.checkNull(item.getTitle()), "S"/*q_type -COMMENT Question type M= Multiple select, S= Single select*/,
                        String.valueOf(1)/*isfactual -  DEFAULT '1' COMMENT '0 = Not factual, 1 = factual'*/,
                        ""/*tags - COMMENT 'question tags separted by ::'*/,
                        ""/*q_explain - COMMENT 'Solution explaination. In case solution explaination need to be shown to the user'*/,
                        String.valueOf(0)/*priority - DEFAULT '0' COMMENT 'Toughness 0-10, 10 means toughest'*/,
                        String.valueOf(item.getCatId())/*cat_id -  COMMENT 'CND category Id'*/,
                        ""/*question_type - DEFAULT 'T' COMMENT 'P=Practice,T=Test'*/,
                        String.valueOf(0) /*parent_id - DEFAULT '0' COMMENT 'Parent id of the question, used in case of variation'*/,
                        ""/*q_link - COMMENT 'question url from where question has been picked'*/,
                        CommonUtility.checkNull(item.getqVideo()),
                        CommonUtility.checkNull(item.getqAudio()), String.valueOf(0)/*deleted -  DEFAULT '0' COMMENT '1 = deleted, 0= not deleted'*/,
                        ""/*created - COMMENT 'Unix timestamp of create date'*/,
                        ""/*updated - COMMENT 'Unix timestamp of update date'*/, String.valueOf(1)/*update_by  - DEFAULT '1' COMMENT 'Updated by which user'*/,
                        CommonUtility.checkNull(item.getqImage()), item.getOptDisplay(), CommonUtility.checkNull(item.getCatName()), CommonUtility.checkNull(item.getCatImage())
                };

                insert.bindAllArgsAsStrings(parameters);
                insert.execute();

                List<NewGame.Options> options = item.getOptions();

                updateQuizOptions(options, item.getQuesId(), pageCalled, data.getGameId(), data.getSponsor());

            } //for loop close
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, " failure updateQuizQuestions " + e.getMessage());
            return false;
        } finally {
            Log.d(TAG, " endTransaction");
            db.endTransaction();

        }
        closeDB(db);
        return true;
    }

    /**
     * inserts questions options
     *
     * @param optionsList
     * @param quesId
     * @param pageCalled
     * @param gameId
     * @param sponsor
     * @return
     */
    public boolean updateQuizOptions(List<NewGame.Options> optionsList, int quesId, int pageCalled, int gameId, NewGame.Sponsor sponsor) {
        Log.e(TAG, "::updateQuizOptions:");
        SQLiteDatabase db = this.getWritableDatabase();


        try {
//            db.beginTransaction();
            db.beginTransactionNonExclusive();
            String query = "INSERT OR REPLACE INTO" + QUESTIONS_OPTION_TABLE + " VALUES (?, ?, ?, ?, ?, " +
                    "?, ?, ?, ? )";
            SQLiteStatement insert = db.compileStatement(query);

            for (int i = 0; i < optionsList.size(); i++) {
                NewGame.Options item = optionsList.get(i);
                String parameters[] = new String[]{Integer.toString(item.getOptId()), CommonUtility.checkNull(String.valueOf(quesId)) /* COMMENT 'Question Id'*/,
                        CommonUtility.checkNull(item.getName())/* COMMENT 'Option which need to be shown'*/,
                        String.valueOf(item.getIsCorrect())/*option_correct -DEFAULT '0' COMMENT '0 If option is wrong, 1 if option is correct'*/,
                        String.valueOf(item.getFixLast())/*fix_last - DEFAULT '0' COMMENT '1 means fix this option slot to last'*/,
                        String.valueOf(item.getSeq())/*deleted -  DEFAULT '0' COMMENT '1 = deleted, 0= not deleted'*/,
                        ""/*created - COMMENT 'Unix timestamp of create date'*/,
                        ""/*updated - COMMENT 'Unix timestamp of update date'*/, ""/*update_by  - DEFAULT '1' COMMENT 'Updated by which user'*/

                };

                insert.bindAllArgsAsStrings(parameters);
                insert.execute();


            } //for loop close
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, " failure updateQuizOptions " + e.getMessage());
            return false;
        } finally {
            Log.d(TAG, " endTransaction");
            db.endTransaction();
            if (CommonUtility.dialog != null) {
                CommonUtility.dialog.dismiss();
            }
            Intent startQuiz = new Intent(mContext, TriviaLoader.class);
            Bundle b = new Bundle();
            b.putString("gameId", String.valueOf(gameId));
            b.putSerializable("sponsor", sponsor);
            CommonUtility.showActivity(mContext, "pageCalled", pageCalled, true, startQuiz, b);
        }
        closeDB(db);
        return true;
    }

    /**
     * inserts questions options
     *
     * @param newGame
     * @return
     */
    public boolean updateERE_QUES_SET_QUES(NewGame newGame) {
        Log.e(TAG, "::updateERE_QUES_SET_QUES:");
        SQLiteDatabase db = this.getWritableDatabase();


        try {
//            db.beginTransaction();
            db.beginTransactionNonExclusive();
            String query = "INSERT OR REPLACE INTO" + QUESTION_SET_QUES_TABLE + " VALUES (?, ?, ?, ?, ?, " +
                    "?, ?, ?, ? )";
            SQLiteStatement insert = db.compileStatement(query);
            NewGame.GameProperties gameProperties = newGame.getGameProperties();
            List<NewGame.Questions> questions = newGame.getGameProperties().getQuestions();

            for (int i = 0; i < questions.size(); i++) {
                NewGame.Questions item = questions.get(i);
                String parameters[] = new String[]{Integer.toString(i), String.valueOf(gameProperties.getSetId()) /* COMMENT 'set Id'*/,
                        String.valueOf(item.getQuesId())/* COMMENT 'question id'*/,
                        String.valueOf(item.getIsBonus())/*is bonus question or not*/,
                        String.valueOf(0)/*priority*/,
                        String.valueOf(0)/*deleted -  DEFAULT '0' COMMENT '1 = deleted, 0= not deleted'*/,
                        "0"/*created - COMMENT 'Unix timestamp of create date'*/,
                        "0"/*updated - COMMENT 'Unix timestamp of update date'*/,
                        ""/*update_by  - DEFAULT '1' COMMENT 'Updated by which user'*/

                };

                insert.bindAllArgsAsStrings(parameters);
                insert.execute();

            } //for loop close
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, " failure updateERE_QUES_SET_QUES " + e.getMessage());
            return false;
        } finally {
            Log.d(TAG, " endTransaction updateERE_QUES_SET_QUES");
            db.endTransaction();
        }
        closeDB(db);
        return true;
    }

    private void closeDB(SQLiteDatabase db) {
        //db.close();
    }
}
