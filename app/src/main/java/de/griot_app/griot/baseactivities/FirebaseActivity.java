package de.griot_app.griot.baseactivities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalCommentData;
import de.griot_app.griot.dataclasses.LocalGroupData;
import de.griot_app.griot.dataclasses.LocalGuestData;
import de.griot_app.griot.dataclasses.LocalInterviewData;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalUserData;
import de.griot_app.griot.dataclasses.UserData;

/**
 *  Abstract base activity for all griot-app-activities.
 *  Provides Firebase-Authentification, Firebase-DatabaseReferences and Firebase-StorageReferences.
 *  mValueEventListener and mChildEventListener have to be instantiated in subclasses, if needed
 */
public abstract class FirebaseActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseUser mUser;
    protected String mUserID;
    protected LocalUserData mLocalUserData;
    protected FirebaseDatabase mDatabase;
    protected DatabaseReference mDatabaseRootReference;
    protected DatabaseReference mDatabaseRef;
    protected Query mQuery;
    protected FirebaseStorage mStorage;
    protected StorageReference mStorageRootReference;
    protected StorageReference mStorageRef;
    protected ValueEventListener mValueEventListener;
    protected ChildEventListener mChildEventListener;

    /*
    //procected LocalData mLocalData;
    protected HashMap<String, LocalUserData> mLocalUserData;
    protected HashMap<String, LocalGuestData> mLocalGuestData;
    protected HashMap<String, LocalGroupData> mLocalGroupData;
    protected HashMap<String, LocalInterviewData> mLocalInterviewData;
    protected HashMap<String, LocalInterviewQuestionData> mLocalInterviewQuestionData;
    protected HashMap<String, LocalCommentData> mLocalCommentData;
*/

    /**
     * Abstract method, which returns the TAG of the extending subclass.
     * This method can be used, when the TAG of the concrete subclass is needed.
     * Note, that GriotBaseActivity itself doesn't provide a TAG field.
     * @return  TAG of the extending subclass
     */
    protected abstract String getSubClassTAG();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(getSubClassTAG(), "onAuthStateChanged: singed in: " + user.getUid());
                } else {
                    Log.d(getSubClassTAG(), "onAuthStateChanged: signed out: ");
                }
            }
        };

        //TODO: verschieben an sichere Position (Zuweisung nur gültig bei angemeldetem User
        mUser = mAuth.getCurrentUser();
        //mUserID = mUser.getUid();
        //TODO

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRootReference = mDatabase.getReference();

        mStorage = FirebaseStorage.getInstance();
        mStorageRootReference = mStorage.getReference();

        /*
        //mLocalData = new LocalData();
        mLocalUserData = new HashMap<>();
        mLocalGuestData = new HashMap<>();
        mLocalGroupData = new HashMap<>();
        mLocalInterviewData = new HashMap<>();
        mLocalInterviewQuestionData = new HashMap<>();
        mLocalCommentData = new HashMap<>();
        */
    }

    /*
    public HashMap<String, LocalUserData> getUserData() { return mLocalUserData; }
    public HashMap<String, LocalGuestData> getGuestData() { return mLocalGuestData; }
    public HashMap<String, LocalGroupData> getGroupData() { return mLocalGroupData; }
    public HashMap<String, LocalInterviewData> getInterviewData() { return mLocalInterviewData; }
    public HashMap<String, LocalInterviewQuestionData> getInterviewQuestionData() { return mLocalInterviewQuestionData; }
    public HashMap<String, LocalCommentData> getCommentData() { return mLocalCommentData; }
*/

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    protected void loadUserInformation() {
        mUser = mAuth.getCurrentUser();
        mUserID = mUser.getUid();
        Query query = mDatabaseRootReference.child("users").orderByKey().equalTo(mUserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(getSubClassTAG(), "getValueEventListener: onDataChange:");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mLocalUserData = ds.getValue(LocalUserData.class);
                    mLocalUserData.setCategory(getString(R.string.text_yourself));
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
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(getSubClassTAG(), "Error downloading user profile image file");
                            mLocalUserData.setPictureLocalURI("");
                        }
                    });
                } catch (Exception e) {}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}


