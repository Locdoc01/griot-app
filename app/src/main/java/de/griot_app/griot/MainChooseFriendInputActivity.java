package de.griot_app.griot;

import android.os.Bundle;

public class MainChooseFriendInputActivity extends GriotBaseInputActivity {

    private static final String TAG = GriotBaseInputActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_record_interview);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_input_choose_friend;
    }
}
