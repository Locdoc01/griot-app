package de.griot_app.griot.mainactivities;

import android.os.Bundle;

import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;

public class MainQuestionmailActivity extends GriotBaseActivity {

    private static final String TAG = MainQuestionmailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_questionmail);
        mButtonQuestionmail.setImageResource(R.drawable.plus);

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_questionmail;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}
}
