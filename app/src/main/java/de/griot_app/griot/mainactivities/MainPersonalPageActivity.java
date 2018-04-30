package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
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

import java.io.File;
import java.util.ArrayList;

import de.griot_app.griot.contacts_profiles.ContactManagmentActivity;
import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.contacts_profiles.OwnProfileInputActivity;
import de.griot_app.griot.adapters.LocalInterviewDataAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalInterviewData;
import de.griot_app.griot.views.ProfileImageView;

/**
 * This activity shows the personal page of the user. It provides the following functionalities:
 * - profile image and name of the user, which can be clicked an lead to the users profile
 * - A button to options about the user
 * - A button to the contact managment page
 * - A button to the questionmail
 * - A button to create a new guest profile
 * - An overview about all interviews, where the user was either interviewer or narrator
 */
//TODO: Notification icons for new question mail incommings or notifications about important social media events
public class MainPersonalPageActivity extends GriotBaseActivity implements View.OnTouchListener {

    private static final String TAG = MainPersonalPageActivity.class.getSimpleName();

    //Views
    private ImageView mButtonAddGuest;
    private ProfileImageView mPivUser;
    private TextView mTextViewUser;
    private ImageView mButtonOptions;
    private ImageView mImageViewFriendsGroups;
    private TextView mTextViewFriendsGroups;
    private FrameLayout mButtonFriendsGroups;
    private ImageView mImageViewQuestionmail;
    private TextView mTextViewQuestionmail;
    private FrameLayout mButtonQuestionmail;
    private TextView mTextViewMedias;

    private int mVideoCount;
    private int mAudioCount;

    // ListView, that holds the interview items
    private RecyclerView mRecyclerViewInterviews;

    //ArrayList containing the data of interviews
    private ArrayList<LocalInterviewData> mListLocalInterviewData;

    //Data-View-Adapter for the ListView
    private LocalInterviewDataAdapter mLocalInterviewDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Hide the app bar and the floating action button for questionmail
        mAppBar.setVisibility(View.GONE);
        super.mButtonQuestionmail.setVisibility(View.GONE);

        mTitle.setVisibility(View.GONE);
        mLineAppBar.setVisibility(View.GONE);
        mButtonProfile.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        //Get references to layout objects
        mButtonAddGuest = (ImageView) findViewById(R.id.button_add_guest);
        mPivUser = (ProfileImageView) findViewById(R.id.piv_user);
        mTextViewUser = (TextView) findViewById(R.id.textView_user);
        mButtonOptions = (ImageView) findViewById(R.id.button_options);
        mImageViewFriendsGroups = (ImageView) findViewById(R.id.imageView_friends_groups);
        mTextViewFriendsGroups = (TextView) findViewById(R.id.textView_friends_groups);
        mButtonFriendsGroups = (FrameLayout) findViewById(R.id.button_friends_groups);
        mImageViewQuestionmail = (ImageView) findViewById(R.id.imageView_questionmail);
        mTextViewQuestionmail = (TextView) findViewById(R.id.textView_questionmail);
        mButtonQuestionmail = (FrameLayout) findViewById(R.id.button_questionmail);
        mTextViewMedias = (TextView) findViewById(R.id.textView_medias);

        mButtonAddGuest.setOnTouchListener(this);
        mPivUser.setOnTouchListener(this);
        mTextViewUser.setOnTouchListener(this);
        mButtonOptions.setOnTouchListener(this);
        mButtonFriendsGroups.setOnTouchListener(this);
        mButtonQuestionmail.setOnTouchListener(this);

        mListLocalInterviewData = new ArrayList<>();

        mRecyclerViewInterviews = (RecyclerView) findViewById(R.id.recyclerView_main_profile_overview);


        //Set the ValieEventListener to obtains all necessary data from Firebase
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListLocalInterviewData.clear();
                //Obtain interview data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final LocalInterviewData localInterviewData = ds.getValue(LocalInterviewData.class);

