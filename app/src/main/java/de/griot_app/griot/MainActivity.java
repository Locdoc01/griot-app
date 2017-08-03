package de.griot_app.griot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends GriotBaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main;
    }
}
