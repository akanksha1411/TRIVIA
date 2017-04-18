package toi.com.trivia.activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import toi.com.trivia.R;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class TriviaLoader extends AppCompatActivity {


    ReadPref readPref;
    int UID;
    List<NewGame.Questions> questions = new ArrayList<>();
    Timer timer1 = new Timer();
    static int qListPos = 0;
    static int qCatPos = 0;
    public static DBController dbController;
    AnimatorSet set;
    int typeFlag = 0;
    int totalCount = 0;
    PublisherInterstitialAd mInterstitialAd;
    int pageCalled;
    final List<String> imageUrls = new ArrayList<>();
    final List<String> imageQids = new ArrayList<>();
    final List<String> categoryImages = new ArrayList<>();
    final List<String> categoryName = new ArrayList<>();
    String gameId;
    DownloadImages downloadImages = null;
    public static AppCompatActivity activity;
    String sponsorImage = "", sponsorName = "";
    public SavePref savePref;

    @Override
    protected void onStart() {
        Log.d("onstart-loader", "###########################");
        super.onStart();
    }


    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_loader);
        Log.d("onCreate -loader", "###########################");

        readPref = new ReadPref(getApplicationContext());
        savePref = new SavePref(getApplicationContext());
        savePref.saveIsGameCalled(false);
        UID = Integer.parseInt(readPref.getUID());
        sponsorName = readPref.getSponsorName();


        activity = this;
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.GAME_LOADER, sponsorName);


        //showAd();

        dbController = new DBController(getApplicationContext());

        final ImageView trivia_logo = (ImageView) findViewById(R.id.loading_logo);

        questions = dbController.findALLQuestions();// get all question for this quiz

        Bundle bundle = getIntent().getExtras();
        pageCalled = bundle.getInt("pageCalled");//specify from where it has called
        gameId = bundle.getString("gameId");//gameId of this game
        NewGame.Sponsor sponsor = (NewGame.Sponsor) bundle.getSerializable("sponsor");
        if (CommonUtility.chkString(sponsor.getImg_url())) {
            sponsorImage = sponsor.getImg_url();
            sponsorName = sponsor.getName();
        }

        startFlipAnimation(trivia_logo);

        timer1.schedule(new TimerTask() {

            @Override
            public void run() {

                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            startFlipAnimation(trivia_logo);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);

        //imageUrls - contains all the urls that needs to downloaded / imageQids - contains the question id for all the images downloaded
        try {
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).getqImage() != null) {
                    if (questions.get(i).getqImage().length() != 0) {
                        imageUrls.add(questions.get(i).getqImage());
                        imageQids.add(String.valueOf(questions.get(i).getQuesId()));
                        Log.d("Pranav", "Question Image ["+ questions.get(i).getQuesId()+ "] Image Name ["+questions.get(i).getqImage()+"]");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //categoryImages - contains urls for category images / categoryName - names of the catgory images to save the images with category name
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCatImage().length() != 0) {
                Log.d("Pranav", "Porcessing cateogory "+questions.get(i).getCatName());
                File imgFile = new File(Environment.getExternalStorageDirectory()
                        .getPath()
                        + "/Android/data/" + getApplicationContext().getPackageName() + "/TOI_TRIVIA/CatImages/"
                        + CommonUtility.replaceSpace(questions.get(i).getCatName()) + ".png");
                if (!imgFile.exists()) {
                    if (!categoryImages.contains(questions.get(i).getCatImage())) {
                        Log.d("Pranav", "Added URL for processing"+questions.get(i).getCatImage()+ ""+questions.get(i).getCatName()+"");
                        categoryImages.add(questions.get(i).getCatImage());
                        categoryName.add(questions.get(i).getCatName());
                    }
                }
            }
        }
        try {
            totalCount = imageUrls.size() + categoryImages.size();
            Log.d("Pranav", "--------- Total category image to process ["+categoryImages.size()+"----------");
            if (CommonUtility.chkString(sponsor.getImg_url())) {
                typeFlag = 2;
                downloadImages = new DownloadImages(sponsor.getImg_url(), "");
                downloadImages.execute();
            } else {
                if (imageUrls.size() != 0) {
                    typeFlag = 0;
                    //initial start for images download pro
                    downloadImages = new DownloadImages(imageUrls.get(qListPos), imageQids.get(qListPos));
                    downloadImages.execute();
                    //new DownloadImages().execute();
                } else if (categoryImages.size() != 0) {

                    typeFlag = 1;
                    downloadImages = new DownloadImages(categoryImages.get(qCatPos), categoryName.get(qCatPos));
                    downloadImages.execute();
                } else {
                    set.cancel();
                    //when images are downloaded - get the game started
                    Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                    Bundle b = getSponsorBundle(sponsorImage, sponsorName);
                    CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            set.cancel();
            //when images are down
            // cloaded - get the game started
            Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
            Bundle b = getSponsorBundle(sponsorImage, sponsorName);

            CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

        }


    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trivia_loader);
        Log.d("onCreate -loader", "###########################");

        readPref = new ReadPref(getApplicationContext());
        savePref = new SavePref(getApplicationContext());
        savePref.saveIsGameCalled(false);
        UID = Integer.parseInt(readPref.getUID());
        sponsorName = readPref.getSponsorName();


        activity = this;
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), TriviaConstants.GAME_LOADER, sponsorName);


        //showAd();

        dbController = new DBController(getApplicationContext());

        final ImageView trivia_logo = (ImageView) findViewById(R.id.loading_logo);

        questions = dbController.findALLQuestions();// get all question for this quiz

        Bundle bundle = getIntent().getExtras();
        pageCalled = bundle.getInt("pageCalled");//specify from where it has called
        gameId = bundle.getString("gameId");//gameId of this game
        NewGame.Sponsor sponsor = (NewGame.Sponsor) bundle.getSerializable("sponsor");
        if (CommonUtility.chkString(sponsor.getImg_url())) {
            sponsorImage = sponsor.getImg_url();
            sponsorName = sponsor.getName();
        }

        startFlipAnimation(trivia_logo);

        timer1.schedule(new TimerTask() {

            @Override
            public void run() {

                try {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            startFlipAnimation(trivia_logo);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000);

        ArrayList<TriviaUrls> triviaUrlses = new ArrayList<>();

        //imageUrls - contains all the urls that needs to downloaded / imageQids - contains the question id for all the images downloaded
        try {
            for (int i = 0; i < questions.size(); i++) {
                if (questions.get(i).getqImage() != null) {
                    if (questions.get(i).getqImage().length() != 0) {
                        triviaUrlses.add(new TriviaUrls(questions.get(i).getqImage(), String.valueOf(questions.get(i).getQuesId()), TriviaConstants.DEFAULT_ZERO));

                        Log.d("Pranav", "Question Image ["+ questions.get(i).getQuesId()+ "] Image Name ["+questions.get(i).getqImage()+"]");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //categoryImages - contains urls for category images / categoryName - names of the catgory images to save the images with category name
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).getCatImage().length() != 0) {
                Log.d("Pranav", "Porcessing cateogory "+questions.get(i).getCatName());
                File imgFile = new File(Environment.getExternalStorageDirectory()
                        .getPath()
                        + "/Android/data/" + getApplicationContext().getPackageName() + "/TOI_TRIVIA/CatImages/"
                        + CommonUtility.replaceSpace(questions.get(i).getCatName()) + ".png");
                if (!imgFile.exists()) {
                    if (!categoryImages.contains(questions.get(i).getCatImage())) {
                        Log.d("Pranav", "Added URL for processing ["+questions.get(i).getCatImage()+ "] cat name ["+questions.get(i).getCatName()+"]");
                        triviaUrlses.add(new TriviaUrls(questions.get(i).getCatImage(), questions.get(i).getCatName(),TriviaConstants.DEFAULT_ONE));


                    }
                }
            }
        }
        try {

            Log.d("Pranav", "--------- Total category image to process ["+categoryImages.size()+"----------");
            if (CommonUtility.chkString(sponsor.getImg_url())) {
                typeFlag = 2;
                triviaUrlses.add(new TriviaUrls(sponsor.getImg_url(),sponsorName ,2));
            }

            if(triviaUrlses.size() == 0){
                set.cancel();
                //when images are downloaded - get the game started
                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                Bundle b = getSponsorBundle(sponsorImage, sponsorName);
                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);
            }else{
                TriviaUrls[] triviaUrlArray = new TriviaUrls[triviaUrlses.size()];
                int i=0;
                for(TriviaUrls s: triviaUrlses){
                    triviaUrlArray[i++] = s;
                }
                new DownloadImagesNew().execute(triviaUrlArray);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            set.cancel();
            //when images are down
            // cloaded - get the game started
            Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
            Bundle b = getSponsorBundle(sponsorImage, sponsorName);

            CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

        }


    }

    private Bundle getSponsorBundle(String sponsorImage, String sponsorName) {
        Bundle bundle = new Bundle();
        bundle.putString("sponsorImage", sponsorImage);
        bundle.putString("sponsorName", sponsorName);
        return bundle;
    }


    /**
     * Starts animation 360 degree i.e verticall flipping to logo
     *
     * @param trivia_logo
     */

    private void startFlipAnimation(ImageView trivia_logo) {

        try {
            set = (AnimatorSet) AnimatorInflater.loadAnimator(getApplicationContext(), R.animator.flipping_animation);
            set.setTarget(trivia_logo);
            set.cancel();
            set.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    /**
     * background task that downloads the quiz and category images and starts the quiz after downloaded.
     */
    private class DownloadImages extends AsyncTask<String, Void, String> {
        String path;
        String text;


        public DownloadImages(String path, String text) {
            this.path = path;
            this.text = text;
        }

        public DownloadImages() {

        }

        @Override
        protected String doInBackground(String... params) {


            Boolean killed_status = readPref.getIsGameKilled();
            if (!killed_status) {
                try {
                    HttpURLConnection urlConnection = null;
                    String file_name = "trivia";
                    if (typeFlag == TriviaConstants.DEFAULT_ZERO) {//typeFlag shows the image type
                        //for quiz images
                        file_name = String.valueOf(CommonUtility.checkNull(gameId)) + "_" + text + ".png";
                        ;
                    } else if (typeFlag == TriviaConstants.DEFAULT_ONE) {
                        //for category images
                        file_name = CommonUtility.replaceSpace(text) + ".png";

                    } else if (typeFlag == 2) {

                        file_name = CommonUtility.replaceSpace(sponsorName) + ".png";
                    }

                    Log.d("Pranav", "Processing in background ["+file_name+"]" );


                    String filepath = "";

                    File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();

                    String filename = file_name;
                    Log.i("Local filename:", "" + filename);
                        /*File sponsorFile = new File(Environment.getExternalStorageDirectory()
                                .getPath()
                                + "/Android/data/" + getActivity().getPackageName() + "/files/.CatImages/";*/
                    File headFolder = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName(), "TOI_TRIVIA");
                    if (!headFolder.exists()) {
                        headFolder.mkdir();
                    }

                    File mediaStorageDir = null;
                    if (typeFlag == TriviaConstants.DEFAULT_ZERO) {
                        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "QuizImages");
                    } else if (typeFlag == TriviaConstants.DEFAULT_ONE) {
                        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "CatImages");
                    } else if (typeFlag == 2) {
                        mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "SponsorImages");
                    }
                    if (!mediaStorageDir.exists()) {
                        mediaStorageDir.mkdir();
                    }
                    File file = new File(mediaStorageDir, filename);
                    if (!file.exists()) {
                        file.createNewFile();
                        URL url = new URL(path);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        FileOutputStream fileOutput = new FileOutputStream(file);
                        InputStream inputStream = urlConnection.getInputStream();
                        int totalSize = urlConnection.getContentLength();
                        int downloadedSize = 0;
                        byte[] buffer = new byte[1024];
                        int bufferLength = 0;
                        while ((bufferLength = inputStream.read(buffer)) > 0) {
                            fileOutput.write(buffer, 0, bufferLength);
                            downloadedSize += bufferLength;
                            Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalS" +
                                    "ize:" + totalSize);
                        }
                        fileOutput.close();
                        if (downloadedSize == totalSize) filepath = file.getPath();


                        Log.i("filepath:", " " + filepath);
                        return filepath;
                    }else{
                        return file.getPath();
                    }

                } catch (InterruptedIOException e) {
                    //set.cancel();
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    //set.cancel();
                    e.printStackTrace();
                    return null;

                }
            } else {
                return "kill";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Pranav", "Inside onPostExecute ["+result+"] QList Count ["+qListPos+"] qCat Pos ["+qCatPos+"]"    );
            if (CommonUtility.chkString(result)) {
                if (!result.equals("kill")) {
                    try {
                        if (result != null) {
                            if (qListPos + qCatPos == totalCount) {
                                set.cancel();
                                //when images are downloaded - get the game started
                                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                                Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

                            } else {
                                if (imageUrls.size() != 0 || categoryImages.size() != 0) {
                                    if (imageUrls.size() > qListPos) {
                                        typeFlag = 0;
                                        Log.d("Pranav", "Going to download Quiz Image number ["+qListPos+"] of Qlist Size["+imageUrls.size()+"]" );
                                        downloadImages = new DownloadImages(imageUrls.get(qListPos), imageQids.get(qListPos));
                                        qListPos++;
                                        downloadImages.execute();
                                    } else if (categoryImages.size() > qCatPos) {
                                        Log.d("Pranav", "Going to download Category Image number ["+qCatPos+"] of Cat Size["+imageUrls.size()+"]" );
                                        typeFlag = 1;
                                        downloadImages = new DownloadImages(categoryImages.get(qCatPos), categoryName.get(qCatPos));
                                        qCatPos++;
                                        downloadImages.execute();
                                    } else {
                                        //when images are downloaded - get the game started
                                        Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                                        Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                                        CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

                                    }
                                } else {
                                    //when images are downloaded - get the game started
                                    Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                                    Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                                    CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

                                }

                            }
                        } else {
                            set.cancel();
                            //when images are downloaded - get the game started
                            Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                            Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                            CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);
                        }
                    } catch (Exception e) {
                        set.cancel();
                        //when images are downloaded - get the game started
                        Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                        Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                        CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
            } else {
                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

            }
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            qListPos = 0;
            Log.d("onDestroy", "###########################");

            if (downloadImages != null) {
                downloadImages.cancel(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        savePref.isReadyShown(false);
        if (CommonUtility.doNogoutTask()) {
            super.onBackPressed();
            savePref.saveIsGameKilled(false);
        }
    }

    /**
     * background task that downloads the quiz and category images and starts the quiz after downloaded.
     */
    private class DownloadImagesNew extends AsyncTask<TriviaUrls, Void, String> {


        @Override
        protected String doInBackground(TriviaUrls... urlsToDld) {


            Boolean killed_status = readPref.getIsGameKilled();
            if (!killed_status) {
                try {

                    int count = urlsToDld.length;
                    long totalSize = 0;
                    for (int i = 0; i < count; i++) {
                        TriviaUrls triviaUrls = urlsToDld[i];
                        totalSize += downloadImage(triviaUrls.getPath(), triviaUrls.getText(), triviaUrls.getType());

                        //totalSize += Downloader.downloadFile(urls[i]);
                        //publishProgress((int) ((i / (float) count) * 100));
                        // Escape early if cancel() is called
                        if (isCancelled())
                            break;
                    }
                    return totalSize+"";

                } catch (InterruptedIOException e) {
                    //set.cancel();
                    e.printStackTrace();
                    return null;
                } catch (Exception e) {
                    //set.cancel();
                    e.printStackTrace();
                    return null;

                }
            } else {
                return "kill";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("Pranav", "Inside onPostExecute ["+result+"] QList Count ["+qListPos+"] qCat Pos ["+qCatPos+"]"    );
            if (CommonUtility.chkString(result)) {
                if (!result.equals("kill")) {
                    try {
                        if (result != null) {

                            Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                            Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                            CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);


                        } else {
                            set.cancel();
                            //when images are downloaded - get the game started
                            Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                            Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                            CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);
                        }
                    } catch (Exception e) {
                        set.cancel();
                        //when images are downloaded - get the game started
                        Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                        Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                        CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
            } else {
                Intent startQuiz = new Intent(getApplicationContext(), StartQuiz.class);
                Bundle b = getSponsorBundle(sponsorImage, sponsorName);

                CommonUtility.showActivity(getApplicationContext(), "pageCalled", pageCalled, true, startQuiz, b);

            }
        }


        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }

    private class TriviaUrls{
        String path;
        String text;
        int type;


        public TriviaUrls(String path, String text, int type) {
            this.path = path;
            this.text = text;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public TriviaUrls() {
        }
    }

    public int downloadImage(String dldURL, String fileName, int fileType) throws IOException {

        HttpURLConnection urlConnection = null;
        String file_name = "trivia";
        if (fileType == TriviaConstants.DEFAULT_ZERO) {//typeFlag shows the image type
            //for quiz images
            file_name = String.valueOf(CommonUtility.checkNull(gameId)) + "_" + fileName + ".png";
        } else if (fileType == TriviaConstants.DEFAULT_ONE) {
            //for category images
            file_name = CommonUtility.replaceSpace(fileName) + ".png";
        } else if (fileType == 2) {
            file_name = CommonUtility.replaceSpace(sponsorName) + ".png";
        }

        Log.d("Pranav", "Processing in background ["+file_name+"]" );


        String filepath = "";

        File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();

        String filename = file_name;
        Log.i("Local filename:", "" + filename);
        File headFolder = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName(), "TOI_TRIVIA");
        if (!headFolder.exists()) {
            headFolder.mkdir();
        }

        File mediaStorageDir = null;
        if (fileType == TriviaConstants.DEFAULT_ZERO) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "QuizImages");
        } else if (fileType == TriviaConstants.DEFAULT_ONE) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "CatImages");
        } else if (fileType == 2) {
            mediaStorageDir = new File(Environment.getExternalStorageDirectory() + "/Android/data/" + getPackageName() + "/TOI_TRIVIA", "SponsorImages");
        }
        if (!mediaStorageDir.exists()) {
            mediaStorageDir.mkdir();
        }
        File file = new File(mediaStorageDir, filename);
        if (!file.exists()) {
            file.createNewFile();
            URL url = new URL(dldURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
            }
            fileOutput.close();
            /*if (downloadedSize == totalSize)
                filepath = file.getPath();*/


            Log.i("filepath:", " " + filepath);
            return downloadedSize;
        }else{
            return 0;
        }
    }

}
