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
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.android.debug.hv.ViewServer;

import java.util.List;

import de.griot_app.griot.R;

/**
 * Created by marcel on 14.07.17.
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
    //    private boolean mIsFlashOn = false;
    //private String mFocusMode;
    private Camera.Size mOptimalVideoSize;
    private CameraPreview mCameraPreview;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //TODO: entfernen
        ViewServer.get(this).addWindow(this);

        mButtonFlash = (ImageView) findViewById(R.id.button_flash);
        mButtonChangeCamera = (ImageView) findViewById(R.id.button_change_camera);
        mButtonChangeCamera.setColorFilter(Color.WHITE);

        mButtonFlash.setOnClickListener(this);
        mButtonChangeCamera.setOnClickListener(this);

        // Wenn weniger als 2 Kameras vorhanden sind, wird der CameraChangeButton aus dem Layout entfernt
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
    protected void onResume() {
        super.onResume();

        //TODO: entfernen
        ViewServer.get(this).setFocusedWindow(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }


    public void onDestroy() {
        super.onDestroy();

        //TODO: entfernen
        ViewServer.get(this).removeWindow(this);
    }


        /*
    // According to the Camera API the actual recommendation for checking for Camera is to use
    // getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA).
    // Unfortunately this didn't work properly since the method always returned true no matter
    // if there really was an actual camera available or not
    // However, the method Camera.getNumberOfCameras() that I use instead, also works in that matter
    // at least for Android API Levels supported by my app.
    private boolean deviceHasCamera() {
        Log.d(TAG, "deviceHasCamera: ");
        return Camera.getNumberOfCameras() != 0;
    }
*/



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



    private void setOptimalCameraParameters() {
        Log.d(TAG, "getCameraParameters: ");
        if (mCamera != null) {
            mParameters = mCamera.getParameters();
            List<String> stringList;
            /*
            List<Camera.Size> sizeList;

            // Optimal Video Size
            Camera.Size optimalSize = null;
            long resolution = 0;
            sizeList = mParameters.getSupportedPreviewSizes();
            List<Camera.Size> supportedSizes = mCamera.getParameters().getSupportedVideoSizes();
            for (Camera.Size size : supportedSizes) {
                if (size.width * size.height > resolution) {
                    resolution = size.width * size.height;
                    optimalSize = size;
                }
            }
            Log.d(TAG, "getCameraParameters: optimal video size: width: " + optimalSize.width + " , height: " + optimalSize.height);
            mOptimalVideoSize = optimalSize;
*/
            // Optimal Video Size
            mOptimalVideoSize = mParameters.getPreferredPreviewSizeForVideo();
            Log.d(TAG, "getCameraParameters: preffered video preview size: width: " + mParameters.getPreferredPreviewSizeForVideo().width + " , height: " + mParameters.getPreferredPreviewSizeForVideo().height);


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

            //Recording Hint
            // makes sure, that recording a video will start faster
            // has to be set BEFOR starting the preview
            mParameters.setRecordingHint(true);

            mCamera.setParameters(mParameters);
        }
    }


    private void initiateCameraPreview() {
        Log.d(TAG, "initiateCameraPreview: ");

/*
        Point point = new Point();
        if (android.os.Build.VERSION.SDK_INT >= 17 ) { //android.os.Build.VERSION_CODES.JELLY_BEAN_MR1){
            //Display.getRealSize() gibt die tatsächliche Bildschirmgröße zurück, also inklusive system decoration (verfügbar seit API 17)
            getWindowManager().getDefaultDisplay().getRealSize(point);
        } else {
            //Display.getSize() gibt die Bildschirmgröße zurück, die Applicationen zur Verfügung stehen (physical size - system decoration)
            getWindowManager().getDefaultDisplay().getSize(point);
        }
*/

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int displayWidth = point.x;
        int displayHeight = point.y;
        Log.d(TAG, "initiateCameraPreview: display: width: " + displayWidth + " , height: " + displayHeight);

        int videoSizeWidth = mOptimalVideoSize.width;
        int videoSizeHeight = mOptimalVideoSize.height;

        Log.d(TAG, "initiateCameraPreview: videoSize: width: " + videoSizeWidth + " , height: " + videoSizeHeight);
        Log.d(TAG, "initiateCameraPreview: videoFrameRate: " + mCamcorderProfile.videoFrameRate);

        double displayRatio = (double) displayWidth / displayHeight;
        double videoSizeRatio = (double) videoSizeWidth / videoSizeHeight;
        Log.d(TAG, "initiateCameraPreview: displayRatio: " + displayRatio);
        Log.d(TAG, "initiateCameraPreview: videoSizeRatio: " + videoSizeRatio);


        //TODO:
        //TODO das zugrunde liegende FrameLayout im Layout von mBackground ist als Wrap_content eingestellt. Eigentlich müsste hier die Dimension der CameraPreview
        // TODO über deren LayoutParameter wie folgt eingestellt werden. Das FrameLayout müsste sich dann an diese Größe anpassen.
        // TODO: PRÜFEN



        //Änderungen hier haben generelle Auswirkungen auf die Preview, also während aufgenommen wird und während nicht. Die Einstellung hier sollte die optimale für WÄHREND
        //der Aufnahme sein !!

        // folgende Abfrage sorgt dafür, dass die Größe des FrameLayouts, welches die Preview zeigt, optimal auf den Bildschirm passt, ohne beschnitten zu werden.
        if (displayRatio > videoSizeRatio) {
            Log.d(TAG, "initiateCameraPreview: displayRatio > videoFrameRate: FrameLayoutCameraPreview: width: " + mBackground.getLayoutParams().width + " , height: " + mBackground.getLayoutParams().height);
            mBackground.getLayoutParams().width = (int) ((double) displayHeight * videoSizeWidth / videoSizeHeight);
            mBackground.getLayoutParams().height = displayHeight;
            Log.d(TAG, "initiateCameraPreview: displayRatio > videoFrameRate: FrameLayoutCameraPreview: width: " + mBackground.getLayoutParams().width + " , height: " + mBackground.getLayoutParams().height);
        } else if (displayRatio < videoSizeRatio) {
            Log.d(TAG, "initiateCameraPreview: displayRatio < videoFrameRate: FrameLayoutCameraPreview: width: " + mBackground.getLayoutParams().width + " , height: " + mBackground.getLayoutParams().height);
            mBackground.getLayoutParams().width = displayWidth;
            mBackground.getLayoutParams().height = (int) ((double) displayWidth * videoSizeHeight / videoSizeWidth);
            Log.d(TAG, "initiateCameraPreview: displayRatio < videoFrameRate: FrameLayoutCameraPreview: width: " + mBackground.getLayoutParams().width + " , height: " + mBackground.getLayoutParams().height);
        } else {
            Log.d(TAG, "initiateCameraPreview: displayRatio == videoFrameRate: FrameLayoutCameraPreview: width: " + mBackground.getLayoutParams().width + " , height: " + mBackground.getLayoutParams().height);
            mBackground.getLayoutParams().width = displayWidth;
            mBackground.getLayoutParams().height = displayHeight;
            Log.d(TAG, "initiateCameraPreview: displayRatio == videoFrameRate: FrameLayoutCameraPreview: width: " + mBackground.getLayoutParams().width + " , height: " + mBackground.getLayoutParams().height);
        }

        // Fängt einen gerätespezifischen Bug ab
        if (Build.MANUFACTURER.equals("asus") && Build.MODEL.equals("K015")) {
            Log.e(TAG, "Model: " + Build.MANUFACTURER + " " + Build.MODEL);
            mBackground.getLayoutParams().height++;
        }

//        mBackground.requestLayout();

        mCameraPreview = new CameraPreview(RecordVideoActivity.this, mCamera);
        mBackground.removeAllViews();
        mBackground.addView(mCameraPreview);

        //ViewGroup parent = (ViewGroup) mBackground.getParent();
        //parent.removeView(mBackground);
        //parent.addView(mBackground, 0);

        //mButtonRecord.bringToFront();

        if (mFlashModeTorchSupported) {
            mButtonFlash.setVisibility(View.VISIBLE);
        } else {
            mButtonFlash.setVisibility(View.GONE);
        }
    }


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
        Log.d(TAG, "changeFlashMode: " + params.getFlashMode());
        mCamera.setParameters(params);
    }


    private void changeCamera() {
        Log.d(TAG, "changeCamera: ");
        mCurrentCamera = (mCurrentCamera == CAMERA_BACK ? CAMERA_FRONT : CAMERA_BACK);

        mCamcorderProfile = CamcorderProfile.get(mCurrentCamera, mCurrentVideoQuality);
        mChronometers.setBitRate(mCamcorderProfile.videoBitRate);
        setup();
    }


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

            Log.e(TAG, "fileFormat: " + mCamcorderProfile.fileFormat);
            Log.e(TAG, "videoFrameRate: " + mCamcorderProfile.videoFrameRate);
            Log.e(TAG, "videoFrameWidth: " + mCamcorderProfile.videoFrameWidth + " , videoFrameHeight: " + mCamcorderProfile.videoFrameHeight);
            Log.e(TAG, "videoCodec: " + mCamcorderProfile.videoCodec);
            Log.e(TAG, " ");

