package de.griot_app.griot.contacts_profiles;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

import java.io.File;

import de.griot_app.griot.R;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.LocalUserData;
import de.griot_app.griot.views.ProfileImageView;

/**
 * Input activity, which shows an existing user profile.
 */
public class UserProfileInputActivity extends GriotBaseInputActivity {

    private static final String TAG = UserProfileInputActivity.class.getSimpleName();

    //private static final int REQUEST_GALLERY = 888;

    //Intent data
    private String contactID;

    //Views
    private ProfileImageView mProfileImage;
    private EditText mEditFirstname;
    private EditText mEditLastname;
    private EditText mEditEmail;
    private ImageView mButtonDatePicker;
    private TextView mTextViewDate;
    private TextView mTextViewRelationship;
    private Button mButtonSave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get possible intent data
        contactID = getIntent().getStringExtra("contactID");

        mTitle.setText(R.string.title_user_profile);

        mButtonLeft.setVisibility(View.GONE);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setVisibility(View.GONE);

        //Get references to the layout objects
        mProfileImage = (ProfileImageView) findViewById(R.id.piv_profile_image);
        mEditFirstname = (EditText) findViewById(R.id.editText_firstname);
        mEditLastname = (EditText) findViewById(R.id.editText_lastname);
        mEditEmail = (EditText) findViewById(R.id.editText_email);
        mButtonDatePicker = (ImageView) findViewById(R.id.button_datepicker);
        mTextViewDate = (TextView) findViewById(R.id.textView_date);
        mTextViewRelationship = (TextView) findViewById(R.id.textView_relationship);

        mEditFirstname.setEnabled(false);
        mEditLastname.setEnabled(false);
        mEditEmail.setEnabled(false);

        mButtonDatePicker.setVisibility(View.GONE);

        mButtonSave = (Button) findViewById(R.id.button_save);
        mButtonSave.setVisibility(View.GONE);

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserProfileInputActivity.this, "Fotoansicht", Toast.LENGTH_SHORT);
            }
        });

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_person_profile;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}


    @Override
    protected void onStart() {
        super.onStart();

        // hides the keyboard, even if EditText gets focus on startup
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Obtain user data from Firebase, if the profile of an existing user was selected
        mDatabaseRootReference.child("users").orderByKey().equalTo(contactID).addListenerForSingleValueEvent(new ValueEventListener() {
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
                } catch (Exception e) {
                }

                //initialize the views with the obtained data
                mEditFirstname.setText(mLocalUserData.getFirstname());
                mEditLastname.setText((mLocalUserData.getLastname()));
                int day = mLocalUserData.getBday();
                int month = mLocalUserData.getBmonth();
                int year = mLocalUserData.getByear();
                mTextViewDate.setText("" + day + "." + (month + 1) + "." + year);
                mEditEmail.setText((mLocalUserData.getEmail()));
//                    mTextViewRelationship.setText(((mLocalUserData).getRelationship()));  //TODO
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        finish();
    }

}