package de.griot_app.griot;

import android.os.Bundle;

public class MainNotificationsActivity extends GriotBaseActivity {

    private static final String TAG = MainNotificationsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_notifications);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_notifications;
    }
}
