package de.griot_app.griot;

import android.os.Bundle;

public class NotificationsActivity extends GriotBaseActivity {

    private static final String TAG = NotificationsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_notifications);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_notifications;
    }
}
