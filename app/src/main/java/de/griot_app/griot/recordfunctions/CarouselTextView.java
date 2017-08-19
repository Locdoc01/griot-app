package de.griot_app.griot.recordfunctions;

/**
 * Created by marcel on 17.07.17.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.util.TypedValue;

/**
 * Custom TextView, which dynamically sets its textSize automatically to fit the whole holding text completely into one line.
 */
public class CarouselTextView extends AppCompatTextView {

    public static final String TAG = CarouselTextView.class.getSimpleName();

    private float mDensity;

    /** mResized determines, if the TextView has been mResized. */
    private boolean mResized = false;

    /** mRecorded determines, if the Question has been mRecorded yet. */
    private boolean mRecorded = false;

    /** mLines holds the Number of Lines. This was necessary, since TextView.getLineCount() not always returns the right value. */
    private int mLines = 1;

    /**
     * Constructor
     * @param context   The holding activity
     */
    public CarouselTextView(Context context) {
        super(context);
        mDensity = context.getResources().getDisplayMetrics().density;
        setIncludeFontPadding(false);
//            setMaxLines(1);
        if (Build.VERSION.SDK_INT > 16) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }
    }


    public boolean wasResized() { return mResized; }
    public boolean wasRecorded() { return mRecorded; }
    public void setRecorded(boolean recorded) { mRecorded = recorded; }
    public int getLines() { return mLines; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.v(TAG, "onMeasure: getLineCount: " + getLineCount() + " , getTextSize: " + getTextSize() + " , getText.length: " + getText().length());

        // if text is to long to fit in one line, textSize will be decreased by 5px per step and be measured again, until it fits.
        // (During the textview's measurment process it occures sometimes, that getLineCount() is equal to the text length.
        // Therefore the resizement of the textView only will be performed, if getLineCount() < getText.length()
        if (getLineCount() > 1 && getLineCount() < getText().length()) {
            if (getLineCount() == 2) {
                if (getTextSize() - 5 > 15 * mDensity) {
                    mResized = true;
                    // setTextSize needs unit along with textsize in px (getTextSize returns size in px)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() - 5);
                    measure(widthMeasureSpec, heightMeasureSpec);
                }
            } else {
                mResized = true;
                // setTextSize needs unit along with textsize in px
                setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize() - 5);
                measure(widthMeasureSpec, heightMeasureSpec);
            }
        }
        mLines = getLineCount();
    }
}
