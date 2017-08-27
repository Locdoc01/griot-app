package de.griot_app.griot;

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

import de.griot_app.griot.adapters.TopicCatalogAdapter;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.LocalQuestionData;
import de.griot_app.griot.dataclasses.LocalTopicData;
import de.griot_app.griot.dataclasses.TopicCatalog;
import de.griot_app.griot.mainactivities.MainChooseFriendInputActivity;

/**
 * This Activity allows the user to choose a topic and its questions from the topic catalog for an interview. It's the second step of the "prepare-interview"-dialog.
 */
public class ChooseTopicInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ChooseTopicInputActivity.class.getSimpleName();

    //holds the topic item id, if a topic was selected. It's used on this activity for managing the selection. It's also used as intent-data
    private int topicSelectedItemID = -1;

    //intent-data
    private int narratorSelectedItemID;
    private String narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private Boolean narratorIsUser;

    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureURL;

    String[] interviewQuestions;

    //Views
    TextView mTextViewPerson;
    ImageView mButtonCancelPerson;
    ImageView mButtonAddTopic;

    //DataClass for topic catalog
    TopicCatalog mTopicCatalog;

    //Expandable ListView, that holds the topic catalog
    ExpandableListView mExpandListView;

    //Data-View-Adapter for TopicCatalog
    TopicCatalogAdapter mAdapter;

    //store the individual TopicCatalog of the current user
    ArrayList<Boolean> mUserTopicStates;
    HashMap<String, Long> mUserQuestionStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //stores topic item id, if selected so far (that can be the case, if this activity got startet by another part of "prepare interview"-dialog)
        topicSelectedItemID = getIntent().getIntExtra("topicSelectedItemID", -1);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);

        //Next-Button is disabled at start, if no topic was selected so far
        if (topicSelectedItemID < 0) {
            mButtonRight.setEnabled(false);
            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
        }

        // gets intent-data about previous selection of narrator
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

        //shows the selected narrator
        mTextViewPerson.setText(getString(R.string.text_choosed_person) + ":  " + narratorName);

        //cancel-person-button (if button is pressed, the selection of narrator is canceled and the user got back to person selection
        mButtonCancelPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseTopicInputActivity.this, MainChooseFriendInputActivity.class));
                finish();
            }
        });

        //adds a topic to topic catalog
        mButtonAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implementieren
                Toast.makeText(ChooseTopicInputActivity.this, "Thema hinzufügen", Toast.LENGTH_SHORT).show();
            }
        });

        mExpandListView = (ExpandableListView) findViewById(R.id.expandListView_input_choose_topic);


        // If a Topic-ListItem is clicked, the topic gets expanded or collapsed
        // comment out, if this is not wanted
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

        // If a Question-ListItem is clicked, the question gets activated or deactivated
        mExpandListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                //get a reference to the appropriate DataClass-object for the clicked Question-ListItem
                LocalQuestionData data = (LocalQuestionData) mAdapter.getChild(groupPosition, childPosition);
                if (data.getQuestionState() == LocalQuestionData.QuestionState.OFF) {
                    //change the toggle-button
                    ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_on, null));
                    //and change the QuestionState in die DataClass-object
                    data.setQuestionState(LocalQuestionData.QuestionState.ON);
                } else if (data.getQuestionState() == LocalQuestionData.QuestionState.ON) {
                    ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_off, null));
                    data.setQuestionState(LocalQuestionData.QuestionState.OFF);
                }
                return true;
            }
        });


        // obtain the individual topic states for standard topics of the current user
        mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardTopics");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserTopicStates = (ArrayList<Boolean>) dataSnapshot.getValue();

                // obtain the indidual question states for standard questions of the current user
                mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardQuestions");
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUserQuestionStates = (HashMap<String, Long>) dataSnapshot.getValue();

                        // obtains standard topics for topic catalog from Firebase
                        mDatabaseRef = mDatabaseRootReference.child("standardTopics");
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mTopicCatalog = new TopicCatalog();

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    LocalTopicData localTopicData = ds.getValue(LocalTopicData.class);
                                    //adds a new LocalTopicData for the current topic to the TopicCatalog
                                    localTopicData.setTopicState(mUserTopicStates.get(localTopicData.getTopicKey()));
                                    mTopicCatalog.getTopics().put(localTopicData.getTopicKey(), localTopicData);

                                    //adds a headItem to the question list for the current topic in the TopicCatalog
                                    LocalQuestionData headItem = new LocalQuestionData();
                                    headItem.setQuestion(getString(R.string.title_questions));
                                    headItem.setQuestionState(LocalQuestionData.QuestionState.OFF);
                                    headItem.setTopicKey(localTopicData.getTopicKey());
                                    mTopicCatalog.getTopics().get(localTopicData.getTopicKey()).getQuestions().add(headItem);
                                }

                                //obtain standard questions for topic catalog from Firebase
                                mDatabaseRef = mDatabaseRootReference.child("standardQuestions");
                                //listener for question data
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

                                        //set the adapter
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
            ((LocalTopicData) mAdapter.getGroup(groupPosition)).setSelected(true);
            mButtonRight.setEnabled(true);
            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
        } else {
            if (topicSelectedItemID ==groupPosition) {
                ((LocalTopicData) mAdapter.getGroup(groupPosition)).setSelected(false);
                topicSelectedItemID = -1;
                mButtonRight.setEnabled(false);
                mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
            } else {
                ((LocalTopicData) mAdapter.getGroup(groupPosition)).setSelected(false);
                topicSelectedItemID = groupPosition;
                ((LocalTopicData) mAdapter.getGroup(groupPosition)).setSelected(true);
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

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        // navigates back to previous page of "prepare interview"-dialog
        // selection of narrator gets sent to previous page.
        Intent intent = new Intent(ChooseTopicInputActivity.this, MainChooseFriendInputActivity.class);
        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        startActivity(intent);
        finish();
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        //TODO schreibe QuestionStates nach Firebase

        // Navigates to next page of "prepare interview"-dialog
        // All relevant data for the interview or the dialog-pages get sent to the next page.
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