package am.apo.filharmonik;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Date;

/**
 * Created by henrikgardishyan on 12/31/14.
 */
public class ApoContentFlipper implements ApoWebView.OnClickListener{
    protected Activity mParent;
    protected RelativeLayout mFrontView;
    protected RelativeLayout mBackView;
    protected ApoWebView mDescriptionView;

    protected boolean mDirectionBack;
    protected int mAnimationCounter;

    public ApoContentFlipper(Activity parent)
    {
        mParent = parent;
        mDirectionBack = false;
        mFrontView = (RelativeLayout)mParent.findViewById(R.id.apo_front_view);
        mBackView = (RelativeLayout)mParent.findViewById(R.id.apo_back_view);

        mDescriptionView = new ApoWebView(mParent, false, true);
        mDescriptionView.setOnClickListener(this);
    }

    @Override
    public void onWebClick() {

        if(mDirectionBack && 0==mAnimationCounter)
        {
            applyRotation(0, mDirectionBack ? -90 : 90); //???
        }
    }

    protected void applyRotation(float start, float end) {
        mAnimationCounter++;

        float centerX = mFrontView.getWidth() / 2.0f;
        float centerY = mFrontView.getHeight() / 2.0f;

        Flip3dAnimation rotation = new Flip3dAnimation(mParent, start, end, centerX, centerY);
        rotation.setDuration(mParent.getResources().getInteger(R.integer.card_flip_time_half));
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new ApoAnimationListener());

        if(mDirectionBack)
        {
            mBackView.startAnimation(rotation);
        }
        else
        {
            mFrontView.startAnimation(rotation);
        }
    }

    public final class ApoAnimationListener implements Animation.AnimationListener {
        Date mStartDate;

        public ApoAnimationListener() { }

        public void onAnimationStart(Animation animation) {
            mStartDate = new Date();
        }

        public void onAnimationEnd(Animation animation) {
            long timeElapsed = (new Date()).getTime() - mStartDate.getTime();

            if(mAnimationCounter < 2)
            {
                mDirectionBack = !mDirectionBack;

                mFrontView.setVisibility(mDirectionBack ? View.GONE : View.VISIBLE);
                mBackView.setVisibility(mDirectionBack ? View.VISIBLE : View.GONE);
                mDescriptionView.setWebVisibility(mBackView.getVisibility());

                mFrontView.post(new SwapViews());
            }
            else
            {
                mAnimationCounter = 0;
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }
    }

    protected final class SwapViews implements Runnable {

        public SwapViews() { }

        public void run() {
            applyRotation(mDirectionBack ? -90 : 90, 0);
        }
    }
}
