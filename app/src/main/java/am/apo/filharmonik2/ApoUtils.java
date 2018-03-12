package am.apo.filharmonik2;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
        editor.apply();
    }

    public String getLanguage()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mApoContext);
        return preferences.getString(mApoContext.getString(R.string.pref_curr_lang), "eng");
    }
}
