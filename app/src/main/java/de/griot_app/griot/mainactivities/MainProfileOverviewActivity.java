package de.griot_app.griot.mainactivities;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;

import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;

public class MainProfileOverviewActivity extends GriotBaseActivity {

    private static final String TAG = MainProfileOverviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_profile_overview);
        mButtonProfile.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_profile_overview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }
}