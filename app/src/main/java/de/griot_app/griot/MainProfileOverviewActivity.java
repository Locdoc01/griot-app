package de.griot_app.griot;

import android.os.Bundle;

public class MainProfileOverviewActivity extends GriotBaseActivity {

    private static final String TAG = MainProfileOverviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_profile_overview);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_profile_overview;
    }
}