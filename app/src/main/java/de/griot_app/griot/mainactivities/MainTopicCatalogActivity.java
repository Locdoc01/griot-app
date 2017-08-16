package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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

import de.griot_app.griot.ChooseTopicInputActivity;
import de.griot_app.griot.adapter.TopicCatalogAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.dataclasses.LocalQuestionData;
import de.griot_app.griot.dataclasses.QuestionGroup;
import de.griot_app.griot.dataclasses.TopicCatalog;
import de.griot_app.griot.dataclasses.TopicData;
import de.griot_app.griot.startactivities.LoginActivity;
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
                    ((ImageView)((ConstraintLayout)v).findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.up, null));
                } else {
                    parent.expandGroup(groupPosition);
                    ((ImageView)((ConstraintLayout)v).findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.down, null));
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        mTopicCatalog = new TopicCatalog();
        // obtains topic catalog data from Firebase
        mDatabaseRef = mDatabaseRootReference.child("standardTopics");
        // listener for topic data
        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mTopicCatalog.getQuestionGroups().clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TopicData topicData = ds.getValue(TopicData.class);
                    QuestionGroup group = new QuestionGroup();

                    group.setTopicKey(topicData.getTopicKey());
                    group.setTopic(topicData.getTopic());
                    mTopicCatalog.getQuestionGroups().put(group.getTopicKey(), group);

                    LocalQuestionData headItem = new LocalQuestionData();
                    headItem.setQuestion(getString(R.string.title_questions));
                    headItem.setTopicKey(topicData.getTopicKey());
                    mTopicCatalog.getQuestionGroups().get(topicData.getTopicKey()).getQuestions().add(headItem);
                }

                mDatabaseRef = mDatabaseRootReference.child("standardQuestions");
                //listener for question data
                mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            LocalQuestionData localQuestionData = ds.getValue(LocalQuestionData.class);
                            mTopicCatalog.getQuestionGroups().get(localQuestionData.getTopicKey()).getQuestions().add(localQuestionData);
                        }

                        //TODO: ExtraTopics und ExtraQuestions laden

                        if(topicSelectedItemID >=0) {
                            mTopicCatalog.getQuestionGroups().get(topicSelectedItemID).setSelected(true);
                        }

                        //set the adapter
                        mAdapter = new TopicCatalogAdapter(MainTopicCatalogActivity.this, mTopicCatalog, false);
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
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_topic_catalog;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }
}
