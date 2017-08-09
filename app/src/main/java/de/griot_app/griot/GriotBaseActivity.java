package de.griot_app.griot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 *  Abstract base activity for usual griot-app-activities.
 *  Provides the following base functionality: title bar, bottom button bar, floating action button,
 *  Firebase-Authentification, Firebase-DatabaseReferences and Firebase-StorageReferences.
 *  mValueEventListener and mChildEventListener have to be instantiated in subclasses, if needed
 *  TODO MainMenu
 */
public abstract class GriotBaseActivity extends FirebaseActivity implements View.OnClickListener {

    //TODO: löschen
    //private static final String TAG = GriotBaseActivity.class.getSimpleName();

    protected Toolbar mAppBar;
    protected TextView mTitle;

    protected ImageView mButtonHome;
    protected ImageView mButtonProfile;
    protected ImageView mButtonRecord;
    protected ImageView mButtonNotifications;
    protected ImageView mButtonTopicCatalog;
    protected FloatingActionButton mButtonQuestionmail;

    /**
     * Abstract method, which returns the appropriate layout id for extending subclass.
     * This method can be used in onCreate() to inflate the appropriate layout for the extending subclass
     * @return  layout id for extending subclass
     */
    protected abstract int getSubClassLayoutId();


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
        mButtonNotifications = (ImageView) findViewById(R.id.button_notifications);
        mButtonTopicCatalog = (ImageView) findViewById(R.id.button_topic_catalog);
        mButtonQuestionmail = (FloatingActionButton) findViewById(R.id.fab_questionmail);
        mButtonQuestionmail.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotWhite, null));

        mButtonHome.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
        mButtonRecord.setOnClickListener(this);
        mButtonNotifications.setOnClickListener(this);
        mButtonTopicCatalog.setOnClickListener(this);
        mButtonQuestionmail.setOnClickListener(this);

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
            case R.id.fab_questionmail:
                // same behavior as button_profile
                if (!getSubClassTAG().equals(MainQuestionmailActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainQuestionmailActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                }
                break;
        }
    }
}

