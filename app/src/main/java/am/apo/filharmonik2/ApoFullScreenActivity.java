package am.apo.filharmonik2;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.batch.android.Batch;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import am.apo.filharmonik2.util.SystemUiHider;

/**
 * Created by user on 11/5/14.
 */
public class ApoFullScreenActivity extends Activity {

    protected static final String TAG = "ANPO";
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    protected SystemUiHider mSystemUiHider;
    private View mContentView;
    protected static int activityCount;
    protected int activityIndex;
    protected static int invalidatePreviousActivityIndex = 0;

    public void changePreviousActivityLanguage() {
        invalidatePreviousActivityIndex = activityIndex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(getResources().getBoolean(R.bool.device_is_phone) ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        activityIndex = ++activityCount;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Batch.onStart(this);
    }

    @Override
    protected void onStop()
    {
        Batch.onStop(this);
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        Batch.onDestroy(this);
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        Batch.onNewIntent(this, intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (activityIndex < invalidatePreviousActivityIndex) {
//            recreate();
            invalidatePreviousActivityIndex = 0;
            onLanguageChange();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setRequestedOrientation(getResources().getBoolean(R.bool.device_is_phone) ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        mContentView = findViewById(R.id.full_screen_content);
        mSystemUiHider = SystemUiHider.getInstance(this, mContentView);

        mSystemUiHider.setup();

        mSystemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {

            @Override
            @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
            public void onVisibilityChange(boolean visible) {

                if(visible)
                {
                    delayedHide(AUTO_HIDE_DELAY_MILLIS);
                }
            }
        });

        mSystemUiHider.hide();

        mContentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_DOWN==motionEvent.getAction()) {
                    onContentClick();
                }
                return false;
            }
        });

        initImageLoader();
    }

    public boolean onContentClick()
    {
        return true;
    }


    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    private void initImageLoader()
    {
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.placeholder)
                .showImageForEmptyUri(R.drawable.placeholder)
                .showImageOnFail(R.drawable.placeholder)
                .cacheInMemory(true)
                .cacheOnDisk(true)
//                .displayer(new RoundedBitmapDisplayer(angle))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(500)
                .defaultDisplayImageOptions(options)
                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 45 * 1000, 60 * 1000))
                .build();

        ImageLoader.getInstance().init(config);
    }

    protected void onLanguageChange() {
        recreate();
    }
}

