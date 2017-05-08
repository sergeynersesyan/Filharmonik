package am.apo.filharmonik;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by henrikgardishyan on 11/8/14.
 */
public class PhotoGridAdapter extends BaseAdapter{

    Activity mActivity;
    JSONArray mJsonData;

    public PhotoGridAdapter(Activity activity, JSONObject jsonObj)
    {
        mActivity = activity;

        try {
            mJsonData = jsonObj.getJSONArray("data");
        }
        catch(JSONException e)
        {
            mJsonData = null;
        }
    }

    @Override
    public int getCount() {
        return null==mJsonData ? 0 : mJsonData.length();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {
        try {
            return mJsonData.getJSONObject(i);
        }
        catch (JSONException e){}

        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        try {
            View lView;
            if(null==view ) {
                LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                lView = inflater.inflate(R.layout.photo_grid_item, null);
                //lView = LayoutInflater.from(mActivity).inflate(R.layout.photo_grid_item, viewGroup, false);
            }
            else
            {
                lView = view;
            }

            final RoundedImageView imageButton = (RoundedImageView) lView.findViewById(R.id.photo_grid_image);

            final String thumbURL = mJsonData.getJSONObject(i).getString(ApoContract.APO_JSON_THUMBNAIL);

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

            ImageLoader.getInstance().displayImage(thumbURL, imageButton, options);

            return lView;
        }
        catch (Exception e){}

        return null;
    }
}

