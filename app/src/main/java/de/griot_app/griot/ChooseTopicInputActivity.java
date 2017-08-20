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
import de.griot_app.griot.dataclasses.QuestionGroup;
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


    ArrayList<Boolean> mUserStandardTopics;
    HashMap<String, Long> mUserStandardQuestions;


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

/*
//TODO. später löschen (müsste bearbeitet werden, um verwendet werden zu können)
        //If a Topic-ListItem is longclicked, the topic gets selected
        //delete the comment, if this is wanted
        mExpandListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (ExpandableListView.getPackedPositionType(id)==ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                    int groupPosition = ExpandableListView.getPackedPositionGroup(id);
                    if (mExpandListView.isGroupExpanded(groupPosition)) {
                        mExpandListView.collapseGroup(groupPosition);
                        mTopicCatalog.getQuestionGroups().get(groupPosition).setExpanded(true);
                    } else {
                        mExpandListView.expandGroup(groupPosition);
                        mTopicCatalog.getQuestionGroups().get(groupPosition).setExpanded(false);
                    }
                    /*
                    if (mSelectedItem==null) {
                        mSelectedItem = (QuestionGroup) mAdapter.getGroup(groupPosition);
                        mSelectedItem.setSelected(true);
                        mButtonRight.setEnabled(true);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                    } else {
                        if (mSelectedItem==mAdapter.getGroup(groupPosition)) {
                            mSelectedItem.setSelected(false);
                            mSelectedItem = null;
                            mButtonRight.setEnabled(false);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                        } else {
                            mSelectedItem.setSelected(false);
                            mSelectedItem = (QuestionGroup) mAdapter.getGroup(groupPosition);
                            mSelectedItem.setSelected(true);
                            mButtonRight.setEnabled(true);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                        }
                    }
                    *//*
                    mAdapter.notifyDataSetChanged();
                    return true;
                } else {
                    return false;
                }
            }
        });
*/

