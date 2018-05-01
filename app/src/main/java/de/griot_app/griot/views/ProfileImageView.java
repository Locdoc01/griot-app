package de.griot_app.griot.views;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import de.griot_app.griot.ImageLoader;
import de.griot_app.griot.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ProfileImageView provides a round profile image with a thin round lightgrey border.
 * After creation it is cleared and shows only the boarder. If an image was successfully loaded,
 * it will be shown together with the boarder.
 * If an image couldn't be loaded successfully, the View shows a gender neutral avatar as placeholder
 * together with the border instead.
 * The image can be cleared again. A plus sign can be shown, but only, if the image is cleared.
 */
public class ProfileImageView extends ConstraintLayout {

    private static final String TAG = ProfileImageView.class.getSimpleName();

    private static final int PLACERHOLDER_RESOURCE = R.drawable.avatar_single;

    private Context mContext;

    private ImageLoader imageLoader;

    //Views
    private ImageView mPlus;
    private ImageView mCircle;
    private CircleImageView mProfileImage;

    /**
     * Constructors
     */
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

    /**
     * Initializes the ProfilImageView. After initialization it shows only the circular border and the image is cleared.
     */
    private void init() {
        imageLoader = new ImageLoader(mContext);
        View v = LayoutInflater.from(mContext).inflate(R.layout.class_profile_image, this);
        //Get references to layout objects
        mProfileImage = v.findViewById(R.id.circleImageView_profile_image);
        mCircle = v.findViewById(R.id.imageView_circle);
        mPlus = v.findViewById(R.id.imageView_plus);
    }

    /**
     * Returns the ImageView, that is supposed to hold the profile image. It can be set and changed by its own methods.
     * @return CircleImageView, that is supposed to hold the profile image.
     */
    @Deprecated
    public CircleImageView getProfileImage() { return mProfileImage; }

    @Deprecated
    public ImageView getProfileImagePlus() { return mPlus; }

    @Deprecated
    public ImageView getProfileImageCircle() { return mCircle; }

    @Deprecated
    public void setBlue() {}

    /**
     * Loads an image from the passed image source and shows it. The source can be of the following types:
     * Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     * If there is no image available at the passed source, there will be shown a gender neutral avatar image
     * as placeholder.
     * If null or any other type which is not a valid image source is passed, the placeholder will also be shown.
     * @param imageSource   image source, which can be of any type including null.
     */
    public <T> void loadImageFromSource(T imageSource) {
        imageLoader.load(mProfileImage, imageSource, PLACERHOLDER_RESOURCE);
    }

    /**
     * Clears the image or, if shown, the placeholder, so that only the circular boarder and
     * (depending on its visibility) the plus sign will be shown
     */
    public void clearImage() {
        mProfileImage.setImageBitmap(null);
    }


    /**
     * Sets the visibility of the plus sign acccording to the value of showPlus. Though, it would only be visible,
     * if the image is cleared.
     * @param showPlus   If true, visibility is set to View.VISIBILE, otherwise to View.INVISIBLE
     */
    public void showPlus(boolean showPlus) {
        mPlus.setVisibility(showPlus ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Sets the visibility of the circular boarder acccording to the value of showCircle.
     * @param showCircle   If true, visibility is set to View.VISIBILE, otherwise to View.INVISIBLE
     */
    public void showCircle(boolean showCircle) {
        mCircle.setVisibility(showCircle ? View.VISIBLE : View.INVISIBLE);
    }
}
