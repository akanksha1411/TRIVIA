package toi.com.trivia.fragments;

/**
 * Created by Akanksha on 17/10/16.
 */

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import toi.com.trivia.R;
import toi.com.trivia.activities.StartQuiz;
import toi.com.trivia.model.NewGame;
import toi.com.trivia.model.RandamisedPojo;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.TriviaConstants;

public class VideoFragment extends Fragment {

    // Declare variables
    ProgressDialog pDialog;
    VideoView videoview;
    int pageCalled;
    // Insert your Video URL
    String VideoURL = "http://cdnbakmi.kaltura.com/p/303932/sp/30393200/serveFlavor/flavorId/0_hcx2qvpl/name/0_hcx2qvpl.mp4", sponsorName = "", sponsorImage = "";
    AnimatorSet set;
    ReadPref readPref;
    SavePref savePref;
    Timer timer1 = new Timer();

    public VideoFragment(int pageCalled) {
        this.pageCalled = pageCalled;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.videoview_main, container, false);
        readPref = new ReadPref(getActivity().getApplicationContext());
        savePref = new SavePref(getActivity().getApplicationContext());
        List<RandamisedPojo> question_list = StartQuiz.returnRandamizedQues();
        int mCurrentPosition = readPref.getCurrentPosition();

        int qid = question_list.get(mCurrentPosition).getQ_id();

        NewGame.Questions questions = StartQuiz.returnQuestions(String.valueOf(qid));
        final LinearLayout buffer_layout = (LinearLayout) view.findViewById(R.id.buffer_layout);
        final ImageView trivia_logo = (ImageView) view.findViewById(R.id.loading_logo);
        startFlipAnimation(trivia_logo);
        trivia_logo.setVisibility(View.VISIBLE);
        timer1.schedule(new TimerTask() {

            @Override
            public void run() {

                try {
                    StartQuiz.activity.runOnUiThread(new Runnable() {

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

        // Find your VideoView in your video_main.xml layout
        videoview = (VideoView) view.findViewById(R.id.VideoView);
        // Execute StreamVideo AsyncTask
        ImageView close = (ImageView) view.findViewById(R.id.close_video);
        // Create a progressbar
        pDialog = new ProgressDialog(getActivity());
        // Set progressbar title
        pDialog.setTitle("Android Video Streaming Tutorial");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        // pDialog.show();

        try {
            // Start the MediaController
            MediaController mediacontroller = new MediaController(
                    getActivity());
            mediacontroller.setAnchorView(videoview);
            // Get the URL from String VideoURL
            Uri video = Uri.parse(questions.getqVideo().toString());
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new OnPreparedListener() {

            // Close the progress bar and play the video
            public void onPrepared(MediaPlayer mp) {

                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                        Log.d("Buffer", "------>Buffer" + i + "duration--" + mediaPlayer.getCurrentPosition());

                    }
                });
                set.cancel();
                //trivia_logo.setVisibility(View.GONE);
                buffer_layout.setVisibility(View.GONE);
                if (!videoview.isPlaying()) {
                    videoview.start();
                }

            }
        });

        videoview.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                set.cancel();
                buffer_layout.setVisibility(View.GONE);
                return false;
            }
        });


        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                //completed video

                StartQuiz.replaceFragmentWithoutHistory(new QuizScreen(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.QUESTION_SCREEN, true, (StartQuiz) getActivity());

            }
        });


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //close video and go to question
                StartQuiz.replaceFragmentWithoutHistory(new QuizScreen(pageCalled), TriviaConstants.SCREEN_TYPE, TriviaConstants.QUESTION_SCREEN, true, (StartQuiz) getActivity());

            }
        });
        return view;
    }

    /**
     * Starts animation 360 degree i.e verticall flipping to logo
     *
     * @param trivia_logo
     */

    private void startFlipAnimation(ImageView trivia_logo) {

        try {
            set = (AnimatorSet) AnimatorInflater.loadAnimator(StartQuiz.activity, R.animator.flipping_animation);
            set.setTarget(trivia_logo);
            set.cancel();
            set.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the layout from video_main.xml


    }


}