/*
//TODO. später löschen (müsste bearbeitet werden, um verwendet werden zu können)
        // If a Topic-ListItem is clicked, the topic gets selected
        // delete the comment, if this is wanted
        mExpandListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (mSelectedItem==null) {
                    mSelectedItem = (QuestionGroup) mAdapter.getGroup(groupPosition);
                    mSelectedItem.setSelected(true);
                    mButtonRight.setEnabled(true);
                    mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                } else {
                    if (mSelectedItem==mAdapter.getGroup(groupPosition)) {
                        mSelectedItem.setSelected(false);
                        mSelectedItem = null;
                        mButtonRight.setEnabled(false);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                    } else {
                        mSelectedItem.setSelected(false);
                        mSelectedItem = (QuestionGroup) mAdapter.getGroup(groupPosition);
                        mSelectedItem.setSelected(true);
                        mButtonRight.setEnabled(true);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                    }
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });
*/

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

        mExpandListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LocalQuestionData data = (LocalQuestionData) mAdapter.getChild(groupPosition, childPosition);
                if (data.getQuestionState() == LocalQuestionData.QuestionState.OFF) {
                    ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_on, null));
                    data.setQuestionState(LocalQuestionData.QuestionState.ON);
                } else if (data.getQuestionState() == LocalQuestionData.QuestionState.ON) {
                    ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_off, null));
                    data.setQuestionState(LocalQuestionData.QuestionState.OFF);
                }
                return true;
            }
        });


        mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardTopics");
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserStandardTopics = (ArrayList<Boolean>) dataSnapshot.getValue();

                mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardQuestions");
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mUserStandardQuestions = (HashMap<String, Long>) dataSnapshot.getValue();

                        mTopicCatalog = new TopicCatalog();
                        // obtains topic catalog data from Firebase
                        mDatabaseRef = mDatabaseRootReference.child("standardTopics");
                        // listener for topic data
                        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mTopicCatalog.getQuestionGroups().clear();

                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    LocalTopicData localTopicData = ds.getValue(LocalTopicData.class);
                                    QuestionGroup group = new QuestionGroup();

                                    group.setTopicKey(localTopicData.getTopicKey());
                                    group.setTopic(localTopicData.getTopic());
                                    mTopicCatalog.getQuestionGroups().put(group.getTopicKey(), group);

                                    LocalQuestionData headItem = new LocalQuestionData();
                                    headItem.setQuestion(getString(R.string.title_questions));
                                    headItem.setTopicKey(localTopicData.getTopicKey());
                                    mTopicCatalog.getQuestionGroups().get(localTopicData.getTopicKey()).getQuestions().add(headItem);
                                }

                                mDatabaseRef = mDatabaseRootReference.child("standardQuestions");
                                //listener for question data
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            LocalQuestionData localQuestionData = ds.getValue(LocalQuestionData.class);
                                            localQuestionData.setQuestionKey(ds.getKey());
                                            localQuestionData.setQuestionState(mUserStandardQuestions.get(ds.getKey()));
                                            mTopicCatalog.getQuestionGroups().get(localQuestionData.getTopicKey()).getQuestions().add(localQuestionData);
                                        }

                                        //TODO: ExtraTopics und ExtraQuestions laden

                                        if(topicSelectedItemID >=0) {
                                            mTopicCatalog.getQuestionGroups().get(topicSelectedItemID).setSelected(true);
                                        }

                                        //set the adapter
                                        mAdapter = new TopicCatalogAdapter(ChooseTopicInputActivity.this, mTopicCatalog);
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
     * The selection gets stored in the appropriate QuestionGroup-object.
     * This method can be called from an OnclickListener defined in the adapter.
     * @param groupPosition Topic position in the ExpandableListView
     */
    public void buttonCheckClicked(int groupPosition) {
        if (topicSelectedItemID <0) {
            topicSelectedItemID = groupPosition;
            ((QuestionGroup) mAdapter.getGroup(groupPosition)).setSelected(true);
            mButtonRight.setEnabled(true);
            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
        } else {
            if (topicSelectedItemID ==groupPosition) {
                ((QuestionGroup) mAdapter.getGroup(groupPosition)).setSelected(false);
                topicSelectedItemID = -1;
                mButtonRight.setEnabled(false);
                mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
            } else {
                ((QuestionGroup) mAdapter.getGroup(groupPosition)).setSelected(false);
                topicSelectedItemID = groupPosition;
                ((QuestionGroup) mAdapter.getGroup(groupPosition)).setSelected(true);
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

        // Navigates back to next page of "prepare interview"-dialog
        // All relevant data for the interview or the dialog-pages get sent to the next page.
        Intent intent = new Intent(this, ChooseMediumInputActivity.class);
        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        intent.putExtra("narratorID", narratorID);
        intent.putExtra("narratorName", narratorName);
        intent.putExtra("narratorPictureURL", narratorPictureURL);
        intent.putExtra("narratorIsUser", narratorIsUser);

        QuestionGroup item = (QuestionGroup) mAdapter.getGroup(topicSelectedItemID);
        intent.putExtra("topicSelectedItemID", topicSelectedItemID);
        intent.putExtra("topicKey", item.getTopicKey());
        intent.putExtra("topic", item.getTopic());

        ArrayList<String> questionsSelected = new ArrayList<>();
        for (LocalQuestionData localQuestionData : ((QuestionGroup) mAdapter.getGroup(topicSelectedItemID)).getQuestions()) {
            if (localQuestionData.getQuestionState()== LocalQuestionData.QuestionState.ON) {
                questionsSelected.add(localQuestionData.getQuestion());
            }
        }

        interviewQuestions = new String[questionsSelected.size()];

        for ( int i=0 ; i<questionsSelected.size() ; i++ ) {
            interviewQuestions[i] = questionsSelected.get(i);
        }

        intent.putExtra("interviewQuestions", interviewQuestions);

        startActivity(intent);
        finish();
    }


    @Override
    protected void onStop() {

        LocalQuestionData question;
        String questionKey;
        long questionState;
        mUserStandardQuestions.clear();
        for (int i=0 ; i<mTopicCatalog.getQuestionGroups().size() ; i++) {
            // must start at index 1, because the first LocalQuestionData-Object holds the headerItem in each topic
            for (int j=1 ; j<mTopicCatalog.getQuestionGroups().get(i).getQuestions().size() ; j++) {
                question = mTopicCatalog.getQuestionGroups().get(i).getQuestions().get(j);
                questionKey = question.getQuestionKey();
                questionState = question.getQuestionState();
                mUserStandardQuestions.put(questionKey, questionState);
            }
        }
        mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardTopics");
        mDatabaseRef.setValue(mUserStandardTopics);
        mDatabaseRef = mDatabaseRootReference.child("users").child(mAuth.getCurrentUser().getUid()).child("standardQuestions");
        mDatabaseRef.setValue(mUserStandardQuestions);
        super.onStop();
    }
}