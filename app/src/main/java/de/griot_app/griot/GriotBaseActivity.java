package de.griot_app.griot;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

/**
 *  Abstract base activity for usual griot-app-activities.
 *  Provides base functionality like main menu, title bar and main button bar.
 */
public abstract class GriotBaseActivity extends AppCompatActivity {

    private static final String TAG = GriotBaseActivity.class.getSimpleName();

    private Toolbar mToolbar;

    /**
     * Abstract method, that returns the appropriate layout id for extending subclass.
     * This method can be used in onCreate() to inflate the appropriate layout for the extending subclass
     * @return  layout id for extending subclass
     */
    protected abstract int getSubClassLayoutId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getSubClassLayoutId());

        mToolbar = (Toolbar) findViewById(R.id.app_bar_griot_base);
        setSupportActionBar(mToolbar);
    }

}
