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
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Calendar;

import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.GuestData;
import de.griot_app.griot.dataclasses.LocalGuestData;
import de.griot_app.griot.views.ProfileImageView;

public class GuestProfileInputActivity extends GriotBaseInputActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = GuestProfileInputActivity.class.getSimpleName();

    private static final int REQUEST_GALLERY = 888;

    private boolean mImageChanged = false;
    private boolean mDateSet = false;

    //intent-data
    private String contactID;
    //private boolean isUser;

    private ProfileImageView mProfileImage;
    private Uri mUriLocalProfileImage;

    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditEmail;

    private ImageView mButtonDatePicker;
    private Calendar mCalendar;
    private DatePickerDialog mDatePickerDialog;
    private TextView mTextViewDate;
    private TextView mTextViewRelationship;

    private Button mButtonSave;

    private View.OnClickListener mClickListener;
    private View.OnTouchListener mTouchListener;

    //Data class object
    private GuestData mGuestData;
    private LocalGuestData mLocalGuestData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        contactID = getIntent().getStringExtra("contactID");
       // isUser = getIntent().getBooleanExtra("isUser", true);

        if (contactID!=null) {
            mTitle.setText(R.string.title_guest_profile);
        } else {
            mTitle.setText(R.string.title_add_guest);
        }

        mButtonLeft.setVisibility(View.GONE);
        mButtonCenter.setText(R.string.button_cancel);
        mButtonRight.setVisibility(View.GONE);

        //Initialization of views for create account tab
        mProfileImage = (ProfileImageView) findViewById(R.id.piv_profile_image);
        mUriLocalProfileImage = null;

        mEditFirstname = (EditText) findViewById(R.id.editText_firstname);
        mEditLastname = (EditText) findViewById(R.id.editText_lastname);
        mEditEmail = (EditText) findViewById(R.id.editText_email);

        mButtonDatePicker = (ImageView) findViewById(R.id.button_datepicker);
        mCalendar = Calendar.getInstance();
        if (contactID==null) {
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            mDatePickerDialog = new DatePickerDialog(GuestProfileInputActivity.this, GuestProfileInputActivity.this, year, month, day);
        }
        mTextViewDate = (TextView) findViewById(R.id.textView_date);
        mTextViewRelationship = (TextView) findViewById(R.id.textView_relationship);

        mButtonSave = (Button) findViewById(R.id.button_save);

        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditFirstname.setError(null);
                mEditLastname.setError(null);
                mTextViewDate.setError(null);
                mEditEmail.setError(null);
            }
        };

        mTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        switch (v.getId()) {
                            case R.id.piv_profile_image:
                                return true;
                            case R.id.button_datepicker:
                                ((ImageView)v).setColorFilter(ContextCompat.getColor(GuestProfileInputActivity.this, R.color.colorGriotBlue));
                                return true;
                            case R.id.button_save:
                                ((TextView)v).setTextColor(ContextCompat.getColor(GuestProfileInputActivity.this, R.color.colorGriotBlue));
                                return true;
                        }
                        return false;
                    case MotionEvent.ACTION_UP:
                        switch (v.getId()) {
                            case R.id.piv_profile_image:
                                mImageChanged = true;
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1,1)
                                        .start(GuestProfileInputActivity.this);
                                return true;
                            case R.id.button_datepicker:
                                ((ImageView)v).setColorFilter(ContextCompat.getColor(GuestProfileInputActivity.this, R.color.colorGriotDarkgrey));
                                if (!mDateSet) {
                                    int year = mCalendar.get(Calendar.YEAR);
                                    int month = mCalendar.get(Calendar.MONTH);
                                    int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                                    mDatePickerDialog = new DatePickerDialog(GuestProfileInputActivity.this, GuestProfileInputActivity.this, year, month, day);
                                }
                                mDatePickerDialog.show();
                                return true;
                            case R.id.button_save:
                                ((TextView)v).setTextColor(ContextCompat.getColor(GuestProfileInputActivity.this, R.color.colorGriotDarkgrey));
                                saveProfile();
                                return true;
                        }
                        return false;
                }
                return false;
            }
        };

        mEditFirstname.setOnClickListener(mClickListener);
        mEditLastname.setOnClickListener(mClickListener);
        mEditEmail.setOnClickListener(mClickListener);

        mProfileImage.setOnTouchListener(mTouchListener);
        mButtonDatePicker.setOnTouchListener(mTouchListener);
        mButtonSave.setOnTouchListener(mTouchListener);

        //mGuestData = new GuestData();
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_person_profile;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    //called, when a date was set through mDatePickerDialog
    @Override
    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
        //set the chosen date to mDatePickerDialog, so that it will be set to this date on next call
        mDatePickerDialog.getDatePicker().updateDate(year, month, day);
        mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
        mTextViewDate.setError(null);
        mCalendar.set(year,month,day,0,0,0);
        mDateSet = true;
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


    /**
     * validates the input data for creating an guest profile. Checks for the following:
     * - are all required fields filled? (required are firstname and email)
     * - has email-Adress a typical valid form?
     * If one ore both required field is invalid, error messages are shown at the appropriate fields.
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

        // if one of the inputfields were empty so far
        if (!valid) {
            focus.requestFocus(); // set focus to first empty field
            return valid;
        }

        return valid;
    }

    public void saveProfile() {

        if (!validateForm()) {
            return;
        }

        mGuestData = new GuestData();

        contactID = mDatabaseRootReference.child("guests").push().getKey();

        //set database reference to /guests/contactID
        mDatabaseRef = mDatabaseRootReference.child("guests").child(contactID);

        // Guest details from input form are stored in mGuestData
        mGuestData.setFirstname(mEditFirstname.getText().toString().trim());
        mGuestData.setLastname(mEditLastname.getText().toString().trim());
        if (mDateSet) {
            mGuestData.setBirthday(mCalendar.getTime().toString());
            mGuestData.setBYear(mCalendar.get(Calendar.YEAR));
            mGuestData.setBMonth(mCalendar.get(Calendar.MONTH));
            mGuestData.setBDay(mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        mGuestData.setEmail(mEditEmail.getText().toString().trim());
        ((GuestData) mGuestData).setRelationship(mTextViewRelationship.getText().toString()); // TODO: gbfs. anpassen

//        if (mImageChanged) {
            //set storage reference to /guests/contactID/profilePicture
            mStorageRef = mStorageRootReference.child("guests").child(contactID).child("profilePicture.jpg");

            // if a profile image was chosen, it will be uploaded to cloud-Storage
            if (mUriLocalProfileImage != null) {
                //upload file with local URI stored in mUriLocalProfileImage
                mStorageRef.putFile(mUriLocalProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // on success the remote downloadURL will be stored to mGuestData.pictureURL
                        //TODO: Alternative finden
                        mGuestData.setPictureURL(taskSnapshot.getDownloadUrl().toString());
                        // send data to database (must be here, after profile picture was send to Storage, otherwise pictureURL will be empty in database)
                        mDatabaseRef.setValue(mGuestData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // on failure mLocalUserData.pictureURL will remain empty.
                        Toast.makeText(GuestProfileInputActivity.this, "Profile Image Error", Toast.LENGTH_SHORT).show();
                        Log.e(getSubClassTAG(), "Error uploading profile image");
                        mDatabaseRef.setValue(mGuestData);
                    }
                });
            } else {
                mStorageRootReference.child("guests").child("profilePicture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mGuestData.setPictureURL(uri.toString());
                        Log.d(TAG, "---------------------------------" + uri.toString());
                        // if no profile image was chosen, mLocalUserData.pictureURL will be set to downloadUrl of standard-avatar-picture located in Storage-folder "guests"
                        mDatabaseRef.setValue(mGuestData);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // on failure mLocalUserData.pictureURL will remain empty.
                        Log.e(getSubClassTAG(), "Error obtaining avatar image uri");
                        mDatabaseRef.setValue(mGuestData);
                    }
                });
            }
/*
        } else {
            mGuestData.setPictureURL(mLocalGuestData.getPictureURL());
            mDatabaseRef.setValue(mGuestData);
        }
*/
    }


    @Override
    protected void onStart() {
        super.onStart();

        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (contactID!=null && !mImageChanged) {
            //TODO: Auslagern, ober überarbeiten oder vereinheitlichen
            // Obtain guest data from Firebase, if the profile of an existing guest was selected

            mDatabaseRootReference.child("guests").orderByKey().equalTo(contactID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(getSubClassTAG(), "getValueEventListener: onDataChange:");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        mLocalGuestData = ds.getValue(LocalGuestData.class);
                    }

                    File file = null;
                    try {
                        file = File.createTempFile("profile_image" + "_", ".jpg");
                    } catch (Exception e) {
                    }
                    final String path = file.getPath();

                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mLocalGuestData.getPictureURL());
                        mStorageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mLocalGuestData.setPictureLocalURI(path);
                                mProfileImage.getProfileImage().setImageURI(Uri.parse(mLocalGuestData.getPictureLocalURI()));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading guest profile image file");
                                mLocalGuestData.setPictureLocalURI("");
                            }
                        });
                    } catch (Exception e) {
                    }

                    mEditFirstname.setText(mLocalGuestData.getFirstname());
                    mEditLastname.setText((mLocalGuestData.getLastname()));
                    if (mLocalGuestData.getBDay()!=null) {
                        //   mCalendar.setTime(mLocalUserData.getBirthday());  //TODO: anpassen
                        int day = mLocalGuestData.getBDay();
                        int month = mLocalGuestData.getBMonth();
                        int year = mLocalGuestData.getBYear();
                        mDatePickerDialog = new DatePickerDialog(GuestProfileInputActivity.this, GuestProfileInputActivity.this, year, month, day);
                        mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
                        mDateSet = true;
                    }
                    mEditEmail.setText((mLocalGuestData.getEmail()));

                    mTextViewRelationship.setText((mLocalGuestData.getRelationship()));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        finish();
    }

}