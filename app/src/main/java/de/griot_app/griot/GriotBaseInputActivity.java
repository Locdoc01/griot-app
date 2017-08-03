package de.griot_app.griot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * BaseActivity for griot-app-activities which are involved in input dialogs.
 * Provides base functionality for those activities like title bar and navigation bar.
 */
public class GriotBaseInputActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_griot_base_input);
    }
}
