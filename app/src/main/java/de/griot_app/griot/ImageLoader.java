package de.griot_app.griot;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;


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
     * Loads an image from the passed source via Glide into an the passed imageView.
     * If there is no image available at the source or the source in not valid, an alternative image from the passed
     * placeholder source will be loaded. The scaleTyp can be set to centerCrop.
     * @param imageView         The ImageView into which the image will be loaded
     * @param imageSource       The source of the image
     * @param placeholderSource The source of the placeholder
     * @param centerCrop        If true, the scaleType centerCrop will be aplied
     * @param <T1>              The type of the image source. following types are valid:
     *            Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     * @param <T2>              The type of the placeholder source. following types are valid:
     *            Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     */
    public <T1, T2> void load(ImageView imageView, T1 imageSource, T2 placeholderSource, boolean centerCrop) {
        try {
            GlideRequest<Drawable> glideRequest = GlideApp.with(mContext)
                    .load(imageSource)
                    //if load fails, the placeholder will be loaded (only if placeholderSource is not null)
                    .error(GlideApp.with(mContext)
                            .load(placeholderSource));

            if (centerCrop) {
                glideRequest = glideRequest.centerCrop();
            }

            glideRequest.into(imageView);

        } catch (IllegalArgumentException iae) {
            iae.printStackTrace();
        }
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
        load(imageView, imageSource, placeholderSource, false);
    }

    /**
     * Loads an image from the passed source via Glide into an the passed imageView.
     * The scaleTyp can be set to centerCrop.
     * @param imageView     The ImageView into which the image will be loaded
     * @param imageSource   The source of the image
     * @param centerCrop    If true, the scaleType centerCrop will be aplied
     * @param <T1>          The type of the image source. following types are valid:
     *            Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     */
    public <T1> void load(ImageView imageView, T1 imageSource, boolean centerCrop) {
        load(imageView, imageSource, null, centerCrop);
    }


    /**
     * Loads an image from the passed source via Glide into an the passed imageView.
     * @param imageView     The ImageView into which the image will be loaded
     * @param imageSource   The source of the image
     * @param <T1>          The type of the image source. following types are valid:
     *            Uri, File, byte[], Object, Bitmap, String (url), Drawable, Integer (resourceID)
     */
    public <T1> void load(ImageView imageView, T1 imageSource) {
        load(imageView, imageSource, null, false);
    }

}
