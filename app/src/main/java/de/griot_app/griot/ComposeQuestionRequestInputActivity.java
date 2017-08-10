package de.griot_app.griot;

import android.os.Bundle;
import android.util.Log;

public class ComposeQuestionRequestInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ComposeQuestionRequestInputActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_compose_question_request);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_compose_request;
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
