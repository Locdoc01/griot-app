package de.griot_app.griot.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import de.griot_app.griot.R;


//TODO: Alternative implementation of ProfileImageView, which doesn't work. Maybe it can be found a solution later
/**
 * Custom ImageView, which shows a round profile image, surrounded by a lightgrey circle.
 * If there is no valid image path set throupg setImagePath(), a plus-sign on the backgroundcolor will be visible instead of the image.
 */
public class ProfileImageViewOld extends AppCompatImageView {

    private static final String TAG = ProfileImageViewOld.class.getSimpleName();

    private Context mContext;
    private String mImagePath;
    private Bitmap mBitmapSource;
    private Bitmap mBitmapSourceScaled;
    private Bitmap mBitmapDestination;
    private Bitmap mBitmapDestinationScaled;
    private BitmapShader mShader;
    private Paint mPaint;



    public ProfileImageViewOld(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ProfileImageViewOld(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ProfileImageViewOld(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        //
        setLayerType(LAYER_TYPE_HARDWARE, null);
        mPaint = new Paint();
    }

    /**
     * Set the path of the image. If the path is invalid, there will be a plus-sign on backgroundcolor visible instead an image
     * @param imagePath  A String, that holds a path to an image file.
     */
    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }

    private void set() {
        if (BitmapFactory.decodeFile(mImagePath)==null) {

            //TODO: Nur Zwischenlösung. Allgemeine Lösung finden
            mBitmapSource = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.empty_16_9);
            //Bitmap plus = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plus);
            //mBitmapSourceScaled = Bitmap.createScaledBitmap(plus, getHeight() / 236 * 72, getHeight() / 236 * 72, false);
        } else {
            mBitmapSource = BitmapFactory.decodeFile(mImagePath);
        }

        if (mBitmapSource.getWidth() > mBitmapSource.getHeight()) {
            mBitmapSourceScaled = Bitmap.createScaledBitmap(mBitmapSource, getHeight() * mBitmapSource.getWidth() / mBitmapSource.getHeight(), getHeight(), false);
        } else if (mBitmapSource.getWidth() < mBitmapSource.getHeight()) {
            mBitmapSourceScaled = Bitmap.createScaledBitmap(mBitmapSource, getHeight(), getHeight() * mBitmapSource.getHeight() / mBitmapSource.getWidth(), false);

        } else {
            mBitmapSourceScaled = Bitmap.createScaledBitmap(mBitmapSource, getHeight(), getHeight(), false);
        }

        mBitmapDestination = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.circle_red);

        /*
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.shape_circle_darkgrey, null);
        Log.e(TAG, "drawable.getIntrinsicHeight: " + drawable.getIntrinsicWidth() + " , drawable.getIntrinsicHeight(): " + drawable.getIntrinsicHeight());
        mBitmapDestination = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mBitmapDestination);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        */

        mBitmapDestinationScaled = Bitmap.createScaledBitmap(mBitmapDestination, getHeight(), getHeight(), false);



        //mShader = new BitmapShader(mBitmapSourceScaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);



        mPaint.setAntiAlias(true);

        //mPaint.setStyle(Paint.Style.FILL);

        // draw the background
        //mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
        //mPaint.setColor(Color.TRANSPARENT);
        //canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), mPaint);

        //mPaint.setStyle(Paint.Style.STROKE);
        //mPaint.setStrokeWidth(2);

        //draw the outlining circle with radius = height/2-1, so that the outline stroke will be fully visible (because stroke width is 2)
        //mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotLightgrey));
        //canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-1, mPaint);

        mPaint.setStyle(Paint.Style.FILL);

        //mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
        //canvas.drawRect(0,0,getWidth(), getHeight(), mPaint);

        set();
//
        canvas.drawBitmap(mBitmapDestinationScaled, 0, 0, mPaint);

        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        mPaint.setXfermode(new PorterDuffXfermode(mode));
        //mPaint.setColorFilter(new PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.MULTIPLY));

