package de.griot_app.griot;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class FirebaseUtils {

    private static FirebaseAuth mAuth;
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mDatabaseRootReference;
    private static FirebaseStorage mStorage;
    private static StorageReference mStorageRootReference;

    public static FirebaseAuth getAuth() {
        if (mAuth == null) {
            mAuth = FirebaseAuth.getInstance();
        }
        return mAuth;
    }

    public static FirebaseDatabase getDatabase() {
        if (mDatabase == null) {
            mDatabase = FirebaseDatabase.getInstance();
        }
        return mDatabase;
    }

    public static DatabaseReference getDatabaseRootReference() {
        if (mDatabaseRootReference == null) {
            mDatabaseRootReference = getDatabase().getReference();
        }
        return mDatabaseRootReference;
    }

    public static FirebaseStorage getStorage() {
        if (mStorage == null) {
            mStorage = FirebaseStorage.getInstance();
        }
        return mStorage;
    }

    public static StorageReference getStorageRootReference() {
        if (mStorageRootReference == null) {
            mStorageRootReference = getStorage().getReference();
        }
        return mStorageRootReference;
    }
}
