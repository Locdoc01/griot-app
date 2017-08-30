package de.griot_app.griot.perform_interview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.griot_app.griot.R;
import de.griot_app.griot.adapters.TopicCatalogAdapter;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.LocalQuestionData;
import de.griot_app.griot.dataclasses.LocalTopicData;
import de.griot_app.griot.dataclasses.TopicCatalog;
import de.griot_app.griot.mainactivities.MainChooseFriendInputActivity;

/**
 * This Activity allows the user to choose a topic and its questions from the topic catalog for an interview.
 * It's the second step of the "prepare-interview"-dialog.
 * It also shows the selection of narrator and allow to cancel it,
 * which would lead the user back to the narrator selection page.
 * Apart from that this page provides the same functionality as the topic catalog page.
 */
public class ChooseTopicInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ChooseTopicInputActivity.class.getSimpleName();

    //holds the topic item id, if a topic was selected. It's used on this activity for managing the selection and as intent-data
    private int topicSelectedItemID = -1;

    //Intent-data
    private int narratorSelectedItemID;
    private String narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private Boolean narratorIsUser;

    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureURL;

    private String[] interviewQuestions;

    //Views
    private TextView mTextViewPerson;
    private ImageView mButtonCancelPerson;
    private ImageView mButtonAddTopic;

    //DataClass for topic catalog
    private TopicCatalog mTopicCatalog;

    //Expandable ListView, that holds the topic catalog
    private ExpandableListView mExpandListView;

    //Data-View-Adapter for TopicCatalog
    private TopicCatalogAdapter mAdapter;

    //Hold the individual TopicCatalog of the current user
    private ArrayList<Boolean> mUserTopicStates;
    private HashMap<String, Long> mUserQuestionStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Holds topic item id, if selected so far (that can be the case, if the user went back from a later part of "prepare interview"-dialog))
        topicSelectedItemID = getIntent().getIntExtra("topicSelectedItemID", -1);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);

        //Next-Button is disabled at start, until a topic gets selected
        if (topicSelectedItemID < 0) {
            mButtonRight.setEnabled(false);
            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
        }

        //Get intent-data
        narratorSelectedItemID = getIntent().getIntExtra("narratorSelectedItemID", -1);
        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureURL = getIntent().getStringExtra("narratorPictureURL");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", true);

        interviewerID = getIntent().getStringExtra("interviewerID");
        interviewerName = getIntent().getStringExtra("interviewerName");
        interviewerPictureURL = getIntent().getStringExtra("interviewerPictureURL");

        mTextViewPerson = (TextView) findViewById(R.id.textView_person);
        mButtonCancelPerson = (ImageView) findViewById(R.id.button_cancel_person);
        mButtonAddTopic = (ImageView) findViewById(R.id.button_add_topic);

        //Shows the selected narrator
        mTextViewPerson.setText(getString(R.string.text_choosed_person) + ":  " + narratorName);

        //Cancel-person-button (if button is pressed, the selection of narrator is canceled and the user got back to narrator selection page
        mButtonCancelPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseTopicInputActivity.this, MainChooseFriendInputActivity.class));
                finish();
            }
        });

        //Adds a topic to topic catalog
        mButtonAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChooseTopicInputActivity.this, "Thema hinzufügen", Toast.LENGTH_SHORT).show();
                //TODO implementieren
            }
        });

        mExpandListView = (ExpandableListView) findViewById(R.id.expandListView_input_choose_topic);


        //If a topic list item is clicked, the topic gets expanded or collapsed
        //TODO: comment out, if this is not wanted
        mExpandListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                    ((ImageView) v.findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.up, null));
                } else {
                    parent.expandGroup(groupPosition);
                    ((ImageView) v.findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.down, null));
                }
                return true;
            }
        });

        //If a question list item is clicked, the question gets activated or deactivated
        mExpandListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (childPosition != 0) {
                    //Get a reference to the appropriate DataClass-object for the clicked question list item
                    LocalQuestionData data = (LocalQuestionData) mAdapter.getChild(groupPosition, childPosition);
                    if (data.getQuestionState() == LocalQuestionData.QuestionState.OFF) {
                        //Change the toggle-button
                        ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_on, null));
                        //Change the question state in die DataClass-object
                        data.setQuestionState(LocalQuestionData.QuestionState.ON);
                    } else if (data.getQuestionState() == LocalQuestionData.QuestionState.ON) {
                        ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_off, null));
                        data.setQuestionState(LocalQuestionData.QuestionState.OFF);
                    }
                    return true;
                }
                return false;
            }
        });

        //Obtain the individual topic catalog of the current user from Firebase database
        //Obtain the individual topic states for standard topics
        mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardTopics");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserTopicStates = (ArrayList<Boolean>) dataSnapshot.getValue();

                //Obtain the indidual question states for standard questions
                mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardQuestions");
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUserQuestionStates = (HashMap<String, Long>) dataSnapshot.getValue();

                        //Obtains standard topics for topic catalog
                        mDatabaseRef = mDatabaseRootReference.child("standardTopics");
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mTopicCatalog = new TopicCatalog();

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    LocalTopicData localTopicData = ds.getValue(LocalTopicData.class);
                                    //Adds a new LocalTopicData for the current topic to the topic catalog
                                    localTopicData.setTopicState(mUserTopicStates.get(localTopicData.getTopicKey()));
                                    mTopicCatalog.getTopics().put(localTopicData.getTopicKey(), localTopicData);

                                    //Adds a head list item to the question list for the current topic in the topic catalog
                                    LocalQuestionData headItem = new LocalQuestionData();
                                    headItem.setQuestion(getString(R.string.title_questions));
                                    headItem.setQuestionState(LocalQuestionData.QuestionState.OFF);
                                    headItem.setTopicKey(localTopicData.getTopicKey());
                                    mTopicCatalog.getTopics().get(localTopicData.getTopicKey()).getQuestions().add(headItem);
                                }

                                //Obtain standard questions for topic catalog from Firebase
                                mDatabaseRef = mDatabaseRootReference.child("standardQuestions");
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            LocalQuestionData localQuestionData = ds.getValue(LocalQuestionData.class);
                                            localQuestionData.setQuestionKey(ds.getKey());
                                            localQuestionData.setQuestionState(mUserQuestionStates.get(ds.getKey()));
                                            mTopicCatalog.getTopics().get(localQuestionData.getTopicKey()).getQuestions().add(localQuestionData);
                                        }

                                        //TODO: ExtraTopics und ExtraQuestions laden

                                        if(topicSelectedItemID >=0) {
                                            mTopicCatalog.getTopics().get(topicSelectedItemID).setSelected(true);
                                        }

                                        //Set the adapter
                                        mAdapter = new TopicCatalogAdapter(ChooseTopicInputActivity.this, mTopicCatalog, true);
                                        mExpandListView.setAdapter(mAdapter);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "Error loading Questions");
                                        //TODO: implementieren, falls erforderlich
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "Error loading Topics");
                                //TODO: implementieren, falls erforderlich
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Error loading Topics");
                        //TODO: implementieren, falls erforderlich
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error loading Topics");
                //TODO: implementieren, falls erforderlich
            }
        });
    }


    /**
     * manages the topic selection along with the next-button functionality.
     * The selection gets stored in the appropriate LocalTopicData-object.
     * This method can be called from an OnclickListener defined in the adapter.
     * @param groupPosition Topic position in the ExpandableListView
     */
    public void buttonCheckClicked(int groupPosition) {
        if (topicSelectedItemID <0) {
            topicSelectedItemID = groupPosition;
            ((LocalTopicData) mAdapter.getGroup(topicSelectedItemID)).setSelected(true);
            mButtonRight.setEnabled(true);
            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
        } else {
            if (topicSelectedItemID ==groupPosition) {
                ((LocalTopicData) mAdapter.getGroup(topicSelectedItemID)).setSelected(false);
                topicSelectedItemID = -1;
                mButtonRight.setEnabled(false);
                mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
            } else {
                ((LocalTopicData) mAdapter.getGroup(topicSelectedItemID)).setSelected(false);
                topicSelectedItemID = groupPosition;
                ((LocalTopicData) mAdapter.getGroup(topicSelectedItemID)).setSelected(true);
                mButtonRight.setEnabled(true);
                mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
            }
        }
        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_choose_topic;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}


    @Override
    protected void buttonLeftPressed() {
        Log.d(TAG, "buttonLeftPressed: ");

        finish();
    }

    //Navigates back to previous page of "prepare interview"-dialog
    //selection of narrator gets sent to previous page by intent
    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        Intent intent = new Intent(ChooseTopicInputActivity.this, MainChooseFriendInputActivity.class);
        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        startActivity(intent);
        finish();
    }

    //Navigates to next page of "prepare interview"-dialog
    //All relevant data for the interview or the dialog-pages get sent to the next page by intent.
    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        //TODO save current question states to Firebase

        Intent intent = new Intent(this, ChooseMediumInputActivity.class);
        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        intent.putExtra("narratorID", narratorID);
        intent.putExtra("narratorName", narratorName);
        intent.putExtra("narratorPictureURL", narratorPictureURL);
        intent.putExtra("narratorIsUser", narratorIsUser);

        intent.putExtra("interviewerID", interviewerID);
        intent.putExtra("interviewerName", interviewerName);
        intent.putExtra("interviewerPictureURL", interviewerPictureURL);

        LocalTopicData item = (LocalTopicData) mAdapter.getGroup(topicSelectedItemID);
        intent.putExtra("topicSelectedItemID", topicSelectedItemID);
        intent.putExtra("topicKey", item.getTopicKey());
        intent.putExtra("topic", item.getTopic());

        ArrayList<String> questionsSelected = new ArrayList<>();
        for (LocalQuestionData localQuestionData : ((LocalTopicData) mAdapter.getGroup(topicSelectedItemID)).getQuestions()) {
            if (localQuestionData.getQuestionState()== LocalQuestionData.QuestionState.ON) {
                questionsSelected.add(localQuestionData.getQuestion());
            }
        }

        interviewQuestions = new String[questionsSelected.size()];

        for ( int i=0 ; i<questionsSelected.size() ; i++ ) {
            interviewQuestions[i] = questionsSelected.get(i);
        }

        intent.putExtra("allQuestions", interviewQuestions);

        startActivity(intent);
        finish();
    }


    //Save the current state of topics and questions to Firebase database, when leaving the this activity
    @Override
    protected void onStop() {
        LocalQuestionData question;
        String questionKey;
        long questionState;
        mUserQuestionStates.clear();
        for (int i = 0; i<mTopicCatalog.getTopics().size() ; i++) {
            // must start at index 1, because the first LocalQuestionData-Object holds the headerItem in each topic
            for (int j = 1; j<mTopicCatalog.getTopics().get(i).getQuestions().size() ; j++) {
                question = mTopicCatalog.getTopics().get(i).getQuestions().get(j);
                questionKey = question.getQuestionKey();
                questionState = question.getQuestionState();
                mUserQuestionStates.put(questionKey, questionState);
            }
        }
        mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardTopics");
        mDatabaseRef.setValue(mUserTopicStates);
        mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardQuestions");
        mDatabaseRef.setValue(mUserQuestionStates);
        super.onStop();
    }
}