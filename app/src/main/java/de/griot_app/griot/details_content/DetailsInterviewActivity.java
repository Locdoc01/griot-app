package de.griot_app.griot.details_content;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import de.griot_app.griot.R;
import de.griot_app.griot.adapters.InterviewQuestionDataDetailsAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.dataclasses.InterviewQuestionData;
import de.griot_app.griot.interfaces.OnItemClickListener;

/**
 * Activity that shows the details of a selected interview, including al of its belonging interview questions
 */
public class DetailsInterviewActivity extends GriotBaseActivity implements OnItemClickListener<InterviewQuestionData>{

    private static final String TAG = DetailsInterviewActivity.class.getSimpleName();

    private Intent mIntentReceived;

    //Intent-data
    private String selectedInterviewID;
    private String interviewTitle;
    private String dateYear;
    private String dateMonth;
    private String dateDay;
    private String topic;
    private String medium;
    private String length;
    private String pictureURL;
    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureURL;
    private String narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private boolean narratorIsUser;
    private String[] associatedUsers;
    private String[] associatedGuests;
    private String[] tags;
    private int numberComments;

    //RecyclerView, that holds the interview question items
    private RecyclerView mRecyclerViewInterviewQuestions;

    //ArrayList containing the data of interview questions
    private ArrayList<InterviewQuestionData> mListInterviewQuestionData;

    //Data-View-Adapter for the RecyclerView
    private InterviewQuestionDataDetailsAdapter mInterviewQuestionDataDetailsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntentReceived = getIntent();

        //Get intent data
        selectedInterviewID = mIntentReceived.getStringExtra("selectedInterviewID");

        interviewTitle = mIntentReceived.getStringExtra("interviewTitle");
        dateYear = mIntentReceived.getStringExtra("dateYear");
        dateMonth = mIntentReceived.getStringExtra("dateMonth");
        dateDay = mIntentReceived.getStringExtra("dateDay");
        topic = mIntentReceived.getStringExtra("topic");
        medium = mIntentReceived.getStringExtra("medium");
        length = mIntentReceived.getStringExtra("length");
        pictureURL = mIntentReceived.getStringExtra("pictureURL");
        interviewerID = mIntentReceived.getStringExtra("interviewerID");
        interviewerName = mIntentReceived.getStringExtra("interviewerName");
        interviewerPictureURL = mIntentReceived.getStringExtra("interviewerPictureURL");
        narratorID = mIntentReceived.getStringExtra("narratorID");
        narratorName = mIntentReceived.getStringExtra("narratorName");
        narratorPictureURL = mIntentReceived.getStringExtra("narratorPictureURL");
        narratorIsUser = mIntentReceived.getBooleanExtra("narratorIsUser", false);
        associatedUsers = mIntentReceived.getStringArrayExtra("associatedUsers");
        associatedGuests = mIntentReceived.getStringArrayExtra("associatedGuests");
        tags = mIntentReceived.getStringArrayExtra("tags");
        numberComments = mIntentReceived.getIntExtra("numberComments", 0);


        //Hides the app bar
        mAppBar.setVisibility(View.GONE);
        mLineAppBar.setVisibility(View.GONE);
        //mTitle.setText(interviewTitle);
//        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mListInterviewQuestionData = new ArrayList<>();

        //get reference to ListView and add header & footer
        mRecyclerViewInterviewQuestions = (RecyclerView) findViewById(R.id.recyclerView_interviewQuestions);

        // Set ValueEventListener to obtains all interview question data from Firebase
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListInterviewQuestionData.clear();
                //obtain interview data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final InterviewQuestionData interviewQuestionData = ds.getValue(InterviewQuestionData.class);
                    interviewQuestionData.setContentID(ds.getKey());
                    mListInterviewQuestionData.add(interviewQuestionData);
                }
                //set adapter
                mInterviewQuestionDataDetailsAdapter = new InterviewQuestionDataDetailsAdapter(DetailsInterviewActivity.this, mListInterviewQuestionData, mIntentReceived);
                mRecyclerViewInterviewQuestions.setLayoutManager(new LinearLayoutManager(DetailsInterviewActivity.this));
                mRecyclerViewInterviewQuestions.setAdapter(mInterviewQuestionDataDetailsAdapter);
                mInterviewQuestionDataDetailsAdapter.setOnItemClickListener(DetailsInterviewActivity.this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

    }

    @Override
    public void onItemClick(InterviewQuestionData dataItem) {
        //create Intent and put extra data to it
        Intent intent = new Intent(DetailsInterviewActivity.this, DetailsInterviewQuestionActivity.class);
        intent.putExtra("selectedInterviewQuestionID", dataItem.getContentID());

        intent.putExtra("question", dataItem.getQuestion());
        intent.putExtra("dateYearQuestion", dataItem.getDateYear());
        intent.putExtra("dateMonthQuestion", dataItem.getDateMonth());
        intent.putExtra("dateDayQuestion", dataItem.getDateDay());
        intent.putExtra("topic", topic);
        intent.putExtra("medium", medium);
        intent.putExtra("lengthQuestion", dataItem.getLength());
        intent.putExtra("pictureURLQuestion", dataItem.getPictureURL());
        intent.putExtra("interviewerID", interviewerID);
        intent.putExtra("interviewerName", interviewerName);
        intent.putExtra("interviewerPictureURL", interviewerPictureURL);
        intent.putExtra("narratorID", narratorID);
        intent.putExtra("narratorName", narratorName);
        intent.putExtra("narratorPictureURL", narratorPictureURL);
        intent.putExtra("narratorIsUser", narratorIsUser);

        String[] associatedUsersQuestion = new String[dataItem.getAssociatedUsers().size()];
        Iterator<String> iterator = dataItem.getAssociatedUsers().keySet().iterator();
        for (int i=0 ; i<associatedUsersQuestion.length ; i++) {
            associatedUsersQuestion[i] = iterator.next();
        }
        intent.putExtra("associatedUsersQuestion", associatedUsersQuestion);

        String[] associatedGuestsQuestion = new String[dataItem.getAssociatedGuests().size()];
        iterator = dataItem.getAssociatedGuests().keySet().iterator();
        for (int i=0 ; i<associatedGuestsQuestion.length ; i++) {
            associatedGuestsQuestion[i] = iterator.next();
        }
        intent.putExtra("associatedGuestsQuestion", associatedGuestsQuestion);

        String[] tagsQuestion = new String[dataItem.getTags().size()];
        iterator = dataItem.getTags().keySet().iterator();
        for (int i=0 ; i<tagsQuestion.length ; i++) {
            tagsQuestion[i] = iterator.next();
        }
        intent.putExtra("tagsQuestion", tagsQuestion);

        intent.putExtra("numberComments", numberComments);

        startActivity(intent);
        finish();
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_details_interview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}

    @Override
    protected void onStart() {
        super.onStart();
        //Obtain data from Firebase
        mQuery = mDatabaseRootReference.child("interviewQuestions").orderByChild("interviewID").equalTo(selectedInterviewID);
        mQuery.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: necessary here?
        mQuery.removeEventListener(mValueEventListener);
    }
}
