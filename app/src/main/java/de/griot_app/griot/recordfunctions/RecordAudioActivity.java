package de.griot_app.griot.recordfunctions;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

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

        ImageView imageView = new ImageView(RecordAudioActivity.this);
        imageView.setImageResource(R.drawable.add_avatar);       //TODO Profilbild verwenden

        //FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        //params.gravity = Gravity.CENTER;
        //imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);

        mBackground.addView(imageView);
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

            mFile = getOutputFile(MEDIUM_VIDEO);
            mListFilenames.get(mCarousel.getCurrentIndex()).add(mFile.getPath());
            //alternativen
            //mListFilenames.get(mCarousel.getCurrentIndex()).add(mFile.getName());
            //mListFilenames.get(mCarousel.getCurrentIndex()).add(Uri.fromFile(mFile));     //(dafür List<List<Uri>> erforderlich)
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
        setup();

    }
}
