package de.griot_app.griot;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
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

    //TODO: löschen
    //private static final String TAG = GriotBaseActivity.class.getSimpleName();

    protected Toolbar mAppBar;

    protected TextView mTitle;

    protected ImageView mButtonHome;
    protected ImageView mButtonProfile;
    protected ImageView mButtonRecord;
    protected ImageView mButtonQuestionmail;
    protected ImageView mButtonNotifications;
    protected ImageView mButtonTopicCatalog;

    /**
     * Abstract method, which returns the appropriate layout id for extending subclass.
     * This method can be used in onCreate() to inflate the appropriate layout for the extending subclass
     * @return  layout id for extending subclass
     */
    protected abstract int getSubClassLayoutId();

    /**
     * Abstract method, which returns the TAG of the extending subclass.
     * This method can be used, when the TAG of the concrete subclass is needed.
     * Note, that GriotBaseActivity itself doesn't provide a TAG field.
     * @return  TAG of the extending subclass
     */
    protected abstract String getSubClassTAG();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getSubClassLayoutId());

        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorGriotWhite));

        //set up the Toolbar as app bar
        mAppBar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mAppBar);

        //hides the title, since it's to complicated to center it. Instead a seperate TextView is used for showing the title in center-position
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
        // stops default animation, in case of changing the Activity
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
                // if button_home is pressed, current Activity will be closed, except it's the MainOverview itself
                if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                    finish();
                }
                break;
            case R.id.button_profile:
                // if button is pressed, apropriate Activity will be started, except it isn't already the current one
                // if current Activity wasn't MainOverview, it will be closed, otherwise it will be hold on the backstack
                if (!getSubClassTAG().equals(MainProfileOverviewActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainProfileOverviewActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
            case R.id.button_record:
                // if button_record is pressed, apropriate Activity will be started
                // if current Activity wasn't MainOverview, it will be closed, otherwise it will be hold on the backstack
                startActivity(new Intent(this, MainChooseFriendInputActivity.class));
                if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                    finish();
                }
                break;
            case R.id.button_questionmail:
                // same behavior as button_profile
                if (!getSubClassTAG().equals(MainQuestionmailActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainQuestionmailActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
            case R.id.button_notifications:
                // same behavior as button_profile
                if (!getSubClassTAG().equals(MainNotificationsActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainNotificationsActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
            case R.id.button_topic_catalog:
                // same behavior as button_profile
                if (!getSubClassTAG().equals(MainTopicCatalogActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainTopicCatalogActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
        }
    }

}


