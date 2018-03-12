package am.apo.filharmonik2.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.View;

/**
 * An API 11+ implementation of {@link SystemUiHider}. Uses APIs available in
 * Honeycomb and later (specifically {@link View#setSystemUiVisibility(int)}) to
 * show and hide the system UI.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SystemUiHiderHoneycomb extends SystemUiHiderBase {
    private int mShowFlags;
    private int mHideFlags;


    private boolean mVisible = true;

    private View.OnSystemUiVisibilityChangeListener mSystemUiVisibilityChangeListener;
    protected SystemUiHiderHoneycomb(Activity activity, View anchorView, int flags) {
        super(activity, anchorView, flags);

        mShowFlags = View.SYSTEM_UI_FLAG_VISIBLE;
        mHideFlags = 0;// View.SYSTEM_UI_FLAG_LOW_PROFILE;

        if ((mFlags & FLAG_FULLSCREEN) != 0) {
            mShowFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            mHideFlags |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setup() {

        mSystemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int vis) {
                mOnVisibilityChangeListener.onVisibilityChange(0==(vis & View.SYSTEM_UI_FLAG_LOW_PROFILE));
            }
        };

        mAnchorView.setOnSystemUiVisibilityChangeListener(mSystemUiVisibilityChangeListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void hide() {
        mAnchorView.setSystemUiVisibility(mHideFlags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void show() {
        mAnchorView.setSystemUiVisibility(mShowFlags);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible() {
        return mVisible;
    }
}
