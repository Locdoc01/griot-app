package de.griot_app.griot;

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

import de.griot_app.griot.adapter.TopicCatalogAdapter;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.LocalQuestionData;
import de.griot_app.griot.dataclasses.TopicData;
import de.griot_app.griot.dataclasses.QuestionGroup;
import de.griot_app.griot.dataclasses.TopicCatalog;
import de.griot_app.griot.mainactivities.MainChooseFriendInputActivity;

public class ChooseTopicInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ChooseTopicInputActivity.class.getSimpleName();

    private int topicSelectedItemID = -1;

    private int narratorSelectedItemID;
    private String narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private Boolean narratorIsUser;

    TextView mTextViewPerson;
    ImageView mButtonCancelPerson;
    ImageView mButtonAddTopic;

    TopicCatalog mTopicCatalog;
    ExpandableListView mExpandListView;
    TopicCatalogAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topicSelectedItemID = getIntent().getIntExtra("topicSelectedItemID", -1);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
        if (topicSelectedItemID<0) {
            mButtonRight.setEnabled(false);
            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
        }


        narratorSelectedItemID = getIntent().getIntExtra("narratorSelectedItemID", -1);
        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureURL = getIntent().getStringExtra("narratorPictureURL");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", true);

        mTextViewPerson = (TextView) findViewById(R.id.textView_person);
        mButtonCancelPerson = (ImageView) findViewById(R.id.button_cancel_person);
        mButtonAddTopic = (ImageView) findViewById(R.id.button_add_topic);

        mTextViewPerson.setText(getString(R.string.text_choosed_person) + ":  " + narratorName);

        mButtonCancelPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChooseTopicInputActivity.this, MainChooseFriendInputActivity.class));
                finish();
            }
        });

        mButtonAddTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    ((ImageView)((ConstraintLayout)v).findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.up, null));
                } else {
                    parent.expandGroup(groupPosition);
                    ((ImageView)((ConstraintLayout)v).findViewById(R.id.button_expand)).setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.down, null));
                }
                mAdapter.notifyDataSetChanged();
                return true;
            }
        });

        mExpandListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                return false;
            }
        });

        mTopicCatalog = new TopicCatalog();

        mDatabaseRef = mDatabaseRootReference.child("standardTopics");
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

                        mAdapter = new TopicCatalogAdapter(ChooseTopicInputActivity.this, mTopicCatalog);
                        mExpandListView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Error loading Questions");
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "Error loading Topics");
            }
        });
    }

    /**
     * Selects the topic at the specified groupPosition of the ExpandableListView. This method can be called from an OnclickListener defined in the adapter.
     * @param groupPosition     Topic position in the ExpandableListView
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

        Intent intent = new Intent(ChooseTopicInputActivity.this, MainChooseFriendInputActivity.class);
        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        startActivity(intent);
        finish();
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

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
        startActivity(intent);
        finish();
    }
}