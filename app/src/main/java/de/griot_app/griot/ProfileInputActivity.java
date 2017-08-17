package de.griot_app.griot;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Calendar;

import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.LocalUserData;
import de.griot_app.griot.dataclasses.UserData;
import de.griot_app.griot.views.ProfileImageView;

public class ProfileInputActivity extends GriotBaseInputActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnTouchListener{

    private static final String TAG = ProfileInputActivity.class.getSimpleName();

    private static final int REQUEST_GALLERY = 888;

    private ProfileImageView mProfileImage;
    private Uri mUriLocalProfileImage;

    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditEmail;
    private EditText mEditPassword;
    private EditText mEditPassword2;

    private ImageView mButtonDatePicker;
    private Calendar mCalendar;
    private DatePickerDialog mDatePickerDialog;
    private TextView mTextViewDate;

    private Button mButtonSave;

    //Data class object
    private UserData mUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_edit_your_profile);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setVisibility(View.GONE);
        mButtonRight.setText(R.string.option_delete_account);

        //Initialization of views for create account tab
        mProfileImage = (ProfileImageView) findViewById(R.id.piv_profile_image);
        mUriLocalProfileImage = null;

        mEditFirstname = (EditText) findViewById(R.id.editText_firstname);
        mEditLastname = (EditText) findViewById(R.id.editText_lastname);
        mEditEmail = (EditText) findViewById(R.id.editText_email);
        mEditPassword = (EditText) findViewById(R.id.editText_password);
        mEditPassword2 = (EditText) findViewById(R.id.editText_password2);

        mButtonDatePicker = (ImageView) findViewById(R.id.button_datepicker);
        mCalendar = Calendar.getInstance();
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(ProfileInputActivity.this, ProfileInputActivity.this, year, month, day);
        mTextViewDate = (TextView) findViewById(R.id.textView_date);

        mButtonSave = (Button) findViewById(R.id.button_save);

        mEditFirstname.setOnClickListener(this);
        mEditLastname.setOnClickListener(this);
        mEditEmail.setOnClickListener(this);
        mEditPassword.setOnClickListener(this);
        mEditPassword2.setOnClickListener(this);

        mProfileImage.setOnTouchListener(this);
        mButtonDatePicker.setOnTouchListener(this);
        mButtonSave.setOnTouchListener(this);

        mUserData = new UserData();
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_profile;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    //called, when a date was set through mDatePickerDialog
    @Override
    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
        //set the chosen date to mDatePickerDialog, so that it will be set to this date on next call
        mDatePickerDialog.getDatePicker().updateDate(year, month, day);
        mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
        mEditEmail.requestFocus();
        mTextViewDate.setError(null);
        mCalendar.set(year,month,day,0,0,0);
    }

    @Override
    public void onClick(View v) {
        mEditFirstname.setError(null);
        mEditLastname.setError(null);
        mTextViewDate.setError(null);
        mEditEmail.setError(null);
        mEditPassword.setError(null);
        mEditPassword2.setError(null);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.piv_profile_image:
                        return true;
                    case R.id.button_datepicker:
                        ((ImageView)v).setColorFilter(ContextCompat.getColor(ProfileInputActivity.this, R.color.colorGriotWhite));
                        return true;
                    case R.id.button_save:
                        ((TextView)v).setTextColor(ContextCompat.getColor(ProfileInputActivity.this, R.color.colorGriotWhite));
                        return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.piv_profile_image:
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1,1)
                                .start(ProfileInputActivity.this);
                        return true;
                    case R.id.button_datepicker:
                        ((ImageView)v).setColorFilter(ContextCompat.getColor(ProfileInputActivity.this, R.color.colorGriotDarkgrey));
                        mDatePickerDialog.show();
                        return true;
                    case R.id.button_save:
                        ((TextView)v).setTextColor(ContextCompat.getColor(ProfileInputActivity.this, R.color.colorGriotDarkgrey));
                        saveProfile();  //TODO: methode implementiern
                        return true;
                }
                return false;
        }
        return false;
    }


    //called after an image was chosen and cropped on external Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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


    // TODO: anpassen, so dass Passwortfelder gemeinsam leer sein dürfen. (Dann werden nur die normalen Daten gespeichert.)
    // TODO: Wenn beide Passwortfelder valide gefüllt sind, wird auch das passwort geändert
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
    private boolean validateForm() {
        Log.d(TAG, "validateForm: ");

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
        if (TextUtils.isEmpty(mEditEmail.getText().toString().trim())) {
            mEditEmail.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditEmail;
            }
        } else if (!mEditEmail.getText().toString().trim().matches("\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}@\\p{Alnum}[\\w\\.\\-]*\\p{Alnum}\\.[a-z]{2,}")) {
            mEditEmail.setError(getResources().getString(R.string.error_invalid_email));
            valid = false;
            if (focus == null) {
                focus = mEditEmail;
            }
        } else {
            mEditEmail.setError(null);
        }

        // if first passwordfield is empty
        if (TextUtils.isEmpty(mEditPassword.getText().toString())) {
            mEditPassword.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditPassword;
            }
        } else {
            mEditPassword.setError(null);
        }

        // if second passwordfield is empty
        if (TextUtils.isEmpty(mEditPassword2.getText().toString())) {
            mEditPassword2.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            if (focus == null) {
                focus = mEditPassword2;
            }
        } else {
            mEditPassword2.setError(null);
        }

        // if one of the inputfields were empty so far
        if (!valid) {
            focus.requestFocus(); // set focus to first empty field
            if (TextUtils.isEmpty(mEditPassword.getText().toString())) { // if first password field was empty, clear second one
                mEditPassword2.setText("");
            }
            return valid;
        }

        // if password fields are not equal
        if (!mEditPassword.getText().toString().equals(mEditPassword2.getText().toString())) {
            mEditPassword.setError(getResources().getString(R.string.error_passwords_dont_match));
            mEditPassword2.setError(getResources().getString(R.string.error_passwords_dont_match));
            valid = false;
        } else {
            // if password is smaller than 6 chars
            if (mEditPassword.getText().toString().length() < 6) {
                mEditPassword.setError(getResources().getString(R.string.error_password_must_6_chars));
                mEditPassword2.setError(getResources().getString(R.string.error_password_must_6_chars));
                valid = false;
                // if password contains only numbers or only letters
            } else if (mEditPassword.getText().toString().matches("\\p{Alpha}*") || mEditPassword.getText().toString().matches("\\p{Digit}*")) {
                mEditPassword.setError(getResources().getString(R.string.error_password_must_numbers_digits));
                mEditPassword2.setError(getResources().getString(R.string.error_password_must_numbers_digits));
                valid = false;
            } else {
                mEditPassword.setError(null);
                mEditPassword2.setError(null);
            }
        }
        // if password was not valid so far, clear password fields and set focus to first password field
        if (!valid) {
            mEditPassword.setText("");
            mEditPassword2.setText("");
            mEditPassword.requestFocus();
        }
        return valid;
    }

    public void saveProfile() {
        //TODO: implementieren
    }


    @Override
    protected void onStart() {
        super.onStart();

        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        //TODO: Auslagern, ober überarbeiten oder vereinheitlichen
        // Obtain own user data from Firebase
        mUser = mAuth.getCurrentUser();
        mUserID = mUser.getUid();

        mDatabaseRootReference.child("users").orderByKey().equalTo(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(getSubClassTAG(), "getValueEventListener: onDataChange:");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mLocalUserData = ds.getValue(LocalUserData.class);
                }

                File file = null;
                try {
                    file = File.createTempFile("profile_image" + "_", ".jpg");
                } catch (Exception e) {
                }
                final String path = file.getPath();

                try {
                    mStorageRef = mStorage.getReferenceFromUrl(mLocalUserData.getPictureURL());
                    mStorageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            mLocalUserData.setPictureLocalURI(path);
                            mProfileImage.getProfileImage().setImageURI(Uri.parse(mLocalUserData.getPictureLocalURI()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(getSubClassTAG(), "Error downloading user profile image file");
                            mLocalUserData.setPictureLocalURI("");
                        }
                    });
                } catch (Exception e) {}

                mEditFirstname.setText(mLocalUserData.getFirstname());
                mEditLastname.setText((mLocalUserData.getLastname()));
             //   mCalendar.setTime(mLocalUserData.getBirthday());  //TODO: anpassen
                int day = mLocalUserData.getBDay();
                int month = mLocalUserData.getBMonth();
                int year = mLocalUserData.getBYear();
                mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
                mEditEmail.setText((mLocalUserData.getEmail()));
             }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    protected void buttonLeftPressed() {
        Log.d(TAG, "buttonLeftPressed: ");

        finish();
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        Toast.makeText(ProfileInputActivity.this, "Sicherheitsabfrage", Toast.LENGTH_SHORT).show();
        //TODO: implementieren
    }

}