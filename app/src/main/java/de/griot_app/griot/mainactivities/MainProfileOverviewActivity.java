package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

import de.griot_app.griot.CombinedPersonListCreator;
import de.griot_app.griot.ProfileActivity;
import de.griot_app.griot.adapter.LocalInterviewDataAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewData;
import de.griot_app.griot.dataclasses.LocalUserData;
import de.griot_app.griot.startactivities.LoginActivity;
import de.griot_app.griot.views.ProfileImageView;

public class MainProfileOverviewActivity extends GriotBaseActivity implements View.OnTouchListener {

    private static final String TAG = MainProfileOverviewActivity.class.getSimpleName();

    private ImageView mButtonAddGuest;
    private ProfileImageView mPivUser;
    private TextView mTextViewUser;
    private ImageView mButtonOptions;
    private ImageView mImageViewFriendsGroups;
    private TextView mTextViewFriendsGroups;
    private ImageView mImageViewQuestionmail;
    private TextView mTextViewQuestionmail;
    private TextView mTextViewMedias;

    // ListView, that holds the interview items
    private ListView mListViewInterviews;

    // data list
    private ArrayList<LocalInterviewData> mListLocalInterviewData;

    //Data-View-Adapter for the ListView
    private LocalInterviewDataAdapter mLocalInterviewDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppBar.setVisibility(View.GONE);
        mButtonQuestionmail.setVisibility(View.GONE);

        mTitle.setVisibility(View.GONE);
        mLineAppBar.setVisibility(View.GONE);
        mButtonProfile.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mButtonAddGuest = (ImageView) findViewById(R.id.button_add_guest);
        mPivUser = (ProfileImageView) findViewById(R.id.piv_user);
        mTextViewUser = (TextView) findViewById(R.id.textView_user);
        mButtonOptions = (ImageView) findViewById(R.id.button_options);
        mImageViewFriendsGroups = (ImageView) findViewById(R.id.imageView_friends_groups);
        mTextViewFriendsGroups = (TextView) findViewById(R.id.textView_friends_groups);
        mImageViewQuestionmail = (ImageView) findViewById(R.id.imageView_questionmail);
        mTextViewQuestionmail = (TextView) findViewById(R.id.textView_questionmail);
        mTextViewMedias = (TextView) findViewById(R.id.textView_medias);

        mButtonAddGuest.setOnTouchListener(this);
        mPivUser.setOnTouchListener(this);
        mTextViewUser.setOnTouchListener(this);
        mButtonOptions.setOnTouchListener(this);
        mImageViewFriendsGroups.setOnTouchListener(this);
        mTextViewFriendsGroups.setOnTouchListener(this);
        mImageViewQuestionmail.setOnTouchListener(this);
        mTextViewQuestionmail.setOnTouchListener(this);

        mListLocalInterviewData = new ArrayList<>();

        mListViewInterviews = (ListView) findViewById(R.id.listView_main_profile_overview);


