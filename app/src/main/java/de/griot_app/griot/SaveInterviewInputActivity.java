package de.griot_app.griot;

import android.os.Bundle;
import android.util.Log;

import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.R;

public class SaveInterviewInputActivity extends GriotBaseInputActivity {

    private static final String TAG = SaveInterviewInputActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_save_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_save_interview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }


    @Override
    protected void buttonLeftPressed() {
        Log.d(TAG, "buttonLeftPressed: ");
    }

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");
    }

}