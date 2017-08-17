package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;

import de.griot_app.griot.ChooseMediumInputActivity;
import de.griot_app.griot.ChooseTopicInputActivity;
import de.griot_app.griot.CombinedPersonListCreator;
import de.griot_app.griot.GuestProfileInputActivity;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalUserData;


/**
 * This Activity allows the user to choose a person from his contact list for an interview. It's the first step of the "prepare-interview"-dialog.
 */
public class MainChooseFriendInputActivity extends GriotBaseInputActivity {

    private static final String TAG = MainChooseFriendInputActivity.class.getSimpleName();

    //holds the narrator item id, if a narrator was selected as narrator. It's used on this activity for managing the selection. It's also used as intent-data
    private int narratorSelectedItemID = -1;

    //intent-data
    private int topicSelectedItemID;
    private int topicKey;
    private String topic;

    //Views
    TextView mTextViewTopic;
    ImageView mButtonCancelTopic;
    ImageView mLineTopic;

    //ListView, that holds the contact list
    private ListView mListViewPersons;

    //Creates the PersonlistView as a combination of own user data, guest list data, friend list data and approriate headings
    private CombinedPersonListCreator mCombinedListCreator;

    //firebase-query, to get own user data
    private Query mQueryYou;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //stores narrator item id, if selected so far (that can be the case, if this activity got startet by another part of "prepare interview"-dialog)
        narratorSelectedItemID = getIntent().getIntExtra("narratorSelectedItemID", -1);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);

        //Next-Button is disabled at start, if no person was selected as narrator so far
        if (narratorSelectedItemID<0) {
            mButtonRight.setEnabled(false);
            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
        }

        // get intent-data, in case this activity was started from ChooseTopicInputActivity and a topic was selected
        topicSelectedItemID = getIntent().getIntExtra("topicSelectedItemID", -1);
        topicKey = getIntent().getIntExtra("topicKey", -1);
        topic = getIntent().getStringExtra("topic");

        // if a topic was selected so far, the selection will be shown and can be canceled
        if (topicSelectedItemID>=0) {
            mTextViewTopic = (TextView) findViewById(R.id.textView_topic);
            mButtonCancelTopic = (ImageView) findViewById(R.id.button_cancel_topic);
            mLineTopic = (ImageView) findViewById(R.id.line_topic);

            mTextViewTopic.setVisibility(View.VISIBLE);
            mButtonCancelTopic.setVisibility(View.VISIBLE);
            mLineTopic.setVisibility(View.VISIBLE);

            // show topic selection
            mTextViewTopic.setText(getString(R.string.text_choosed_topic) + ":  " + topic);

            //cancel-topic-button (if button is pressed, the selection is canceled and got hidden
            mButtonCancelTopic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    topicSelectedItemID = -1;

                    mTextViewTopic.setVisibility(View.GONE);
                    mButtonCancelTopic.setVisibility(View.GONE);
                    mLineTopic.setVisibility(View.GONE);
                }
            });
        }

        mListViewPersons = (ListView) findViewById(R.id.listView_main_input_choose_friend);
        mListViewPersons.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        // manages the narrator selection along with the next-button functionality. The selection gets stored in the appropriate LocalPersonData-object
        mListViewPersons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCombinedListCreator.getAdapter().getItem(position).getFirstname().equals(getString(R.string.text_add_guest))) {
                    Intent intent = new Intent(MainChooseFriendInputActivity.this, GuestProfileInputActivity.class);
                    startActivity(intent);
                } else {
                    if (narratorSelectedItemID < 0) {
                        narratorSelectedItemID = position;
                        mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(true);
                        mButtonRight.setEnabled(true);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                    } else {
                        if (narratorSelectedItemID == position) {
                            mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(false);
                            narratorSelectedItemID = -1;
                            mButtonRight.setEnabled(false);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                        } else {
                            mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(false);
                            narratorSelectedItemID = position;
                            mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(true);
                            mButtonRight.setEnabled(true);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));

                        }
                    }
                    mCombinedListCreator.getAdapter().notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //TODO: Auslagern, ober überarbeiten oder vereinheitlichen

        // Obtain own user data from Firebase
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
                    mLocalUserData.setContactID(ds.getKey());
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

                //create the Combined ListView
                mCombinedListCreator = new CombinedPersonListCreator(MainChooseFriendInputActivity.this, narratorSelectedItemID, mLocalUserData, mListViewPersons);
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

        //Navigation to the next page of the "prepare interview"-dialog
        //All relevant data for the interview or the dialog-pages get sent to the next page.
        Intent intent = new Intent();
        LocalPersonData item = mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID);
        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        intent.putExtra("narratorID", item.getContactID());
        intent.putExtra("narratorName", item.getFirstname() + (item.getLastname() == null ? "" : " " + item.getLastname()));
        intent.putExtra("narratorPictureURL", item.getPictureURL());
        intent.putExtra("narratorIsUser", item.getIsUser());

        // if a topic was already selected, ChooseTopicInputActivity will be skipped
        if (topicSelectedItemID >= 0) {
            intent.putExtra("topicSelectedItemID", topicSelectedItemID);
            intent.putExtra("topicKey", topicKey);
            intent.putExtra("topic", topic);
            intent.setClass(MainChooseFriendInputActivity.this, ChooseMediumInputActivity.class);
        } else {
            intent.setClass(MainChooseFriendInputActivity.this, ChooseTopicInputActivity.class);
        }
        startActivity(intent);
        finish();
    }

}
