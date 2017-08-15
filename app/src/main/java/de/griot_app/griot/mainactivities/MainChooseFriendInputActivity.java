package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import de.griot_app.griot.ChooseTopicInputActivity;
import de.griot_app.griot.CombinedPersonListCreator;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.GuestData;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalUserData;
import de.griot_app.griot.dataclasses.UserData;

public class MainChooseFriendInputActivity extends GriotBaseInputActivity {

    private static final String TAG = MainChooseFriendInputActivity.class.getSimpleName();

    private ListView mListViewPersons;

    private CombinedPersonListCreator mCombinedListCreator;

    private Query mQueryYou;
    //private Query mQueryGuests;
    //private Query mQueryFriends;

    private LocalPersonData mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
        mButtonRight.setEnabled(false);
        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));

        mListViewPersons = (ListView) findViewById(R.id.listView_main_input_choose_friend);
        mListViewPersons.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mListViewPersons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectedItem==null) {
                    mSelectedItem = mCombinedListCreator.getAdapter().getItem(position);
                    mSelectedItem.setSelected(true);
                    mButtonRight.setEnabled(true);
                    mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                } else {
                    if (mSelectedItem==mCombinedListCreator.getAdapter().getItem(position)) {
                        mSelectedItem.setSelected(false);
                        mSelectedItem = null;
                        mButtonRight.setEnabled(false);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                    } else {
                        mSelectedItem.setSelected(false);
                        mSelectedItem = mCombinedListCreator.getAdapter().getItem(position);
                        mSelectedItem.setSelected(true);
                        mButtonRight.setEnabled(true);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));

                    }
                }
                mCombinedListCreator.getAdapter().notifyDataSetChanged();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: Auslagern, ober überarbeiten oder vereinheitlichen

        mUser = mAuth.getCurrentUser();
        mUserID = mUser.getUid();

        mQueryYou = mDatabaseRootReference.child("users").orderByKey().equalTo(mUserID);
        //mQueryGuests = mDatabaseRootReference.child("guests");   //TODO genauer spezifizieren
        //mQueryFriends = mDatabaseRootReference.child("users");  //TODO genauer spezifizieren

        mQueryYou.addListenerForSingleValueEvent(new ValueEventListener() {
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

                mCombinedListCreator = new CombinedPersonListCreator(MainChooseFriendInputActivity.this, mLocalUserData, mListViewPersons);
                //mCombinedListCreator.add(mQueryGuests).add(mQueryFriends);
                mCombinedListCreator.loadData();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_input_choose_friend;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }


    @Override
    protected void buttonLeftPressed() {
        Log.d(TAG, "buttonLeftPressed: ");

        finish();
    }

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        finish();
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        Log.d(TAG, "mAgetSelectedItem: " + mSelectedItem);
        Intent intent = new Intent(this, ChooseTopicInputActivity.class);
        if (mSelectedItem != null) {
            intent.putExtra("narratorID", mSelectedItem.getContactID());
            intent.putExtra("narratorName", mSelectedItem.getFirstname() + (mSelectedItem.getLastname() == null ? "" : " " + mSelectedItem.getLastname()));
            intent.putExtra("narratorPictureURL", mSelectedItem.getPictureURL());
            intent.putExtra("narratorIsUser", mSelectedItem.getIsUser());
            //intent.putExtra("narratorPictureLocalURI", mSelectedItem.getPictureLocalURI());
        }
        startActivity(intent);
    }

}
