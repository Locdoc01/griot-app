package de.griot_app.griot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;

/**
 *  Abstract base activity for usual griot-app-activities.
 *  Provides base functionality like main menu, title bar and main button bar.
 */
public abstract class GriotBaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = GriotBaseActivity.class.getSimpleName();

    private Toolbar mAppBar;

    private ImageView mButtonHome;
    private ImageView mButtonProfile;
    private ImageView mButtonRecord;
    private ImageView mButtonQuestionmail;
    private ImageView mButtonNotifications;
    private ImageView mButtonTopicCatalog;

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

        mAppBar = (Toolbar) findViewById(R.id.app_bar_griot_base);
        setSupportActionBar(mAppBar);

        mButtonHome = (ImageView) findViewById(R.id.button_home);
        mButtonProfile = (ImageView) findViewById(R.id.button_profile);
        mButtonRecord = (ImageView) findViewById(R.id.button_record);
        mButtonQuestionmail = (ImageView) findViewById(R.id.button_questionmail);
        mButtonNotifications = (ImageView) findViewById(R.id.button_notifications);
        mButtonTopicCatalog = (ImageView) findViewById(R.id.button_topic_catalog);

        mButtonHome.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
        mButtonRecord.setOnClickListener(this);
        mButtonQuestionmail.setOnClickListener(this);
        mButtonNotifications.setOnClickListener(this);
        mButtonTopicCatalog.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.button_profile:
                startActivity(new Intent(this, ProfileOverviewActivity.class));
                break;
            case R.id.button_record:
                startActivity(new Intent(this, ChooseFriendInputActivity.class));
                break;
            case R.id.button_questionmail:
                startActivity(new Intent(this, QuestionMailActivity.class));
                break;
            case R.id.button_notifications:
                startActivity(new Intent(this, NotificationsActivity.class));
                break;
            case R.id.button_topic_catalog:
                startActivity(new Intent(this, TopicCatalogActivity.class));
                break;
            default:
                startActivity(new Intent(this, MainActivity.class));

        }
    }
}
