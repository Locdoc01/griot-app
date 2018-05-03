package de.griot_app.griot;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;


/**
 * ImageLoader encapsulates the image loading via Glide library and provides a simplified consistent
 * interface for loading images.
 */
public class ImageLoader {

    private Context mContext;

    /**
     * Constructor
     * @param context   The Context, which contains the ImageViews
     */
    public ImageLoader(Context context) {
        mContext = context;
    }


    /**
     * Loads an image from the passed source via Glide into an the passed imageView
     * @param imageView     The ImageView into which the image will be loaded
     * @param imageSource   The source of the image
     * @param <T1>          The type of the image source. following types are valid:
     *            Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     */
    public <T1> void load(ImageView imageView, T1 imageSource) {
        load(imageView, imageSource, null);
    }


    /**
     * Loads an image from the passed source via Glide into an the passed imageView.
     * If there is no image available at the source or the source in not valid, an alternative image from the passed
     * placeholder source will be loaded.
     * @param imageView         The ImageView into which the image will be loaded
     * @param imageSource       The source of the image
     * @param placeholderSource The source of the placeholder
     * @param <T1>              The type of the image source. following types are valid:
     *            Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     * @param <T2>              The type of the placeholder source. following types are valid:
     *            Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     */
    public <T1, T2> void load(ImageView imageView, T1 imageSource, T2 placeholderSource) {
        Glide.with(mContext)
                .load(imageSource)
                .error(Glide.with(mContext)
                        .load(placeholderSource))
                .into(imageView);
    }
}
