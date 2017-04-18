package toi.com.trivia.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;

import com.github.androidprogresslayout.ProgressLayout;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import toi.com.trivia.R;
import toi.com.trivia.adapters.ArchiveListAdapter;
import toi.com.trivia.api.APICalls;
import toi.com.trivia.model.ArchiveItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;
import toi.com.trivia.utility.TriviaConstants;

public class GameArchive extends AppCompatActivity implements TriviaConstants {
    private static final String TAG = "GameArchive";
    static ArchiveListAdapter listAdapter;
    public static ExpandableListView expListView;
    static List<String> listDataHeader = new ArrayList<>();
    static HashMap<String, List<ArchiveItems.Result>> listDataChild = new HashMap<>();
    static ReadPref readPref;
    static String UID;
    public static ProgressLayout progressLayout;
    public static AppCompatActivity activity;
    SavePref savePref;
    public static Context context;
    int prevPos = 0;
    BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!CommonUtility.haveNetworkConnection(context)) {
                Snackbar.make(getWindow().getDecorView(), TriviaConstants.No_INTERNET, Snackbar.LENGTH_SHORT).show();
               /*Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), TriviaConstants.No_INTERNET, Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        CommonUtility.fetchArchive(getApplicationContext(), String.valueOf(UID), TriviaConstants.GAME_END);
                    }
                });

                snackbar.show();*/
            } else {


               /* ReadPref readPref = new ReadPref(context);
                SavePref savePref = new SavePref(context);
                String mapString = readPref.getUserAnswer();
                // use properties to restore the map
                HashMap<String, String> map2 = new HashMap<String, String>();

                if (mapString.length() != 0) {
                    Properties props = new Properties();
                    try {
                        props.load(new StringReader(mapString.substring(1, mapString.length() - 1).replace(", ", "\n")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (Map.Entry<Object, Object> e : props.entrySet()) {
                        map2.put((String) e.getKey(), (String) e.getValue());
                    }
                    map2 = CommonUtility.loadAnswerMap(mapString);

                    savePref.saveUserAnswer("");

                    APICalls.SubmitAnswers((AppCompatActivity) context, map2, 0);//flag=0 for checking request
                }*/

            }
        }


    };


    @Override
    protected void onStart() {
        super.onStart();

        Bundle b = getIntent().getExtras();
        int screenType = b.getInt(SCREEN_TYPE);

        if (screenType == GAME_END) {
            //GA entry ANALYTICS

            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Game End Page", CLICK, "Trivia_And_Entry_Game_Archive");
        } else if (screenType == RESULT_OUT_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Result Announced Page", CLICK, "Trivia_And_Entry_Game_Archive");
        } else if (screenType == DASHBOARD_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Dashboard", CLICK, "Trivia_And_Entry_Game_Archive");


        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_archive);
        readPref = new ReadPref(getApplicationContext());
        savePref = new SavePref(getApplicationContext());
        UID = readPref.getUID();
        activity = this;

        CommonUtility.fetchArchive(getApplicationContext(), String.valueOf(UID), TriviaConstants.GAME_END);
        //register reciever for network connection change
        initNetworkStateReciever();
        progressLayout = (ProgressLayout) findViewById(R.id.progress_layout);
        progressLayout.showProgress();
        context = getApplicationContext();
        //CommonUtility.initGoogleTagManager(context);

        final String sponsorName = readPref.getSponsorName();
        initToolbar();
        CommonUtility.initBackground(getWindow().getDecorView().getRootView(), getApplicationContext(), GAME_ARCHIVE, sponsorName);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expListView.setIndicatorBounds(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
            //expListView.setIndicatorBounds(width - GetPixelFromDips(40), width - GetPixelFromDips(10));
        } else {
            expListView.setIndicatorBoundsRelative(width - GetPixelFromDips(35), width - GetPixelFromDips(5));
          //  expListView.setIndicatorBoundsRelative(width - GetPixelFromDips(40), width - GetPixelFromDips(10));
        }


        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(final int groupPosition) {
                if (groupPosition != prevPos)
                    expListView.collapseGroup(prevPos);

                prevPos = groupPosition;
                // expListView.setSelectedGroup(groupPosition);
                savePref.saveLastArchivePointer(String.valueOf(groupPosition));

            }
        });
        //CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), "Trivia_And_Login", "Game End Page", CLICK);

        //CommonUtility.pushCokeEvent(getApplicationContext(),"Trivia_And_Login", "ArchiveSection","", "TriviaPath","");
        //send pageview
        CommonUtility.updateAnalytics(context, "Game Archive");
        Bundle b = getIntent().getExtras();
        int screenType = b.getInt(SCREEN_TYPE);

        if (screenType == GAME_END) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Game End Page", CLICK, "Trivia_And_Entry_Game_Archive");
        } else if (screenType == RESULT_OUT_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Result Announced Page", CLICK, "Trivia_And_Entry_Game_Archive");
        } else if (screenType == DASHBOARD_SCREEN) {
            //GA entry ANALYTICS
            CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + ENTRY + "Game Archive", "Dashboard", CLICK, "Trivia_And_Entry_Game_Archive");
        }


        String mapString = readPref.getUserAnswer();
        // use properties to restore the map
        HashMap<String, String> map2 = new HashMap<String, String>();

        if (mapString.length() != 0) {
            Properties props = new Properties();
            try {
                props.load(new StringReader(mapString.substring(1, mapString.length() - 1).replace(", ", "\n")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Map.Entry<Object, Object> e : props.entrySet()) {
                map2.put((String) e.getKey(), (String) e.getValue());
            }
            map2 = CommonUtility.loadAnswerMap(mapString);

            savePref.saveUserAnswer("");

            APICalls.SubmitAnswers(activity, map2, 0);//flag=0 for checking request
        }
    }

    public int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    /**
     * Gets the Content from api and called from the api success response
     * sets the value to respective sections
     */
    public static void initUI() {
        ArchiveItems items = new ArchiveItems();
        items = APICalls.getArchiveItems();

        try {
            if (expListView != null) {
                listDataHeader = new ArrayList<String>();
                listDataChild = new HashMap<>();

                //adding the values in headers list and childs hashmap
                for (int i = 0; i < items.getGames().size(); i++) {
                    ArchiveItems.Games list = items.getGames().get(i);
                    String header = "<html><b>" + list.getDate().toString() + "</b></html>" /*+ " " + " | " + "PLAYED:" + (list.getResult().size() - list.getGameRemain()) + " " + "| GAMES REMAINING: " + String.valueOf(list.getGameRemain())                String header = "<html><b>" + list.getDate().toString() + "</b></html>" /*+ " " + " | " + "PLAYED:" + (list.getResult().size() - list.getGameRemain()) + " " + "| GAMES REMAINING: " + String.valueOf(list.getGameRemain())*/;

                    listDataHeader.add(header);

                    //putting haspmap with child quizes to be shown in gridview
                    for (int j = 0; j < list.getResult().size(); j++) {
                        listDataChild.put(header, list.getResult());
                    }
                }
                listAdapter = new ArchiveListAdapter(activity, listDataHeader, listDataChild, UID, items);
                // setting list adapter
                expListView.setAdapter(listAdapter);
                //added so that 0 position is always open when you open archive page
                String lastPointer = readPref.getLastArchivePointer();
                expListView.expandGroup(Integer.parseInt(lastPointer));

                // Listview Group click listener
                expListView.setOnGroupClickListener(new OnGroupClickListener() {

                    @Override

                    public boolean onGroupClick(ExpandableListView parent, View v,
                                                int groupPosition, long id) {

                        // ImageView groupIndicator = (ImageView) v.findViewById(R.id.archive_expand_sign);

                    /*int lastPosition = Integer.parseInt(readPref.getLastArchivePointer());
                    if (groupPosition != lastPosition) {
                        parent.collapseGroup(lastPosition);
                    }
                    expListView.smoothScrollToPosition(groupPosition);
                    expListView.setItemsCanFocus(true);
                    TextView groupIndicator = (TextView) v.findViewById(R.id.archive_text);

                    if (parent.isGroupExpanded(groupPosition)) {
                        parent.collapseGroup(groupPosition);
                        groupIndicator.setImageResource(R.drawable.down_arrow_24);
                    } else {
                        parent.expandGroup(groupPosition);

                        groupIndicator.setImageResource(R.drawable.up_arrow_24);
                    }

                    return true;

                        groupIndicator.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up_arrow_24, 0);
                    }*/


                        return false;

                    }
                });


                progressLayout.showContent();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * set up toolbar for game archive page
     */
    public void initToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(getResources().getString(R.string.game_archive));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //GA entry ANALYTICS
                CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Game Archive", BACK_ARROW, TriviaConstants.CLICK, "Trivia_And_Exit_Game_Archive");
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
        registerReceiver(networkStateReceiver, filter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onresume archive", "--called");
        if (!CommonUtility.haveNetworkConnection(context)) {
            Snackbar.make(getWindow().getDecorView(), TriviaConstants.No_INTERNET, Snackbar.LENGTH_SHORT).show();
        } else {


            ReadPref readPref = new ReadPref(context);
            SavePref savePref = new SavePref(context);
            String mapString = readPref.getUserAnswer();
            // use properties to restore the map
            HashMap<String, String> map2 = new HashMap<String, String>();

            if (mapString.length() != 0) {
                Properties props = new Properties();
                try {
                    props.load(new StringReader(mapString.substring(1, mapString.length() - 1).replace(", ", "\n")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (Map.Entry<Object, Object> e : props.entrySet()) {
                    map2.put((String) e.getKey(), (String) e.getValue());
                }
                map2 = CommonUtility.loadAnswerMap(mapString);

                savePref.saveUserAnswer("");

                APICalls.SubmitAnswers(activity, map2, 0);//flag=0 for checking request
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //destroy the receiver for network connection
        unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //GA entry ANALYTICS

        CommonUtility.updateAnalyticGtmEvent(getApplicationContext(), GA_PREFIX + EXIT + "Game Archive", DEVICE_BACK, TriviaConstants.CLICK, "Trivia_And_Exit_Game_Archive");

    }
}