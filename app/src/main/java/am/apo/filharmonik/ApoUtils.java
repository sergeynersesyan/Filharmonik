package am.apo.filharmonik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Created by user on 11/5/14.
 */
public class ApoUtils {

    private static ApoUtils singleton = null;
    private static Context mApoContext;

    public static ApoUtils sharedUtils(Context context)
    {
        if(null != context) {
            mApoContext = context;
        }

        if(null==singleton)
        {
            singleton = new ApoUtils();
        }
        return  singleton;
    }

    ApoUtils()
    {
    }

    public void setLanguage(String currLang)
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mApoContext);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(mApoContext.getString(R.string.pref_curr_lang), currLang);
        editor.commit();
    }

    public String getLanguage()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mApoContext);
        return preferences.getString(mApoContext.getString(R.string.pref_curr_lang), "eng");
    }
}
