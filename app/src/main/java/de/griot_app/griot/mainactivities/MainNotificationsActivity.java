package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;

import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.startactivities.LoginActivity;

public class MainNotificationsActivity extends GriotBaseActivity {

    private static final String TAG = MainNotificationsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_notifications);
        mButtonNotifications.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        //TODO: löschen
        findViewById(R.id.signout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(MainNotificationsActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_notifications;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }
}
