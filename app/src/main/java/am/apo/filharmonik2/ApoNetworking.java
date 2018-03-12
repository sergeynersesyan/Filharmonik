package am.apo.filharmonik2;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 11/6/14.
 */
public class ApoNetworking {

    private final int SERVER_CHECK_TIMEOUT_MS = 10000;
    private final String FEED_URL = "http://filharmonik.com/feed.php";
    private String mCurrLang;
    private Context mApoContext;
    private String mSid;
    private String mResolution;
    private SharedPreferences mSectionPrefs;

    ApoNetworking(Context context, String sid, String resolution)
    {
        mApoContext = context;
        mCurrLang = ApoUtils.sharedUtils(mApoContext).getLanguage();
        mSid = sid;
        mResolution = resolution;
        mSectionPrefs = mApoContext.getSharedPreferences("section_" + mSid + "_" + mCurrLang, Context.MODE_PRIVATE);
    }

    // 0 => '@2x', 1 => '~ipad', 2 => '~ipad@2x'
    public void requestSection()
    {
        String lastCheckDateTime = mSectionPrefs.getString(mApoContext.getString(R.string.last_check_date_time), mApoContext.getString(R.string.pref_past_new_year));

        if(lastCheckDateTime.equals(mApoContext.getString(R.string.pref_past_new_year))) {
            updateCache();
        }
        else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date lastDate = sdf.parse(lastCheckDateTime);
                Date currDate = new Date();
                long diffInMS = currDate.getTime() - lastDate.getTime();

                if (diffInMS > SERVER_CHECK_TIMEOUT_MS) {
                    requestVersion();
                }
                else {
                    JSONObject lastSavedSection = new JSONObject(mSectionPrefs.getString(mApoContext.getString(R.string.last_check_response), ""));
                    onOK(lastSavedSection);
                }
            }
            catch(Exception e)
            {
                onError(mApoContext.getString(R.string.APO_NETWORK_ERROR));
            }
         }
    }

    public JSONObject getLastSaved()
    {
        try {
            return new JSONObject(mSectionPrefs.getString(mApoContext.getString(R.string.last_check_response), ""));
        }
        catch (Exception e){}

        return null;
    }

    private void onVersionRequest(String version)
    {
        try {
            JSONObject lastSavedSection = new JSONObject(mSectionPrefs.getString(mApoContext.getString(R.string.last_check_response), ""));

            if (lastSavedSection.getString("version").equals(version)) {
                onOK(lastSavedSection);
            }
            else
            {
                updateCache();
            }
        }
        catch (Exception e)
        {
            onError(mApoContext.getString(R.string.APO_NETWORK_ERROR));
        }
    }

    void onOK(JSONObject obj)
    {

    }

    void onError(String errStr)
    {

    }

    private void updateCache()
    {
        RequestParams params = new RequestParams();
        params.put("cmd", "feedall");
        params.put("lang", mCurrLang);
        params.put("sid", mSid);
        params.put("res", mResolution);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(mApoContext, FEED_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                try {
                    String jsonStr = new String(response);
                    onOK(new JSONObject(jsonStr));

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SharedPreferences.Editor editor = mSectionPrefs.edit();
                    editor.putString(mApoContext.getString(R.string.last_check_date_time), sdf.format(new Date()));
                    editor.putString(mApoContext.getString(R.string.last_check_response), jsonStr);
                    editor.apply();
                }
                catch(Exception e)
                {
                    onError(e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                onError(mApoContext.getString(R.string.APO_NETWORK_ERROR));
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    private void requestVersion()
    {
        RequestParams params = new RequestParams();
        params.put("cmd", "checkversions");
        params.put("lang", mCurrLang);
        params.put("sid", mSid);
        params.put("res", mResolution);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(mApoContext, FEED_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {

                try {
                    String jsonStr = new String(response);
                    JSONArray jsonArray = new JSONArray(jsonStr);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject sectionObj = jsonArray.getJSONObject(i);

                        if (mSid.equals(sectionObj.getString("sid"))) {
                            onVersionRequest(sectionObj.getString("version"));
                        }
                    }
                }
                catch (Exception e)
                {
                    onError(mApoContext.getString(R.string.APO_NETWORK_ERROR));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                try {
                    onError(mApoContext.getString(R.string.APO_NETWORK_ERROR));
                }
                catch (Exception ex){}
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }
}
