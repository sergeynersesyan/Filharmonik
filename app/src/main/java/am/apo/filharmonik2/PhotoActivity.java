package am.apo.filharmonik2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;


public class PhotoActivity extends ApoSectionActivity implements AdapterView.OnItemClickListener{

    private PhotoGridAdapter mAdapter;
    private GridView mGridView;
    private GestureDetectorCompat mDetector;
    private Animator mCurrentAnimator;
    private ImageView mExpandedImageView;
    private TextView mPhotoLabel;
    private View mContainerView;
    private ProgressBar mPhotoLoadProgress;
    private LinearLayout mCoverView;
    private Button mSaveToGallery;
    private int mShortAnimationDuration;
    private float mScaleFinal;
    private Rect mStartBounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setSectionID(ApoContract.APO_PHOTO);

        mShortAnimationDuration = 500;//mActivity.getResources().getInteger(android.R.integer.config_shortAnimTime);

        mGridView = (GridView)findViewById(R.id.photo_grid);
        mGridView.setNumColumns(getGridColumns());
        mGridView.setOnItemClickListener(this);

        mContainerView = findViewById(R.id.full_screen_content);
        mPhotoLoadProgress = (ProgressBar)findViewById(R.id.photo_load_progress);
        mCoverView = (LinearLayout)findViewById(R.id.cover_view);

        mPhotoLabel = (TextView)findViewById(R.id.photo_label);

        mExpandedImageView = (ImageView)findViewById(R.id.expanded_image);

        mDetector = new GestureDetectorCompat(this, new ApoGestureDetector());

        mExpandedImageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (null != mDetector) {
                    return mDetector.onTouchEvent(motionEvent);
                }
                return false;
            }
        });

        mSaveToGallery = (Button)findViewById(R.id.save_to_gallery);
        mSaveToGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage(((BitmapDrawable)mExpandedImageView.getDrawable()).getBitmap());
                mSaveToGallery.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        JSONObject item = (JSONObject)mAdapter.getItem(i);

        try {
            String thumbURL = item.getString(ApoContract.APO_JSON_THUMBNAIL);
            final String pictureURL = item.getString(ApoContract.APO_JSON_PICTURE);
            final String photoDescription = item.getString(ApoContract.APO_JSON_DESCRIPTION);


            //final View thumbView = mGridView.getChildAt(i);
            final View thumbView = view;
  //          Log.e("VVV", "Item clicked view: " + view + ", thumbView: " + thumbView + ", i: " + i + ", l: " + l);

            ImageLoader.getInstance().loadImage(thumbURL, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {

                    //Log.e("VVV", "Bitmap loaded, view: " + view + ", thumbView: " + thumbView + ", i: " + i + ", l: " + l);
                    mScaleFinal = zoomOutFromThumb(thumbView, loadedImage, pictureURL, photoDescription);
                    //imageButton.setImageBitmap(getRoundedCornerBitmap(loadedImage));
                }
            });

/*        File file = ImageLoader.getInstance().getDiscCache().get(thumbURL);
         Log.e("VVV", "File for: " + thumbURL + ": " + file);
*/
        }
        catch (Exception e){}
    }

    public boolean onTouchEvent(MotionEvent event){
       // Log.e("DD", "OnTouch: " + event);
        if (this.mDetector != null) return this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        loadSection();
    }

    @Override
    public void onSectionReady(JSONObject obj) {
        super.onSectionReady(obj);

        mAdapter = new PhotoGridAdapter(PhotoActivity.this, obj);
        mGridView.setAdapter(mAdapter);
    }

    private float zoomOutFromThumb(final View thumbView, Bitmap thumbBitmap, final String pictureURL, final String photoDescription) {

        if (null != mCurrentAnimator) {
            mCurrentAnimator.cancel();
        }

        ImageLoader.getInstance().cancelDisplayTask(mExpandedImageView);

        mExpandedImageView.setImageBitmap(thumbBitmap);
        mPhotoLabel.setText(photoDescription);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        mStartBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(mStartBounds);
        mContainerView.getGlobalVisibleRect(finalBounds, globalOffset);
        mStartBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height() > (float) mStartBounds.width() / mStartBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) mStartBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - mStartBounds.width()) / 2;
            mStartBounds.left -= deltaWidth;
            mStartBounds.right += deltaWidth;
        }
        else {
            // Extend start bounds vertically
            startScale = (float) mStartBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - mStartBounds.height()) / 2;
            mStartBounds.top -= deltaHeight;
            mStartBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        mExpandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        mExpandedImageView.setPivotX(0f);
        mExpandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(mExpandedImageView, "X", mStartBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, "Y", mStartBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, "ScaleX", startScale, 1f))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, "ScaleY", startScale, 1f))
                .with(ObjectAnimator.ofFloat(mCoverView, "alpha", 0, 0.8f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;

                mPhotoLabel.setVisibility(View.VISIBLE);

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                 .build();

                ImageLoader.getInstance().displayImage(pictureURL, mExpandedImageView, options, new SimpleImageLoadingListener(){

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        super.onLoadingStarted(imageUri, view);
                        mPhotoLoadProgress.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        mPhotoLoadProgress.setVisibility(View.GONE);
                    }
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        mPhotoLoadProgress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        super.onLoadingCancelled(imageUri, view);
                        mPhotoLoadProgress.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        return startScale;
    }

    void zoomInToThumb()
    {
        ImageLoader.getInstance().cancelDisplayTask(mExpandedImageView);
        mSaveToGallery.setVisibility(View.GONE);

        mPhotoLabel.setVisibility(View.GONE);

        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        // Animate the four positioning/sizing properties in parallel,
        // back to their original values.
        AnimatorSet set = new AnimatorSet();
        set.play(ObjectAnimator.ofFloat(mExpandedImageView, "X", mStartBounds.left))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, "Y", mStartBounds.top))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, "ScaleX", mScaleFinal))
                .with(ObjectAnimator.ofFloat(mExpandedImageView, "ScaleY", mScaleFinal))
                .with(ObjectAnimator.ofFloat(mCoverView, "alpha", 0.8f, 0));

        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mExpandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mExpandedImageView.setVisibility(View.GONE);
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;
    }

    class ApoGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if(e2.getX() - e1.getX() > 0) {
//                Log.e(DEBUG_TAG, "onFling: to left");
            }
            else
            {
  //              Log.e(DEBUG_TAG, "onFling: to right");
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
    //        Log.e(DEBUG_TAG, "onLongPRess: " + e);
            mSaveToGallery.setVisibility(View.VISIBLE);
            super.onLongPress(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            mSaveToGallery.setVisibility(View.GONE);
          //  Log.e(DEBUG_TAG, "onDown: " + e);
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            zoomInToThumb();
            return super.onSingleTapConfirmed(e);
        }
    }

    private void saveImage(Bitmap finalBitmap) {

        try {
            File apoDir = new File( Environment.getExternalStorageDirectory().getPath() + "/Pictures");
            apoDir.mkdirs();
            Random generator = new Random();
            int n = 10000;
            n = generator.nextInt(n);
            String fileName = "apo-image-"+ n +".jpg";
            File mediaFile = new File (apoDir, fileName);
            Log.e("FILE", "final file: " + mediaFile.getPath());

            if (mediaFile.exists ()) {
                mediaFile.delete();
            }
            FileOutputStream out = new FileOutputStream(mediaFile);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.MediaColumns.DATA, mediaFile.getAbsolutePath()) ;

            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if(View.VISIBLE==mExpandedImageView.getVisibility()) {
            zoomInToThumb();
        }
        else {
            super.onBackPressed();
        }
    }
}
