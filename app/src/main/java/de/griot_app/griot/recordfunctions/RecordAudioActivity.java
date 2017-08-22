package de.griot_app.griot.recordfunctions;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import de.griot_app.griot.R;

/**
 * Created by marcel on 14.07.17.
 */

public class RecordAudioActivity extends RecordActivity {

    private static final String TAG = RecordVideoActivity.class.getSimpleName();

    private int mOutputFormat = MediaRecorder.OutputFormat.MPEG_4;
    private int mAudioEncoder = MediaRecorder.AudioEncoder.AAC;     //TODO: alternativ AAC_ELD testen
    private int mAudioSamplingRate = 44100;                         //TODO: alternativ 48000 testen
    private int mAudioBitRate = 96000;                              //TODO: alternativ 128000 testen

    private Timer mTimerAmplitude;
    private int mAmplitude;


    //TODO: prüfen auf Optimierung
    private class AmplitudeTask extends TimerTask {
        public void run() {
            mAmplitude = mMediaRecorder.getMaxAmplitude();
            Log.d(TAG, "Amplitude: " + mAmplitude);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMedium = MEDIUM_AUDIO;

        mChronometers.setBitRate( mAudioBitRate );
        mCarousel.setInvertedLayout();

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_record_audio;
    }

    /*
    @Override
    protected void onPause() {
        super.onPause();
        //releaseCamera();
    }
    */

    /*
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id....:
                break;
        }
    }
*/



    @Override
    protected void setup() {
        Log.d(TAG, "setup: ");

        /*
        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        int displayWidth = point.x;
        int displayHeight = point.y;
        Log.d(TAG, "setupBackground: display: width: " + displayWidth + " , height: " + displayHeight);

        mBackground.getLayoutParams().width = displayWidth;
        mBackground.getLayoutParams().height = displayHeight;
*/

        final ImageView imageViewBackground = new ImageView(RecordAudioActivity.this);

        File file = null;
        try {
            file = File.createTempFile("profile_image" + "_", ".jpg");
        } catch (Exception e) {
        }
        final String path = file.getPath();

        try {
            FirebaseStorage.getInstance()
                    .getReferenceFromUrl(narratorPictureURL)
                    .getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    imageViewBackground.setImageURI(Uri.parse(path));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error downloading background image file");
                    imageViewBackground.setImageResource(R.drawable.avatar_single);
                }
            });
        } catch (Exception e) {}

        //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        //params.gravity = Gravity.CENTER;
        //imageView.setLayoutParams(params);
        imageViewBackground.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageViewBackground.setColorFilter(filter);

        mBackground.addView(imageViewBackground);
    }



    @Override
    protected boolean startRecording() {
        Log.d(TAG, "startRecordingNewVideo: ");

        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(mOutputFormat);

            mFile = getOutputFile();

            mCurrentRecordingIndex = mCarousel.getCurrentIndex();

            mMediaRecorder.setOutputFile(mFile.getPath());

            //TODO evt verschiedene Qualitäten als Option anbieten
            mMediaRecorder.setAudioEncoder(mAudioEncoder);
            mMediaRecorder.setAudioSamplingRate(mAudioSamplingRate);
            mMediaRecorder.setAudioEncodingBitRate(mAudioBitRate);


            //TODO: prüfen auf Optimierung
            mTimerAmplitude = new Timer();

            mMediaRecorder.prepare();

            mMediaRecorder.start();
            mChronometers.start();
            mTimerAmplitude.scheduleAtFixedRate(new AmplitudeTask(), 0, 100);

        } catch (Exception e) {
            Log.e(TAG, "Error starting MediaRecorder: " + e.getMessage());

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
        mTimerAmplitude.cancel();

        // if current question wasn't recorded so far
        if (allMediaMultiFilePaths.get(mCurrentRecordingIndex).isEmpty()) {
            recordedQuestionsCount++;
        }

        // add the (next) file path of the current questions media file.
        allMediaMultiFilePaths.get(mCurrentRecordingIndex).add(mFile.getPath());
        //alternativen
        //allMediaMultiFilePaths.get(mCarousel.getCurrentIndex()).add(mFile.getName());
        //allMediaMultiFilePaths.get(mCarousel.getCurrentIndex()).add(Uri.fromFile(mFile));     //(dafür List<List<Uri>> erforderlich)

        mCurrentRecordingIndex = -1;
        setup();
    }
}
