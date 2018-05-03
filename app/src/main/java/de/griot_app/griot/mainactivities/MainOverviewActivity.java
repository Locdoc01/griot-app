package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import de.griot_app.griot.dataclasses.InterviewData;
import de.griot_app.griot.details_content.DetailsInterviewActivity;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.adapters.InterviewDataAdapter;
import de.griot_app.griot.interfaces.OnItemClickListener;


/**
 * Main activity that provides an overview over all interviews, where the user is either interviewer, narrator or shareholder.
 */
public class MainOverviewActivity extends GriotBaseActivity implements OnItemClickListener<InterviewData>{

    private static final String TAG = MainOverviewActivity.class.getSimpleName();

    //RecyclerView, that holds the interview items
    private RecyclerView mRecyclerViewInterviews;

    //ArrayList containing the data of interviews
    private ArrayList<InterviewData> mListInterviewData;

    //Data-View-Adapter for the RecyclerView
    private InterviewDataAdapter mInterviewDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_overview);
        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mListInterviewData = new ArrayList<>();

        mRecyclerViewInterviews = (RecyclerView) findViewById(R.id.recyclerView_main_overview);

        //Set the ValueEventListener to obtains all necessary data from Firebase
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListInterviewData.clear();
                //Obtain interview data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final InterviewData interviewData = ds.getValue(InterviewData.class);
                    interviewData.setContentID(ds.getKey());
                    mListInterviewData.add(interviewData);
                }
                //Set the adapter
                mInterviewDataAdapter = new InterviewDataAdapter(MainOverviewActivity.this, mListInterviewData);
                mRecyclerViewInterviews.setLayoutManager(new LinearLayoutManager(MainOverviewActivity.this));
                mRecyclerViewInterviews.setAdapter(mInterviewDataAdapter);
                mInterviewDataAdapter.setOnItemClickListener(MainOverviewActivity.this);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mDatabaseRef = mDatabaseRootReference.child("interviews");
        mDatabaseRef.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onItemClick(InterviewData dataItem) {
        Intent intent = new Intent(MainOverviewActivity.this, DetailsInterviewActivity.class);
        intent.putExtra("selectedInterviewID", dataItem.getContentID());
        intent.putExtra("interviewTitle", dataItem.getTitle());
        intent.putExtra("dateYear", dataItem.getDateYear());
        intent.putExtra("dateMonth", dataItem.getDateMonth());
        intent.putExtra("dateDay", dataItem.getDateDay());
        intent.putExtra("topic", dataItem.getTopic());
        intent.putExtra("medium", dataItem.getMedium());
        intent.putExtra("length", dataItem.getLength());
        intent.putExtra("pictureURL", dataItem.getPictureURL());
        intent.putExtra("interviewerID", dataItem.getInterviewerID());
        intent.putExtra("interviewerName", dataItem.getInterviewerName());
        intent.putExtra("interviewerPictureURL", dataItem.getInterviewerPictureURL());
        intent.putExtra("narratorID", dataItem.getNarratorID());
        intent.putExtra("narratorName", dataItem.getNarratorName());
        intent.putExtra("narratorPictureURL", dataItem.getNarratorPictureURL());
        intent.putExtra("narratorIsUser", dataItem.getNarratorIsUser());

        String[] associatedUsers = new String[dataItem.getAssociatedUsers().size()];
        Iterator<String> iterator = dataItem.getAssociatedUsers().keySet().iterator();
        for (int i=0 ; i<associatedUsers.length ; i++) {
            associatedUsers[i] = iterator.next();
        }
        intent.putExtra("associatedUsers", associatedUsers);

        String[] associatedGuests = new String[dataItem.getAssociatedGuests().size()];
        iterator = dataItem.getAssociatedGuests().keySet().iterator();
        for (int i=0 ; i<associatedGuests.length ; i++) {
            associatedGuests[i] = iterator.next();
        }
        intent.putExtra("associatedGuests", associatedGuests);

        String[] tags = new String[dataItem.getTags().size()];
        iterator = dataItem.getTags().keySet().iterator();
        for (int i=0 ; i<tags.length ; i++) {
            tags[i] = iterator.next();
        }
        intent.putExtra("tags", tags);

        intent.putExtra("numberComments", dataItem.getNumberComments());
        startActivity(intent);
    }


    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_overview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_bar_main_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
