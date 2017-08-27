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
import de.griot_app.griot.dataclasses.LocalQuestionData;
import de.griot_app.griot.dataclasses.LocalTopicData;
import de.griot_app.griot.dataclasses.TopicCatalog;
import de.griot_app.griot.R;

public class MainTopicCatalogActivity extends GriotBaseActivity {

    private static final String TAG = MainTopicCatalogActivity.class.getSimpleName();

    //holds the topic item id, if a topic was selected. It's used on this activity for managing the selection. It's also used as intent-data
    private int topicSelectedItemID = -1;

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

        //adds a topic to topic catalog
        mButtonAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO implementieren
                Toast.makeText(MainTopicCatalogActivity.this, "Thema hinzufügen", Toast.LENGTH_SHORT).show();
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
                    ((ImageView)v.findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.up, null));
                } else {
                    parent.expandGroup(groupPosition);
                    ((ImageView)v.findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.down, null));
                }
                return true;
            }
        });

        mExpandListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                LocalQuestionData data = (LocalQuestionData) mAdapter.getChild(groupPosition, childPosition);
                if (data.getQuestionState() == LocalQuestionData.QuestionState.OFF) {
                    ((ImageView)v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_on, null));
                    data.setQuestionState(LocalQuestionData.QuestionState.ON);
                } else if (data.getQuestionState() == LocalQuestionData.QuestionState.ON) {
                    ((ImageView)v.findViewById(R.id.button_toggle)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.toggle_off, null));
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
                                        mAdapter = new TopicCatalogAdapter(MainTopicCatalogActivity.this, mTopicCatalog);
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

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_topic_catalog;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}
}
