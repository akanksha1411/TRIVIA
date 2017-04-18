package toi.com.trivia.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.model.PrizesItems;
import toi.com.trivia.utility.ui.NonScrollListView;

public class PrizesExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<PrizesItems.Prizesss>> _listDataChild;
    private int position;
    public static NonScrollListView single_day_prizes;
    public static PrizesItemsBaseAdapter itemsBaseAdapter;
    public static int[] imageId = new int[]{
            R.drawable.trophy_gold,
            R.drawable.silver_trophy,
            R.drawable.bronze_trophy,
            R.drawable.trophy_trans
    };

    public static int[] rankImg = new int[]{
            R.drawable.rank_1,
            R.drawable.rank_2,
            R.drawable.rank_3,
            R.drawable.trophy_trans
    };
    /*ImageView prizes_images, rank_img;
    private TextView prizes_desp, prizes_title;*/

    public PrizesExpandableListAdapter(Context context, List<String> listDataHeader,
                                       HashMap<String, List<PrizesItems.Prizesss>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }


    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(_listDataHeader.get(groupPosition));
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        try {
            final List<PrizesItems.Prizesss> childText = (List<PrizesItems.Prizesss>) getChild(groupPosition, childPosition);
        /*Random random = new Random();
        int maxIndex = childText.length;
        int generatedIndex = random.nextInt(maxIndex);*/

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.prize_daily_child, parent, false);
            }
            single_day_prizes = (NonScrollListView) convertView.findViewById(R.id.single_day_prizes);

            if ((groupPosition % 2) == 0) {
                convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archive_grey));
            } else {
                convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archieve_dark_grey));
            }
            if (childText != null && childText.size() != 0) {
                // if (groupPosition == 0) {
                itemsBaseAdapter = new PrizesItemsBaseAdapter(_context, childText);
                single_day_prizes.setAdapter(itemsBaseAdapter);
                /*}else{

                }*/
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            try {
                //LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.prizes_parent, parent, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if ((groupPosition % 2) == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archive_grey));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(_context, R.color.archieve_dark_grey));
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.archive_text);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
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