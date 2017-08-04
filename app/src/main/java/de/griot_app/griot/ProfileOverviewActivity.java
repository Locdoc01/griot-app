package de.griot_app.griot;

import android.os.Bundle;

public class ProfileOverviewActivity extends GriotBaseActivity {

    private static final String TAG = ProfileOverviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_your_profile);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_profile_overview;
    }
}