//            mCamcorderProfile.fileFormat = MediaRecorder.OutputFormat.MPEG_4;
//            mCamcorderProfile.videoFrameRate = 30;
//            mCamcorderProfile.videoFrameWidth = optimalVideoSize.width;
//            mCamcorderProfile.videoFrameHeight = optimalVideoSize.height;
//            mCamcorderProfile.videoCodec = MediaRecorder.VideoEncoder.DEFAULT;

            Log.e(TAG, "fileFormat: " + mCamcorderProfile.fileFormat);
            Log.e(TAG, "videoFrameRate: " + mCamcorderProfile.videoFrameRate);
            Log.e(TAG, "videoFrameWidth: " + mCamcorderProfile.videoFrameWidth + " , videoFrameHeight: " + mCamcorderProfile.videoFrameHeight);
            Log.e(TAG, "videoCodec: " + mCamcorderProfile.videoCodec);
            mMediaRecorder.setProfile(mCamcorderProfile);
            //    mMediaRecorder.setVideoEncodingBitRate(mCamcorderProfile.videoBitRate);

            mFile = getOutputFile(MEDIUM_VIDEO);

            Log.d(TAG, "mFile.toString: " + mFile.toString());
            Log.d(TAG, "mFile.getPath.toString: " + mFile.getPath());
            Log.d(TAG, "Uri.fromFile(mFile).toString: " + Uri.fromFile(mFile).toString());
            Log.d(TAG, "mFile.getName: " + mFile.getName());

            //TODO Fehler wenn Carousel leer ist. (Nur relevant, wenn Fall wirklich auftreten kann)
            mListFilenames.get(mCarousel.getCurrentIndex()).add(mFile.getPath());
            //alternativen
            //mListFilenames.get(mCarousel.getCurrentIndex()).add(mFile.getName());
            //mListFilenames.get(mCarousel.getCurrentIndex()).add(Uri.fromFile(mFile));     //(dafür List<List<Uri>> erforderlich)

            // hat keinen Effekt:
            mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());

            mMediaRecorder.setOutputFile(mFile.getPath());
            mMediaRecorder.setVideoSize(mOptimalVideoSize.width, mOptimalVideoSize.height);

            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mChronometers.start();

            // da ein KameraWechsel während der Aufnahme zu einem Fehler führen würde
            // muss der ChangeCamera-Button solange deaktiviert werden
            mButtonChangeCamera.setEnabled(false);
            //TODO testen !!!!!!!!!!
            //mButtonChangeCamera.setImageResource(R.mipmap.btn_change_camera_disabled);
            mButtonChangeCamera.setColorFilter(Color.parseColor("#505154"));

        }catch (Exception e) {
            Log.e(TAG, "Error starting MediaRecorder: " + e.getMessage());
            mCamera.lock();
            return false;
        }
        return true;
    }


    @Override
    protected void stopRecording() {
        Log.d(TAG, "stopRecording: ");
        try {
            mMediaRecorder.stop();

        } catch (Exception e) {
            Log.e(TAG, "Error stopping record: " + e.getMessage());
        }
        mChronometers.stop();
        setup();
        mButtonChangeCamera.setEnabled(true);
        mButtonChangeCamera.setColorFilter(Color.WHITE);
    }


    private void releaseCamera() {
        Log.d(TAG, "releaseCamera: ");
        if (mCamera != null) {
            // not necessary on Android > 4.0, UNLESS mMediaRecorder.prepare() didn't throw an exception
            mCamera.lock();
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

}
