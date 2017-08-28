package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.Query;

import de.griot_app.griot.perform_interview.ChooseMediumInputActivity;
import de.griot_app.griot.perform_interview.ChooseTopicInputActivity;
import de.griot_app.griot.adapters.CombinedPersonListCreator;
import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.dataclasses.LocalPersonData;


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

    //firebase-queries for the CombinedListCreator
    private Query mQueryYou;
    private Query mQueryGuests;
    private Query mQueryFriends;

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
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_input_choose_friend;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }


    @Override
    protected void doOnStartAfterLoadingUserInformation() {
        mQueryGuests = mDatabaseRootReference.child("guests");   //TODO genauer spezifizieren
        mQueryFriends = mDatabaseRootReference.child("users");  //TODO genauer spezifizieren
        //create the Combined ListView
        mCombinedListCreator = new CombinedPersonListCreator(MainChooseFriendInputActivity.this, narratorSelectedItemID, mOwnUserData, mListViewPersons);
        mCombinedListCreator.setMode(CombinedPersonListCreator.PERSONS_CHOOSE_MODE);
        mCombinedListCreator.add(mQueryGuests);
        mCombinedListCreator.add(mQueryFriends);
        mCombinedListCreator.loadData();
    }


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

        item = mCombinedListCreator.getAdapter().getItem(0);
        intent.putExtra("interviewerID", mUserID);
        intent.putExtra("interviewerName", item.getFirstname() + (item.getLastname() == null ? "" : " " + item.getLastname()));
        intent.putExtra("interviewerPictureURL", item.getPictureURL());

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
