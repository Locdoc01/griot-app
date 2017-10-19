package de.griot_app.griot.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalGuestData;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalUserData;

/**
 * Loads data lists from Firebase Database from several locations in several single querys,
 * adds the data to several query-specific ArrayList-containers,
 * adds a category headLine to the first entry of each ArrayList identical to the database-node,
 * puts the single lists together to a combined list
 * and set the correspondent adapter to the ListView.
 * The own user data gets stored as own category at the top of the list.
 * First Element of the guests category is a special item, that allows to add a new guest profile
 */
public class CombinedPersonListCreator {

    private static final String TAG = CombinedPersonListCreator.class.getSimpleName();

    //TODO: change "person" to "contact"

    /**
     * Determine the mode for the correspondent ListView.
     * PERSONS_OPTIONS_MODE : Creates a person contacts ListView and shows options button for every item, which, when clicked opens the options menu for the appropriate item.
     * PERSONS_CHOOSE_MODE : Creates a person contacts ListView and makes the items selectable. Only one item is selectable at a time. If an item got selected,
     * it shows a check-sign and the appropriate DataClass-Object stores the selected state
     * GROUPS_OPTIONS_MODE : Creates a group contacts ListView and shows options button for every item, which, when clicked opens the options menu for the appropriate item.
     * GROUPS_CHOOSE_MODE : Creates a group contacts ListView and makes the items selectable. Only one item is selectable at a time. If an item got selected,
     * it shows a check-sign and the appropriate DataClass-Object stores the selected state
     */
    public static final int PERSONS_OPTIONS_MODE = 0;
    public static final int PERSONS_CHOOSE_MODE = 1;
    public static final int GROUPS_OPTIONS_MODE = 2;
    public static final int GROUPS_CHOOSE_MODE = 3;

    //Stores the mode for the correspontent ListView.
    private int mMode = PERSONS_OPTIONS_MODE;

    private Activity mContext;

    //Own user information
    private LocalUserData mOwnUserData;

    //The combined ListView, that is shown on the screen
    private ListView mCombinedListView;

    //The combined data list
    private ArrayList<LocalPersonData> mCombinedList;

    //A list of single data lists, that are going to be combined
    private ArrayList<ArrayList<LocalPersonData>> mSingleLists;

    //Firebase classes
    private FirebaseStorage mStorage;
    private StorageReference mStorageRef;

    //List of Firebase querys
    private ArrayList<Query> mDatabaseQuerys;

    //Data-View-Adapter for the ListView
    private ArrayAdapter<LocalPersonData> mAdapter;

    //Necessary to prevent multiple additions of "add guest"-item
    private boolean mAddGuestAdded = false;

    //Stores the item id, when an item is selected
    private int mSelectedItemID;

    //Constructors

    /**
     * Constructor for ListView modes PERSON_OPTION_MODE & GROUP_OPTION_MODE.
     * @param context calling Activity
     * @param combinedlistView combined ListView, which holdes all data elements from the single ArrayLists from the single Database Queries
     */
    public CombinedPersonListCreator(Activity context, ListView combinedlistView) {
        this(context, -1, null, combinedlistView);
    }

    /**
     * Constructor for ListView modes PERSON_CHOOSE_MODE & GROUP_CHOOSE_MODE.
     * @param context Calling Activity
     * @param combinedlistView Combined ListView, which holdes all data elements from the single ArrayLists from the single Database Queries
     * @param selectedItemID The selected ListView item. If none item is selected, the value is -1
     * @param ownUserData LocalUserData object, which holdes the own user information, obtained from Firebase Database
     */
    public CombinedPersonListCreator(Activity context, int selectedItemID, LocalUserData ownUserData, ListView combinedlistView) {

        mContext = context;

        mOwnUserData = ownUserData;

        mCombinedListView = combinedlistView;
        mCombinedList = new ArrayList<>();

        mDatabaseQuerys = new ArrayList<>();
        mSingleLists = new ArrayList<>();

        mStorage = FirebaseStorage.getInstance();

        mSelectedItemID = selectedItemID;
    }


    /*
     * Adds a database query from where the data will be obtained and puts it in an ArrayList.
     * A correspondent single data list is created and stored in another ArrayList for data lists.
     * The methods returns a this-reference, so that several calls can be concaternated
     * @param query A database query, from which the data will be obtained.
     * @return A this-reference
     */

    public CombinedPersonListCreator add(Query query) {
        Log.d(TAG, "add:");

        mDatabaseQuerys.add(query);
        mSingleLists.add(new ArrayList<LocalPersonData>());

        return this;
    }


    /**
     * Returns the Data-View-Adapter for the ListView
     * @return mAdapter
     */
    public ArrayAdapter<LocalPersonData> getAdapter() { return mAdapter; }