                    //Count the medias
                    if (localInterviewData.getMedium().equals("video")) {
                        mVideoCount++;
                    } else if (localInterviewData.getMedium().equals("audio")) {
                        mAudioCount++;
                    }
                    mListLocalInterviewData.add(localInterviewData);
                }
                //Set the adapter
                mLocalInterviewDataAdapter = new LocalInterviewDataAdapter(MainPersonalPageActivity.this, mListLocalInterviewData);
                mRecyclerViewInterviews.setLayoutManager(new LinearLayoutManager(MainPersonalPageActivity.this));
                mRecyclerViewInterviews.setAdapter(mLocalInterviewDataAdapter);

                //Update the textView_medias
                mTextViewMedias.setText("" + (mVideoCount==0 ? getString(R.string.text_none) : mVideoCount) + " "
                        + (mVideoCount==1 ? getString(R.string.text_video) : getString(R.string.text_videos)) + " / "
                        + (mAudioCount==0 ? getString(R.string.text_none) : mAudioCount) + " "
                        + (mAudioCount==1 ? getString(R.string.text_audio) : getString(R.string.text_audios)));

                //Create temporary files to store the pictures from Firebase Storage
                for ( int i=0 ; i<mListLocalInterviewData.size() ; i++ ) {
                    final int index = i;
                    File fileMediaCover = null;
            //        File fileInterviewer = null;
            //        File fileNarrator = null;
                    try {
                        fileMediaCover = File.createTempFile("mediaCover" + i + "_", ".jpg");
            //            fileInterviewer = File.createTempFile("interviewer" + i + "_", ".jpg");
            //            fileNarrator = File.createTempFile("narrator" + i + "_", ".jpg");
                    } catch (Exception e) {
                    }
                    final String pathMediaCover = fileMediaCover.getPath();
            //        final String pathInterviewer = fileInterviewer.getPath();
            //        final String pathNarrator = fileNarrator.getPath();

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
/*
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
                    */
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabaseRef = mDatabaseRootReference.child("interviews");
        mDatabaseRef.addValueEventListener(mValueEventListener);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_personal_page;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    //Called on super.onStart after own user information was obtained from Firebase
    @Override
    protected void doOnStartAfterLoadingUserInformation() {
        mTextViewUser.setText(mOwnUserData.getFirstname() + " " + mOwnUserData.getLastname());
        mPivUser.loadImageFromSource(mOwnUserData.getPictureURL());
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.button_add_guest:
                        mButtonAddGuest.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.piv_user:
                        return true;
                    case R.id.textView_user:
                        mTextViewUser.setTextColor(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.button_options:
                        mButtonOptions.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.button_friends_groups:
                        mImageViewFriendsGroups.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotBlue));
                        mTextViewFriendsGroups.setTextColor(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotBlue));
                        return true;
                    case R.id.button_questionmail:
                        mImageViewQuestionmail.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotBlue));
                        mTextViewQuestionmail.setTextColor(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotBlue));
                        return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                switch (v.getId()) {
                    case R.id.button_add_guest:
                        Log.d(TAG, "add guest clicked: ");
                        mButtonAddGuest.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotDarkgrey));
                        Intent intent = new Intent(MainPersonalPageActivity.this, GuestProfileInputActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.piv_user:
                    case R.id.textView_user:
                        Log.d(TAG, "own user profile image or name clicked: ");
                        mTextViewUser.setTextColor(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotDarkgrey));
                        startActivity(new Intent(MainPersonalPageActivity.this, OwnProfileInputActivity.class));
                        return true;
                    case R.id.button_options:
                        Log.d(TAG, "options clicked: ");
                        mButtonOptions.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotDarkgrey));
                        Toast.makeText(MainPersonalPageActivity.this, "Optionen anzeigen", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.button_friends_groups:
                        Log.d(TAG, "contactmanagment clicked: ");
                        mImageViewFriendsGroups.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotDarkgrey));
                        mTextViewFriendsGroups.setTextColor(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotDarkgrey));
                        startActivity(new Intent(MainPersonalPageActivity.this, ContactManagmentActivity.class));
                        finish();
                        return true;
                    case R.id.button_questionmail:
                        Log.d(TAG, "questionmail clicked: ");
                        mImageViewQuestionmail.setColorFilter(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotDarkgrey));
                        mTextViewQuestionmail.setTextColor(ContextCompat.getColor(MainPersonalPageActivity.this, R.color.colorGriotDarkgrey));
                        startActivity(new Intent(this, MainQuestionmailActivity.class));
                        finish();
                        return true;
                }
                return false;
        }
        return false;
    }

}