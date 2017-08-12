package de.griot_app.griot;


import android.content.Context;

import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;


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
        View v = LayoutInflater.from(mContext).inflate(R.layout.object_profile_image, this);

        mProfileImage = (ImageView) v.findViewById(R.id.profile_image);
        mProfileImageCircle = (ImageView) v.findViewById(R.id.profile_image_circle);
        mProfileImagePlus = (ImageView) v.findViewById(R.id.profile_image_plus);
    }

    public ImageView getProfileImage() { return mProfileImage; }

    public ImageView getProfileImageCircleWhite() { return mProfileImageCircle; }

    public ImageView getProfileImagePlus() { return mProfileImagePlus; }

    public void setWhite() { mProfileImageCircle.setImageResource(R.drawable.piv_circle_white); }

    public void setBlue() { mProfileImageCircle.setImageResource(R.drawable.piv_circle_blue); }

}
