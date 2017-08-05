package de.griot_app.griot;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 *  Abstract base activity for usual griot-app-activities.
 *  Provides base functionality like main menu, title bar and main button bar.
 */
public abstract class GriotBaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = GriotBaseActivity.class.getSimpleName();

    protected TextView mTitle;

    protected Toolbar mAppBar;

    protected ImageView mButtonHome;
    protected ImageView mButtonProfile;
    protected ImageView mButtonRecord;
    protected ImageView mButtonQuestionmail;
    protected ImageView mButtonNotifications;
    protected ImageView mButtonTopicCatalog;

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

        mAppBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mAppBar);
        getSupportActionBar().setTitle("");

        mTitle = (TextView) findViewById(R.id.title);

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
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    /**
     * TODO: Beschreibung ändern:
     * Funktionsweise der Button in Diagramm beschreiben
     * Bei App-Start wird MainOverview geöffnet
     * In MainOverView:
     *      Druck auf Home: Keine Änderung
     *      Druck auf andere Taste: öffnet neue Activity
     *      Druck auf Zurück: Beendet Programm
     * In RecordActivity:
     *      Druck auf Zurück: Beendet aktuelle Activity
     * In anderer Activity:
     *      Druck auf eigenen Button: Keine Änderung
     *      Druck auf andere Taste: öffnet andere Activity und schließt aktuelle
     *      Druck auf Home: Beendet aktuelle Activity
     *      Druck auf Zurück: Beendet aktuelle Activity
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_home:
                if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                    finish();
                }
                break;
            case R.id.button_profile:
                if (!getSubClassTAG().equals(MainProfileOverviewActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainProfileOverviewActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
            case R.id.button_record:
                startActivity(new Intent(this, MainChooseFriendInputActivity.class));
                if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                    finish();
                }
                break;
            case R.id.button_questionmail:
                if (!getSubClassTAG().equals(MainQuestionmailActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainQuestionmailActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
            case R.id.button_notifications:
                if (!getSubClassTAG().equals(MainNotificationsActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainNotificationsActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
            case R.id.button_topic_catalog:
                if (!getSubClassTAG().equals(MainTopicCatalogActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainTopicCatalogActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
            default:
        }
    }

}


