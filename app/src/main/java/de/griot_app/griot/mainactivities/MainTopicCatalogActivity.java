package de.griot_app.griot.mainactivities;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.griot_app.griot.adapters.TopicCatalogAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.dataclasses.QuestionData;
import de.griot_app.griot.dataclasses.TopicData;
import de.griot_app.griot.dataclasses.TopicCatalog;
import de.griot_app.griot.R;

/**
 * This activity provides the topic catalog. It provides an expandable List of all standard topics , that the user hasn't deleted from his page
 * and all extra topics, that the user has added by himself. If a topic is expanded, it shows all standard questions for that topic,
 * that the user hasn't deleted from his page and all extra questions for that topic, that the user has added by himself.
 * It also shows the question state for every question.
 * Questions and Topics can be searched and added. Also can own questions and Topics added.
 */
//TODO: search field for questions is missing
public class MainTopicCatalogActivity extends GriotBaseActivity {

    private static final String TAG = MainTopicCatalogActivity.class.getSimpleName();

    //Views
    ImageView mButtonAddTopic;

    //DataClass for topic catalog
    TopicCatalog mTopicCatalog;

    //Expandable ListView, that holds the topic catalog
    ExpandableListView mExpandListView;

    //Data-View-Adapter for TopicCatalog
    TopicCatalogAdapter mAdapter;

    ArrayList<Boolean> mUserTopicStates;
    HashMap<String, Long> mUserQuestionStates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_topics);
        mButtonTopicCatalog.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mButtonAddTopic = (ImageView) findViewById(R.id.button_add_topic);

        //Adds a topic to topic catalog
        mButtonAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainTopicCatalogActivity.this, "Thema hinzufügen", Toast.LENGTH_SHORT).show();
                //TODO
            }
        });

        mExpandListView = (ExpandableListView) findViewById(R.id.expandListView_input_choose_topic);

        //If a topic list item is clicked, the topic gets expanded or collapsed
        // TODO: comment out, if this is not wanted
        mExpandListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                    ((TopicData)mAdapter.getGroup(groupPosition)).setExpanded(false);
                } else {
                    parent.expandGroup(groupPosition);
                    ((TopicData)mAdapter.getGroup(groupPosition)).setExpanded(true);
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        //If a question list item is clicked, the question state changes
        mExpandListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (childPosition != 0) {
                    QuestionData data = (QuestionData) mAdapter.getChild(groupPosition, childPosition);
                    if (data.getQuestionState() == QuestionData.QuestionState.OFF) {
                        ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_on, null));
                        data.setQuestionState(QuestionData.QuestionState.ON);
                    } else if (data.getQuestionState() == QuestionData.QuestionState.ON) {
                        ((ImageView) v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_off, null));
                        data.setQuestionState(QuestionData.QuestionState.OFF);
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
                                    TopicData topicData = ds.getValue(TopicData.class);
                                    //Adds a new TopicData for the current topic to the TopicCatalog
                                    topicData.setTopicState(mUserTopicStates.get(topicData.getTopicKey()));
                                    mTopicCatalog.getTopics().put(topicData.getTopicKey(), topicData);

                                    //Adds a head list item to the question list for the current topic in the TopicCatalog
                                    QuestionData headItem = new QuestionData();
                                    headItem.setQuestion(getString(R.string.title_questions));
                                    headItem.setQuestionState(QuestionData.QuestionState.OFF);
                                    headItem.setTopicKey(topicData.getTopicKey());
                                    mTopicCatalog.getTopics().get(topicData.getTopicKey()).getQuestions().add(headItem);
                                }

                                //Obtain the standard questions for topic catalog
                                mDatabaseRef = mDatabaseRootReference.child("standardQuestions");
                                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                            QuestionData questionData = ds.getValue(QuestionData.class);
                                            questionData.setQuestionKey(ds.getKey());
                                            questionData.setQuestionState(mUserQuestionStates.get(ds.getKey()));
                                            mTopicCatalog.getTopics().get(questionData.getTopicKey()).getQuestions().add(questionData);
                                        }

                                        //TODO: obtain ExtraTopics and ExtraQuestions

                                        //Set the adapter
                                        mAdapter = new TopicCatalogAdapter(MainTopicCatalogActivity.this, mTopicCatalog);
                                        mExpandListView.setAdapter(mAdapter);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.e(TAG, "Error loading Questions");
                                        //TODO: implement if necessary
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, "Error loading Topics");
                                //TODO: implement if necessary
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Error loading Topics");
                        //TODO: implement if necessary
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error loading Topics");
                //TODO: implement if necessary
            }
        });
    }

    //Save the current state of topics and questions to Firebase database, when leaving the topic catalog activity
    @Override
    protected void onStop() {
        QuestionData question;
        String questionKey;
        long questionState;
        mUserQuestionStates.clear();
        for (int i = 0; i<mTopicCatalog.getTopics().size() ; i++) {
            //Must start at index 1, because the first QuestionData-Object holds the header list item in each topic
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

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_topic_catalog;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}
}
