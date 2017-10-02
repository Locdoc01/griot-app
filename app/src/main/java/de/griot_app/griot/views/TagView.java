package de.griot_app.griot.views;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.griot_app.griot.R;

/**
 * TagView shows a white tag in a blue box. It provides an optional delete button, which is visible by default and can be hidden.
 */
public class TagView extends ConstraintLayout {

    private static final String TAG = TagView.class.getSimpleName();

    private Context mContext;

    private TextView mTextViewTag;
    private ImageView mButtonDeleteTag;
    private ImageView mImageViewSpacer;

    /**
     * Constructors
     * All three of them are necessary in order to create an object of this class from a layout, so that it could be inflated
     */
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

    /**
     * Initializations to be performed in constructurs.
     */
    private void init() {
        View v =  LayoutInflater.from(mContext).inflate(R.layout.class_tag_view, this);

        mTextViewTag = (TextView) v.findViewById(R.id.textView_tag);
        mButtonDeleteTag = (ImageView) v.findViewById(R.id.button_delete_tag);
        mImageViewSpacer = (ImageView) v.findViewById(R.id.imageView_spacer);
    }

    /**
     * Returns a reference to the TextView, which holds the tag
     * @return Reference to the tag-holding TextView
     */
    public TextView getTextViewTag() { return mTextViewTag; }

    /**
     * Returns a reference to the detete button. An Eventlistener for the button has to be added and implemented in the using activity
     * @return Reference to the delete button
     */
    public ImageView getButtonDeleteTag() { return mButtonDeleteTag; }

    /**
     * Sets the tag
     * @param tag
     */
    public void setTag(String tag) { mTextViewTag.setText(tag); }

    /**
     * Sets the visibility of the delete button.
     * @param visibility
     */
    public void setVisibilityDeleteButton(boolean visibility) { mButtonDeleteTag.setVisibility(visibility ? VISIBLE : GONE); }
}

