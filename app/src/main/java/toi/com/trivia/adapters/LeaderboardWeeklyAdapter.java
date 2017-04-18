package toi.com.trivia.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import toi.com.trivia.R;

import toi.com.trivia.activities.Leaderboard_New;
import toi.com.trivia.model.LeaderboardItems;
import toi.com.trivia.prefs.ReadPref;
import toi.com.trivia.prefs.SavePref;
import toi.com.trivia.utility.CommonUtility;

/**
 * Created by akanksha on 21/1/16.
 */
public class LeaderboardWeeklyAdapter extends BaseAdapter {
    Context baseContext;
    List<LeaderboardItems.Rankings> feedsList = new ArrayList<>();
    int stepCount;
    int qId;
    SavePref savePref;
    Boolean flag = true;
    int uid;
    ReadPref readPref;

    public LeaderboardWeeklyAdapter(Context baseContext, List<LeaderboardItems.Rankings> feedsList, String UID) {
        this.baseContext = baseContext;
        this.feedsList = feedsList;
        //this.uid = Integer.parseInt(UID);
    }


    public void updateItem(List<LeaderboardItems.Rankings> items) {
        feedsList = new ArrayList<>();
        feedsList = items;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return feedsList.size();
    }

    @Override
    public Object getItem(int position) {
        return feedsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        RecordHolder holder = null;
        savePref = new SavePref(baseContext);
        readPref = new ReadPref(baseContext);
        uid = Integer.parseInt(readPref.getUID());
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) baseContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.leaderboard_items, parent, false);
            holder = new RecordHolder();
            holder.rank_no = (TextView) row.findViewById(R.id.rank_no);
            holder.user_score = (TextView) row.findViewById(R.id.user_score);
            holder.user_name = (TextView) row.findViewById(R.id.user_name);
            holder.user_image = (ImageView) row.findViewById(R.id.user_image);
            holder.trophy = (ImageView) row.findViewById(R.id.trophy);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }
        final LeaderboardItems.Rankings mDataset = feedsList.get(position);

        try {
            holder.user_name.setText(mDataset.getName());
            holder.rank_no.setText(CommonUtility.roundedRankText(mDataset.getRank()));
            holder.user_score.setText(String.valueOf(mDataset.getScore()));
            final int is_won = (mDataset.getIswon());
            /*final RecordHolder finalHolder1 = holder;
            final RecordHolder finalHolder2 = holder;*/

            openImage(holder.user_image, mDataset.getImgurl(), String.valueOf(mDataset.getUid()));
            if ((position % 2) == 0) {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.archive_grey));
            } else {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.grey));
            }
            int user_rank = 0;
            try {
                user_rank = Integer.parseInt(mDataset.getRank());

            } catch (Exception e) {
                e.printStackTrace();
            }
            CommonUtility.showTrophyImage(holder.trophy, is_won, uid, mDataset.getUid(), user_rank);


           /* if (position == 5) {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.yellow));
                holder.user_name.setTextColor(baseContext.getResources().getColor(R.color.black));
                holder.user_score.setTextColor(baseContext.getResources().getColor(R.color.black));
                holder.user_name.setText(readPref.getUserName());
                openImage(holder.user_image, readPref.getUserImage(), String.valueOf(mDataset.getUid()));
            } else {
                holder.user_name.setTextColor(baseContext.getResources().getColor(R.color.white));
                holder.user_score.setTextColor(baseContext.getResources().getColor(R.color.white));
            }*/

            if (uid == mDataset.getUid()) {
                row.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.yellow));
                holder.user_name.setTextColor(baseContext.getResources().getColor(R.color.black));
                holder.user_score.setTextColor(baseContext.getResources().getColor(R.color.black));
                if (is_won == 1) {
                    if (user_rank >= 4) {
                        holder.trophy.setImageResource(R.drawable.black_gen_trophy);
                    }

                }
            } else {
                holder.user_name.setTextColor(baseContext.getResources().getColor(R.color.white));
                holder.user_score.setTextColor(baseContext.getResources().getColor(R.color.white));
            }
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (is_won == 1) {
                        Leaderboard_New.openPrizesView(mDataset.getUid(), mDataset.getPname(), mDataset.getName(), mDataset.getPimgurl(), mDataset.getRank(), baseContext);
                    } else {

                    }
                }
            });
            notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return row;
    }

    static class RecordHolder {
        TextView rank_no, user_name, user_score;
        ImageView user_image, trophy;

    }

    /**
     * set image to imageview for user
     *
     * @param quiz_image
     * @param url
     */
    private void openImage(final ImageView quiz_image, String url, String uid) {
        try {
            File profilePicture = new File(Environment.getExternalStorageDirectory()
                    .getPath()
                    + "/Android/data/" + baseContext.getPackageName() + "/files/Trivia/"
                    + "TriviaProfilePicture" + "_" + uid + ".jpg");
            if (profilePicture.exists()) {
                Log.d("file image", "called");
                Bitmap myBitmap = BitmapFactory.decodeFile(profilePicture.getAbsolutePath());
                quiz_image.setImageBitmap(myBitmap);
            } else {
                if (CommonUtility.chkString(url)) {
                    Log.d("url image", "called");
                 /*   Picasso.with(baseContext).load(CommonUtility.checkNull(url)).transform(new CommonUtility.RoundedTransformation(10, 0))
                            .placeholder(R.drawable.default_post_img)
                            .error(R.drawable.default_post_img)
                            .into(quiz_image);*/
                    if (baseContext != null) {
                        Glide.with(baseContext)
                                .load(CommonUtility.checkNull(CommonUtility.checkNull(url)))
                                .asBitmap()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)

                                .placeholder(R.drawable.default_post_img)
                                .error(R.drawable.default_post_img)
                                .into(quiz_image);
                    }
                } else {
                   /* Bitmap icon = BitmapFactory.decodeResource(baseContext.getResources(),
                            R.drawable.default_avatar);
                    quiz_image.setImageBitmap(icon);*/

                    quiz_image.setImageBitmap(decodeSampledBitmapFromResource(baseContext.getResources(), R.drawable.default_avatar, 100, 100));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
