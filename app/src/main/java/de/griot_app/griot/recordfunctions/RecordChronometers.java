package de.griot_app.griot.recordfunctions;

import android.app.Activity;
import android.content.Context;
import android.media.CamcorderProfile;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import de.griot_app.griot.R;

/**
 * Created by marcel on 13.07.17.
 */

public class RecordChronometers extends FrameLayout {

    private static final String TAG = RecordChronometers.class.getSimpleName();

    private Context mContext;
    private Chronometer mChronometerRecord;
    private TextView mChronometerRemain;
    private long mInitSeconds;
    private int mBitRate = -1;

    // All three constructors are necessary in order to create an object of this class from a layout, so that it could be found by findViewbyid()
    public RecordChronometers(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RecordChronometers(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public RecordChronometers(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }


    private void init() {
        View v =  LayoutInflater.from(mContext).inflate(R.layout.record_chronometers, this);

        mChronometerRecord = (Chronometer) v.findViewById(R.id.chronometer_record);
        mChronometerRemain = (TextView) v.findViewById(R.id.chronometer_remain);
    }


    public void setBitRate(int bitRate) {
        mBitRate = bitRate;
        updateChronometerRemain(getAvailableSeconds());
    }


    public void start() throws Exception {
        if (mBitRate == -1) {
            throw new Exception("Error: BitRate not set");
        }
        mInitSeconds = getAvailableSeconds();
        updateChronometerRemain(mInitSeconds);
        mChronometerRecord.setBase(SystemClock.elapsedRealtime());
        mChronometerRecord.start();
        mChronometerRecord.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Log.d(TAG, "onChronometerTick: ");
                updateChronometerRemain(--mInitSeconds);
            }
        });
    }


    public void stop() {
        mChronometerRecord.stop();
        mInitSeconds = getAvailableSeconds();
        updateChronometerRemain(mInitSeconds);
        mChronometerRecord.setBase(SystemClock.elapsedRealtime());
    }


    private long getAvailableSeconds() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long availableBytes;
        if (android.os.Build.VERSION.SDK_INT < 18) {
            availableBytes = stat.getAvailableBlocks() * stat.getBlockSize();
        } else {
            availableBytes = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        }
        Log.d(TAG, "BlockSize: " + stat.getBlockSize() + " , Blocks: " + stat.getAvailableBlocks() + " , Bytes: " + availableBytes + " , Bitrate: " + mBitRate + " , Seconds: " + availableBytes * 8 / mBitRate);
        return availableBytes * 8 / mBitRate;

    }


    private void updateChronometerRemain(long seconds) {
        int hours, minutes;
        hours = (int)((double)seconds/(60*60));

        seconds = seconds - hours * 60*60;
        minutes = (int)((double)seconds/60);

        seconds = seconds - minutes * 60;

        mChronometerRemain.setText("" + (hours==0 ? "" : hours + ":") + (minutes<10 ? "0" + minutes : minutes) + ":" + (seconds<10 ? "0" + seconds : seconds));

    }

}

