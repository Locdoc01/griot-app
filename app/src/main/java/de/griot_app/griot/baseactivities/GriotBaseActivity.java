package de.griot_app.griot.baseactivities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.griot_app.griot.ImageLoader;
import de.griot_app.griot.questionmail.ComposeQuestionRequestInputActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.mainactivities.MainChooseFriendInputActivity;
import de.griot_app.griot.mainactivities.MainNotificationsActivity;
import de.griot_app.griot.mainactivities.MainOverviewActivity;
import de.griot_app.griot.mainactivities.MainPersonalPageActivity;
import de.griot_app.griot.mainactivities.MainQuestionmailActivity;
import de.griot_app.griot.mainactivities.MainTopicCatalogActivity;

/**
 * Abstract base activity for usual activities of griot-app.
 *
 * Provides the following base functionality:
 * title bar, bottom button bar, floating action button, progress bar, main menu, options menues
 * and all functionalities from FirebaseActivity.
 */
 //TODO main menu & options menues
public abstract class GriotBaseActivity extends FirebaseActivity implements View.OnClickListener {

    protected ImageLoader mImageLoader;

    protected Toolbar mAppBar;
    protected TextView mTitle;
    protected ImageView mLineAppBar;

    protected ImageView mButtonHome;
    protected ImageView mButtonProfile;
    protected ImageView mButtonRecord;
    protected ImageView mButtonNotifications;
    protected ImageView mButtonTopicCatalog;
    protected FloatingActionButton mButtonQuestionmail;

/*
    protected ImageView mBackgroundOptionsMenu;
    protected boolean mOptionsMenuVisible;
    protected ListView mListViewOptionsMenu;
    protected ArrayList<OptionsMenuItem> mListOptions;
    protected OptionsMenuAdapter mOptionsMenuAdapter;
    protected ObjectAnimator mAnimatorBackgroundOptionsMenu;
    protected ObjectAnimator mAnimatorOptionsMenu;
    protected AnimatorSet mAnimatorSetOptionsMenu;
*/

    private ImageView mBackgroundProgress;
    private ProgressBar mProgressBar;
    private TextView mTextViewProgress;

    /**
     * Abstract method, which returns the appropriate layout contactID for extending subclass.
     * This method can be used in onCreate() to inflate the appropriate layout for the extending subclass
     * @return  layout contactID for extending subclass
     */
    protected abstract int getSubClassLayoutId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getSubClassLayoutId());

        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorGriotWhite));

        mImageLoader = new ImageLoader(this);

        //set up the Toolbar as app bar
        mAppBar = (Toolbar) findViewById(R.id.base_app_bar);
        setSupportActionBar(mAppBar);

        //hides the title, since centering it is not supported. Instead a seperate TextView is used for showing the title in center-position
        getSupportActionBar().setTitle("");

        // get references to the layout objects
        mTitle = (TextView) findViewById(R.id.base_title);
        mLineAppBar = (ImageView) findViewById(R.id.base_line_top);
        mButtonHome = (ImageView) findViewById(R.id.button_home);
        mButtonProfile = (ImageView) findViewById(R.id.button_profile);
        mButtonRecord = (ImageView) findViewById(R.id.button_record);
        mButtonNotifications = (ImageView) findViewById(R.id.button_notifications);
        mButtonTopicCatalog = (ImageView) findViewById(R.id.button_topic_catalog);
        mButtonQuestionmail = (FloatingActionButton) findViewById(R.id.fab_questionmail);
        mButtonQuestionmail.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotWhite, null));
//        mBackgroundOptionsMenu = (ImageView) findViewById(R.id.background_options_menu);
//        mListViewOptionsMenu = (ListView) findViewById(R.id.listView_options_menu);
        mBackgroundProgress = (ImageView) findViewById(R.id.base_background_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.base_progressBar);
        mTextViewProgress = (TextView) findViewById(R.id.base_textView_progress);

//        mOptionsMenuVisible = false;

//        mBackgroundOptionsMenu.setOnClickListener(this);

        mButtonHome.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
        mButtonRecord.setOnClickListener(this);
        mButtonNotifications.setOnClickListener(this);
        mButtonTopicCatalog.setOnClickListener(this);
        mButtonQuestionmail.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stops default animation, when Activity changes
        overridePendingTransition(0, 0);
    }

/*
    @Override
    public void onBackPressed() {
        if (mOptionsMenuVisible) {
            hideOptionsMenu();
        } else {
            super.onBackPressed();
        }
    }
*/

    /**
     * Shows the progress bar along with the given message
     * @param message
     */
    protected void showProgressBar(String message) {
        mButtonHome.setOnClickListener(null);
        mButtonProfile.setOnClickListener(null);
        mButtonRecord.setOnClickListener(null);
        mButtonNotifications.setOnClickListener(null);
        mButtonTopicCatalog.setOnClickListener(null);
        mButtonQuestionmail.setOnClickListener(null);

        mTextViewProgress.setText(message);

        mBackgroundProgress.setVisibility(View.VISIBLE);
        mTextViewProgress.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }
