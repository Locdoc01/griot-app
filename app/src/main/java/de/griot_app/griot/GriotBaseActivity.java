package de.griot_app.griot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 *  BaseActivity for usual griot-app-activities. Provides base functionality like main menu, title bar and main button bar.
 */
public class GriotBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_griot_base);
    }
}
