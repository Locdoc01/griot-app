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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.R;
import de.griot_app.griot.baseactivities.FirebaseActivity;
import de.griot_app.griot.dataclasses.UserData;
import de.griot_app.griot.mainactivities.MainOverviewActivity;

/**
 * Activity for creating app accounts and signing in. It holds both functionalities under two different Tabs.
 * On create-account-tab the user can choose an image from phone gallery and crop it.
 * Date of birth can be chosen through a DatePickerDialog, which will be shown by clicking on the calendar icon.
 * EditText fields validate input data. If on or more of them are invalid, the date will not be send and error messages will be shown.
 * If creating account or signing in was successfull, the user will get to main overview activity.
 * Otherwise he will stay on login activity to try again.
 */
public class LoginActivity extends FirebaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnTouchListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int REQUEST_GALLERY = 888;

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

    //Data class object
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

        //Initialization of views for create account tab
        mProfileImage = (ProfileImageView) findViewById(R.id.piv_profile_image);
        mProfileImage.setBlue();
        mUriLocalProfileImage = null;

        mEditFirstname = (EditText) findViewById(R.id.editText_firstname);
        mEditLastname = (EditText) findViewById(R.id.editText_lastname);
        mEditCreateAccountEmail = (EditText) findViewById(R.id.editText_create_account_email);
        mEditCreateAccountPassword = (EditText) findViewById(R.id.editText_create_account_password);
        mEditCreateAccountPassword2 = (EditText) findViewById(R.id.editText_create_account_password2);

        mButtonDatePicker = (ImageView) findViewById(R.id.button_datepicker);
        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(LoginActivity.this, LoginActivity.this, year, month, day);
        mTextViewDate = (TextView) findViewById(R.id.textView_date);

        mButtonCreateAccount = (Button) findViewById(R.id.button_create_account);

        //Initialization of views for signin tab
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

    //called, when a date was set through mDatePickerDialog
    @Override
    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
        //set the chosen date to mDatePickerDialog, so that it will be set to this date on next call
        mDatePickerDialog.getDatePicker().updateDate(year, month, day);
        mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
        mEditCreateAccountEmail.requestFocus();
        mTextViewDate.setError(null);
        mCalendar.set(year,month,day,0,0,0);
    }

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



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.piv_profile_image:
                        //TODO: runden Farbwechsel realisieren (in ProfileImageView)
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
                        ((ImageView)v).setColorFilter(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        //TODO: keinen sichtbaren Effekt. Kann gelöscht werden
                        //mDatePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        mDatePickerDialog.show();
                        return true;
                    case R.id.button_create_account:
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        attemptCreateAccount();
                        return true;
                    case R.id.button_forgotten:
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        //TODO implementieren
                        return true;
                    case R.id.button_signin:
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        attemptSignIn();
                        return true;
                }
                return false;
        }
        return false;
    }

    //called after an image was chosen on an external Activity
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


    private void showProgressBar(String message) {
        mTextViewProgress.setText(message);
        mTextViewProgress.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mTabHost.setVisibility(View.INVISIBLE);
    }

    private void hideProgressBar() {
        mTextViewProgress.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mTabHost.setVisibility(View.VISIBLE);
    }

    //nessecary to setup the TabHost with custom look and functionality
    private void setupTabHost() {
        Log.d(TAG, "setupTabHost: ");

        mTabHost.setup();
        TabHost.TabSpec tabSpec;
        final TabWidget tabWidget = mTabHost.getTabWidget();
        ViewGroup.LayoutParams params;

        // set height of tabwidget through LayoutParams
        params = tabWidget.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.dimen_height_tabwidget);
        tabWidget.setLayoutParams(params);

        // add tab for creating an account
        tabSpec = mTabHost.newTabSpec(getResources().getString(R.string.button_create_account));
        tabSpec.setContent(R.id.tab_create_account);
        tabSpec.setIndicator(getResources().getString(R.string.button_create_account));
        mTabHost.addTab(tabSpec);

        // add tab for signing in
        tabSpec = mTabHost.newTabSpec(getResources().getString(R.string.button_signin));
        tabSpec.setContent(R.id.tab_signin);
        tabSpec.setIndicator(getResources().getString(R.string.button_signin));
        mTabHost.addTab(tabSpec);

        // set attributes for tab titles like lowercase and non-bold letters
        for (int i = 0; i < 2; i++) {
            TextView tabtext = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tabtext.setAllCaps(false);
            tabtext.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
            tabtext.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.dimen_textSize_normal));
            tabtext.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
        }

        // set initial background colors for tabs
        tabWidget.getChildAt(0).setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotBlue));
        tabWidget.getChildAt(1).setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorTabSelectedLogin));

        // a click on a tab changes the tabs colors. Since the OnClickListener overrides the original click-functionality of the tabs, the current tab has to be set again
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

    //TODO: Funktionalität bei Gelegenheit prüfen
    /**
     * validates the input data for creating an account. Checks for the following:
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


        // if surname field is empty
        if (TextUtils.isEmpty(mEditFirstname.getText().toString().trim())) {
            mEditFirstname.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            focus = mEditFirstname;
        } else {
            mEditFirstname.setError(null);
        }

        // if lastname field is empty
        if (TextUtils.isEmpty(mEditLastname.getText().toString().trim())) {
            mEditLastname.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditLastname;
            }
        } else {
            mEditLastname.setError(null);
        }

        // if birthday is not set
        if (mTextViewDate.getText().toString().equals(R.string.date_empty)) {
            mTextViewDate.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mButtonDatePicker;
            }
        } else {
            mTextViewDate.setError(null);
        }

        // if emailfield is empty or has invalid form
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

        // if first passwordfield is empty
        if (TextUtils.isEmpty(mEditCreateAccountPassword.getText().toString())) {
            mEditCreateAccountPassword.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountPassword;
            }
        } else {
            mEditCreateAccountPassword.setError(null);
        }

        // if second passwordfield is empty
        if (TextUtils.isEmpty(mEditCreateAccountPassword2.getText().toString())) {
            mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountPassword2;
            }
        } else {
            mEditCreateAccountPassword2.setError(null);
        }

        // if one of the inputfields were empty so far
        if (!valid) {
            focus.requestFocus(); // set focus to first empty field
            if (TextUtils.isEmpty(mEditCreateAccountPassword.getText().toString())) { // if first password field was empty, clear second one
                mEditCreateAccountPassword2.setText("");
            }
            return valid;
        }

        // if password fields are not equal
        if (!mEditCreateAccountPassword.getText().toString().equals(mEditCreateAccountPassword2.getText().toString())) {
            mEditCreateAccountPassword.setError(getResources().getString(R.string.error_passwords_dont_match));
            mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_passwords_dont_match));
            valid = false;
        } else {
            // if password is smaller than 6 chars
            if (mEditCreateAccountPassword.getText().toString().length() < 6) {
                mEditCreateAccountPassword.setError(getResources().getString(R.string.error_password_must_6_chars));
                mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_password_must_6_chars));
                valid = false;
                // if password contains only numbers or only letters
            } else if (mEditCreateAccountPassword.getText().toString().matches("\\p{Alpha}*") || mEditCreateAccountPassword.getText().toString().matches("\\p{Digit}*")) {
                mEditCreateAccountPassword.setError(getResources().getString(R.string.error_password_must_numbers_digits));
                mEditCreateAccountPassword2.setError(getResources().getString(R.string.error_password_must_numbers_digits));
                valid = false;
            } else {
                mEditCreateAccountPassword.setError(null);
                mEditCreateAccountPassword2.setError(null);
            }
        }
        // if password was not valid so far, clear password fields and set focus to first password field
        if (!valid) {
            mEditCreateAccountPassword.setText("");
            mEditCreateAccountPassword2.setText("");
            mEditCreateAccountPassword.requestFocus();
        }
        return valid;
    }

    //TODO: Funktionalität bei Gelegenheit prüfen
    /**
     * validates the input data for signing in. Checks for the following:
     * - are both fields filled?
     * - has email-Adress a typical valid form?
     * If one or more fields have invalid data, error messages are shown at the appropriate fields.
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
     * checks, if input data is valid and then tries to create a new app account at Firebase based on the data.
     */
    private void attemptCreateAccount() {
        Log.d(TAG, "attemptCreateAccount: " + mEditCreateAccountEmail.getText().toString().trim());

        if (!validateFormCreateAccount()) {
            return;
        }

        showProgressBar(getString(R.string.dialog_creating_account));

        // actual call for creating the account. An OnCompleteListener //TODO
        mAuth.createUserWithEmailAndPassword(mEditCreateAccountEmail.getText().toString().trim(), mEditCreateAccountPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmailAndPassword: onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    hideProgressBar();
                    //TODO: Progressbar-Steuerung in AsynkTask verlagern
                    Log.w(TAG, "createUserWithEmailAndPassword: failed ", task.getException());
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    //TODO: Im Fehlerfall Passwortfelder leeren
                    // TODO: falls Email schon vorhanden war, auch leeren
                    // TODO: mögliche Fehlerfälle ermitteln und System-Meldungen in deutsche Meldungen umwandeln
                } else {
                    // obtain userId
                    mUser = task.getResult().getUser();
                    mUserID = mUser.getUid();
                    //set database reference to /users/mUserID
                    mDatabaseRef = mDatabaseRootReference.child("users").child(mUserID);

                    // User details from input form are stored in mLocalUserData
                    mUserData.setFirstname(mEditFirstname.getText().toString().trim());
                    mUserData.setLastname(mEditLastname.getText().toString().trim());
                    mUserData.setBirthday(mCalendar.getTime().toString());
                    mUserData.setBYear(mCalendar.get(Calendar.YEAR));
                    mUserData.setBMonth(mCalendar.get(Calendar.MONTH));
                    mUserData.setBDay(mCalendar.get(Calendar.DAY_OF_MONTH));
                    mUserData.setEmail(mEditCreateAccountEmail.getText().toString().trim());

                    //set storage reference to /users/mUserID/profilePicture
                    mStorageRef = mStorageRootReference.child("users").child(mUserID).child("profilePicture.jpg");

                    // if a profile image was chosen, it will be uploaded to cloud-Storage
                    if (mUriLocalProfileImage != null) {
                        //upload file with local URI stored in mUriLocalProfileImage
                        mStorageRef.putFile(mUriLocalProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // on success the remote downloadURL will be stored to mLocalUserData.pictureURL
                                //TODO: Alternative finden
                                mUserData.setPictureURL(taskSnapshot.getDownloadUrl().toString());
                                // send data to database (must be here, after profile picture was send to Storage, otherwise pictureURL will be empty in database)
                                mDatabaseRef.setValue(mUserData);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // on failure mLocalUserData.pictureURL will remain empty.
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
                                // if no profile image was chosen, mLocalUserData.pictureURL will be set to downloadUrl of standard-avatar-picture located in Storage-folder "users"
                                mDatabaseRef.setValue(mUserData);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // on failure mLocalUserData.pictureURL will remain empty.
                                Log.e(getSubClassTAG(), "Error obtaining avatar image uri");
                                mDatabaseRef.setValue(mUserData);
                            }
                        });
                    }

                    // start MainOverview
                    Intent intent = new Intent(LoginActivity.this, MainOverviewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }



    /**
     * checks, if input data is valid and then tries to sign in at Firebase based on the data
     */
    private void attemptSignIn() {
        Log.d(TAG, "attemptSignIn: " + mEditSignInEmail.getText().toString().trim());

        if (!validateFormSignIn()) {
            return;
        }
        showProgressBar(getString(R.string.dialog_signing_in));

        mAuth.signInWithEmailAndPassword(mEditSignInEmail.getText().toString().trim(), mEditSignInPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmailAndPasswort: onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    hideProgressBar();
                    mEditSignInEmail.setText("");
                    mEditSignInPassword.setText("");
                    mEditSignInEmail.requestFocus();
                    Log.w(TAG, "signInWithEmailAndPassword: failed ", task.getException());
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO: im Fehlerfall Passwortfeld leeren
                    // TODO: falls Email unbekannt war, auch leeren
                    // TODO: mögliche Fehlerfälle ermitteln und System-Meldungen in deutsche Meldungen umwandeln
                } else {
                    Intent intent = new Intent(LoginActivity.this, MainOverviewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


}
