package de.griot_app.griot.startactivities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import de.griot_app.griot.dataclasses.LocalTopicData;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.baseactivities.FirebaseActivity;
import de.griot_app.griot.dataclasses.UserData;
import de.griot_app.griot.mainactivities.MainOverviewActivity;

/**
 * This activity allows the user to create a new account for Griot-app at Firebase or sign in to an existing account. It holds both functionalities under two different Tabs.
 * On create-account-tab the user can choose an image from the device gallery and crop it if desired.
 * A DatePickerDialog, which shows up by clicking on the calendar icon. allows to choose the date of birth.
 * By clicking on the create-account-button or sign-in-button the input data gets validated before sending to database.
 * If some of the input data is invalid, no data will be sent and error messages will show up at the specific invalid fields.
 * If creating account or signing in was successfull, the user will get to main overview activity otherwise he stays at LoginActivity to try again.
 */
public class LoginActivity extends FirebaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnTouchListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    //private static final int REQUEST_GALLERY = 888;

    private ProgressBar mProgressBar;
    private TextView mTextViewProgress;
    private TabHost mTabHost;

    //Views from create account tab
    private ProfileImageView mProfileImage;
    private Uri mUriLocalProfileImage;
    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditCreateAccountEmail;
    private EditText mEditCreateAccountPassword;
    private EditText mEditCreateAccountPassword2;
    private ImageView mButtonDatePicker;
    private Calendar mCalendar;
    private DatePickerDialog mDatePickerDialog;
    private TextView mTextViewDate;
    private Button mButtonCreateAccount;

    //Views from signin tab
    private EditText mEditSignInEmail;
    private EditText mEditSignInPassword;
    private TextView mButtonForgotten;
    private Button mButtonSignIn;

    //Data class object for user information
    private UserData mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialization of generell views
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextViewProgress = (TextView) findViewById(R.id.textView_progress);
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        setupTabHost();

        //Get references to layout objects for create account tab
        mProfileImage = (ProfileImageView) findViewById(R.id.piv_profile_image);
        mEditFirstname = (EditText) findViewById(R.id.editText_firstname);
        mEditLastname = (EditText) findViewById(R.id.editText_lastname);
        mEditCreateAccountEmail = (EditText) findViewById(R.id.editText_create_account_email);
        mEditCreateAccountPassword = (EditText) findViewById(R.id.editText_create_account_password);
        mEditCreateAccountPassword2 = (EditText) findViewById(R.id.editText_create_account_password2);
        mButtonDatePicker = (ImageView) findViewById(R.id.button_datepicker);
        mButtonCreateAccount = (Button) findViewById(R.id.button_create_account);

        //Some initializations
        mProfileImage.setBlue();
        mUriLocalProfileImage = null;

        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(LoginActivity.this, LoginActivity.this, year, month, day);
        mTextViewDate = (TextView) findViewById(R.id.textView_date);

        //Get references to layout objects for sign in tab
        mEditSignInEmail = (EditText) findViewById(R.id.editText_signin_email);
        mEditSignInPassword = (EditText) findViewById(R.id.editText_signin_password);
        mButtonForgotten = (TextView) findViewById(R.id.button_forgotten);
        mButtonSignIn = (Button) findViewById(R.id.button_signin);

        mEditFirstname.setOnClickListener(this);
        mEditLastname.setOnClickListener(this);
        mEditCreateAccountEmail.setOnClickListener(this);
        mEditCreateAccountPassword.setOnClickListener(this);
        mEditCreateAccountPassword2.setOnClickListener(this);
        mEditSignInEmail.setOnClickListener(this);
        mEditSignInPassword.setOnClickListener(this);

        /*
        // In case, that DatePicker should be opened automatically per Click on Next in EditLastName
        mEditLastname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mDatePickerDialog.show();
                    return true;
                }
                return false;
            }
        });
        */

        mProfileImage.setOnTouchListener(this);
        mButtonDatePicker.setOnTouchListener(this);
        mButtonCreateAccount.setOnTouchListener(this);
        mButtonForgotten.setOnTouchListener(this);
        mButtonSignIn.setOnTouchListener(this);

        mUserData = new UserData();
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}

    //Sets the date to TextView and Calendar, when a date was set by mDatePickerDialog.
    @Override
    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
        Log.d(TAG, "onDateSet: year: " + year + " , month: " + month + " , day: " + day);
        //set the chosen date to mDatePickerDialog, so that it will be set to this date on next call
        mDatePickerDialog.getDatePicker().updateDate(year, month, day);
        mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
        mEditCreateAccountEmail.requestFocus();
        mTextViewDate.setError(null);
        mCalendar.set(year,month,day,0,0,0);
    }

    //Hide possible error messages from input views by click in one of them
    @Override
    public void onClick(View v) {
        mEditFirstname.setError(null);
        mEditLastname.setError(null);
        mTextViewDate.setError(null);
        mEditCreateAccountEmail.setError(null);
        mEditCreateAccountPassword.setError(null);
        mEditCreateAccountPassword2.setError(null);

        mEditSignInEmail.setError(null);
        mEditSignInPassword.setError(null);
    }


    //Set OnTouchListener for button views
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.piv_profile_image:
                        //TODO: Farbwechsel für quatratischen Rand um ProfileImageView realisieren
                        //v.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorTabSelectedLogin));
                        return true;
                    case R.id.button_datepicker:
                        ((ImageView)v).setColorFilter(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotWhite));
                        return true;
                    case R.id.button_create_account:
                    case R.id.button_forgotten:
                    case R.id.button_signin:
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotWhite));
                        return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.piv_profile_image:
                        Log.d(TAG, "profile image clicked: ");
                        //v.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotBlue));

                        //choosing an image without cropping
                        /*
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_GALLERY);
                        */
                        //choosing an image from CropImageActivity, where it can be cropped
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(LoginActivity.this);
                        return true;
                    case R.id.button_datepicker:
                        Log.d(TAG, "datepicker clicked: ");
                        ((ImageView)v).setColorFilter(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        mDatePickerDialog.show();
                        return true;
                    case R.id.button_create_account:
                        Log.d(TAG, "create account clicked: ");
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        attemptCreateAccount();
                        return true;
                    case R.id.button_forgotten:
                        Log.d(TAG, "password forgotten clicked: ");
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        //TODO implementieren
                        return true;
                    case R.id.button_signin:
                        Log.d(TAG, "sign in clicked: ");
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        attemptSignIn();
                        return true;
                }
                return false;
        }
        return false;
    }

    //Called after an image was chosen on an external Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //after choosing an image without cropping
        /*
        if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    mLocalUriProfilePhoto = data.getData();
                    mImageProfile.setImageURI(mLocalUriProfilePhoto);
                }
            }
        }
        */
        //after choosing an image from CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mUriLocalProfileImage = result.getUri();
                mProfileImage.getProfileImage().setImageURI(Uri.parse(mUriLocalProfileImage.getPath()));
                mProfileImage.getProfileImage().setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        hideProgressBar();
        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Shows the progress bar along with a given progress message and hides everything else
     * @param message Message, that shows up along with the progress bar
     */
    private void showProgressBar(String message) {
        Log.d(TAG, "showProgressBar: message: " + message);
        mTextViewProgress.setText(message);
        mTextViewProgress.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTabHost.setVisibility(View.INVISIBLE);
    }

    /**
     * Hides the progressbar and shows everything else
     */
    private void hideProgressBar() {
        Log.d(TAG, "hideProgressBar: ");
        mTextViewProgress.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTabHost.setVisibility(View.VISIBLE);
    }

    //Set up the TabHost with custom look and functionality
    private void setupTabHost() {
        Log.d(TAG, "setupTabHost: ");

        mTabHost.setup();
        TabHost.TabSpec tabSpec;
        final TabWidget tabWidget = mTabHost.getTabWidget();
        ViewGroup.LayoutParams params;

        //Set height of tabwidget by LayoutParams
        params = tabWidget.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.dimen_height_tabwidget);
        tabWidget.setLayoutParams(params);

        //Add tab for create account
        tabSpec = mTabHost.newTabSpec(getResources().getString(R.string.button_create_account));
        tabSpec.setContent(R.id.tab_create_account);
        tabSpec.setIndicator(getResources().getString(R.string.button_create_account));
        mTabHost.addTab(tabSpec);

        //Add tab for sign in
        tabSpec = mTabHost.newTabSpec(getResources().getString(R.string.button_signin));
        tabSpec.setContent(R.id.tab_signin);
        tabSpec.setIndicator(getResources().getString(R.string.button_signin));
        mTabHost.addTab(tabSpec);

        //Set attributes for tab titles like lowercase and non-bold letters
        for (int i = 0; i < 2; i++) {
            TextView tabtext = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tabtext.setAllCaps(false);
            tabtext.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
            tabtext.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_textSize_normal));
            tabtext.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }

        //Set initial background colors for tabs
        tabWidget.getChildAt(0).setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotBlue));
        tabWidget.getChildAt(1).setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorTabSelectedLogin));

        //A click on a tab changes the tabs colors. Since the OnClickListener overrides the original click-functionality of the tabs, the current tab has to be set again
        tabWidget.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabWidget.getChildAt(0).setBackgroundColor(Color.TRANSPARENT);
                tabWidget.getChildAt(1).setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorTabSelectedLogin));
                mTabHost.setCurrentTab(0);
            }
        });

        tabWidget.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabWidget.getChildAt(0).setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorTabSelectedLogin));
                tabWidget.getChildAt(1).setBackgroundColor(Color.TRANSPARENT);
                mTabHost.setCurrentTab(1);
            }
        });

    }

    //TODO: check functionality
    /**
     * validates the input data for create-account-tab. Checks for the following:
     * - are all fields filled? (except profile image, which can be empty)
     * - has email-Adress a typical valid form?
     * - do both passwords match?
     * - is password at least 6 chars long?
     * - has password at least numbers and letters?
     * If one or more fields have invalid data, error messages are shown at the appropriate fields.
     * In case of an error the invalid fields as well as both password fields will be cleared.
     */
    private boolean validateFormCreateAccount() {
        Log.d(TAG, "validateFormCreateAccount: ");

        boolean valid = true;   // set to false, if one of the inputfields is not valid
        View focus = null;      // set to the first invalid inputfield, if there is any


        //If surname field is empty
        if (TextUtils.isEmpty(mEditFirstname.getText().toString().trim())) {
            mEditFirstname.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            focus = mEditFirstname;
        } else {
            mEditFirstname.setError(null);
        }

        //If lastname field is empty
        if (TextUtils.isEmpty(mEditLastname.getText().toString().trim())) {
            mEditLastname.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditLastname;
            }
        } else {
            mEditLastname.setError(null);
        }

        //If birthday is not set
        if (mTextViewDate.getText().toString().equals(R.string.date_empty)) {
            mTextViewDate.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mButtonDatePicker;
            }
        } else {
            mTextViewDate.setError(null);
        }

        //If email field is empty or has invalid form
        if (TextUtils.isEmpty(mEditCreateAccountEmail.getText().toString().trim())) {
            mEditCreateAccountEmail.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountEmail;
            }
        } else if (!mEditCreateAccountEmail.getText().toString().trim().matches("\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}@\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}\\.[a-z]{2,}")) {
            mEditCreateAccountEmail.setError(getResources().getString(R.string.error_invalid_email));
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountEmail;
            }
        } else {
            mEditCreateAccountEmail.setError(null);
        }

        //If first password field is empty
        if (TextUtils.isEmpty(mEditCreateAccountPassword.getText().toString())) {
            mEditCreateAccountPassword.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountPassword;
            }
        } else {
            mEditCreateAccountPassword.setError(null);
        }

        //If second password field is empty
        if (TextUtils.isEmpty(mEditCreateAccountPassword2.getText().toString())) {
            mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountPassword2;
            }
        } else {
            mEditCreateAccountPassword2.setError(null);
        }

        //If one of the input fields were empty so far
        if (!valid) {
            //Set focus to first empty field
            focus.requestFocus();
            //If first password field was empty, clear second one
            if (TextUtils.isEmpty(mEditCreateAccountPassword.getText().toString())) {
                mEditCreateAccountPassword2.setText("");
            }
            return valid;
        }

        //If password fields are not equal
        if (!mEditCreateAccountPassword.getText().toString().equals(mEditCreateAccountPassword2.getText().toString())) {
            mEditCreateAccountPassword.setError(getResources().getString(R.string.error_passwords_dont_match));
            mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_passwords_dont_match));
            valid = false;
        } else {
            //If password is smaller than 6 chars
            if (mEditCreateAccountPassword.getText().toString().length() < 6) {
                mEditCreateAccountPassword.setError(getResources().getString(R.string.error_password_must_6_chars));
                mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_password_must_6_chars));
                valid = false;
                //If password contains only numbers or only letters
                //TODO: check, if password contain only special characters
            } else if (mEditCreateAccountPassword.getText().toString().matches("\\p{Alpha}*") || mEditCreateAccountPassword.getText().toString().matches("\\p{Digit}*")) {
                mEditCreateAccountPassword.setError(getResources().getString(R.string.error_password_must_numbers_digits));
                mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_password_must_numbers_digits));
                valid = false;
            } else {
                mEditCreateAccountPassword.setError(null);
                mEditCreateAccountPassword2.setError(null);
            }
        }
        //If password was not valid so far, clear password fields and set focus to first password field
        if (!valid) {
            mEditCreateAccountPassword.setText("");
            mEditCreateAccountPassword2.setText("");
            mEditCreateAccountPassword.requestFocus();
        }
        return valid;
    }

    //TODO: check functionality
    /**
     * validates the input data for sign-in-tab. Checks for the following:
     * - are both fields filled?
     * - has email adress a typical valid form?
     * If one or both fields have invalid data, error messages are shown at the appropriate fields.
     * In case of an error the password field will be cleared.
     */
    private boolean validateFormSignIn() {
        Log.d(TAG, "validateFormSignIn: ");

        boolean valid = true;
        View focus = null;
        if (TextUtils.isEmpty(mEditSignInEmail.getText().toString().trim())) {
            mEditSignInEmail.setError(getResources().getString(R.string.error_required_field));
            focus = mEditSignInEmail;
            valid = false;
        } else if (!mEditSignInEmail.getText().toString().trim().matches("\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}@\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}\\.[a-z]{2,}")) {
            mEditSignInEmail.setError(getResources().getString(R.string.error_invalid_email));
            focus = mEditSignInEmail;
            valid = false;
        } else {
            mEditSignInEmail.setError(null);
        }

        if (TextUtils.isEmpty(mEditSignInPassword.getText().toString())) {
            mEditSignInPassword.setError(getResources().getString(R.string.error_required_field));
            mEditSignInPassword.requestFocus();
            if (focus == null) {
                focus = mEditSignInPassword;
            }
            valid = false;
        } else {
            mEditSignInPassword.setError(null);
        }
        if (!valid) {
            mEditSignInPassword.setText("");
            focus.requestFocus();
        }
        return valid;
    }


    /**
     * Tries to create a new account for Griot-app at Firebase based on the input data, if the data is valid.
     */
    private void attemptCreateAccount() {
        Log.d(TAG, "attemptCreateAccount: " + mEditCreateAccountEmail.getText().toString().trim());

        if (!validateFormCreateAccount()) {
            return;
        }

        showProgressBar(getString(R.string.progress_creating_account));

        String email = mEditCreateAccountEmail.getText().toString().trim();
        String password = mEditCreateAccountPassword.getText().toString();
        //Actual call for creating the account. onComplete() of the attached OnCompleteListener is called, when the task is done
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmailAndPassword: onComplete: " + task.isSuccessful());

                //If the account creation failed
                if (!task.isSuccessful()) {
                    hideProgressBar();
                    //TODO: Progressbar-Steuerung in AsynkTask verlagern
                    Log.w(TAG, "createUserWithEmailAndPassword: failed ", task.getException());
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    mEditCreateAccountPassword.setText("");
                    mEditCreateAccountPassword2.setText("");
                    mEditCreateAccountPassword.requestFocus();
                    // TODO: In case, that the email already existed, empty email field
                    // TODO: determine possible error and system messages and add german string resources for them to the project
                } else {
                    //If account was successfully created
                    //Obtain user Id from Firebase
                    mUser = task.getResult().getUser();
                    mUserID = mUser.getUid();
                    //Set database reference to /users/mUserID
                    mDatabaseRef = mDatabaseRootReference.child("users").child(mUserID);

                    //Store user details from input form in data class object
                    mUserData.setFirstname(mEditFirstname.getText().toString().trim());
                    mUserData.setLastname(mEditLastname.getText().toString().trim());
                    mUserData.setBirthday(mCalendar.getTime().toString());
                    mUserData.setBYear(mCalendar.get(Calendar.YEAR));
                    mUserData.setBMonth(mCalendar.get(Calendar.MONTH));
                    mUserData.setBDay(mCalendar.get(Calendar.DAY_OF_MONTH));
                    mUserData.setEmail(mEditCreateAccountEmail.getText().toString().trim());

                    //Obtain standard topics from Firebase database
                    mDatabaseRootReference.child("standardTopics").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<Boolean> standardTopics = new ArrayList<>();
                            for ( int i=0 ; i<dataSnapshot.getChildrenCount() ; i++) {
                                standardTopics.add(true);
                            }
                            mUserData.setStandardTopics(standardTopics);

                            //Obtain standard questions from Firebase database
                            mDatabaseRootReference.child("standardQuestions").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    HashMap<String, Integer> standardQuestions = new HashMap<>();
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        standardQuestions.put(ds.getKey(), 0);
                                    }
                                    mUserData.setStandardQuestions(standardQuestions);

                                    //Set storage reference to /users/mUserID/profilePicture
                                    mStorageRef = mStorageRootReference.child("users").child(mUserID).child("profilePicture.jpg");

                                    //If a profile image was chosen, it will be uploaded to Firebase Storage
                                    if (mUriLocalProfileImage != null) {
                                        //Upload file with local URI stored in mUriLocalProfileImage
                                        mStorageRef.putFile(mUriLocalProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                //On success the remote downloadURL will be stored to mLocalUserData.pictureURL
                                                //TODO: Alternative finden
                                                mUserData.setPictureURL(taskSnapshot.getDownloadUrl().toString());
                                                //Send data to database (must be here, after profile picture was send to Firebase Storage, otherwise pictureURL will be empty in database)
                                                mDatabaseRef.setValue(mUserData);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //On failure mLocalUserData.pictureURL will remain empty
                                                Toast.makeText(LoginActivity.this, "Profile Image Error", Toast.LENGTH_SHORT).show();
                                                Log.e(getSubClassTAG(), "Error uploading profile image");
                                                mDatabaseRef.setValue(mUserData);
                                            }
                                        });
                                    } else {
                                        mStorageRootReference.child("users").child("profilePicture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                mUserData.setPictureURL(uri.toString());
                                                //If no profile image was chosen, mLocalUserData.pictureURL will be set to a downloadUrl of a standard avatar picture
                                                // located in Storage folder "users"
                                                mDatabaseRef.setValue(mUserData);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                //On failure mLocalUserData.pictureURL will remain empty.
                                                Log.e(getSubClassTAG(), "Error obtaining avatar image uri");
                                                mDatabaseRef.setValue(mUserData);
                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.e(getSubClassTAG(), "Error downloading standard questions");
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.e(getSubClassTAG(), "Error downloading standard topics");
                        }
                    });

                    //Start MainOverviewAvtivity
                    Intent intent = new Intent(LoginActivity.this, MainOverviewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



    /**
     * Tries to sign in to an existing account of Griot-app at Firebase based on the input data, if the data is valid.
     */
    private void attemptSignIn() {
        Log.d(TAG, "attemptSignIn: " + mEditSignInEmail.getText().toString().trim());

        if (!validateFormSignIn()) {
            return;
        }
        showProgressBar(getString(R.string.progress_signing_in));

        String email = mEditSignInEmail.getText().toString().trim();
        String password = mEditSignInPassword.getText().toString();
        //Actual call for signing in. onComplete() of the attached OnCompleteListener is called, when the task is done
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmailAndPasswort: onComplete: " + task.isSuccessful());

                //If signing in failed
                if (!task.isSuccessful()) {
                    hideProgressBar();
                    //Empty both edittext fields
                    mEditSignInPassword.setText("");
                    mEditSignInPassword.requestFocus();
                    Log.w(TAG, "signInWithEmailAndPassword: failed ", task.getException());
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO: if email was unknown to Firebase, empty email field
                    // TODO: mögliche Fehlerfälle ermitteln und System-Meldungen in deutsche Meldungen umwandeln
                    // TODO: determine possible error and system messages and add german string resources for them to the project
                } else {
                    //If user is successfully signed in, start MainOverviewActivity
                    Intent intent = new Intent(LoginActivity.this, MainOverviewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


}
