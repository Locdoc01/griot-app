package de.griot_app.griot.recordfunctions;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Provides a surface for showing the live preview of the camera, which is rendered in a secondary thread
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mContext = context;

        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "surfaceCreated: Error setting Camera Preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int surfaceWidth, int surfaceHeight) {
        Log.d(TAG, "surfaceChanged: surfaceFormat: " + format + ", width: " + surfaceWidth + ", height: " + surfaceHeight);

        if (mHolder.getSurface() == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, "surfaceChanged: Error stopping Preview (Ignore):" + e.getMessage());
        }

        // Changes here just have an effect to the preview, when the camera is NOT recording

        /*
        Camera.Size optimalVideoSize = null;
        List<Camera.Size> supportedVideoSizes = mCamera.getParameters().getSupportedVideoSizes();
        long videoResolution = 0;
        for (Camera.Size s : supportedVideoSizes) {
            Log.d(TAG, "surfaceChanged: supportedVideoSizes: width: " + s.width + ", height: " + s.height);
            if ( s.width * s.height > videoResolution ) {
                videoResolution = s.width * s.height;
                optimalVideoSize = s;
            }
        }
        Log.d(TAG, "surfaceChanged: optimalVideoSize: width: " + optimalVideoSize.width + ", height: " + optimalVideoSize.height);
*/

        Camera.Size optimalPreviewSize = null;
        List<Camera.Size> supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        long previewResolution = 0;
        for (Camera.Size s : supportedPreviewSizes) {
            Log.d(TAG, "surfaceChanged: supportedPreviewSizes: width: " + s.width + ", height: " + s.height);
            if ( s.width * s.height > previewResolution ) {
                previewResolution = s.width * s.height;
                optimalPreviewSize = s;
            }
        }
        Log.d(TAG, "surfaceChanged: optimalPreviewSize: width: " + optimalPreviewSize.width + ", height: " + optimalPreviewSize.height);


        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            Log.e(TAG, "surfaceChanged: Error setting Parameters of Camera: " + e.getMessage());
        }

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "surfaceChanged: Error setting Camera Preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: ");
        mHolder.removeCallback(this);

    }
}
