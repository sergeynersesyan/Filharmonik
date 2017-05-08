package am.apo.filharmonik;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Date;

/**
 * Created by henrikgardishyan on 12/30/14.
 */
public class StaticContentFlipper extends ApoContentFlipper{

    private ImageView mImageView;
    private ImageButton mFlipButton;
    private ImageButton mBackButton;
    private ImageButton mForwardButton;

    OnArrowClickListener mArrowClickListener;

    public StaticContentFlipper(Activity parent)
    {
        super(parent);

        mImageView = (ImageView)mParent.findViewById(R.id.apo_static_image);

        mFlipButton = (ImageButton)mParent.findViewById(R.id.flip_button);
        mFlipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(0==mAnimationCounter) {
                    applyRotation(0, 90);
                }
            }
        });
    }

    public void setCardInfo(int imageResID, String textUrl)
    {
        mImageView.setImageDrawable(mParent.getResources().getDrawable(imageResID));
        mDescriptionView.loadUrl(textUrl);
        if(mDirectionBack && 0==mAnimationCounter)
        {
            applyRotation(0, -90);
        }
    }

    public void setArrowClickListener(OnArrowClickListener listener)
    {
        mArrowClickListener = listener;
        mBackButton = (ImageButton)mParent.findViewById(R.id.move_back);
        mBackButton.setVisibility(View.VISIBLE);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrowClickListener.onBackClicked();
            }
        });
        mForwardButton = (ImageButton)mParent.findViewById(R.id.move_forward);
        mForwardButton.setVisibility(View.VISIBLE);
        mForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mArrowClickListener.onForwardClicked();
            }
        });
    }

    public interface OnArrowClickListener{
        public void onBackClicked();
        public void onForwardClicked();
    }
}
