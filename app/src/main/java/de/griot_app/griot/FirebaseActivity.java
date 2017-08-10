package de.griot_app.griot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    protected FirebaseDatabase mDatabase;
    protected DatabaseReference mDatabaseRootReference;
    protected DatabaseReference mDatabaseRef;
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
    }

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
}


