package am.apo.filharmonik;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by henrikgardishyan on 12/18/14.
 */
public class RoundedImageView extends ImageView {

    public RoundedImageView(Context context)
    {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        try {
            Drawable drawable = getDrawable();

            Bitmap srcBitmap = ((BitmapDrawable) drawable).getBitmap();

            Bitmap roundedBitmap = getRoundedCornerBitmap(srcBitmap);

            final Rect rectBitmap = new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight());
            final Rect rectView = new Rect(0, 0, getWidth(), getHeight());

            canvas.drawBitmap(roundedBitmap, rectBitmap, rectView, null);
        }
        catch (Exception e) {
            super.onDraw(canvas);
        }
    }

    private Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        //canvas.drawRoundRect(rectF, 15, 15, paint);
        int angle = bitmap.getWidth()/8;
        canvas.drawRoundRect(rectF, angle, angle, paint);
        //canvas.drawCircle(rectF.centerX(), rectF.centerY(), rectF.height()/2, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
