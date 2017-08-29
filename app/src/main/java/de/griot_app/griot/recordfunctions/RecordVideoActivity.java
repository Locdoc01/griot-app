package de.griot_app.griot.recordfunctions;

import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.debug.hv.ViewServer;

import java.util.List;

import de.griot_app.griot.R;

/**
 * This activity provides the video recording functionality for Griot-App.
 */
public class RecordVideoActivity extends RecordActivity implements View.OnClickListener {

    private static final String TAG = RecordVideoActivity.class.getSimpleName();

    private static final int CAMERA_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    private static final int CAMERA_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private ImageView mButtonFlash;
    private ImageView mButtonChangeCamera;

    private int mCurrentCamera = CAMERA_BACK;
    private int mCurrentVideoQuality = CamcorderProfile.QUALITY_HIGH;
    private CamcorderProfile mCamcorderProfile;

    private Camera mCamera;
    private Camera.Parameters mParameters;
    private boolean mFlashModeTorchSupported;
    private Camera.Size mOptimalVideoSize;
    private CameraPreview mCameraPreview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMedium = MEDIUM_VIDEO;

        //TODO: entfernen
        ViewServer.get(this).addWindow(this);

        mButtonFlash = (ImageView) findViewById(R.id.button_flash);
        mButtonChangeCamera = (ImageView) findViewById(R.id.button_change_camera);
        mButtonChangeCamera.setColorFilter(Color.WHITE);

        mButtonFlash.setOnClickListener(this);
        mButtonChangeCamera.setOnClickListener(this);

        // If there is only one camera available, the change camera button is hidden
        if (Camera.getNumberOfCameras() < 2) {
            mButtonChangeCamera.setVisibility(View.GONE);
        }

