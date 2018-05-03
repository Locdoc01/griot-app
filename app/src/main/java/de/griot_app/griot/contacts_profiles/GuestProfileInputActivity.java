package de.griot_app.griot.contacts_profiles;

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

import de.griot_app.griot.R;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.GuestData;
import de.griot_app.griot.views.ProfileImageView;

/**
 * **************************************************************************************
 *  This class uses the Android Image Cropper library, which is under following licence:
 * Originally forked from edmodo/cropper.
 * Copyright 2016, Arthur Teplitzki, 2013, Edmodo, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 * **************************************************************************************
 *
 * Input activity, which allows to create a new guest profile or to show an existing one.
 * If an existing profile is shown, it can be altered and saved again.
 */
public class GuestProfileInputActivity extends GriotBaseInputActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = GuestProfileInputActivity.class.getSimpleName();

    //private static final int REQUEST_GALLERY = 888;

    //Intent data
    private String contactID;

    //Views
    private ProfileImageView mProfileImage;
    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditEmail;
    private ImageView mButtonDatePicker;
    private DatePickerDialog mDatePickerDialog;
    private TextView mTextViewDate;
    private TextView mTextViewRelationship;
    private Button mButtonSave;

    private View.OnClickListener mClickListener;
    private View.OnTouchListener mTouchListener;

    private Calendar mCalendar;
    private Uri mUriLocalProfileImage;

    //Switch variables
    private boolean mImageChanged = false;
    private boolean mDateSet = false;

    //Data class object
    private GuestData mGuestData;
    private GuestData mLocalGuestData;  //TODO: change implementation, so that just one GuestData is used


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get possible intent data
        contactID = getIntent().getStringExtra("contactID");

        //If the contactID of an existing guest was obtained by intent, the appropriate profile for that guest will be shown.
        //Otherwise the profile form shows up empty
        //Title and center navigation button are set accordingly
        if (contactID!=null) {
            mTitle.setText(R.string.title_guest_profile);
            mButtonCenter.setText(R.string.button_back);
        } else {
            mTitle.setText(R.string.title_add_guest);
            mButtonCenter.setText(R.string.button_cancel);
        }

        //Hide left and right base navigation buttons
        mButtonLeft.setVisibility(View.GONE);
        mButtonRight.setVisibility(View.GONE);

        //Get references to the layout objects
        mProfileImage = (ProfileImageView) findViewById(R.id.piv_profile_image);
        mEditFirstname = (EditText) findViewById(R.id.editText_firstname);
        mEditLastname = (EditText) findViewById(R.id.editText_lastname);
        mEditEmail = (EditText) findViewById(R.id.editText_email);
        mButtonDatePicker = (ImageView) findViewById(R.id.button_datepicker);
        mTextViewDate = (TextView) findViewById(R.id.textView_date);
        mTextViewRelationship = (TextView) findViewById(R.id.textView_relationship);
        mButtonSave = (Button) findViewById(R.id.button_save);

        //Set date
        mCalendar = Calendar.getInstance();
        if (contactID==null) {
            int year = mCalendar.get(Calendar.YEAR);
            int month = mCalendar.get(Calendar.MONTH);
            int day = mCalendar.get(Calendar.DAY_OF_MONTH);
            mDatePickerDialog = new DatePickerDialog(GuestProfileInputActivity.this, GuestProfileInputActivity.this, year, month, day);
        }

        mUriLocalProfileImage = null;

        //Hide possible error messages from input views by click in one of them
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditFirstname.setError(null);
                mEditLastname.setError(null);
                mTextViewDate.setError(null);
                mEditEmail.setError(null);
            }
        };

        //OnTouchListener for button views
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
                                Log.d(TAG, "profile image clicked: ");
                                CropImage.activity()
                                        .setGuidelines(CropImageView.Guidelines.ON)
                                        .setAspectRatio(1,1)
                                        .start(GuestProfileInputActivity.this);
                                return true;
                            case R.id.button_datepicker:
                                Log.d(TAG, "datepicker clicked: ");
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
                                Log.d(TAG, "save button clicked: ");
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
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_person_profile;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}


    //Sets the date to TextView and Calendar, when a date was set through mDatePickerDialog.
    @Override
    public void onDateSet(DatePicker datepicker, int year, int month, int day) {
        Log.d(TAG, "onDateSet: year: " + year + " , month: " + month + " , day: " + day);
        //set the chosen date to mDatePickerDialog, so that it will be set to this date on next call
        mDatePickerDialog.getDatePicker().updateDate(year, month, day);
        mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
        mTextViewDate.setError(null);
        mCalendar.set(year,month,day,0,0,0);
        mDateSet = true;
    }


    //Sets the profile image, after an image was chosen and cropped on external Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode: " + requestCode + " , resultCode: " + resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageChanged = true;
                mUriLocalProfileImage = result.getUri();
                mProfileImage.loadImageFromSource(mUriLocalProfileImage);
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

        //Set to false, if one of the inputfields is not valid
        boolean valid = true;
        //Set to the first invalid inputfield, if there is any
        View focus = null;


        //If surname field is empty
        if (TextUtils.isEmpty(mEditFirstname.getText().toString().trim())) {
            mEditFirstname.setError(getResources().getString(R.string.error_required_field));
            valid = false;
            focus = mEditFirstname;
        } else {
            mEditFirstname.setError(null);
        }

        //If email field is empty or has invalid form
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

        //If one of the input fields were empty so far
        if (!valid) {
            focus.requestFocus(); // set focus to first empty field
            return valid;
        }

        return valid;
    }

    /**
     * Saves the input data to Firebase and creates a guest profile, if input data is valid.
     */
    public void saveProfile() {
        Log.d(TAG, "saveProfile: ");

        if (!validateForm()) {
            return;
        }
        mGuestData = new GuestData();

        //Only on creating a new guest profile a push-key gets obtained from Firebase database. Otherwise the altered profile will be stored to the same contactID
        if (contactID ==null) {
            contactID = mDatabaseRootReference.child("guests").push().getKey();
        }

        //set database reference to /guests/contactID
        mDatabaseRef = mDatabaseRootReference.child("guests").child(contactID);

        //Guest details from input form are stored in mGuestData
        mGuestData.setFirstname(mEditFirstname.getText().toString().trim());
        mGuestData.setLastname(mEditLastname.getText().toString().trim());
        mGuestData.setHostID(mUserID);
        if (mDateSet) {
            mGuestData.setBirthday(mCalendar.getTime().toString());
            mGuestData.setByear(mCalendar.get(Calendar.YEAR));
            mGuestData.setBmonth(mCalendar.get(Calendar.MONTH));
            mGuestData.setBday(mCalendar.get(Calendar.DAY_OF_MONTH));
        }
        mGuestData.setEmail(mEditEmail.getText().toString().trim());
        mGuestData.setRelationship(mTextViewRelationship.getText().toString());

        //Set storage reference to /guests/contactID/profilePicture
        mStorageRef = mStorageRootReference.child("guests").child(contactID).child("profilePicture.jpg");

        //If a profile image was chosen, it will be uploaded to cloud-Storage
        if (mUriLocalProfileImage != null) {
                //Upload file with local URI stored in mUriLocalProfileImage
                mStorageRef.putFile(mUriLocalProfileImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //On success the remote downloadURL will be stored to mGuestData.pictureURL
                        //TODO: find alternative
                        mGuestData.setPictureURL(taskSnapshot.getDownloadUrl().toString());
                        //Send data to database (must be here, after profile picture was send to Storage, otherwise pictureURL will be empty in database)
                        mDatabaseRef.setValue(mGuestData);
                        doAfterUpload();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //On failure mUserData.pictureURL will remain empty.
                        Toast.makeText(GuestProfileInputActivity.this, "Profile Image Error", Toast.LENGTH_SHORT).show();
                        Log.e(getSubClassTAG(), "Error uploading profile image");
                        mDatabaseRef.setValue(mGuestData);
                        doAfterUpload();
                    }
                });
        } else if (mLocalGuestData == null) {
            //If a profile image already exists
            mStorageRootReference.child("guests").child("profilePicture.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    mGuestData.setPictureURL(uri.toString());
                    // if no profile image was chosen, mUserData.pictureURL will be set to downloadUrl of standard-avatar-picture located in Storage-folder "guests"
                    mDatabaseRef.setValue(mGuestData);
                    doAfterUpload();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // on failure mUserData.pictureURL will remain empty.
                    Log.e(getSubClassTAG(), "Error obtaining avatar image uri");
                    mDatabaseRef.setValue(mGuestData);
                    doAfterUpload();
                }
            });
            //If no profile Image extists or was chosen
        } else {
            mGuestData.setPictureURL(mLocalGuestData.getPictureURL());
            mDatabaseRef.setValue(mGuestData);
            doAfterUpload();
        }
    }

    /**
     * This method is called in saveProfile() after the attempted upload of profile data was finished
     */
    private void doAfterUpload() {
        Log.d(TAG, "doAfterUpload: ");
        //Add guest to user in Database
        mDatabaseRef = mDatabaseRootReference.child("users").child(mUserID).child("guests").child(contactID);
        mDatabaseRef.setValue(mGuestData.getRelationship());
        Toast.makeText(GuestProfileInputActivity.this, "Gast gespeichert", Toast.LENGTH_LONG).show();
        //Change title and navigation button of activity
        mTitle.setText(R.string.title_guest_profile);
        mButtonCenter.setText(R.string.button_back);
    }


    @Override
    protected void onStart() {
        super.onStart();

        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Obtain guest data from Firebase, if the profile of an existing guest was selected
        if (contactID==null) {
            mProfileImage.showPlus(true);
        } else {
            mDatabaseRootReference.child("guests").orderByKey().equalTo(contactID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(getSubClassTAG(), "getValueEventListener: onDataChange:");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        mLocalGuestData = ds.getValue(GuestData.class);
                    }

                    if (!mImageChanged) {
                        mProfileImage.loadImageFromSource(mLocalGuestData.getPictureURL());
                    }
                    //initialize the views with the obtained data
                    mEditFirstname.setText(mLocalGuestData.getFirstname());
                    mEditLastname.setText((mLocalGuestData.getLastname()));
                    if (mLocalGuestData.getBday() != null) {
                        //   mCalendar.setTime(mUserData.getBirthday());  //TODO: delete
                        int day = mLocalGuestData.getBday();
                        int month = mLocalGuestData.getBmonth();
                        int year = mLocalGuestData.getByear();
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