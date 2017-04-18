package toi.com.trivia.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.github.androidprogresslayout.ProgressLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.adapters.PrizesExpandableListAdapter;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.model.PrizesItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PrizesWeek.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PrizesWeek#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrizesWeek extends Fragment implements TriviaConstants {

    private static final String ARG_SECTION_NUMBER = "section_number";
    public static ExpandableListView expListView;
    public static PrizesExpandableListAdapter listAdapter;
    private static PrizesItems.Prizes prizes;
    private static Context context;
    private static List<String> listDataHeader;
    public static HashMap<String, List<PrizesItems.Prizesss>> listDataChild = new HashMap<>();
    static ReadPref readPref;
    private static ProgressLayout progressLayout;

    public PrizesWeek() {
        // Required empty public constructor
    }

    public static PrizesWeek newInstance(int sectionNumber) {
        PrizesWeek fragment = new PrizesWeek();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readPref = new ReadPref(getActivity());


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_prizesdaily, container, false);
        expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        context = getActivity();
        String ntw = CommonUtility.getNetworkType(context);
        progressLayout = (ProgressLayout) view.findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        final String currrentGameId = readPref.getCurrentGameId();
        HashMap<String, String> map = new HashMap<>();
        map.put(PARAM_GAME_ID, currrentGameId); // A FOR ANDROID
        map.put("mode", "W");
        map.put("prize_time", CommonUtility.getCurrentTimeStamp());
        map.put(PARAM_NETWORK, ntw);
        APICalls.triviaPrizes(getActivity(), map);

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
         //   expListView.setIndicatorBounds(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        } else {
            expListView.setIndicatorBoundsRelative(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
           // expListView.setIndicatorBoundsRelative(width - GetPixelFromDips(50), width - GetPixelFromDips(10));
        }
        //initUI();
        return view;


    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    public static void initUI() {

        PrizesItems prizesItems = new PrizesItems();
        prizesItems = APICalls.getPrizesItemsWeekly();
        final PrizesItems items = prizesItems;
        try {
            System.out.println("CALLED !!");
            // listDataChild = new ArrayList<>();

            listDataHeader = new ArrayList<String>();
            listDataChild = new HashMap<>();
            //adding the values in headers list and childs hashmap

            if (items.getPrizes().size() != 0) {
                for (int i = 0; i < items.getPrizes().size(); i++) {
                    PrizesItems.Prizes list = items.getPrizes().get(i);
                    String header = list.getTitle().toString();
                    listDataHeader.add(header);

                    //putting haspmap with child quizes to be shown in gridview
                    // for (int j = 0; j < list.getPrizesss().size(); j++) {
                    if (i == 0) {
                        listDataChild.put(header, list.getPrizesss());
                    }
                    // }
                }
            }
            listAdapter = new PrizesExpandableListAdapter(context, listDataHeader, listDataChild);
            expListView.setAdapter(listAdapter);
            expListView.expandGroup(0);


            // Listview Group expanded listener
            expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {
                    //expListView.setSelectedGroup(groupPosition);
                    PrizesItems.Prizes list = items.getPrizes().get(groupPosition);
                    String header = list.getTitle().toString();

                    //putting haspmap with child quizes to be shown in gridview
                    for (int j = 0; j < list.getPrizesss().size(); j++) {
                        listDataChild.put(header, list.getPrizesss());
                    }
                    listAdapter.notifyDataSetChanged();


                }
            });

            // Listview Group collasped listener
            expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

                @Override
                public void onGroupCollapse(int groupPosition) {

                }
            });

            // Listview on child click listener
            expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {


         /*
           Toast.makeText(
          getClass(),
                            listDataHeader.get(groupPosition)
                                    + " : "
                                    + listDataChild.get(
                                    listDataHeader.get(groupPosition)).get(
                                    childPosition), Toast.LENGTH_SHORT).show();
*/

                    return false;
                }
            });

            expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {

                   /* TextView groupIndicator = (TextView) v.findViewById(R.id.archive_text);
                    if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                        groupIndicator.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down_arrow_24, 0);
                    } else {
                        parent.expandGroup(groupPosition);
                        groupIndicator.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow_24, 0);
                    }
*/
                    return false;
                }
            });


            progressLayout.showContent();


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

 /*   public void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, String[]>();

        // Adding child data
        listDataHeader.add("December 2016  |   Week2");
        listDataHeader.add("December 2016  |   Week1");
        listDataHeader.add("November 2016  |   Week4");
        listDataHeader.add("November 2016  |   Week3");
        listDataHeader.add("November 2016  |   Week2");
        listDataHeader.add("November 2016  |   Week1");

        listDataChild.put(listDataHeader.get(0), getResources().getStringArray(R.array.prizes_array)); // Header, Child data
        listDataChild.put(listDataHeader.get(1), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(2), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(3), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(4), getResources().getStringArray(R.array.prizes_array));
        listDataChild.put(listDataHeader.get(5), getResources().getStringArray(R.array.prizes_array));// Header, Child data

    }*/
}