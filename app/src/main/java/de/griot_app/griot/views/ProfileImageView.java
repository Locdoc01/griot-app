package de.griot_app.griot.views;


import android.content.Context;

import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import de.griot_app.griot.R;

/**
 * ProfileImageView provides a round profile image with a thin lightgrey Border. If no image resource is set, it will show a plus-sign over background color.
 * The background color around the circle is Griot-white by default and can be changed to Griot-blue.
 * To set a profile image resource, use getProfileImage() to get a reference to the ImageView, that is supposed to hold the image. With that reference the image recource
 * can be set by one of of its own ImageView-class-methods.
 *
 * Example:
 * ProfileImageView piv = findViewById(R.contactID.profile_image);
 * piv.getProfileImage.setImageURI(uri);
 */
public class ProfileImageView extends ConstraintLayout {

    private static final String TAG = ProfileImageView.class.getSimpleName();

    private Context mContext;

    private ImageView mProfileImage;
    private ImageView mProfileImageCircle;
    private ImageView mProfileImagePlus;

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
        View v = LayoutInflater.from(mContext).inflate(R.layout.class_profile_image, this);

        mProfileImage = (ImageView) v.findViewById(R.id.profile_image);
        mProfileImageCircle = (ImageView) v.findViewById(R.id.profile_image_circle);
        mProfileImagePlus = (ImageView) v.findViewById(R.id.profile_image_plus);
    }

    /**
     * Returns the ImageView, that is supposed to hold the profile image. It can be set and changed by its own methods.
     * @return ImageView, that is supposed to hold the profile image.
     */
    public ImageView getProfileImage() { return mProfileImage; }

    /**
     * Returns the ImageView, that holds the plus-sign. Thus it could be changed by ImageView-methods.
     * @return ImageView, that holds the plus-sign.
     */
    public ImageView getProfileImagePlus() { return mProfileImagePlus; }

    /**
     * Returns the ImageView, that holds the circular border. Thus it could be changed by ImageView-methods.
     * @return ImageView, that holds the circular border.
     */
    public ImageView getProfileImageCircle() { return mProfileImageCircle; }

    /**
     * Sets the background color around the circle to Griot-white. (default)
     */
    public void setWhite() { mProfileImageCircle.setImageResource(R.drawable.piv_circle_white); }

    /**
     * Sets the background color around the circle to Griot-blue
     */
    public void setBlue() { mProfileImageCircle.setImageResource(R.drawable.piv_circle_blue); }

}
