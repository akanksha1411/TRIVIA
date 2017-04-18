package toi.com.trivia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.List;

import toi.com.trivia.R;
import toi.com.trivia.model.PrizesItems;
import toi.com.trivia.utility.CommonUtility;

/**
 * Created by akanksha on 6/12/16.
 */
public class PrizesItemsBaseAdapter extends BaseAdapter {


    private Context baseContext;
    RecordHolder holder;
    int position;
    List<PrizesItems.Prizesss> _listDataChild;
    public static int[] rankImg = new int[]{
            R.drawable.rank_1,
            R.drawable.rank_2,
            R.drawable.rank_3

    };

    public PrizesItemsBaseAdapter(Context _context, List<PrizesItems.Prizesss> _listDataChild) {
        this.baseContext = _context;
        this._listDataChild = _listDataChild;
    }

    public PrizesItemsBaseAdapter(Context context, List<PrizesItems.Prizesss> childText, int[] rankImg, int[] imageId) {
        this.baseContext = context;
        this._listDataChild = childText;
        this.rankImg = rankImg;

    }

    @Override
    public int getCount() {
        return _listDataChild.size();
    }

    @Override
    public Object getItem(int i) {
        return _listDataChild.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        View convertView = view;

        position = i;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.prizes_items, parent, false);
            holder = new RecordHolder();
            holder.prizes_images = (ImageView) convertView.findViewById(R.id.prizes_images);
            holder.rank_img = (ImageView) convertView.findViewById(R.id.rank_img);
            holder.prizes_title = (TextView) convertView.findViewById(R.id.prizes_title);
            holder.prizes_desp = (TextView) convertView.findViewById(R.id.prizes_desp);
            convertView.setTag(holder);

        } else {
            holder = (RecordHolder) convertView.getTag();
        }
        try {
            PrizesItems.Prizesss list = _listDataChild.get(position);
            holder.prizes_title.setText(list.getname());
            holder.prizes_desp.setText(showDesp(position, list));
            if (baseContext != null) {
                Glide.with(baseContext)
                        .load(CommonUtility.checkNull(CommonUtility.checkNull(list.getImage())))
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.default_post_img)
                        .error(R.drawable.default_post_img)
                        .into(holder.prizes_images);
            }
                    /*.into(new BitmapImageViewTarget(holder.prizes_images) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(baseContext.getResources(),
                                    Bitmap.createScaledBitmap(resource, 0,30, false));
                            drawable.setCircular(true);
                            holder.prizes_images.setImageDrawable(drawable);
                        }
                    });
*/
            holder.rank_img.setImageResource(rankImg[position]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private String showDesp(int position, PrizesItems.Prizesss list) {
        String desp = "";
        String space = " ";
        if (position == 0) {
            desp = baseContext.getResources().getString(R.string.top_scorer_text) + space + baseContext.getResources().getString(R.string.winning_text) + space + list.getname();
        } else if (position == 1) {
            desp = baseContext.getResources().getString(R.string.first_scorer_text) + space + baseContext.getResources().getString(R.string.winning_text) + space + list.getname();
        } else if (position == 2) {
            desp = baseContext.getResources().getString(R.string.second_scorer_text) + space + baseContext.getResources().getString(R.string.winning_text) + space + list.getname();
        } else {
            desp = "Rank " + list.getRank() + space + baseContext.getResources().getString(R.string.winning_text) + space + list.getname();
        }

        return desp;
    }

    static class RecordHolder {
        private ImageView prizes_images, rank_img;
        private TextView prizes_title, prizes_desp;
    }

    public void updateItem(List<PrizesItems.Prizes> prizesList) {
        _listDataChild = new ArrayList<>();
        _listDataChild = prizesList.get(position).getPrizesss();
        notifyDataSetChanged();

    }
}
