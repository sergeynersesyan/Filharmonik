package am.apo.filharmonik;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import org.json.JSONObject;

public class PlayerPlayActivity extends ApoSectionActivity implements ApoMediaPlayer.PlayerCallbackListener, ApoMediaControl.MediaControlCallbackListener {

    private ApoMediaPlayer mPlayer;
    private ApoMediaControl mMediaControl;
    private TextView mDescriptionArea;
    private boolean mFoundInFavorites;
    private FavoritesHelper mFavoritesHelper;
    private ImageButton mFavoritesButton;
    private JSONObject mJSONObject;
    private Runnable mAutoHideRunnable;
    private RelativeLayout mMediaArea;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_play);
        setSectionID(getIntent().getStringExtra(ApoContract.APO_SECTION_ID));

        Log.e(TAG, "PlayerPlayActivity onCreate");
        mHandler = new Handler();

        mAutoHideRunnable = new Runnable() {
            @Override
            public void run() {
                Log.e("BBB", "Autohide runnable called");
                mMediaControl.hide();
                mSystemUiHider.setFlagHideNavigation();
            };
        };

        try {
            mJSONObject = new JSONObject(getIntent().getStringExtra(ApoContract.APO_JSON));

            String url = mJSONObject.getString(getSectionID().equals(ApoContract.APO_MUSIC) ? ApoContract.APO_JSON_MUSIC : ApoContract.APO_JSON_VIDEO);
            setSubTitle(mJSONObject.getString(ApoContract.APO_JSON_TITLE));
            mDescriptionArea = (TextView) findViewById(R.id.text_description);
            mDescriptionArea.setText(mJSONObject.getString(ApoContract.APO_JSON_DESCRIPTION));
            String coverUrl = null;
            if(getSectionID().equals(ApoContract.APO_MUSIC)) {
                coverUrl = mJSONObject.getString(ApoContract.APO_JSON_COVER);
            }
            mPlayer = new ApoMediaPlayer(this, url, coverUrl, getSectionID().equals(ApoContract.APO_MUSIC));
            mMediaControl = new ApoMediaControl(this, getSectionID().equals(ApoContract.APO_MUSIC));

            mFavoritesHelper = new FavoritesHelper(getApplicationContext(), getSectionID().equals(ApoContract.APO_MUSIC));

            mFavoritesButton = (ImageButton) findViewById(R.id.favorites_button);
            mFavoritesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mFoundInFavorites)
                    {
                        mFavoritesHelper.removeFromFavorites(mJSONObject);
                    }
                    else
                    {
                        mFavoritesHelper.addToFavorites(mJSONObject);
                    }
                    mFoundInFavorites = !mFoundInFavorites;
                    mFavoritesButton.setSelected(mFoundInFavorites);
                }
            });

            mFoundInFavorites = mFavoritesHelper.isFavorite(mJSONObject);
            mFavoritesButton.setSelected(mFoundInFavorites);

            mMediaArea = (RelativeLayout) findViewById(R.id.media_area);
            mMediaArea.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if( MotionEvent.ACTION_UP==motionEvent.getAction()) {
                        Log.e("BBB", "media area touched");
                        mMediaControl.show();
                        toggleAutoHide();
                    }
                    return true;
                }
            });

            mPlayer.toggle();
        }
        catch (Exception e) {
            Log.e("BBB", "### Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("BBB", "Activity onResume");
        mSystemUiHider.setFlagHideNavigation();
        toggleAutoHide();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mPlayer.finish();
        super.onDestroy();
    }

    @Override
    public void onToggle() {
        mPlayer.toggle();
        toggleAutoHide();
    }

    @Override
    public void onSeek(int position) {
        mPlayer.seekTo(position);
    }

    @Override
    public void onPeriodic(int totalSeconds, int position) {
        mMediaControl.setPeriodicInfo(mPlayer.isPlaying(), totalSeconds, position);
    }

    @Override
    public void onBufferingUpdated(int percent)
    {
        mMediaControl.setBufferingInfo(percent);
    }

    @Override
    public void onCompletion() {
        Log.e("BBB", "Player onCompletion");

        mPlayer.finish();
        mMediaControl.reset();
    }

    private void toggleAutoHide()
    {
        Log.e("BBB", "Autohide TOGGLED");
        mHandler.removeCallbacks(mAutoHideRunnable);
        mHandler.postDelayed(mAutoHideRunnable, 4000);
    }
}