        mCamcorderProfile = CamcorderProfile.get(mCurrentCamera, mCurrentVideoQuality);
        mChronometers.setBitRate(mCamcorderProfile.videoBitRate);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_record_video;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }


    //TODO: entfernen
    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
    }
    //TODO: entfernen
    public void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_flash:
                changeFlashMode();
                break;
            case R.id.button_change_camera:
                changeCamera();
                break;
        }
    }

    /**
     * Opens the camera, set optimal camera parameters and initalizes the camera preview
     */
    @Override
    protected void setup() {
        Log.d(TAG, "setup: cameraId: " + mCurrentCamera);
        releaseCamera();
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // On some devices, this method may take a long time to complete.
                    // That's why it is called from a worker thread to avoid blocking the main application UI thread
                    mCamera = Camera.open(mCurrentCamera);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // these two methods are called after the camera got opened
                            setOptimalCameraParameters();
                            initiateCameraPreview();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error opening Camera: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * determine the optimal camera parameters and set the appropriate attributes
     */
    private void setOptimalCameraParameters() {
        Log.d(TAG, "getCameraParameters: ");
        if (mCamera != null) {
            mParameters = mCamera.getParameters();
            List<String> stringList;

            // Optimal Video Size
            mOptimalVideoSize = mParameters.getPreferredPreviewSizeForVideo();
            Log.d(TAG, "getCameraParameters: preferred video preview size: width: " + mParameters.getPreferredPreviewSizeForVideo().width + " , height: " + mParameters.getPreferredPreviewSizeForVideo().height);

            //Flash Mode
            stringList = mParameters.getSupportedFlashModes();
            for (String s : stringList) {
                Log.d(TAG, "getCameraParameters: available flash modes: " + s);
            }
            if (stringList != null) {
                if (stringList.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                    mFlashModeTorchSupported = true;
                } else {
                    mFlashModeTorchSupported = false;
                }
            } else {
                mFlashModeTorchSupported = false;
            }
            Log.d(TAG, "getCameraParameters: flash mode torch available:" + mFlashModeTorchSupported);

            // Focus Mode
            // TODO: implementation of autofocus functionality (which will be used in case, that continuous focus mode is not availabe)
            // since minimum SDK Level is 16, it's not likely that there will be a supported device which has no autofocus mode at all
            stringList = mParameters.getSupportedFocusModes();
            if (stringList.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            } else if (stringList.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            Log.d(TAG, "getCameraParameters: focus mode: " + mParameters.getFocusMode());

            //Recording Hint makes sure, that recording a video will start faster
            //has to be set BEFOR starting the preview
            mParameters.setRecordingHint(true);

            mCamera.setParameters(mParameters);
        }
    }


    private void initiateCameraPreview() {
        Log.d(TAG, "initiateCameraPreview: ");

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int displayWidth = point.x;
        int displayHeight = point.y;

        int videoSizeWidth = mOptimalVideoSize.width;
        int videoSizeHeight = mOptimalVideoSize.height;

        double displayRatio = (double) displayWidth / displayHeight;
        double videoSizeRatio = (double) videoSizeWidth / videoSizeHeight;

        //TODO:
        //TODO das zugrunde liegende FrameLayout im Layout von mBackground ist als Wrap_content eingestellt. Eigentlich müsste hier die Dimension der CameraPreview
        // TODO über deren LayoutParameter wie folgt eingestellt werden. Das FrameLayout müsste sich dann an diese Größe anpassen.
        // TODO: PRÜFEN

        //Changes here have generel effect to the preview, during recording and not. The settings here should be optimal for DURING recording!!

        //Makes sure, that the FrameLayout, which holds the preview is optimally fit to the screen without cropping
        if (displayRatio > videoSizeRatio) {
            mBackground.getLayoutParams().width = (int) ((double) displayHeight * videoSizeWidth / videoSizeHeight);
            mBackground.getLayoutParams().height = displayHeight;
        } else if (displayRatio < videoSizeRatio) {
            mBackground.getLayoutParams().width = displayWidth;
            mBackground.getLayoutParams().height = (int) ((double) displayWidth * videoSizeHeight / videoSizeWidth);
        } else {
            mBackground.getLayoutParams().width = displayWidth;
            mBackground.getLayoutParams().height = displayHeight;
        }

        // catch a device specific bug
        if (Build.MANUFACTURER.equals("asus") && Build.MODEL.equals("K015")) {
            Log.e(TAG, "Model: " + Build.MANUFACTURER + " " + Build.MODEL);
            mBackground.getLayoutParams().height++;
        }

        mCameraPreview = new CameraPreview(RecordVideoActivity.this, mCamera);
        mBackground.removeAllViews();
        mBackground.addView(mCameraPreview);

        if (mFlashModeTorchSupported) {
            mButtonFlash.setVisibility(View.VISIBLE);
        } else {
            mButtonFlash.setVisibility(View.GONE);
        }
    }


    /**
     * Switches the flash mode between flash torch mode and flash off.
     */
    private void changeFlashMode() {
        Log.d(TAG, "changeFlashMode: ");
        Camera.Parameters params = mCamera.getParameters();
        if (params.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mButtonFlash.setImageResource(R.drawable.flash_on2);
        } else {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mButtonFlash.setImageResource(R.drawable.flash_off2);
        }
        mCamera.setParameters(params);
    }


    /**
     * Switches the cameras
     */
    private void changeCamera() {
        Log.d(TAG, "changeCamera: ");
        mCurrentCamera = (mCurrentCamera == CAMERA_BACK ? CAMERA_FRONT : CAMERA_BACK);

        mCamcorderProfile = CamcorderProfile.get(mCurrentCamera, mCurrentVideoQuality);
        mChronometers.setBitRate(mCamcorderProfile.videoBitRate);
        setup();
    }


    /**
     * Initializes the MediaRecorder and starts the recording.
     * @return true, if recording started successfully, false, else.
     */
    //TODO alle Aufnahmeattribute als Objectattribute rausziehen und in Model einpflegen (so wie bei Audio)
    @Override
    protected boolean startRecording() {
        Log.d(TAG, "startRecordingNewVideo: ");

        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            // mCamera.getParameters() darf nicht NACH mCamera.unlock() erfolgen
            mMediaRecorder.reset();
            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            // values in a MediaRecorder cannot be set without calling reset()
            // alternative: creation of a custom CamcorderProfile, make the changes there and set the
            // profile on the MediaRecorder using mMediaRecorder.setProfile like this:
            //TODO: Unterstützung und Definition verschiedener CamcorderProfile (auch abhängig von der Kamera (vorne/hinten))
            //   mCamcorderProfile = CamcorderProfile.get(mCurrentCamera, mCurrentVideoQuality);
/*
            Log.e(TAG, "fileFormat: " + mCamcorderProfile.fileFormat);
            Log.e(TAG, "videoFrameRate: " + mCamcorderProfile.videoFrameRate);
            Log.e(TAG, "videoFrameWidth: " + mCamcorderProfile.videoFrameWidth + " , videoFrameHeight: " + mCamcorderProfile.videoFrameHeight);
            Log.e(TAG, "videoCodec: " + mCamcorderProfile.videoCodec);
            Log.e(TAG, " ");
*/
/*
            mCamcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
            mCamcorderProfile.videoFrameRate = 30;
            mCamcorderProfile.videoFrameWidth = optimalVideoSize.width;
            mCamcorderProfile.videoFrameHeight = optimalVideoSize.height;
            mCamcorderProfile.videoCodec = MediaRecorder.VideoEncoder.DEFAULT;
*/
/*
            Log.e(TAG, "fileFormat: " + mCamcorderProfile.fileFormat);
            Log.e(TAG, "videoFrameRate: " + mCamcorderProfile.videoFrameRate);
            Log.e(TAG, "videoFrameWidth: " + mCamcorderProfile.videoFrameWidth + " , videoFrameHeight: " + mCamcorderProfile.videoFrameHeight);
            Log.e(TAG, "videoCodec: " + mCamcorderProfile.videoCodec);
*/
            mMediaRecorder.setProfile(mCamcorderProfile);
            //    mMediaRecorder.setVideoEncodingBitRate(mCamcorderProfile.videoBitRate);

            mMediaFile = getOutputFile();
/*
            Log.d(TAG, "mMediaFile.toString: " + mMediaFile.toString());
            Log.d(TAG, "mMediaFile.getPath.toString: " + mMediaFile.getPath());
            Log.d(TAG, "Uri.fromFile(mMediaFile).toString: " + Uri.fromFile(mMediaFile).toString());
            Log.d(TAG, "mMediaFile.getName: " + mMediaFile.getName());
*/
            //TODO Fehler wenn Carousel leer ist. (Nur relevant, wenn Fall wirklich auftreten kann)
            mCurrentRecordingIndex = mCarousel.getCurrentIndex();

            // hat keinen Effekt:
            mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());

            mMediaRecorder.setOutputFile(mMediaFile.getPath());
            mMediaRecorder.setVideoSize(mOptimalVideoSize.width, mOptimalVideoSize.height);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mChronometers.start();

            // da ein KameraWechsel während der Aufnahme zu einem Fehler führen würde
            // muss der ChangeCamera-Button solange deaktiviert werden
            mButtonChangeCamera.setEnabled(false);
            mButtonChangeCamera.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));

        }catch (Exception e) {
            Log.e(TAG, "Error starting MediaRecorder: " + e.getMessage());
            e.printStackTrace();
            mCamera.lock();
            return false;
        }
        return true;
    }


    /**
     * stops the recording
     */
    @Override
    protected void stopRecording() {
        Log.d(TAG, "stopRecording: ");

        try {
            mMediaRecorder.stop();
        } catch (Exception e) {
            Log.e(TAG, "Error stopping record: " + e.getMessage());
        }
        mChronometers.stop();

        //If current question wasn't recorded so far
        if (allMediaMultiFilePaths.get(mCurrentRecordingIndex).isEmpty()) {
            recordedQuestionsCount++;
        }

        //Add the (next) file path of the current questions media file.
        allMediaMultiFilePaths.get(mCurrentRecordingIndex).add(Uri.fromFile(mMediaFile).toString());

        mCurrentRecordingIndex = -1;
        setup();
        mButtonChangeCamera.setEnabled(true);
        mButtonChangeCamera.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotWhite, null));
    }


    private void releaseCamera() {
        Log.d(TAG, "releaseCamera: ");
        if (mCamera != null) {
            //Not necessary on Android > 4.0, UNLESS mMediaRecorder.prepare() didn't throw an exception
            mCamera.lock();
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
}
