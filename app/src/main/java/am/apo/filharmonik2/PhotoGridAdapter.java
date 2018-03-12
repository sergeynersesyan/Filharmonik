package am.apo.filharmonik2;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

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

