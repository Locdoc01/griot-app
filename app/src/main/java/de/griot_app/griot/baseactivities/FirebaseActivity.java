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

import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalUserData;

/**
 *  Abstract base activity for all activities in griot-app.
 *
 *  Provides Firebase-Authentification, Firebase-DatabaseReferences, Firebase-StorageReferences
 *  and loads own user information.
 *
 *  If some actions in subclasses have to be done AFTER own user information was obtained from Firebase, they can be put into
 *  doOnStartAfterLoadingUserInformation(), which has to be overwritten in subclasses. This method gets called
 *  in FirebaseActivity.onStart(), after own user information was obtained.
 *
 *  mValueEventListener and mChildEventListener have to be instantiated in subclasses, if needed.
 */
public abstract class FirebaseActivity extends AppCompatActivity {

    protected FirebaseAuth mAuth;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseUser mUser;
    protected String mUserID;
    protected LocalUserData mLocalUserData;
    protected LocalUserData mOwnUserData;
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


    /**
     * Abstract method, which is called in onStart(), AFTER own user information was obtained from Firebase.
     * This method has to be overwritten in subclasses an can be used to run code after own user information was obtained.
     */
    protected abstract void doOnStartAfterLoadingUserInformation();


    //Get Firebase references and creates an AuthStateListener
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

    //Set the AuthStateListener
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        // if the user is signed in, obtain user information
        if (mAuth.getCurrentUser() != null) {
            loadUserInformation();
        }
    }

    //Remove the AuthStateListener
    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    //Obtain own user information
    protected void loadUserInformation() {
        Log.d(getSubClassTAG(), "loadUserInformation: ");

        mUser = mAuth.getCurrentUser();
        mUserID = mUser.getUid();

        Query query = mDatabaseRootReference.child("users").orderByKey().equalTo(mUserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(getSubClassTAG(), "getValueEventListener: onDataChange:");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mOwnUserData = ds.getValue(LocalUserData.class);
                    mOwnUserData.setContactID(ds.getKey());
                    mOwnUserData.setCategory(getString(R.string.text_yourself));
                }

                File file = null;
                try {
                    file = File.createTempFile("profile_image" + "_", ".jpg");
                } catch (Exception e) {
                }
                final String path = file.getPath();

                try {
                    mStorageRef = mStorage.getReferenceFromUrl(mOwnUserData.getPictureURL());
                    mStorageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            mOwnUserData.setPictureLocalURI(path);

                            //Gets specified in subclasses
                            doOnStartAfterLoadingUserInformation();
                            //TODO: Falls Fehler auftreten, nach "hier" verschieben

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(getSubClassTAG(), "Error downloading user profile image file");
                            mOwnUserData.setPictureLocalURI("");

                            //Gets specified in subclasses
                            doOnStartAfterLoadingUserInformation();
                            //TODO: Falls Fehler auftreten, nach "hier" verschieben

                        }
                    });
                } catch (Exception e) {}

                //hier
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //TODO
            }
        });
    }

}


