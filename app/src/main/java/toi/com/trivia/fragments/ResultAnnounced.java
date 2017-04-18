package toi.com.trivia.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.androidprogresslayout.ProgressLayout;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import toi.com.trivia.R;
import toi.com.trivia.activities.AnswersActivity;
import toi.com.trivia.activities.GameArchive;
import toi.com.trivia.activities.ResultScreen;
import toi.com.trivia.databases.DBController;
import toi.com.trivia.model.ResultItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultAnnounced.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultAnnounced#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultAnnounced extends Fragment implements View.OnClickListener, TriviaConstants {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    static ReadPref readPref;

    static CircleImageView user_image;
    static String UID;
    static String sponsorName;
    public static ProgressLayout progressLayout;
    static PieChart pieChart;
    public static AppCompatActivity context;

    static TextView user_name, reach_out_email, result_title, game_score, game_bonus, time_bonus, share_button, answers_button, total_score, archive_result_text;
    static Button play_button;
    static FrameLayout result_layout;
    static View view;
    ResultItems items = new ResultItems();
    private OnFragmentInteractionListener mListener;
    public static DBController dbController;
    SavePref savePref;
    private static final long THRESHOLD_MILLIS = 1000L;
    private long lastClickMillisResult;

    public ResultAnnounced(ResultItems items) {
        // Required empty public constructor
        this.items = items;
    }

    public ResultAnnounced() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultAnnounced.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultAnnounced newInstance(String param1, String param2) {
        ResultAnnounced fragment = new ResultAnnounced();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (!CommonUtility.haveNetworkConnection(context)) {
                Snackbar.make(progressLayout, TRY_LATER, Snackbar.LENGTH_SHORT).show();

            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_announced, container, false);

        //register reciever for network connection change
        initNetworkStateReciever();
        savePref = new SavePref(getActivity());
        readPref = new ReadPref(getActivity());
        UID = readPref.getUID();
        context = ResultScreen.context;
        progressLayout = (ProgressLayout) view.findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        dbController = new DBController(context);
        sponsorName = readPref.getSponsorName();

        user_image = (CircleImageView) view.findViewById(R.id.user_image);
        pieChart = (PieChart) view.findViewById(R.id.chart);

        result_layout = (FrameLayout) view.findViewById(R.id.result_layout);

        user_name = (TextView) view.findViewById(R.id.user_name);
        reach_out_email = (TextView) view.findViewById(R.id.reach_out_email);
        result_title = (TextView) view.findViewById(R.id.result_title);
        game_score = (TextView) view.findViewById(R.id.game_score);
        game_bonus = (TextView) view.findViewById(R.id.game_bonus);
        time_bonus = (TextView) view.findViewById(R.id.time_bonus);
        share_button = (TextView) view.findViewById(R.id.share_button);
        answers_button = (TextView) view.findViewById(R.id.answers_button);
        total_score = (TextView) view.findViewById(R.id.total_score);
        play_button = (Button) view.findViewById(R.id.play_button);
        archive_result_text = (TextView) view.findViewById(R.id.archive_result_text);

        play_button.setOnClickListener(this);
        share_button.setOnClickListener(this);
        answers_button.setOnClickListener(this);
        CommonUtility.initBackground(view, context, TriviaConstants.RESULT_SCREEN, sponsorName);
        initUI();
        //send pageview
        CommonUtility.updateAnalytics(context, "Result Announced");
        //initToolbar();
        return view;
    }


    private void initToolbar() {

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
                //GA entry ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(getActivity(), GA_PREFIX + EXIT + "Result Announced", BACK_ARROW, TriviaConstants.CLICK, "Trivia_And_Exit_Result_Announced");
            }
        });
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arow);
    }

    /**
     * init receiver for network connction change
     */
    protected void initNetworkStateReciever() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(networkStateReceiver, filter);

    }

    public void initUI() {
        int is_game_live = items.getIs_game_live();
        int is_played_live = items.getIs_played_live();
        openImage(user_image, items.getUser().getProfile_img());
        savePref.saveIsGameCalled(false);
        try {
            if (null != items) {
                if (is_played_live == DEFAULT_ZERO && is_game_live == DEFAULT_ZERO) {
                    //Result of game played from archive
                    archive_result_text.setVisibility(View.VISIBLE);
                    result_title.setVisibility(View.GONE);
                } else {
                    //result of game played as live game
                    archive_result_text.setVisibility(View.GONE);
                    result_title.setVisibility(View.VISIBLE);
                }
                //  if (items.getIsWon() == 1) {
                //set background

                // }
                ArrayList<Entry> entries = new ArrayList<>();

                entries.add(new Entry(Float.valueOf(CommonUtility.checkNull(String.valueOf(items.getGameScore()))), 0));
                entries.add(new Entry(Float.valueOf(CommonUtility.checkNull(String.valueOf(items.getGameBonus()))), 1));
                entries.add(new Entry(Float.valueOf(CommonUtility.checkNull(String.valueOf(items.getTimeBonus()))), 2));

                PieDataSet dataset = new PieDataSet(entries, "# of Calls");

                ArrayList<String> labels = new ArrayList<String>();
                labels.add("");
                labels.add("");
                labels.add("");

                PieData data = new PieData(labels, dataset);

                dataset.setColors(new int[]{R.color.score_red, R.color.score_blue, R.color.score_green}, context);
                pieChart.setHoleColor(Color.BLACK);
                pieChart.setData(data);
                pieChart.setDrawSliceText(false);
                pieChart.getData().setDrawValues(false);
                pieChart.getLegend().setEnabled(false);
                pieChart.setDescription("");


                user_name.setText(readPref.getUserName().toString());
                if (items.getIsWon() == 1) {
                    result_title.setText(R.string.you_won);
                    result_title.setBackgroundResource(R.drawable.green_ribion);
                    if (CommonUtility.chkString(readPref.getUserEmail())) {
                        reach_out_email.setVisibility(View.VISIBLE);
                        //  reach_out_email.setText(reach_out_email.getText() + SPACE + readPref.getUserEmail());
                        reach_out_email.setText(Html.fromHtml(reach_out_email.getText() + SPACE + "\n" + "<font color='#FFD200'>" + CommonUtility.checkName(String.valueOf(CommonUtility.checkNull(readPref.getUserEmail()))) + "</font>"));

                    } else {
                        reach_out_email.setVisibility(View.GONE);
                    }
                } else {
                    reach_out_email.setVisibility(View.GONE);
                    result_title.setText(R.string.you_missed);
                    result_title.setBackgroundResource(R.drawable.red_ribion);

                }
                game_score.setText(String.valueOf(CommonUtility.getDefaultZero(items.getGameScore())));
                game_bonus.setText(String.valueOf(CommonUtility.getDefaultZero(items.getGameBonus())));
                time_bonus.setText(String.valueOf(CommonUtility.getDefaultZero(items.getTimeBonus())));
                total_score.setText(String.valueOf(Integer.parseInt(CommonUtility.getDefaultZero(items.getGameScore())) + Integer.parseInt(CommonUtility.getDefaultZero(items.getGameBonus())) + Math.round(Float.parseFloat(CommonUtility.getDefaultZero(items.getTimeBonus())))));

                setButtonTextLogin(Integer.parseInt(items.getCurrentGameId()), Integer.parseInt(items.getIsCurrentPlayed()));
                final Handler handler = new Handler();
//100ms wait to scroll to item after applying changes
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ResultScreen.progressLayout.showContent();
                        progressLayout.showContent();

                    }
                }, 500);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setButtonTextLogin(int gameid, int isCurrentPlayed) {

        if (gameid != DEFAULT_ZERO) {
            if (isCurrentPlayed == DEFAULT_ONE) {
                //usr has played the current game and also next game timer is visible
                play_button.setText(context.getResources().getString(R.string.button_play_another));
                play_button.setEnabled(true);
            } else {
                //usr has not played the current game and next game timer is visible.
                play_button.setText(context.getResources().getString(R.string.button_play_new));
                play_button.setEnabled(true);

            }
        } else {
            //no game is active right now play from archieve
            play_button.setText(context.getResources().getString(R.string.button_play_another));
            play_button.setEnabled(true);
        }

    }

    /**
     * set image to imageview for user
     *
     * @param quiz_image
     * @param url
     */
    private static void openImage(final ImageView quiz_image, String url) {

        if (CommonUtility.chkString(readPref.getUserImage())) {
            Log.d("url image", "called");
            /*Picasso.with(context).load(CommonUtility.checkNull(url)).transform(new CommonUtility.RoundedTransformation(10, 0))
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(quiz_image);*/
           /* Glide.with(context)
                    .load(CommonUtility.checkNull(url))
                    .centerCrop()
                    .placeholder(R.drawable.default_avatar)
                    .crossFade()
                    .into(quiz_image);*/

            if (context != null) {
                Glide.with(context)
                        .load(CommonUtility.checkNull(url))
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_avatar)
                        .error(R.drawable.default_avatar)
                        .into(quiz_image);
            }


        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.play_button) {
            beginPlayingGame(Integer.parseInt(items.getIsCurrentPlayed()), items.getCurrentGameId());

        } else if (id == R.id.share_button) {
            //todo open popup and share
            long now = SystemClock.elapsedRealtime();
            //todo open popup and share
            if (now - lastClickMillisResult > THRESHOLD_MILLIS) {
                shareResult();
            }
            lastClickMillisResult = now;
            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Result Announced", "Share Result", TriviaConstants.CLICK, "Trivia_And_Result_Announced");
            //takeScreenShot();
        } else if (id == R.id.answers_button) {
            Intent intent = new Intent(context, AnswersActivity.class);
            CommonUtility.showActivity(context, "gameId", Integer.parseInt(items.getGameId()), true, intent, null);

            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Result Announced", "Share Result", TriviaConstants.CLICK, "Trivia_And_Result_Announced");

        }

    }

    public void beginPlayingGame(int mCurrentGamePlayed, String currentGameId) {
        savePref.isReadyShown(false);
        savePref.saveIsGameKilled(false);
        //if play new called from Dashboard page
        /**
         *if the mCurrentGamePlayed value is 1 i.e user has played the current game and next game timer
         is visible then show archieve link else start new game
         */
        if (mCurrentGamePlayed == DEFAULT_ONE) {
            if (CommonUtility.chkString(items.getGame().getNextGameArchiveId())) {
                if (Integer.parseInt(items.getGame().getNextGameArchiveId()) != 0) {
                    savePref.saveArchieveGameId(items.getGame().getNextGameArchiveId());
                    savePref.saveCurrentPosition(0);
                    // savePref.saveResultGameId(String.valueOf(items.getGame().getNextGameArchiveId()));
                    dbController.clearDatabase(ResultScreen.context, items.getGame().getNextGameArchiveId());
                } else {
                    //another game- Take to archieve
                    Intent intent = new Intent(getActivity(), GameArchive.class);
                    CommonUtility.showActivity(getActivity(), SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                    CommonUtility.fetchArchive(getActivity(), String.valueOf(UID), TriviaConstants.RESULT_OUT_SCREEN);
                }

            } else {
                //another game- Take to archieve
                Intent intent = new Intent(getActivity(), GameArchive.class);
                CommonUtility.showActivity(getActivity(), SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                CommonUtility.fetchArchive(getActivity(), String.valueOf(UID), TriviaConstants.RESULT_OUT_SCREEN);
            }
            //   CommonUtility.fetchArchive(getActivity(), String.valueOf(UID), TriviaConstants.RESULT_OUT_SCREEN);
            //GA ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Result Announced", "Play another game", TriviaConstants.CLICK, "Trivia_And_Result_Announced");

        } else {
            if (Integer.parseInt(currentGameId) != DEFAULT_ZERO) {
                //call api - new game
                CommonUtility.fetchNewGame(getActivity(), Integer.parseInt(currentGameId), TriviaConstants.LOGIN_SCREEN);
                //GA ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Result Announced", "Play new game", TriviaConstants.CLICK, "Trivia_And_Result_Announced");
            } else {
                if (CommonUtility.chkString(items.getGame().getNextGameArchiveId())) {
                    if (Integer.parseInt(items.getGame().getNextGameArchiveId()) != 0) {
                        savePref.saveArchieveGameId(items.getGame().getNextGameArchiveId());
                        savePref.saveCurrentPosition(0);
                        // savePref.saveResultGameId(String.valueOf(items.getGame().getNextGameArchiveId()));
                        dbController.clearDatabase(ResultScreen.context, items.getGame().getNextGameArchiveId());
                    } else {
                        //another game- Take to archieve
                        Intent intent = new Intent(getActivity(), GameArchive.class);
                        CommonUtility.showActivity(getActivity(), SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                        CommonUtility.fetchArchive(getActivity(), String.valueOf(UID), TriviaConstants.RESULT_OUT_SCREEN);
                    }
                } else {
                    //another game- Take to archieve

                    Intent intent = new Intent(getActivity(), GameArchive.class);
                    CommonUtility.showActivity(getActivity(), SCREEN_TYPE, TriviaConstants.RESULT_OUT_SCREEN, true, intent, null);
                    CommonUtility.fetchArchive(getActivity(), String.valueOf(UID), TriviaConstants.RESULT_OUT_SCREEN);
                }
                //GA ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(context, GA_PREFIX + "Result Announced", "Play another game", TriviaConstants.CLICK, "Trivia_And_Result_Announced");
            }
        }

    }


   /* private boolean shareResult() {

        Bitmap icon = takeScreenShot();
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "trivia_result.jpg");
        try {

            if (f.exists()) {
                f.delete();
                f.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();

        }
      //  String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), icon, "title", null);
        //Uri uri = Uri.parse(Environment.getExternalStorageDirectory() + File.separator + "trivia_result.jpg");
        Uri uri = Uri.parse(f.getPath());
        Intent share = new Intent(Intent.ACTION_SEND);
        //share.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        share.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        share.setType("image/jpeg");
        startActivity(Intent.createChooser(share, "Share Result"));
        return true;
    }*/


    public Bitmap takeScreenShot() {
        View view = getActivity().getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        int height = getActivity().getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();

        return b;

    }


    private void savePic(Bitmap b, String strFileName) {
        String filename = "/Trivia/result.jpg";
        File file = new File(Environment.getExternalStorageDirectory(), filename);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void shareResult() {

        Bitmap icon = takeScreenShot();
        try {

            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.jpeg"); // overwrites this image every time
            icon.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            stream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        File imagePath = new File(context.getCacheDir(), "images");
        File newFile = new File(imagePath, "image.jpeg");
        Uri contentUri = FileProvider.getUriForFile(context, "com.toi.reader.activities.fileprovider", newFile);

        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
           // shareIntent.setDataAndType(contentUri, getActivity().getContentResolver().getType(contentUri));
            shareIntent.setType(getActivity().getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            startActivity(Intent.createChooser(shareIntent, "Share your result"));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
