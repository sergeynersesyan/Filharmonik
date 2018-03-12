package am.apo.filharmonik2;

import android.animation.Animator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 11/14/14.
 */
public class PlayerGridAdapter extends BaseAdapter{
    private Activity mActivity;
    private JSONArray mJsonData;
    private JSONArray mJsonDataFiltered;
    private Animator mCurrentAnimator;
    private FavoritesHelper mFavoritesHelper;
    private int mShortAnimationDuration;
    private boolean mMusicMode;
    private  String mFilterText;

    public PlayerGridAdapter(Activity activity, JSONObject jsonObj, boolean musicMode)
    {
        mActivity = activity;
        mMusicMode = musicMode;
        mFavoritesHelper = new FavoritesHelper(mActivity.getApplicationContext(), mMusicMode);
        mFilterText = null;

        mShortAnimationDuration = 500;//mActivity.getResources().getInteger(android.R.integer.config_shortAnimTime);

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
        return null==mJsonDataFiltered ? 0 : mJsonDataFiltered.length();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Object getItem(int i) {

        try{
            return mJsonDataFiltered.getJSONObject(i);
        }
        catch (JSONException e){}
        return null;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        try {
            View lView = view;
            if (lView == null) {
                lView = LayoutInflater.from(mActivity).inflate(R.layout.player_grid_item, viewGroup, false);
            }

            final ImageView imageButton = (ImageView) lView.findViewById(R.id.player_grid_image);
            final TextView textView = (TextView) lView.findViewById(R.id.player_grid_title);
            textView.setText(mJsonDataFiltered.getJSONObject(i).getString(ApoContract.APO_JSON_TITLE));

            final String thumbURL = mJsonDataFiltered.getJSONObject(i).getString(ApoContract.APO_JSON_THUMBNAIL);


            ImageLoader.getInstance().displayImage(thumbURL, imageButton);

/*            ImageLoader.getInstance().loadImage(thumbURL, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {

                    imageButton.setImageBitmap(loadedImage);
                }
            });
*/

            return lView;
        }
        catch (Exception e){}

        return null;
    }

    public void setFilterText(String filterText)
    {
        JSONArray srcArray;

        srcArray = mFavoritesHelper.getValidated(mJsonData);

        mFilterText = filterText;
        if(null==mFilterText)
        {
            mJsonDataFiltered = srcArray;
        }
        else
        {
            String filterTextL = mFilterText.toLowerCase();
            mJsonDataFiltered = new JSONArray();
            for(int i = 0; i < srcArray.length(); ++i)
            {
                try{
                  String title = srcArray.getJSONObject(i).getString("title").toLowerCase();
                  if(title.contains(filterTextL))
                  {
                      mJsonDataFiltered.put(srcArray.getJSONObject(i));
                  }
                }
                catch (JSONException e)
                {}
            }
        }
    }
}
