package am.apo.filharmonik2;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by henrikgardishyan on 12/16/14.
 */
public class ApoMediaPlayer implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{
    private MediaPlayer mMediaPlayer;
    private Activity mParent;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private ProgressBar mMediaLoadProgress;
    private Runnable mPeriodicRunnable;
    private Handler mHandler;
    private String mURL;
    private String mCoverURL;
    private PlayerCallbackListener mCallbackListener;
    private WifiManager.WifiLock mWifiLock;
    private boolean mMusicMode;
    private boolean mPrepared;

    public ApoMediaPlayer(Activity parent, String url, String coverUrl, boolean musicMode)
    {
        mParent = parent;
        mMusicMode = musicMode;
        mMediaLoadProgress = (ProgressBar) mParent.findViewById(R.id.media_load_progress);
        mURL = url;
        mCoverURL = coverUrl;

        try {
            mCallbackListener = (PlayerCallbackListener) mParent;
            mHandler = new Handler();

            mPeriodicRunnable = new Runnable() {

                @Override
                public void run() {

                    sendPeriodicInfo();
                    mHandler.postDelayed(this, 250);
                }
            };
        }
        catch (ClassCastException e){
            mCallbackListener = null;
        }
        mWifiLock = ((WifiManager) mParent.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, "apo_wifi");
    }

    public void toggle()
    {
        Log.e("PL", "toggle called");

        if(null != mMediaPlayer)
        {
            if(mPrepared) {

                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    if(!mMusicMode) {
                        mParent.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                } else {
                    mMediaPlayer.start();
                    if(!mMusicMode) {
                        mParent.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                    }
                }
            }
        }
        else
        {
            if(null==mSurfaceView) {
                mSurfaceView = (SurfaceView) mParent.findViewById(R.id.player_surface);
                mSurfaceView.getHolder().addCallback(this);
                Log.e("PL", "surface set callback called");
            }
            else {
                Log.e("PL", "startinternal called");
                startInternal();
            }
        }
        sendPeriodicInfo();
    }

    public void seekTo(int position)
    {
        Log.e("PL", "seek to called: " + position);

        if(null != mMediaPlayer && mPrepared) {
            mMediaPlayer.seekTo(position * mMediaPlayer.getDuration() / 100);
        }
        sendPeriodicInfo();
    }

    public boolean isPlaying()
    {
        return (null != mMediaPlayer && mPrepared) ? mMediaPlayer.isPlaying() : false;
    }

    public void finish()
    {
        try {
            Log.e("PL", "finish called: " + mMediaPlayer);

            if (null != mHandler) {
                mHandler.removeCallbacksAndMessages(null);
            }

            if (null != mMediaPlayer) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            if (null != mWifiLock) {
                mWifiLock.release();
            }
        }
        catch(Exception e) {}
    }

    private void startInternal()
    {
        if(null != mMediaPlayer && mPrepared) {
            Log.e("BBB", "starting internal, already playing");
        }
        else {
            Log.e("BBB", "starting internal, from scratch");
            mPrepared = false;
            mMediaLoadProgress.setVisibility(View.VISIBLE);
            mMediaPlayer = new MediaPlayer();
            try {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                if(!mMusicMode) {
                    mMediaPlayer.setDisplay(mSurfaceHolder);
                }
                mMediaPlayer.setDataSource(mParent, Uri.parse(mURL));
                mMediaPlayer.setWakeMode(mParent, PowerManager.PARTIAL_WAKE_LOCK);
                mMediaPlayer.setOnPreparedListener(this);
                mMediaPlayer.setOnErrorListener(this);
                mMediaPlayer.prepareAsync();
                mWifiLock.acquire();
            } catch (Exception e) {
                Log.e("PL", "Media player exception: " + e.getMessage());
            }
        }
    }

    private void sendPeriodicInfo()
    {
        int totalSeconds = 0;
        int posSeconds = 0;

        if(null != mMediaPlayer && mPrepared) {
            totalSeconds = mMediaPlayer.getDuration() / 1000;
            posSeconds = mMediaPlayer.getCurrentPosition() / 1000;
        }
        mCallbackListener.onPeriodic(totalSeconds, posSeconds);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
        Toast.makeText(mParent, mParent.getString(R.string.PLAYER_EROR), Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        Log.e("RRR", "player prepared");

        if(mMusicMode){
            ImageView coverImage = (ImageView)mParent.findViewById(R.id.cover_image);
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .build();
            ImageLoader.getInstance().displayImage(mCoverURL, coverImage, options);
        }
        else
        {
            updateVideoSize();
            mParent.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }

        mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                   mCallbackListener.onBufferingUpdated(i);
            }
        });

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.e("PL", "oncompletion called");
                mCallbackListener.onCompletion();
            }
        });

        mMediaLoadProgress.setVisibility(View.GONE);

        mMediaPlayer.start();

        mHandler.postDelayed(mPeriodicRunnable, 250);

        mPrepared = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.e("RRR", "surfaceChanged: " + width + "x" + height);
        mSurfaceHolder = holder;
        updateVideoSize();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        Log.e("RRR", "surfaceCreated");
        mSurfaceHolder = holder;
        startInternal();
    }

    private void updateVideoSize() {

        try {
            int videoWidth = mMediaPlayer.getVideoWidth();
            int videoHeight = mMediaPlayer.getVideoHeight();
            if(videoHeight <= 0 || videoWidth <= 0)
            {
                return;
            }
            float videoRatio = (float) videoWidth / (float) videoHeight;

            // Get the width of the screen
            android.view.ViewGroup.LayoutParams lp = mSurfaceView.getLayoutParams();

            int screenWidth = Math.max(mSurfaceView.getWidth(), mSurfaceView.getHeight());
            int screenHeight = Math.min(mSurfaceView.getHeight(), mSurfaceView.getWidth());

            float screenRatio = (float) screenWidth / (float) screenHeight;

            if(Math.abs(videoRatio - screenRatio) > 0.05) {
                // Get the SurfaceView layout parameters
                if (videoRatio > screenRatio) {
                    lp.width = screenWidth;
                    lp.height = (int) ((float) screenWidth / videoRatio);
                    Log.e("UPDATE", "video: " + videoWidth + "x" + videoHeight + " new screen size 1: " + lp.width + "x" + lp.height);

                } else {
                    lp.height = screenHeight;
                    lp.width = (int) (videoRatio * (float) screenHeight);
                    Log.e("UPDATE", "video: " + videoWidth + "x" + videoHeight + "new screen size 2: " + lp.width + "x" + lp.height);
                }
                mSurfaceView.setLayoutParams(lp);
            }
            else
            {
                Log.e("TAG", "ratios equal: " + videoRatio + " : " + screenRatio);
            }
        }
        catch(Exception e)
        {
            Log.e("TAG", "update video size exception: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.e("TAG", "Surface destroyed: ");
        mSurfaceHolder = null;
        if(!mMusicMode)
        {
            mCallbackListener.onCompletion();
        }
    }

    public interface PlayerCallbackListener{

        public void onPeriodic(int totalSeconds, int position);
        public void onBufferingUpdated(int percent);
        public void onCompletion();
    }
}
