package de.griot_app.griot;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * Custom ImageView, which shows a round profile image, surrounded by a lightgrey circle.
 * If there is no valid image path set throupg setImagePath(), a plus-sign on the backgroundcolor will be visible instead of the image.
 */
public class ProfileImageView extends AppCompatImageView {

    private Context mContext;
    private String mPath;
    private Bitmap mSource;
    private Bitmap mBitmap;
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
     * Set the path from the image to be shown. If the path is invalid, there will be a plus-sign on backgroundcolor visible instead an image
     * @param path  A String, that holds a path to an image file.
     */
    public void setImagePath(String path) {
        mPath = path;
    }


    /**
     *
     */
    private void set() {

        // if mPath is invalid, a bitmap with a plus-sign is created and scaled to proportions based on the screen-designs
        if (BitmapFactory.decodeFile(mPath)==null) {
            Bitmap plus = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.plus);
            mBitmap = Bitmap.createScaledBitmap(plus, getHeight() / 236 * 72, getHeight() / 236 * 72, false);
        } else {
            // else a source bitmap is created from the image file. This bitmap is scaled depending on the orientation of the source image.
            mSource = BitmapFactory.decodeFile(mPath);
            if (mSource.getWidth() > mSource.getHeight()) {
                mBitmap = Bitmap.createScaledBitmap(mSource, getHeight() * mSource.getWidth() / mSource.getHeight(), getHeight(), false);
            } else if (mSource.getWidth() < mSource.getHeight()) {
                mBitmap = Bitmap.createScaledBitmap(mSource, getHeight(), getHeight() * mSource.getHeight() / mSource.getWidth(), false);
            } else {
                mBitmap = Bitmap.createScaledBitmap(mSource, getHeight(), getHeight(), false);
            }
        }
        //TODO: erläutern
        mShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        // draw the background
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
        canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), mPaint);

        //draw the surrounding circle
        mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotLightgrey));
        canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2, mPaint);

        //TODO: finde alternative Position für set(). getWidth() und getHeight() müssen Werte > 0 zurückliefern
        set();
        // if mPath is not valid, draw a circle in backgroundcolor
        if (BitmapFactory.decodeFile(mPath)==null) {
            mPaint.setColor(ContextCompat.getColor(mContext, R.color.colorGriotWhite));
            canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-getHeight()/230*2, mPaint);
            canvas.drawBitmap(mBitmap, getWidth()/2-mBitmap.getWidth()/2, getHeight()/2-mBitmap.getHeight()/2, mPaint);
        } else {
            //else draw the round image
            mPaint.setShader(mShader);
            canvas.drawCircle(getHeight()/2, getHeight()/2, getHeight()/2-getHeight()/230*2, mPaint);
        }
    }
}
