package de.griot_app.griot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

/**
 * BaseActivity for griot-app-activities which are involved in input dialogs.
 * Provides base functionality for those activities like title bar and navigation bar.
 */
public abstract class GriotBaseInputActivity extends AppCompatActivity {

    private static final String TAG = GriotBaseInputActivity.class.getSimpleName();

    protected TextView mTitle;

    protected Toolbar mAppbar;

    /**
     * Abstract method, that returns the appropriate layout id for extending subclass.
     * This method can be used in onCreate() to inflate the appropriate layout for the extending subclass
     * @return  layout id for extending subclass
     */
    protected abstract int getSubClassLayoutId();

    protected abstract String getSubClassTAG();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getSubClassLayoutId());

        mAppbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mAppbar);
        getSupportActionBar().setTitle("");

        mTitle = (TextView) findViewById(R.id.title);
    }
}
