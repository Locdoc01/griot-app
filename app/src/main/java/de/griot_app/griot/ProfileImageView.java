package de.griot_app.griot;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * Custom ImageView, which shows a round profile image, surrounded by a lightgrey circle.
 * If there is no valid image path set throupg setImagePath(), a plus-sign on the backgroundcolor will be visible instead of the image.
 */
public class ProfileImageView extends AppCompatImageView {

    private Context mContext;
    private String mImagePath;
    private Bitmap mBitmapSource;
    private Bitmap mBitmapOutput;
    private BitmapShader mShader;
    private Paint mPaint;



    public ProfileImageView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public ProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public ProfileImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
    }

    /**
     * Set the path of the image. If the path is invalid, there will be a plus-sign on backgroundcolor visible instead an image
     * @param imagePath  A String, that holds a path to an image file.
     */
    public void setImagePath(String imagePath) {
        mImagePath = imagePath;
    }


    /**
     *
     */
    private void set() {

        // if mImagePath is invalid, a bitmap with a plus-sign is created and scaled to proportions based on the screen-designs
        if (BitmapFactory.decodeFile(mImagePath)==null) {
            Bitmap plus = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plus);
            mBitmapOutput = Bitmap.createScaledBitmap(plus, getHeight() / 236 * 72, getHeight() / 236 * 72, false);
        } else {
            // else a source bitmap is created from the image file. This bitmap is scaled depending on the orientation of the source image.
            mBitmapSource = BitmapFactory.decodeFile(mImagePath);
            if (mBitmapSource.getWidth() > mBitmapSource.getHeight()) {
                mBitmapOutput = Bitmap.createScaledBitmap(mBitmapSource, getHeight() * mBitmapSource.getWidth() / mBitmapSource.getHeight(), getHeight(), false);
            } else if (mBitmapSource.getWidth() < mBitmapSource.getHeight()) {
                mBitmapOutput = Bitmap.createScaledBitmap(mBitmapSource, getHeight(), getHeight() * mBitmapSource.getHeight() / mBitmapSource.getWidth(), false);
            } else {
                mBitmapOutput = Bitmap.createScaledBitmap(mBitmapSource, getHeight(), getHeight(), false);
            }
        }
        //TODO: erläutern
        mShader = new BitmapShader(mBitmapOutput, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
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
            canvas.drawBitmap(mBitmapOutput, getWidth()/2-mBitmapOutput.getWidth()/2, getHeight()/2-mBitmapOutput.getHeight()/2, mPaint);
        } else {
            //else draw the round image
            mPaint.setShader(mShader);
            canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-getHeight()/230*2, mPaint);
        }
    }
}
