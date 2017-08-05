package de.griot_app.griot;

import android.os.Bundle;

public class MainQuestionmailActivity extends GriotBaseActivity {

    private static final String TAG = MainQuestionmailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_questionmail);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_questionmail;
    }
}
