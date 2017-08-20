package de.griot_app.griot;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

import de.griot_app.griot.R;

/**
 * Created by marcel on 13.07.17.
 */

public class TagView extends ConstraintLayout {

    private static final String TAG = TagView.class.getSimpleName();

    private Context mContext;

    private TextView mTextViewTag;
    private ImageView mButtonDeleteTag;
    private ImageView mImageViewSpacer;

    // All three constructors are necessary in order to create an object of this class from a layout, so that it could be found by findViewbyid()
    public TagView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TagView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        View v =  LayoutInflater.from(mContext).inflate(R.layout.class_tag_view, this);

        mTextViewTag = (TextView) v.findViewById(R.id.textView_tag);
        mButtonDeleteTag = (ImageView) v.findViewById(R.id.button_delete_tag);
        mImageViewSpacer = (ImageView) v.findViewById(R.id.imageView_spacer);
    }

    public TextView getTextViewTag() { return mTextViewTag; }

    public ImageView getButtonDeleteTag() { return mButtonDeleteTag; }

    public ImageView getImageViewSpacer() { return mImageViewSpacer; }


    public void setTag(String tag) { mTextViewTag.setText(tag); }

    public void setVisibilityDeleteButton(boolean visibility) { mButtonDeleteTag.setVisibility(visibility ? VISIBLE : GONE); }

    public void setVisibilitySpacer(boolean visibility) { mImageViewSpacer.setVisibility(visibility ? VISIBLE : GONE); }
}