        // Obtains all necessary data from Firebase
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListLocalInterviewData.clear();
                //obtain interview data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final LocalInterviewData localInterviewData = ds.getValue(LocalInterviewData.class);
                    mListLocalInterviewData.add(localInterviewData);
                }
                // set the adapter
                mLocalInterviewDataAdapter = new LocalInterviewDataAdapter(MainProfileOverviewActivity.this, mListLocalInterviewData);
                mListViewInterviews.setAdapter(mLocalInterviewDataAdapter);

                //create temporary files to store the pictures from Firebase Storage
                for ( int i=0 ; i<mListLocalInterviewData.size() ; i++ ) {
                    final int index = i;
                    File fileMediaCover = null;
                    File fileInterviewer = null;
                    File fileNarrator = null;
                    try {
                        fileMediaCover = File.createTempFile("mediaCover" + i + "_", ".jpg");
                        fileInterviewer = File.createTempFile("interviewer" + i + "_", ".jpg");
                        fileNarrator = File.createTempFile("narrator" + i + "_", ".jpg");
                    } catch (Exception e) {
                    }
                    final String pathMediaCover = fileMediaCover.getPath();
                    final String pathInterviewer = fileInterviewer.getPath();
                    final String pathNarrator = fileNarrator.getPath();

                    //TODO: try-catch wahrscheinlich nötig, wenn beim Upload der Bilder was schief gelaufen ist.
                    //Obtain pictures for interview media covers from Firebase Storage
                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mListLocalInterviewData.get(index).getPictureURL());
                        mStorageRef.getFile(fileMediaCover).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mListLocalInterviewData.get(index).setPictureLocalURI(pathMediaCover);
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading MediaCover image file");
                                mListLocalInterviewData.get(index).setPictureLocalURI("");
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {}

                    //Obtain interviewer profile pictures from Firebase Storage
                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mListLocalInterviewData.get(index).getInterviewerPictureURL());
                        mStorageRef.getFile(fileInterviewer).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mListLocalInterviewData.get(index).setInterviewerPictureLocalURI(pathInterviewer);
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading Interviewer image file");
                                mListLocalInterviewData.get(index).setInterviewerPictureLocalURI("");
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {}

                    //Obtain narrator profile pictures from Firebase Storage
                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mListLocalInterviewData.get(index).getNarratorPictureURL());
                        mStorageRef.getFile(fileNarrator).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mListLocalInterviewData.get(index).setNarratorPictureLocalURI(pathNarrator);
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading Interviewer image file");
                                mListLocalInterviewData.get(index).setNarratorPictureLocalURI("");
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {}
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_profile_overview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.button_add_guest:
                        mButtonAddGuest.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.piv_user:
                        return true;
                    case R.id.textView_user:
                        mTextViewUser.setTextColor(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.button_options:
                        mButtonOptions.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.imageView_friends_groups:
                    case R.id.textView_friends_groups:
                        mImageViewFriendsGroups.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotBlue));
                        mTextViewFriendsGroups.setTextColor(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.imageView_questionmail:
                    case R.id.textView_questionmail:
                        mImageViewQuestionmail.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotBlue));
                        mTextViewQuestionmail.setTextColor(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotBlue));
                        return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.button_add_guest:
                        mButtonAddGuest.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotDarkgrey));
                        Toast.makeText(MainProfileOverviewActivity.this, "Gast hinzufügen", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.piv_user:
                    case R.id.textView_user:
                        mTextViewUser.setTextColor(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotDarkgrey));
                        startActivity(new Intent(MainProfileOverviewActivity.this, ProfileActivity.class));
                        return true;
                    case R.id.button_options:
                        mButtonOptions.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotDarkgrey));
                        Toast.makeText(MainProfileOverviewActivity.this, "Optionen anzeigen", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.imageView_friends_groups:
                    case R.id.textView_friends_groups:
                        mImageViewFriendsGroups.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotDarkgrey));
                        mTextViewFriendsGroups.setTextColor(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotDarkgrey));
                        Toast.makeText(MainProfileOverviewActivity.this, "Kontaktverwaltung anzeigen", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.imageView_questionmail:
                    case R.id.textView_questionmail:
                        mImageViewQuestionmail.setColorFilter(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotDarkgrey));
                        mTextViewQuestionmail.setTextColor(ContextCompat.getColor(MainProfileOverviewActivity.this, R.color.colorGriotDarkgrey));
                        startActivity(new Intent(this, MainQuestionmailActivity.class));
                        finish();
                        return true;
                }
                return false;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseRef = mDatabaseRootReference.child("interviews");
        mDatabaseRef.addValueEventListener(mValueEventListener);

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
                            mPivUser.getProfileImage().setImageURI(Uri.parse(mLocalUserData.getPictureLocalURI()));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(getSubClassTAG(), "Error downloading user profile image file");
                            mLocalUserData.setPictureLocalURI("");
                        }
                    });
                } catch (Exception e) {}

                mTextViewUser.setText(mLocalUserData.getFirstname() + " " + mLocalUserData.getLastname());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: muss mDatabaseRef hier gesetzt werden?
        //TODO: kann man auf einen Schlag alle Listener entfernen?
        mDatabaseRef = mDatabaseRootReference.child("interviews");
        mDatabaseRef.removeEventListener(mValueEventListener);
    }
}