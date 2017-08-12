package de.griot_app.griot;

import android.os.Bundle;
import android.util.Log;

import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.R;

public class ReviewInterviewInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ReviewInterviewInputActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_review_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_review_interview;
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