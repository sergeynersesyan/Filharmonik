package am.apo.filharmonik2;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by user on 12/22/14.
 */
public class ApoMediaControl {
    private Activity mParent;
    private RelativeLayout mControlBar;
    private TextView mDurationInfo;
    private SeekBar mSeekBar;
    private ImageButton mPlayPause;
    private ImageButton mMaxMinButton;
    private boolean mMaximized;
    private MediaControlCallbackListener mCallbackListener;

    public ApoMediaControl(Activity parent, boolean musicMode)
    {
        mParent = parent;

        try {
            mCallbackListener = (MediaControlCallbackListener) mParent;

            mMaximized = false;

            mControlBar = (RelativeLayout) mParent.findViewById(R.id.control_bar);
            mSeekBar = (SeekBar) mParent.findViewById(R.id.player_seek_bar);
            mSeekBar.setMax(100);
            mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mCallbackListener.onSeek(seekBar.getProgress());
                }
            });
            mDurationInfo = (TextView) mParent.findViewById(R.id.duration_info);

            mPlayPause = (ImageButton) mParent.findViewById(R.id.play_pause_button);
            mPlayPause.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View view) {
                    mCallbackListener.onToggle();
                }
            });

            mMaxMinButton = (ImageButton) mParent.findViewById(R.id.max_min_button);
            if(musicMode) {
                android.view.ViewGroup.LayoutParams slp = mMaxMinButton.getLayoutParams();
                slp.width = 0;
                mMaxMinButton.setLayoutParams(slp);
            }
            else {
                mMaxMinButton.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View view) {

                        if (mParent.getResources().getBoolean(R.bool.device_is_phone)) {
                            mParent.setRequestedOrientation(mMaximized ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }

                        mParent.findViewById(R.id.top_bar).setVisibility(mMaximized ? View.VISIBLE : View.GONE);
                        mParent.findViewById(R.id.bottom_bar).setVisibility(mMaximized ? View.VISIBLE : View.GONE);
                        mParent.findViewById(R.id.description_bar).setVisibility(mMaximized ? View.VISIBLE : View.GONE);

                        RelativeLayout mediaArea = (RelativeLayout) mParent.findViewById(R.id.media_area);
                        android.view.ViewGroup.LayoutParams mediaAreaLayout = mediaArea.getLayoutParams();

                        mediaAreaLayout.height = mMaximized ? (int) mParent.getResources().getDimension(R.dimen.player_area_height) : ViewGroup.LayoutParams.MATCH_PARENT;
                        mediaArea.setLayoutParams(mediaAreaLayout);

                        SurfaceView surfaceView = (SurfaceView) mParent.findViewById(R.id.player_surface);
                        android.view.ViewGroup.LayoutParams slp = surfaceView.getLayoutParams();
                        slp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        slp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        surfaceView.setLayoutParams(slp);

                        mMaximized = !mMaximized;
                    }
                });
            }
        }
        catch (ClassCastException e){
            mCallbackListener = null;
        }
    }

    void hide()
    {
        mControlBar.setVisibility(View.GONE);
    }

    void show()
    {
        mControlBar.setVisibility(View.VISIBLE);
    }

    void setPeriodicInfo(boolean isPlaying, int totalSeconds, int position)
    {
        mPlayPause.setImageDrawable(mParent.getResources().getDrawable(isPlaying ? R.drawable.pause_button : R.drawable.play_button));
        mDurationInfo.setText(String.format("%02d:%02d / %02d:%02d", position / 60, position % 60, totalSeconds / 60, totalSeconds % 60));
        mSeekBar.setProgress(totalSeconds > 0 ? position * 100 / totalSeconds : 0);
    }

    void setBufferingInfo(int percent)
    {
        mSeekBar.setSecondaryProgress(percent);
    }

    void reset()
    {

        mPlayPause.setImageDrawable(mParent.getResources().getDrawable(R.drawable.play_button));
        mControlBar.setVisibility(View.VISIBLE);
        mSeekBar.setProgress(100);
        mSeekBar.setProgress(0);
        mSeekBar.setSecondaryProgress(0);
        mDurationInfo.setText(String.format("%02d:%02d / %02d:%02d", 0, 0, 0, 0));
    }

    public interface MediaControlCallbackListener{

        public void onSeek(int position);
        public void onToggle();
    }
}
