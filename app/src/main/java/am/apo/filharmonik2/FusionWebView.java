package am.apo.filharmonik2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebView;

/**
 * Created by user on 12/15/14.
 */
public class FusionWebView extends WebView {

    private OnClickListener mClickListener;
    private float mPrevX;
    private float mPrevY;

    public FusionWebView(Context context) {
        super(context);
    }

    public FusionWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FusionWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (null != mClickListener) {

            WebView.HitTestResult hr = getHitTestResult();

            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                mPrevX = event.getX();
                mPrevY = event.getY();
                onScrollChanged(getScrollX(), getScrollY(), getScrollX(), getScrollY());

            } else if (MotionEvent.ACTION_UP == event.getAction()) {
             //   Log.e("BBB", "onClick, block: " + mBlockClick + ", dx: " + Math.abs(mPrevX - event.getX()) + ", dy: " + Math.abs(mPrevY - event.getY()));

                if ( Math.abs(mPrevX - event.getX()) < 8f && Math.abs(mPrevY - event.getY()) < 8f &&
                        (null == hr || null == hr.getExtra() || hr.getExtra().isEmpty() || (HitTestResult.EMAIL_TYPE != hr.getType() && HitTestResult.PHONE_TYPE != hr.getType()))) {
                        mClickListener.onClick();
                }
            }
        }

        return super.onTouchEvent(event);
    }


    public void setClickListener(OnClickListener clickListener)
    {
        mClickListener = clickListener;
    }

    public interface OnClickListener {
        void onClick();
    }
}
