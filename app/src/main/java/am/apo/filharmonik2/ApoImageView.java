package am.apo.filharmonik2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by henrikgardishyan on 12/10/14.
 */
public class ApoImageView {

    Activity mParent;
    ImageView mImageView;
    ProgressBar mProgressBar;

    public ApoImageView(Activity parentActivity)
    {
        mParent = parentActivity;
        mProgressBar = (ProgressBar)mParent.findViewById(R.id.apo_image_load_progress);
        mProgressBar.setVisibility(View.GONE);
        mImageView = (ImageView)parentActivity.findViewById(R.id.apo_image_view);
    }

    public void loadUrl(String url)
    {
        mImageView.setImageResource(android.R.color.transparent);
        mProgressBar.setVisibility(View.VISIBLE);

        ImageLoader.getInstance().loadImage(url, new SimpleImageLoadingListener() {

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
                mProgressBar.setVisibility(View.VISIBLE);
            }
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
                mImageView.setImageBitmap(loadedImage);
            }
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    public void loadFromResource(int resID)
    {
        mImageView.setImageDrawable(mParent.getResources().getDrawable(resID));
    }
}
