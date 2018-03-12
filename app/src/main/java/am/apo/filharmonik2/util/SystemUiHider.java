package am.apo.filharmonik2.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;

public abstract class SystemUiHider {

    public static final int FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES = 0x1;

    public static final int FLAG_FULLSCREEN = 0x2;

    public static final int FLAG_HIDE_NAVIGATION = FLAG_FULLSCREEN | 0x4;

    protected Activity mActivity;

    protected View mAnchorView;

    protected int mFlags;

    /**
     * The current visibility callback.
     */
    protected OnVisibilityChangeListener mOnVisibilityChangeListener = sDummyListener;

    public static SystemUiHider getInstance(Activity activity, View anchorView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return new SystemUiHiderHoneycomb(activity, anchorView, FLAG_FULLSCREEN);
        } else {
            return new SystemUiHiderBase(activity, anchorView, FLAG_FULLSCREEN);
        }
    }

    protected SystemUiHider(Activity activity, View anchorView, int flags) {
        mActivity = activity;
        mAnchorView = anchorView;
        mFlags = flags;
    }

    public abstract void setup();

    public abstract boolean isVisible();

    public abstract void hide();

    public abstract void show();

    public void setFlagHideNavigation() {
        mAnchorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    public void clearFlagHideNavigation() {
        mAnchorView.setSystemUiVisibility(FLAG_FULLSCREEN);
    }

    public void setOnVisibilityChangeListener(OnVisibilityChangeListener listener) {
        if (listener == null) {
            listener = sDummyListener;
        }

        mOnVisibilityChangeListener = listener;
    }

    /**
     * A dummy no-op callback for use when there is no other listener set.
     */
    private static OnVisibilityChangeListener sDummyListener = new OnVisibilityChangeListener() {
        @Override
        public void onVisibilityChange(boolean visible) {
        }
    };

    /**
     * A callback interface used to listen for system UI visibility changes.
     */
    public interface OnVisibilityChangeListener {
        /**
         * Called when the system UI visibility has changed.
         *
         * @param visible True if the system UI is visible.
         */
        public void onVisibilityChange(boolean visible);
    }
}
