package de.griot_app.griot;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import de.griot_app.griot.adapter.LocalPersonDataAdapter;
import de.griot_app.griot.dataclasses.LocalGuestData;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalUserData;

/**
 * Loads data lists from Firebase from several locations in several single querys,
 * adds the data to several query-specific list-containers,
 * adds a category headLine identical to the database-node to the first entry of each container,
 * puts the single lists together to a combined list
 * and set the corespondend adapter to the ListView.
 * The own user data gets stored as own category at the top of the list.
 * First Element of the guest category is a special item, that allows to add a new guest profile
 */
public class CombinedPersonListCreator {

    private static final String TAG = CombinedPersonListCreator.class.getSimpleName();

    private Activity mContext;

    //own user data
    private LocalUserData mLocalUserData;

    //the combined ListView, that is shown on the screen
    private ListView mCombinedListView;

    //the combined data list
    private ArrayList<LocalPersonData> mCombinedList;

    // a list of single data lists, that are going to be combined
    private ArrayList<ArrayList<LocalPersonData>> mSingleLists;

    // Firebase classes
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    //list of Firebase querys
    private ArrayList<Query> mDatabaseQuerys;
    //list of Firebase storage references
    private ArrayList<StorageReference> mStorageReferences;

    //Data-View-Adapter for the ListView
    private LocalPersonDataAdapter mAdapter;

    //necessary to prevent multiple additions of "add guest"-item
    private boolean addGuestAdded = false;

    // stores the item id, if an item was selected
    private int mSelectedItemID;


    //constructor
    public CombinedPersonListCreator(Activity context, int selectedItemID, LocalUserData localUserData, ListView combinedlistView) {

        mContext = context;

        mLocalUserData = localUserData;

        mCombinedListView = combinedlistView;
        mCombinedList = new ArrayList<>();

        mDatabaseQuerys = new ArrayList<>();
        mSingleLists = new ArrayList<>();
        mStorageReferences = new ArrayList<>();

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();

        mDatabaseQuerys.add(mDatabase.getReference().child("guests"));      //TODO genauer spezifizieren
        mSingleLists.add(new ArrayList<LocalPersonData>());
        //mStorageReferences.add(mStorage.getReference().child("guests"));

        mDatabaseQuerys.add(mDatabase.getReference().child("users"));     //TODO genauer spezifizieren
        mSingleLists.add(new ArrayList<LocalPersonData>());
        //mStorageReferences.add(mStorage.getReference().child("users"));

        mSelectedItemID = selectedItemID;
    }


    /*
     * Adds a database query from where the data will be read and puts it in a list container.
     * A correspondent single data list is created and stored in a list container for data lists.
     * The methods returns a this-reference, so that several calls can be concaternated
     * @param query A database query, from which the data will be obtained.
     * @return A this-reference
     */
    /*
    public CombinedPersonListCreator add(Query query) {
        Log.d(TAG, "add:");

        mDatabaseQuerys.add(query);
        mSingleLists.add(new ArrayList<LocalPersonData>());

        return this;
    }
*/

    /**
     * Returns the Data-View-Adapter for the ListView
     * @return mAdapter
     */
    public LocalPersonDataAdapter getAdapter() { return mAdapter; }


    /**
     * Adds a ValueEventListener for a single valueEvent (reads just once) to each database query in the query list.
     * The listener will be returned from getDatabaseValueEventListener()
     */
    public void loadData() {
        Log.d(TAG, "loadData:");

        for (int i = 0; i< mDatabaseQuerys.size() ; i++ ) {
            mDatabaseQuerys.get(i).addListenerForSingleValueEvent(getDatabaseValueEventListener(mSingleLists.get(i), mDatabaseQuerys.get(i)));
        }
    }

    /**
     * Creates a ValueEventListener which adds the obtained data lists from Firebase to a data list
     * container, sets a headline for the first entry in the list identical to the database-node,
     * where the data comes from and calls combineList
     * @param list The list container, where the database data will be stored
     * @param query The database query, from which the data will be obtained.
     * @return The created ValueEventListener
     */
    private ValueEventListener getDatabaseValueEventListener(final ArrayList<LocalPersonData> list, final Query query) {
        Log.d(TAG, "getDatabaseValueEventListener:");

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getDatabaseValueEventListener: onDataChange:");
                list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    LocalPersonData localPersonData = ds.getValue(LocalPersonData.class);
                    localPersonData.setContactID(ds.getKey());
                    list.add(localPersonData);
                }

                if (!list.isEmpty()) {
                    switch (query.getRef().getKey()) {
                        case "guests":
                            list.get(0).setCategory(mContext.getString(R.string.text_your_guests));
                            break;
                        case "users":
                            list.get(0).setCategory(mContext.getString(R.string.text_your_friends));
                            break;
                    }
                }

                combineList();      // TODO: evt. nicht an der richtigen Stelle. Liste funktioniert aber einwandfrei

                for ( int i=0 ; i<list.size() ; i++ ) {
                    final int index = i;
                    File file = null;
                    try {
                        file = File.createTempFile("user" + i + "_", ".jpg");
                    } catch (Exception e) {
                    }
                    final String path = file.getPath();

                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(list.get(index).getPictureURL());
                        mStorageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                list.get(index).setPictureLocalURI(path);
                                mAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error downloading user profile image file");
                                list.get(index).setPictureLocalURI("");
                                mAdapter.notifyDataSetChanged();
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

    /**
     * Puts the created single data lists together to a combined data list and set the adapter for
     * that list to the correspondent ListView.
     * Also adds the own user data as first item and a special "add guest"-item as first item in guest category
     */
    private void combineList() {
        Log.d(TAG, "combineList:");

        mCombinedList.clear();
        mCombinedList.add(mLocalUserData);
        for (int i = 0; i< mDatabaseQuerys.size() ; i++ ) {
            if (!mSingleLists.get(i).isEmpty()) {
                if (!addGuestAdded && mSingleLists.get(i).get(0).getCategory().equals(mContext.getString(R.string.text_your_guests))) {
                    LocalGuestData localGuestData = new LocalGuestData();
                    localGuestData.setFirstname(mContext.getString(R.string.text_add_guest));
                    localGuestData.setLastname("");
                    localGuestData.setPictureLocalURI(mContext.getString(R.string.text_add_guest));
                    mSingleLists.get(i).add(0, localGuestData);
                    mSingleLists.get(i).get(0).setCategory(mContext.getString(R.string.text_your_guests));
                    mSingleLists.get(i).get(1).setCategory(null);
                    addGuestAdded = true;
                }
            }
            mCombinedList.addAll(mSingleLists.get(i));


            if(mSelectedItemID>=0) {
                mCombinedList.get(mSelectedItemID).setSelected(true);
            }
        }
        mAdapter = new LocalPersonDataAdapter(mContext, mCombinedList);
        mCombinedListView.setAdapter(mAdapter);
    }
}