    /**
     * Set the mode for the ListView. Posibile values are PERSONS_OPTIONS_MODE (0), PERSONS_CHOOSE_MODE (1), GROUPS_OPTIONS_MODE (2) and GROUPS_CHOOSE_MODE (3)
     * PERSONS_OPTIONS_MODE : Creates a person contacts ListView and shows options button for every item, which, when clicked opens the options menu for the appropriate item.
     * PERSONS_CHOOSE_MODE : Creates a person contacts ListView and makes the items selectable. Only one item is selectable at a time. If an item got selected,
     * it shows a check-sign and the appropriate DataClass-Object stores the selected state
     * GROUPS_OPTIONS_MODE : Creates a group contacts ListView and shows options button for every item, which, when clicked opens the options menu for the appropriate item.
     * GROUPS_CHOOSE_MODE : Creates a group contacts ListView and makes the items selectable. Only one item is selectable at a time. If an item got selected,
     * it shows a check-sign and the appropriate DataClass-Object stores the selected state
     * @param mode  Mode for the ListView
     */
    public void setMode(int mode) { mMode = ((mode>3) ? PERSONS_OPTIONS_MODE : mode); }

    /**
     * Adds a ValueEventListener for a single valueEvent (reads just once) to each Firebase database query in the query list.
     * The listener will be returned from getDatabaseValueEventListener()
     */
    public void loadData() {
        Log.d(TAG, "loadData:");

        for (int i = 0; i< mDatabaseQuerys.size() ; i++ ) {
            mDatabaseQuerys.get(i).addValueEventListener(getDatabaseValueEventListener(mSingleLists.get(i), mDatabaseQuerys.get(i)));
        }
    }

    /**
     * Creates a ValueEventListener which adds the obtained data lists from Firebase to an ArrayList,
     * sets a headline for the first entry in the list identical to the database-node,
     * where the data comes from and calls combineList()
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
                    //Ignore the item, if it represents the user itself
                    if ((mOwnUserData != null && !ds.getKey().equals(mOwnUserData.getContactID())) || mOwnUserData == null) { //TODO: make ownUserData also available, if called from the ContactManagmendActivity (constructors has to be changed)
                        LocalPersonData localPersonData = ds.getValue(LocalPersonData.class);
                        localPersonData.setContactID(ds.getKey());
                        list.add(localPersonData);
                    }
                }

                //If list is empty, a placeholder item for the category is added
                if (list.isEmpty()) {
                    list.add(new LocalPersonData());
                }

                switch (query.getRef().getKey()) {
                    case "guests":
                        list.get(0).setCategory(mContext.getString(R.string.text_your_guests));
                        break;
                    case "users":
                        list.get(0).setCategory(mContext.getString(R.string.text_your_friends));
                        break;
                }

                combineList();      // TODO: may be the wrong place, but ListView works as expected

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
        //add ownUserData, if available
        if (mOwnUserData!=null) {
            mCombinedList.add(mOwnUserData);
        }
        for (int i = 0; i< mDatabaseQuerys.size() ; i++ ) {
            if (!mAddGuestAdded && mSingleLists.get(i).get(0).getCategory().equals(mContext.getString(R.string.text_your_guests))) {
                LocalGuestData localGuestData = new LocalGuestData();
                localGuestData.setFirstname(mContext.getString(R.string.text_add_guest));
                localGuestData.setLastname("");
                localGuestData.setPictureLocalURI(mContext.getString(R.string.text_add_guest));
                if (mSingleLists.get(i).get(0).getFirstname() == null ) {
                    //Remove possible placeholder item, if list was empty
                    mSingleLists.get(i).remove(0);
                } else {
                    //Remove category from first proper guest item, if list was not empty
                    mSingleLists.get(i).get(0).setCategory(null);
                }
                mSingleLists.get(i).add(0, localGuestData);
                mSingleLists.get(i).get(0).setCategory(mContext.getString(R.string.text_your_guests));
                mAddGuestAdded = true;
            }
            mCombinedList.addAll(mSingleLists.get(i));
        }
        if(mSelectedItemID>=0 && !mSingleLists.get(0).isEmpty() && !mSingleLists.get(1).isEmpty()) {
            mCombinedList.get(mSelectedItemID).setSelected(true);
        }
        switch (mMode) {
            case PERSONS_CHOOSE_MODE:
                mAdapter = new LocalPersonDataChooseAdapter(mContext, mCombinedList);
                break;
            case PERSONS_OPTIONS_MODE:
                mAdapter = new LocalPersonDataOptionsAdapter(mContext, mCombinedList);
                break;
            case GROUPS_CHOOSE_MODE:
                // TODO
                break;
            case GROUPS_OPTIONS_MODE:
                // TODO
                break;
        }
        mCombinedListView.setAdapter(mAdapter);
    }
}