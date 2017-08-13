package de.griot_app.griot;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.griot_app.griot.adapter.LocalPersonDataAdapter;
import de.griot_app.griot.dataclasses.LocalGuestData;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalUserData;

/**
 * Loads data lists from Firebase from several locations in several single querys,
 * adds the data to several query-specific list-containers,
 * adds a headLine identical to the database-node to the first entry of each container,
 * puts the single lists together to a combined list
 * and set the corespondend adapter to the ListView.
 */
public class CombinedPersonListCreator {

    private static final String TAG = CombinedPersonListCreator.class.getSimpleName();

    private Activity mContext;

    private LocalUserData mLocalUserData;

    private ListView mCombinedListView;
    private ArrayList<LocalPersonData> mCombinedList;

    private ArrayList<ArrayList<LocalPersonData>> mSingleLists;
    private ArrayList<Query> mQuerys;

    private LocalPersonDataAdapter mAdapter;

    private boolean addGuestAdded = false;


    public CombinedPersonListCreator(Activity context, LocalUserData localUserData, ListView combinedlistView) {

        mContext = context;

        mLocalUserData = localUserData;

        mCombinedListView = combinedlistView;
        mCombinedList = new ArrayList<>();

        mSingleLists = new ArrayList<>();
        mQuerys = new ArrayList<>();
    }

    /**
     * Adds a database query from where the data will be read and puts it in a list container.
     * A correspondent single data list is created and stored in a list container for data lists.
     * The methods returns a this-reference, so that several calls can be concaternated
     * @param query A database query, from which the data will be obtained.
     * @return A this-reference
     */
    public CombinedPersonListCreator add(Query query) {
        Log.d(TAG, "add:");

        mQuerys.add(query);
        mSingleLists.add(new ArrayList<LocalPersonData>());

        return this;
    }


    /**
     * Adds a ValueEventListener for a single valueEvent (reads only once) to each database query in the query list container.
     * The listener will be returned from getValueEventListener()
     */
    public void loadData() {
        Log.d(TAG, "loadData:");

        for ( int i=0 ; i<mQuerys.size() ; i++ ) {
            mQuerys.get(i).addListenerForSingleValueEvent(getValueEventListener(mSingleLists.get(i), mQuerys.get(i)));
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
    private ValueEventListener getValueEventListener(final ArrayList<LocalPersonData> list, final Query query) {
        Log.d(TAG, "getValueEventListener:");

        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "getValueEventListener: onDataChange:");
                list.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    LocalPersonData localPersonData = ds.getValue(LocalPersonData.class);
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
                combineList();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    /**
     * Puts the created single data lists together to a combined data list and set the adapter for
     * that list to the correspondent ListView
     */
    private void combineList() {
        Log.d(TAG, "combineList:");

        mCombinedList.clear();
        mCombinedList.add(mLocalUserData);
        for ( int i=0 ; i<mQuerys.size() ; i++ ) {
            // wenn es sich um die Liste der Gäste handelt, muss die headline vom ersten Item entfernt
            // werden und ein weiteres Item eingefügt werden, um einen Nutzer hinzuzufügen
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
        }
        mAdapter = new LocalPersonDataAdapter(mContext, mCombinedList);
        mCombinedListView.setAdapter(mAdapter);
    }
}