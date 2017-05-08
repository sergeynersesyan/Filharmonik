package am.apo.filharmonik;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import java.util.Date;

public class Flip3dAnimation  extends Animation {
    private Activity mParent;
    private final float mFromDegrees;
    private final float mToDegrees;
    private  float mCenterX;
    private  float mCenterY;
    private Camera mCamera;
    private float mCameraDistanceZ;

    public Flip3dAnimation(Activity activity, float fromDegrees, float toDegrees,
                           float centerX, float centerY) {
        mParent = activity;
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        mCenterX = centerX;
        mCenterY = centerY;
        mCamera = new Camera();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

        float cameraDistance = Math.max(width, height) * 20;
        float densityDpi = mParent.getResources().getDisplayMetrics().densityDpi;
        mCameraDistanceZ = -cameraDistance / densityDpi;
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;

        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        Matrix matrix = t.getMatrix();

        mCamera.save();

        mCamera.setLocation(0, 0, mCameraDistanceZ);
      //  Log.e("ANIMU", "mWidth: " + mWidth + ", mHeight:" + mHeight + ", cameraDistance: " + cameraDistance + ", densityDpi: " + densityDpi + ", z: " + (-cameraDistance / densityDpi));
        mCamera.rotateY(degrees);
        mCamera.getMatrix(matrix);
        mCamera.restore();

        //float scaleFactor = 1.0f - Math.abs(degrees/120.0f);
        float alpha = 1.0f - Math.abs(degrees/90f)/4f;

        matrix.preTranslate(-mCenterX, -mCenterY);
        matrix.postTranslate(mCenterX, mCenterY);
        //matrix.postScale(scaleFactor, 1);

        t.setAlpha(alpha);
    }
}