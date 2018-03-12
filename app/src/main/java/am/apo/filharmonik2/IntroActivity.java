package am.apo.filharmonik2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class IntroActivity extends ApoFullScreenActivity {

    private final String ADD_URL = "http://filharmonik.com/api.php";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private GoogleCloudMessaging mGcm;
    private AtomicInteger msgId = new AtomicInteger();
    private Context mContext;
    private String mRegID;
    private String SENDER_ID = "335176342881";
    private ApoNetworking mNetworker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ImageButton startButton = (ImageButton) findViewById(R.id.start_button);
        Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        startButton.startAnimation(pulse);

//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent menuIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
//                startActivity(menuIntent);
//                finish();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mContext = getApplicationContext();

        if (checkPlayServices()) {
            mGcm = GoogleCloudMessaging.getInstance(this);
            mRegID = getRegistrationId(mContext);

            if (mRegID.isEmpty()) {
                Log.e(TAG, "Push registration not found, going to register...");

                registerInBackground();
            } else {
                Log.e(TAG, "Push registration found: " + mRegID);
            }
        }
        Intent menuIntent = new Intent(getApplicationContext(), MainMenuActivity.class);
        startActivity(menuIntent);
        finish();
//        updateBackgroundImage();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS != resultCode) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(mContext, getString(R.string.PUSH_NOT_SUPPORTED), Toast.LENGTH_LONG);
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {

        final SharedPreferences prefs = getSharedPreferences(ApoFullScreenActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (!registrationId.isEmpty()) {
            int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
            int currentVersion = getAppVersion(context);
            if (registeredVersion != currentVersion) {
                Log.i(TAG, "App version changed.");
                return "";
            }
        }
        return registrationId;
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void registerInBackground() {

        AsyncTask<Void, Void, String> bkgTask = new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    if (null == mGcm) {
                        mGcm = GoogleCloudMessaging.getInstance(mContext);
                    }
                    mRegID = mGcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + mRegID;

                } catch (IOException ex) {
                    msg = "Push registration error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(final String state) {
                super.onPostExecute(state);

                Log.e(TAG, state);

                if (!state.contains("error")) {
                    sendRegistrationIdToBackend();
                }
            }
        };

        bkgTask.execute((Void) null);
    }

    private void sendRegistrationIdToBackend() {

        Log.e(TAG, "Sending reg ID to filharmonik server: " + mRegID);

        RequestParams params = new RequestParams();
        params.put("cmd", "register");
        params.put("dt", mRegID);
        params.put("dp", "1");

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(getApplicationContext(), ADD_URL, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                Log.e(TAG, "RegID successfully sent to server, storing...");
                storeRegistrationId(mContext, mRegID);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                Log.e(TAG, "Failed to send RegID to server...");
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
            }
        });
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getSharedPreferences(ApoFullScreenActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion(context);
        Log.e(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    // not using now
    public void updateBackgroundImage() {
        final SharedPreferences prefs = getSharedPreferences(ApoFullScreenActivity.class.getSimpleName(), Context.MODE_PRIVATE);

        String url = prefs.getString(ApoContract.APO_JSON_IMAGE, null);
        if (null != url) {
            Log.e(TAG, "Loading background section with url: " + url);

            setIntroBackground(url, false, prefs);
        }

        Log.e(TAG, "Checking background section with resolution: " + getString(R.string.device_res_id) + ", existing url: " + url);
        mNetworker = new ApoNetworking(this, ApoContract.APO_BACKGROUND, getString(R.string.device_res_id)) {

            @Override
            void onOK(JSONObject obj) {

                try {
                    JSONArray jsonArr = obj.getJSONArray("data");
                    JSONObject item = jsonArr.getJSONObject(0);
                    final String url = item.getString(ApoContract.APO_JSON_IMAGE);

                    if (null != url) {
                        setIntroBackground(url, true, prefs);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "No background update found: " + e.getMessage());
                }
            }

            @Override
            void onError(String errStr) {
                super.onError(errStr);
                Log.e(TAG, "Background update failed");
            }
        };

        mNetworker.requestSection();
    }

    private void setIntroBackground(final String url, final boolean needUpdate, final SharedPreferences prefs) {
        final String androidUrl = url.substring(0, url.length() - 4) + "_android.jpg";

        Log.e(TAG, "Setting background url: " + androidUrl);

        ImageLoader.getInstance().loadImage(androidUrl, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.full_screen_content);
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    relativeLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), loadedImage));
                } else {
                    relativeLayout.setBackground(new BitmapDrawable(getResources(), loadedImage));
                }

                if (needUpdate) {
                    Log.e(TAG, "Saving background url: " + url);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(ApoContract.APO_JSON_IMAGE, url);
                    editor.commit();
                }
            }
        });
    }
}