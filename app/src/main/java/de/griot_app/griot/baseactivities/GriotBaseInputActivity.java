package de.griot_app.griot.baseactivities;

import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.griot_app.griot.R;

/**
 * Abstract base activity for activities of griot-app, which take user input.
 *
 * Provides the following base functionality:
 * Title bar, customizable navigation buttons at the bottom, a progress bar
 * and all functionalities from FirebaseActivity.
 */
public abstract class GriotBaseInputActivity extends FirebaseActivity implements View.OnTouchListener {

    protected Toolbar mAppbar;

    protected TextView mTitle;

    protected Button mButtonLeft;
    protected Button mButtonCenter;
    protected Button mButtonRight;

    private ImageView mBackgroundProgress;
    private ProgressBar mProgressBar;
    private TextView mTextViewProgress;

    /**
     * Abstract method, which returns the appropriate layout contactID for extending subclass.
     * This method can be used in onCreate() to inflate the appropriate layout for the extending subclass
     * @return  layout contactID for extending subclass
     */
    protected abstract int getSubClassLayoutId();

    /**
     * methods, which provide the customizable functionality for subclass-specific bottom buttons.
     * If a button is needed in subclass, implement the apropriate method with the needed code and set the text to the button in subclass
     * (by default it's set with empty text). Otherwise leave it as it is.
     * This methods are called from onTouch()-method of GriotBaseInputActivity.
     */
    protected void buttonLeftPressed() {}
    protected void buttonCenterPressed() {}
    protected void buttonRightPressed() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getSubClassLayoutId());

        getWindow().getDecorView().setBackgroundColor(ContextCompat.getColor(this, R.color.colorGriotWhite));

        //set up the Toolbar as app bar
        mAppbar = (Toolbar) findViewById(R.id.base_app_bar);
        setSupportActionBar(mAppbar);

        //hides the title, since centering it is not supported. Instead a seperate TextView is used for showing the title in center-position
        getSupportActionBar().setTitle("");

        // get references to the layout objects
        mTitle = (TextView) findViewById(R.id.base_title);
        mButtonLeft = (Button) findViewById(R.id.button_left);
        mButtonCenter = (Button) findViewById(R.id.button_center);
        mButtonRight = (Button) findViewById(R.id.button_right);
        mBackgroundProgress = (ImageView) findViewById(R.id.base_background_progress);
        mProgressBar = (ProgressBar) findViewById(R.id.base_progressBar);
        mTextViewProgress = (TextView) findViewById(R.id.base_textView_progress);

        mButtonLeft.setOnTouchListener(this);
        mButtonCenter.setOnTouchListener(this);
        mButtonRight.setOnTouchListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Shows the progress bar along with the given message
     * @param message
     */
    protected void showProgressBar(String message) {
        mButtonLeft.setVisibility(View.GONE);
        mButtonCenter.setVisibility(View.GONE);
        mButtonRight.setVisibility(View.GONE);

        mTextViewProgress.setText(message);

        mBackgroundProgress.setVisibility(View.VISIBLE);
        mTextViewProgress.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

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
        mButtonLeft.setVisibility(View.VISIBLE);
        mButtonCenter.setVisibility(View.VISIBLE);
        mButtonRight.setVisibility(View.VISIBLE);

        mBackgroundProgress.setVisibility(View.GONE);
        mTextViewProgress.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);
    }


    // OnTouchListener for customizable navigation buttons.
    // If a button is needed in subclass, implement the functionality in the appropriate method in subclass
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ((Button)v).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));
                return true;
            case MotionEvent.ACTION_UP:
                ((Button)v).setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                switch (v.getId()) {
                    case R.id.button_left:
                        buttonLeftPressed();
                        break;
                    case R.id.button_center:
                        buttonCenterPressed();
                        break;
                    case R.id.button_right:
                        buttonRightPressed();
                        break;
                }
                return true;
        }
        return false;
    }

}
