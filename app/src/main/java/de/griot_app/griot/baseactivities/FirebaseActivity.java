package de.griot_app.griot.baseactivities;

import android.content.Intent;
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

import de.griot_app.griot.FirebaseUtils;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.UserData;
import de.griot_app.griot.startactivities.LoginActivity;

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
    protected UserData mUserData;
    protected UserData mOwnUserData;
    protected FirebaseDatabase mDatabase;
    protected DatabaseReference mDatabaseRootReference;
    protected DatabaseReference mDatabaseRef;
    protected Query mQuery;
    protected FirebaseStorage mStorage;
    protected StorageReference mStorageRootReference;
    protected StorageReference mStorageRef;
    protected ValueEventListener mValueEventListener;
    protected ChildEventListener mChildEventListener;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseUtils.getAuth();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = mAuth.getCurrentUser();
                if (mUser != null) {
                    Log.d(getSubClassTAG(), "onAuthStateChanged: singed in: " + mUser.getUid());
                    mUserID = mUser.getUid();
                    // if the user is signed in, obtain user information
                    loadUserInformation();
                } else {
                    Log.d(getSubClassTAG(), "onAuthStateChanged: signed out: ");
                    // if this is not LoginActiviy and if no user is signed in, start LoginActivity and finish this one
                    mUserID = null;
                    if (!(FirebaseActivity.this instanceof LoginActivity)) {
                        Intent intent = new Intent(FirebaseActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        };

        mDatabase = FirebaseUtils.getDatabase();
        mDatabaseRootReference = FirebaseUtils.getDatabaseRootReference();

        mStorage = FirebaseUtils.getStorage();
        mStorageRootReference = FirebaseUtils.getStorageRootReference();

/*
        Log.d(getSubClassTAG(), "" + (this instanceof MainOverviewActivity)); //true
        Log.d(getSubClassTAG(), "" + (this instanceof FirebaseActivity));    //true

        Log.d(getSubClassTAG(), "" + (this.getClass().equals(MainOverviewActivity.class))); // true
        Log.d(getSubClassTAG(), "" + (this.getClass().equals(FirebaseActivity.class))); // false
*/

    }

    //Set the AuthStateListener
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
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

        Query query = mDatabaseRootReference.child("users").orderByKey().equalTo(mUserID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(getSubClassTAG(), "getValueEventListener: onDataChange:");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mOwnUserData = ds.getValue(UserData.class);
                    mOwnUserData.setContactID(ds.getKey());
                    mOwnUserData.setCategory(getString(R.string.text_yourself));
                }
/*
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

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(getSubClassTAG(), "Error downloading user profile image file");
                            mOwnUserData.setPictureLocalURI("");

                            //Gets specified in subclasses
                            doOnStartAfterLoadingUserInformation();

                        }
                    });
                } catch (Exception e) {}
*/
                doOnStartAfterLoadingUserInformation();

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public UserData getOwnUserData() { return mOwnUserData; }

}


