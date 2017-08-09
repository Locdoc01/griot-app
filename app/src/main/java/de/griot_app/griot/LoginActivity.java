package de.griot_app.griot;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

/**
 * Created by marcel on 09.08.17.
 */

public class LoginActivity extends FirebaseActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnTouchListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private static final int REQUEST_GALLERY = 888;

    private ProgressBar mProgressBar;
    private TabHost mTabHost;

    //Views from create account tab
    private ProfileImageView mProfileImage;
    private Uri mUriLocalProfileImage;

    private String mPictureURL; //TODO mit Datenmodel und Firebase abstimmen

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

    private Button mButtonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTabHost = (TabHost) findViewById(R.id.tabHost);
        setupTabHost();

        //Initialization of views from create account tab
        mProfileImage = (ProfileImageView) findViewById(R.id.profile_image);
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

        //Initialization of views from signin tab
        mEditSignInEmail = (EditText) findViewById(R.id.editText_signin_email);
        mEditSignInPassword = (EditText) findViewById(R.id.editText_signin_password);

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
        mButtonCreateAccount.setOnTouchListener(this);
        mButtonSignIn.setOnTouchListener(this);
        mButtonDatePicker.setOnTouchListener(this);

    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
        Log.d(TAG, "onDateSet: Datum wurde gesetzt!");
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
                    case R.id.profile_image:
                        //TODO: runden Farbwechsel realisieren (in ProfileImageView)
                        //v.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorTabSelectedLogin));
                        return true;
                    case R.id.button_datepicker:
                        ((ImageView)v).setColorFilter(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotWhite));
                        return true;
                    case R.id.button_create_account:
                    case R.id.button_signin:
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotWhite));
                        return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.profile_image:
                        //v.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotBlue));

                        //Image-Auswahl ohne Crop
                        /*
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, REQUEST_GALLERY);
                        */
                        //Image-Auswahl mit Aufruf von CropImageActivity
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
                    case R.id.button_signin:
                        ((TextView)v).setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
                        attemptSignIn();
                        return true;
                }
                return false;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Image-Auswahl ohne Crop
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
        //Image-Auswahl mit Aufruf von CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mUriLocalProfileImage = result.getUri();
                mProfileImage.setImagePath(mUriLocalProfileImage.getPath());
                mProfileImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        showProgressBar(false);

        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void showProgressBar(boolean show) {
        if (show) {
            mProgressBar.setVisibility(View.VISIBLE);
            mTabHost.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mTabHost.setVisibility(View.VISIBLE);
        }
    }

    private void setupTabHost() {
        mTabHost.setup();
        TabHost.TabSpec tabSpec;
        final TabWidget tabWidget = mTabHost.getTabWidget();
        ViewGroup.LayoutParams params;

        // set height of tabwidget through LayoutParams
        params = tabWidget.getLayoutParams();
        params.height = getResources().getDimensionPixelSize(R.dimen.dimen_height_tabwidget);
        tabWidget.setLayoutParams(params);

        // add tab for creating an account
        tabSpec = mTabHost.newTabSpec("Account erstellen");
        tabSpec.setContent(R.id.tab_create_account);
        tabSpec.setIndicator("Account erstellen");
        mTabHost.addTab(tabSpec);

        // add tab for signing in
        tabSpec = mTabHost.newTabSpec("Anmelden");
        tabSpec.setContent(R.id.tab_signin);
        tabSpec.setIndicator("Anmelden");
        mTabHost.addTab(tabSpec);

        // set attributes for tab titles like lowercase and non-bold letters
        for (int i = 0; i < 2; i++) {
            TextView tabtext = (TextView) tabWidget.getChildAt(i).findViewById(android.R.id.title);
            tabtext.setAllCaps(false);
            tabtext.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.colorGriotDarkgrey));
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
    private boolean validateFormCreateAccount() {
        boolean valid = true;   // set to false, if one of the inputfields is not valid
        View focus = null;      // set to the first invalid inputfield, if there is any


        // if surname field is empty
        if (TextUtils.isEmpty(mEditFirstname.getText().toString().trim())) {
            mEditFirstname.setError("Pflichtfeld");
            valid = false;
            focus = mEditFirstname;
        } else {
            mEditFirstname.setError(null);
        }

        // if lastname field is empty
        if (TextUtils.isEmpty(mEditLastname.getText().toString().trim())) {
            mEditLastname.setError("Pflichtfeld");
            valid = false;
            if (focus == null) {
                focus = mEditLastname;
            }
        } else {
            mEditLastname.setError(null);
        }

        // if birthday is not set
        if (mTextViewDate.getText().toString().equals("--.--.----")) {
            mTextViewDate.setError("Pflichtfeld");
            Log.d(TAG, "mTextViewDate.equals() == true");
            valid = false;
            if (focus == null) {
                focus = mButtonDatePicker;
            }
        } else {
            mTextViewDate.setError(null);
        }

        // if emailfield is empty or has invalid form
        if (TextUtils.isEmpty(mEditCreateAccountEmail.getText().toString().trim())) {
            mEditCreateAccountEmail.setError("Pflichtfeld");
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountEmail;
            }
        } else if (!mEditCreateAccountEmail.getText().toString().trim().matches("\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}@\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}\\.[a-z]{2,}")) {
            mEditCreateAccountEmail.setError("ungültige Email-Adresse");
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountEmail;
            }
        } else {
            mEditCreateAccountEmail.setError(null);
        }

        // if first passwordfield is empty
        if (TextUtils.isEmpty(mEditCreateAccountPassword.getText().toString())) {
            mEditCreateAccountPassword.setError("Pflichtfeld");
            valid = false;
            if (focus == null) {
                focus = mEditCreateAccountPassword;
            }
        } else {
            mEditCreateAccountPassword.setError(null);
        }

        // if second passwordfield is empty
        if (TextUtils.isEmpty(mEditCreateAccountPassword2.getText().toString())) {
            mEditCreateAccountPassword2.setError("Pflichtfeld");
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
            mEditCreateAccountPassword.setError("Passwörter stimmen nicht überein");
            mEditCreateAccountPassword2.setError("Passwörter stimmen nicht überein");
            valid = false;
        } else {
            // if password is smaller than 6 chars
            if (mEditCreateAccountPassword.getText().toString().length() < 6) {
                mEditCreateAccountPassword.setError("Passwort muss mindestens 6 Zeichen lang sein");
                mEditCreateAccountPassword2.setError("Passwort muss mindestens 6 Zeichen lang sein");
                valid = false;
                // if password contains only numbers or only letters
            } else if (mEditCreateAccountPassword.getText().toString().matches("\\p{Alpha}*") || mEditCreateAccountPassword.getText().toString().matches("\\p{Digit}*")) {
                mEditCreateAccountPassword.setError("Passwort muss mindestens Buchstaben und Zahlen enthalten");
                mEditCreateAccountPassword2.setError("Passwort muss mindestens Buchstaben und Zahlen enthalten");
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
    private boolean validateFormSignIn() {
        boolean valid = true;
        View focus = null;
        if (TextUtils.isEmpty(mEditSignInEmail.getText().toString().trim())) {
            mEditSignInEmail.setError("Pflichtfeld");
            focus = mEditSignInEmail;
            valid = false;
        } else if (!mEditSignInEmail.getText().toString().trim().matches("\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}@\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}\\.[a-z]{2,}")) {
            mEditSignInEmail.setError("ungültige Email-Adresse");
            focus = mEditSignInEmail;
            valid = false;
        } else {
            mEditSignInEmail.setError(null);
        }

        if (TextUtils.isEmpty(mEditSignInPassword.getText().toString())) {
            mEditSignInPassword.setError("Pflichtfeld");
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

    //TODO: Funktionalität genau prüfen, insbesonder den Frirebase und Datenbank kram
    private void attemptCreateAccount() {

        Log.d(TAG, "createAccount:" + mEditCreateAccountEmail.getText().toString().trim());
        if (!validateFormCreateAccount()) {
            return;
        }

        showProgressBar(true);
//        mProgressDialog.setTitle("Signing in ...");
//        mProgressDialog.show();

        mAuth.createUserWithEmailAndPassword(mEditCreateAccountEmail.getText().toString().trim(), mEditCreateAccountPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "createUserWithEmailAndPassword:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    showProgressBar(false);
                    //mProgressDialog.dismiss(); oder mProgressDialog.hide();
                    //TODO: Progressbar-Steuerung in AsynkTask verlagern
                    Log.w(TAG, "createUserWithEmailAndPassword:failed", task.getException());
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    //TODO: Im Fehlerfall Passwortfelder leeren
                } else {
                    // TODO: Kommentar löschen: task.getResult().getUser() == mAuth.getCurrentUser()
                    mUser = task.getResult().getUser();
                    mUid = mUser.getUid();
                    mDatabaseRef = mDatabaseRootReference.child("users").child(mUid);
                    // wenn ein Profilbild ausgewählt wurde:
                    if (mUriLocalProfileImage != null) {
                        mStorageRef = mStorageRootReference.child("users").child(mUid).child("profilePhoto");
                        //Hochladen des Profilbildes mit der lokalen URI mLocalUriProfilPhoto
                        mStorageRef.putFile(mUriLocalProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Bei Erfolg wird die DownloadURL mittels mDownloadUrlProfilPhoto in der Datenbank in den UserDetails gespeichert
                                mPictureURL = taskSnapshot.getDownloadUrl().toString();
                                mDatabaseRef.child("profilePhotoURL").setValue(mPictureURL);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDatabaseRef.child("profilePhotoURL").setValue("");
                                Toast.makeText(LoginActivity.this, "Fehler beim Bild", Toast.LENGTH_SHORT).show();

                            }
                        });
                        // wenn kein Profilbild ausgewählt wurde
                    } else {
                        mDatabaseRef.child("profilePhotoURL").setValue("");
                    }
                    // restliche UserDetails
                    mDatabaseRef.child("surname").setValue(mEditFirstname.getText().toString().trim());
                    mDatabaseRef.child("lastname").setValue(mEditLastname.getText().toString().trim());
                    mDatabaseRef.child("birthday").setValue(mCalendar.getTime().toString());
                    mDatabaseRef.child("email").setValue(mEditCreateAccountEmail.getText().toString().trim());


                    Intent intent = new Intent(LoginActivity.this, MainOverviewActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    //TODO: Funktionalität genau prüfen, insbesonder den Frirebase und Datenbank kram
    private void attemptSignIn() {
        Log.d(TAG, "signIn:" + mEditSignInEmail.getText().toString().trim());
        if (!validateFormSignIn()) {
            return;
        }
        showProgressBar(true);

        mAuth.signInWithEmailAndPassword(mEditSignInEmail.getText().toString().trim(), mEditSignInPassword.getText().toString()).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmailAndPasswort:onComplete:" + task.isSuccessful());

                if (!task.isSuccessful()) {
                    showProgressBar(false);
                    Log.w(TAG, "signInWithEmailAndPassword:failed", task.getException());
                    Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    // TODO: im Fehlerfall Passwortfeld leeren
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
