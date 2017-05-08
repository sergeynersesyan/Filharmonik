package am.apo.filharmonik;

import android.app.Activity;

/**
 * Created by henrikgardishyan on 12/31/14.
 */
public class DynamicContentFlipper extends ApoContentFlipper {

    ApoImageView mImageView;

    public DynamicContentFlipper(Activity parent)
    {
        super(parent);
        mImageView = new ApoImageView(mParent);
    }

    public void setCardInfo(String imgUrl, String text)
    {
        mImageView.loadUrl(imgUrl);
        mDescriptionView.loadFromText(text);
    }

    public void flip()
    {
        applyRotation(0, mDirectionBack ? -90 : 90);
    }
}
