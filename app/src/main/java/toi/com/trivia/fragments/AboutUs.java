package toi.com.trivia.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import toi.com.trivia.R;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AboutUs.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AboutUs#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutUs extends Fragment implements TriviaConstants {




    Context mContext;
    private OnFragmentInteractionListener mListener;
    int screen;

    public AboutUs() {
        // Required empty public constructor
    }

    public AboutUs(int policyScreen) {
        this.screen = policyScreen;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutUs.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutUs newInstance(String param1, String param2) {
        AboutUs fragment = new AboutUs();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);

        TextView textView = (TextView) view.findViewById(R.id.terms_text);
        textView.setText(Html.fromHtml(readTxt(screen)));
        // Inflate the layout for this fragment
        return view;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screen == ABOUT_US_SCREEN) {
            CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "T&C", "Back Press", TriviaConstants.CLICK,"Trivia_And_ Exit_TnC");
        } else if (screen == POLICY_SCREEN) {
            CommonUtility.updateAnalyticGtmEvent(mContext, GA_PREFIX + EXIT + "Privacy Policy", "Back Press", TriviaConstants.CLICK,"Trivia_And_Exit_Privacy_Policy");
        }


    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private String readTxt(int screen) {
        InputStream inputStream = null;
        if (screen == TriviaConstants.ABOUT_US_SCREEN) {
            //send pageview
            CommonUtility.updateAnalytics(getActivity(), "T&C");
            inputStream = getResources().openRawResource(R.raw.terms);
        } else {
            //send pageview
            CommonUtility.updateAnalytics(getActivity(), "Privacy Policy");
            inputStream = getResources().openRawResource(R.raw.privacy);
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int i;
        try {
            i = inputStream.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString();
    }
}
