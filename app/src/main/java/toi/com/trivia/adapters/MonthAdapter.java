package toi.com.trivia.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import toi.com.trivia.R;

import toi.com.trivia.activities.Leaderboard_New;

/**
 * Created by Akanksha on 8/9/16.
 */
public class MonthAdapter extends BaseAdapter {

    String[] month;
    int resource;
    Context context;

    public MonthAdapter(Context applicationContext, int textview_months_item, String[] months) {
        this.context = applicationContext;
        this.resource = textview_months_item;
        this.month = months;
    }


    @Override
    public int getCount() {
        return month.length;
    }

    @Override
    public Object getItem(int i) {
        return month[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // assign the view we are converting to a local variable
        View v = convertView;
        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(resource, null);
        }

        TextView monthName = (TextView) v.findViewById(android.R.id.text1);
        monthName.setText(month[position]);
        monthName.setGravity(Gravity.CENTER);
        monthName.setTextSize(17);

        if ((position) == Leaderboard_New.selectedMonthPos) {
            monthName.setTextColor(context.getResources().getColor(R.color.yellow));
        } else {
            monthName.setTextColor(context.getResources().getColor(R.color.black));
        }
        notifyDataSetChanged();
        return v;
    }
}