/*
    public void showOptionsMenu(ArrayList<OptionsMenuItem> listOptions) {
        Log.d(getSubClassTAG(), "showOptionsMenu: ");
        mListOptions = listOptions;
        mOptionsMenuAdapter = new OptionsMenuAdapter(GriotBaseActivity.this, mListOptions);
        mListViewOptionsMenu.setAdapter(mOptionsMenuAdapter);

        mListViewOptionsMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        // get height of activity without system bars height
        final int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();

        mListViewOptionsMenu.setY(height);
        mBackgroundOptionsMenu.setVisibility(View.VISIBLE);
        mListViewOptionsMenu.setVisibility(View.VISIBLE);

        //the following code will be done AFTER the ListView was drawn, so that its height will be available
        mListViewOptionsMenu.post(new Runnable() {
            @Override
            public void run() {
                mAnimatorBackgroundOptionsMenu = ObjectAnimator.ofFloat(mBackgroundOptionsMenu, "alpha", 0.0f, 0.86f);
                mAnimatorOptionsMenu = ObjectAnimator.ofFloat(mListViewOptionsMenu, "y", height, height - mListViewOptionsMenu.getHeight());
                mAnimatorSetOptionsMenu = new AnimatorSet();
                mAnimatorSetOptionsMenu.setDuration(500);
                mAnimatorSetOptionsMenu.play(mAnimatorBackgroundOptionsMenu).with(mAnimatorOptionsMenu);
                mAnimatorSetOptionsMenu.start();

                mOptionsMenuVisible = true;
            }
        });
    }

    public void hideOptionsMenu() {
        Log.d(getSubClassTAG(), "hideOptionsMenu: ");

        // get height of activity without system bars height
        int height = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();

        mAnimatorBackgroundOptionsMenu = ObjectAnimator.ofFloat(mBackgroundOptionsMenu, "alpha", 0.86f, 0.0f);
        mAnimatorOptionsMenu = ObjectAnimator.ofFloat(mListViewOptionsMenu, "y", height - mListViewOptionsMenu.getHeight(), height);
        mAnimatorSetOptionsMenu = new AnimatorSet();
        mAnimatorSetOptionsMenu.setDuration(500);
        mAnimatorSetOptionsMenu.play(mAnimatorBackgroundOptionsMenu).with(mAnimatorOptionsMenu);

        mAnimatorOptionsMenu.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mBackgroundOptionsMenu.setVisibility(View.GONE);
                mListViewOptionsMenu.setVisibility(View.GONE);
                mOptionsMenuVisible = false;
            }
        });

        mAnimatorSetOptionsMenu.start();
    }
*/

    /**
     * Hides the progress bar and shows the given finish-message
     * @param message
     */
    protected void showProgressBarFinishMessage(String message) {
        mProgressBar.setVisibility(View.GONE);
        mTextViewProgress.setText(message);
    }

    /**
     * Hides progress bar and progress message and shows usual layout
     */
    protected void hideProgressBar() {
        mButtonHome.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
        mButtonRecord.setOnClickListener(this);
        mButtonNotifications.setOnClickListener(this);
        mButtonTopicCatalog.setOnClickListener(this);
        mButtonQuestionmail.setOnClickListener(this);

        mBackgroundProgress.setVisibility(View.GONE);
        mTextViewProgress.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }


    /**
     * Functionality of the main button bar, floating action button and device back-button depending on the running activity
     * In MainOverView:
     *      Press on home-button: No change
     *      Press on Floating Action Button: opens ComposeQuestionRequestInputActivity
     *      Press on the device back-button: stopps the app
     *      Press on other button: opens the appropriate activity
     * In MainChooseFriendInputActivity (has no main-buttonbar):
     *      Press on the device back-button: finishes the currently running activity and returns to MainOverviewActivity
     * In QuestionMail:
     *      Press on home-button: finishes the currently running activity and returns to MainOverviewActivity
     *      Press on Floating Action Button: opens ComposeQuestionRequestInputActivity and finishes currently running activity
     *      Press on the device back-button: finihses the currently running activity and returns to MainOverviewActivity
     *      Press on other button: opens the appropriate activity and finishes the currently running one
     * In other activity:
     *      Press on appropriate button for that activity: No change
     *      Press on home-button: finishes the currently running activity and returns to MainOverviewActivity
     *      Press on the device back-button: finihses the currently running activity and returns to MainOverviewActivity
     *      Press on other button (including Floating Action Button): opens the appropriate activity and finishes the currently running activity
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
                if (!getSubClassTAG().equals(MainPersonalPageActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainPersonalPageActivity.class));
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
                // if FAB is pressed, Questionmail will be started, if it isn't already the current one
                // if current Activity wasn't MainOverview, it will be closed, otherwise it will be hold on the backstack
                // if FAB is pressed from QuestionMail, it will be closed and ComposeQuestionRequest will be started
                if (!getSubClassTAG().equals(MainQuestionmailActivity.class.getSimpleName())) {
                    startActivity(new Intent(this, MainQuestionmailActivity.class));
                    if (!getSubClassTAG().equals(MainOverviewActivity.class.getSimpleName())) {
                        finish();
                    }
                } else {
                    startActivity(new Intent(this, ComposeQuestionRequestInputActivity.class));
                    finish();
                }
                break;
/*
            case R.id.background_options_menu:
                hideOptionsMenu();
                break;
*/
        }
    }
}


