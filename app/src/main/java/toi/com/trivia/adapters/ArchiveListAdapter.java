package toi.com.trivia.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.model.ArchiveItems;
import toi.com.trivia.prefs.ReadPref;


public class ArchiveListAdapter extends BaseExpandableListAdapter {

    private AppCompatActivity _context;
    private List<String> _listDataHeader = new ArrayList<>(); // header titles
    // child data in format of header title, child title
    private HashMap<String, List<ArchiveItems.Result>> _listDataChild = new HashMap<>();
    public static ImageView icon;
    String UID;
    public ArchiveGridAdapter adapter;
    ArchiveItems items = new ArchiveItems();

    public ArchiveListAdapter(AppCompatActivity context, List<String> listDataHeader,
                              HashMap<String, List<ArchiveItems.Result>> listChildData, String UID, ArchiveItems items) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.UID = UID;
        this.items = items;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return _listDataChild.get(_listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        try {
            List<ArchiveItems.Result> test = (List<ArchiveItems.Result>) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.game_child, parent, false);
            }

            GridView txtListChild = (GridView) convertView
                    .findViewById(R.id.archive_quiz_grid);


            if ((groupPosition % 2) == 0) {
                convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archive_grey));
            } else {
                convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archieve_dark_grey));
            }
            if (test != null) {
                //set adapter for childview
                adapter = new ArchiveGridAdapter(_context, test, UID);
                txtListChild.setAdapter(adapter);


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, final ViewGroup parent) {


        final ReadPref readPref = new ReadPref(_context);
        String headerTitle = (String) getGroup(groupPosition);

        final RecordHolder holder;
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.game_parent, parent, false);
            holder = new RecordHolder();
            holder.lblListHeader = (TextView) convertView.findViewById(R.id.archive_text);
            holder.games_count = (TextView) convertView.findViewById(R.id.games_count);
            convertView.setTag(holder);

        } else {
            holder = (RecordHolder) convertView.getTag();
        }

        if ((groupPosition % 2) == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archive_grey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archieve_dark_grey));
        }


        holder.lblListHeader.setText(Html.fromHtml(headerTitle));
        holder.games_count.setText(Html.fromHtml(String.valueOf(items.getGames().get(groupPosition).getResult().size() - items.getGames().get(groupPosition).getGameRemain()) + "/" + "<font color='#FFD200'>" + items.getGames().get(groupPosition).getResult().size() + "</font>"));
        /*List<String> pstatus = CommonUtility.convertCommaStringToList(items.getGames().get(groupPosition).getPstatus());
        int x = holder.ticks_layout.getChildCount();
        if (x == 0) {
            for (int i = 0; i < pstatus.size(); i++) {
                if (i < 4) {
                    TextView tick = new TextView(_context);
                    tick.setTextColor(ContextCompat.getColor(_context, R.color.white));
                    tick.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams params = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        params = new LinearLayout.LayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        params.setMargins(10, 0, 0, 0);
                        tick.setLayoutParams(params);
                    }

                    // tick.setId(i);
                    if (Integer.parseInt(pstatus.get(i)) == TriviaConstants.DEFAULT_ONE) {
                        tick.setBackgroundResource(R.drawable.played_game_tick);
                    } else {
                        tick.setText(String.valueOf(i + 1));
                        tick.setBackgroundResource(R.drawable.non_played_count_back);
                    }

                    holder.ticks_layout.addView(tick);
                }
            }
        }*/

      /*  List<ArchiveItems.Result> child = _listDataChild.get(headerTitle);
        Iterator<ArchiveItems.Result> iterator = child.iterator();*/
        /*int i = 0;
        int x = holder.ticks_layout.getChildCount();
        if (x == 0) {
            while (iterator.hasNext() && i < 4) {
                ArchiveItems.Result quiz = iterator.next();
                String playStatus = quiz.getPlayStatus();

                final TextView lblListHeader = (TextView) convertView
                        .findViewById(R.id.archive_text);


                TextView tick = new TextView(_context);
                tick.setTextColor(ContextCompat.getColor(_context, R.color.white));
                tick.setGravity(Gravity.CENTER);

                // tick.setId(i);
                if (Integer.parseInt(playStatus) == TriviaConstants.DEFAULT_ONE) {
                    tick.setBackgroundResource(R.drawable.played_game_tick);
                } else {
                    tick.setText(String.valueOf(i + 1));
                    tick.setBackgroundResource(R.drawable.non_played_count_back);
                }

                holder.ticks_layout.addView(tick);
                i++;
            }
*//*
            for (int j = 0; j < child.size(); j++) {
                if (j < 4) {
                    ArchiveItems.Result quiz = iterator.next();
                    String playStatus = quiz.getPlayStatus();

                    TextView tick = new TextView(_context);
                    tick.setTextColor(ContextCompat.getColor(_context, R.color.white));
                    tick.setGravity(Gravity.CENTER);
                    // tick.setId(i);
                    if (Integer.parseInt(playStatus) == TriviaConstants.DEFAULT_ONE) {
                        tick.setBackgroundResource(R.drawable.played_game_tick);
                    } else {
                        tick.setText(String.valueOf(j + 1));
                        tick.setBackgroundResource(R.drawable.non_played_count_back);
                    }

                    holder.ticks_layout.addView(tick);
                }
            }*//*

        }*/

        return convertView;
    }

    public static class RecordHolder {
        private TextView lblListHeader, rank_img;
        private TextView prizes_title, prizes_desp;
        private TextView games_count;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
