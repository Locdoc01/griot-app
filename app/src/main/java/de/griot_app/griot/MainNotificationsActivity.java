package de.griot_app.griot;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;

public class MainNotificationsActivity extends GriotBaseActivity {

    private static final String TAG = MainNotificationsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_notifications);
        mButtonNotifications.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_notifications;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }
}