        canvas.drawBitmap(mBitmapSource, 30, 30, mPaint);
//
        /*
        // if mImagePath is not valid, draw a circle in backgroundcolor
        if (BitmapFactory.decodeFile(mImagePath)==null) {
            //ColorDrawable colordrawable = (ColorDrawable) ((Activity)mContext).getWindow().getDecorView().getBackground();
            //mPaint.setColor(colordrawable.getColor());
            //mPaint.setColor(((ColorDrawable)getRootView().getBackground()).getColor());
            //mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
            // canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-getHeight()/230*2, mPaint);
            canvas.drawBitmap(mBitmapSourceScaled, getWidth()/2- mBitmapSourceScaled.getWidth()/2, getHeight()/2- mBitmapSourceScaled.getHeight()/2, mPaint);
        } else {
            //else draw the round image
            mPaint.setShader(mShader);
            canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-getHeight()/230*2, mPaint);
        }
        */
    }





    /**
     *
     */
    /*
    private void set() {
        Log.e(TAG, "----------getHeight: --------------" + getHeight());
        // if mImagePath is invalid, a bitmap with a plus-sign is created and scaled to proportions based on the screen-designs
        if (BitmapFactory.decodeFile(mImagePath)==null) {

            //TODO: Nur Zwischenlösung. Allgemeine Lösung finden
            mBitmapSource = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.empty_16_9);
            //Bitmap plus = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plus);
            //mBitmapSourceScaled = Bitmap.createScaledBitmap(plus, getHeight() / 236 * 72, getHeight() / 236 * 72, false);
        } else {

            // else a source bitmap is created from the image file. This bitmap is scaled depending on the orientation of the source image.
            mBitmapSource = BitmapFactory.decodeFile(mImagePath);
        }

        if (mBitmapSource.getWidth() > mBitmapSource.getHeight()) {
            Log.e(TAG, "if: getHeight() * mBitmapSource.getWidth() / mBitmapSource.getHeight(): " + getHeight() * mBitmapSource.getWidth() / mBitmapSource.getHeight());
            Log.e(TAG, "    mBitmapSource.getHeight: " + mBitmapSource.getHeight() + " , mBitmapSource.getWidth: " + mBitmapSource.getWidth());
            mBitmapSourceScaled = Bitmap.createScaledBitmap(mBitmapSource, getHeight() * mBitmapSource.getWidth() / mBitmapSource.getHeight(), getHeight(), false);
            Log.e(TAG, "    mBitmapSourceScaled.getHeight: " + mBitmapSourceScaled.getHeight() + " , mBitmapSourceScaled.getWidth: " + mBitmapSourceScaled.getWidth());
        } else if (mBitmapSource.getWidth() < mBitmapSource.getHeight()) {
            Log.e(TAG, "else: getHeight() * mBitmapSource.getHeight() / mBitmapSource.getWidth()" + getHeight() * mBitmapSource.getHeight() / mBitmapSource.getWidth());
            Log.e(TAG, "      mBitmapSource.getHeight: " + mBitmapSource.getHeight() + " , mBitmapSource.getWidth: " + mBitmapSource.getWidth());
            mBitmapSourceScaled = Bitmap.createScaledBitmap(mBitmapSource, getHeight(), getHeight() * mBitmapSource.getHeight() / mBitmapSource.getWidth(), false);
            Log.e(TAG, "    mBitmapSourceScaled.getHeight: " + mBitmapSourceScaled.getHeight() + " , mBitmapSourceScaled.getWidth: " + mBitmapSourceScaled.getWidth());
        } else {
            mBitmapSourceScaled = Bitmap.createScaledBitmap(mBitmapSource, getHeight(), getHeight(), false);
        }
        //TODO: erläutern
        mShader = new BitmapShader(mBitmapSourceScaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }
*/
/*
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAntiAlias(true);

        //mPaint.setStyle(Paint.Style.FILL);

        // draw the background
        //mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
        //mPaint.setColor(Color.TRANSPARENT);
        //canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);

        //draw the outlining circle with radius = height/2-1, so that the outline stroke will be fully visible (because stroke width is 2)
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotLightgrey));
        canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-1, mPaint);

        mPaint.setStyle(Paint.Style.FILL);

        //TODO: finde alternative Position für set(). getWidth() und getHeight() müssen Werte > 0 zurückliefern
        set();
        // if mImagePath is not valid, draw a circle in backgroundcolor
        if (BitmapFactory.decodeFile(mImagePath)==null) {
            //ColorDrawable colordrawable = (ColorDrawable) ((Activity)mContext).getWindow().getDecorView().getBackground();
            //mPaint.setColor(colordrawable.getColor());
            //mPaint.setColor(((ColorDrawable)getRootView().getBackground()).getColor());
            //mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
            // canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-getHeight()/230*2, mPaint);
            Log.e(TAG, "getWidth()/2-mBitmapSourceScaled.getWidth()/2: " + (getWidth()/2-mBitmapSourceScaled.getWidth()/2));
            Log.e(TAG, "getHeight()/2-mBitmapSourceScaled.getHeight()/2: " + (getHeight()/2-mBitmapSourceScaled.getHeight()/2));
            canvas.drawBitmap(mBitmapSourceScaled, getWidth()/2-mBitmapSourceScaled.getWidth()/2, getHeight()/2-mBitmapSourceScaled.getHeight()/2, mPaint);
        } else {
            //else draw the round image
            mPaint.setShader(mShader);
            Log.e(TAG, "getHeight()/2-getHeight()/230*2: " + (getHeight()/2-getHeight()/230*2));
            canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-getHeight()/230*2, mPaint);
        }
    }
   */
}
