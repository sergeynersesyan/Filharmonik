package am.apo.filharmonik;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by henrikgardishyan on 12/7/14.
 */
public class FavoritesHelper {

    Context mContext;
    boolean mMusicMode;

    public FavoritesHelper(Context context, boolean musicMode)
    {
        mContext = context;
        mMusicMode = musicMode;
    }

    public void addToFavorites(JSONObject obj)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        JSONArray tmpArray;
        try {
            tmpArray = new JSONArray(prefs.getString(mContext.getString(mMusicMode ? R.string.favorites_array_music : R.string.favorites_array_video), null));
        }
        catch(Exception e)
        {
            tmpArray = new JSONArray();
        }
        tmpArray.put(obj);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(mContext.getString(mMusicMode ? R.string.favorites_array_music : R.string.favorites_array_video), tmpArray.toString());
        editor.commit();
    }

    public void removeFromFavorites(JSONObject obj)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        JSONArray tmpArray = null;
        try {
            tmpArray = new JSONArray(prefs.getString(mContext.getString(mMusicMode ? R.string.favorites_array_music : R.string.favorites_array_video), null));

            JSONArray dstArray = new JSONArray();
            for (int i = 0; i < tmpArray.length(); ++i) {
                if (!tmpArray.getJSONObject(i).getString("id").equals(obj.getString("id"))) {
                    dstArray.put(tmpArray.getJSONObject(i));
                }
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(mContext.getString(mMusicMode ? R.string.favorites_array_music : R.string.favorites_array_video), dstArray.toString());
            editor.commit();
        }
        catch(Exception e) {}
    }

    public boolean isFavorite(JSONObject obj)
    {
        boolean foundInFavorites = false;

        try {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            String jsonStr = prefs.getString(mContext.getString(mMusicMode ? R.string.favorites_array_music : R.string.favorites_array_video), null);

            JSONArray tmpArray;
            if(null != jsonStr) {
                tmpArray = new JSONArray(jsonStr);
            }
            else
            {
                tmpArray = new JSONArray();
            }

            String myID = obj.getString("id");
            for (int i = 0; i < tmpArray.length() && !foundInFavorites; ++i) {
                String candidateID = tmpArray.getJSONObject(i).getString("id");
                if (myID.equals(candidateID)) {
                    foundInFavorites = true;
                }
            }
        }
        catch (JSONException e) {}

        return foundInFavorites;
    }

    public JSONArray getValidated(JSONArray realObjects) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        boolean favoritesMode = prefs.getBoolean(mContext.getString(mMusicMode ? R.string.favorites_mode_music : R.string.favorites_mode_video), false);
        JSONArray srcArray;

        if (favoritesMode) {
            try {
                JSONArray tmpArray = new JSONArray(prefs.getString(mContext.getString(mMusicMode ? R.string.favorites_array_music : R.string.favorites_array_video), null));
                srcArray = new JSONArray();

                for (int i = 0; i < tmpArray.length(); ++i) {
                    String favoriteID = tmpArray.getJSONObject(i).getString("id");
                    boolean foundInReals = false;

                    for (int k = 0; k < realObjects.length() && !foundInReals; ++k) {
                        String realID = realObjects.getJSONObject(k).getString("id");
                        if (favoriteID.equals(realID)) {
                            foundInReals = true;
                        }
                    }
                    if (foundInReals) {
                        srcArray.put(tmpArray.getJSONObject(i));
                    }
                }
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(mContext.getString(mMusicMode ? R.string.favorites_array_music : R.string.favorites_array_video), srcArray.toString());
                editor.commit();
            } catch (Exception e) {
                srcArray = null;
            }
        } else {
            srcArray = realObjects;
        }

        return srcArray;
    }
}
