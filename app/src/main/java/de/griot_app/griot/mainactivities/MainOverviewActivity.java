package de.griot_app.griot.mainactivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import de.griot_app.griot.details_content.DetailsInterviewActivity;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.adapters.LocalInterviewDataAdapter;
import de.griot_app.griot.dataclasses.LocalInterviewData;
import de.griot_app.griot.interfaces.OnItemClickListener;


/**
 * Main activity that provides an overview over all interviews, where the user is either interviewer, narrator or shareholder.
 */
public class MainOverviewActivity extends GriotBaseActivity implements OnItemClickListener<LocalInterviewData>{

    private static final String TAG = MainOverviewActivity.class.getSimpleName();

    //RecyclerView, that holds the interview items
    private RecyclerView mRecyclerViewInterviews;

    //ArrayList containing the data of interviews
    private ArrayList<LocalInterviewData> mListLocalInterviewData;

    //Data-View-Adapter for the RecyclerView
    private LocalInterviewDataAdapter mLocalInterviewDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_overview);
        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mListLocalInterviewData = new ArrayList<>();

        mRecyclerViewInterviews = (RecyclerView) findViewById(R.id.recyclerView_main_overview);

        //Set the ValueEventListener to obtains all necessary data from Firebase
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListLocalInterviewData.clear();
                //Obtain interview data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final LocalInterviewData localInterviewData = ds.getValue(LocalInterviewData.class);
                    localInterviewData.setContentID(ds.getKey());
                    mListLocalInterviewData.add(localInterviewData);
                }
                //Set the adapter
                mLocalInterviewDataAdapter = new LocalInterviewDataAdapter(MainOverviewActivity.this, mListLocalInterviewData);
                mRecyclerViewInterviews.setLayoutManager(new LinearLayoutManager(MainOverviewActivity.this));
                mRecyclerViewInterviews.setAdapter(mLocalInterviewDataAdapter);
                mLocalInterviewDataAdapter.setOnItemClickListener(MainOverviewActivity.this);

                //Create temporary files to store the pictures from Firebase Storage
                for ( int i=0 ; i<mListLocalInterviewData.size() ; i++ ) {
                    final int index = i;
                    File fileMediaCover = null;
                    File fileInterviewer = null;
                    File fileNarrator = null;
                    try {
                        fileMediaCover = File.createTempFile("mediaCover" + i + "_", ".jpg");
                        fileInterviewer = File.createTempFile("interviewer" + i + "_", ".jpg");
                        fileNarrator = File.createTempFile("narrator" + i + "_", ".jpg");
                    } catch (Exception e) {
                    }
                    final String pathMediaCover = fileMediaCover.getPath();
                    final String pathInterviewer = fileInterviewer.getPath();
                    final String pathNarrator = fileNarrator.getPath();

                    //Obtain pictures for interview media covers from Firebase Storage
                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mListLocalInterviewData.get(index).getPictureURL());
                        mStorageRef.getFile(fileMediaCover).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mListLocalInterviewData.get(index).setPictureLocalURI(pathMediaCover);
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading MediaCover image file");
                                mListLocalInterviewData.get(index).setPictureLocalURI("");
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {}

                    //Obtain interviewer profile pictures from Firebase Storage
                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mListLocalInterviewData.get(index).getInterviewerPictureURL());
                        mStorageRef.getFile(fileInterviewer).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mListLocalInterviewData.get(index).setInterviewerPictureLocalURI(pathInterviewer);
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading Interviewer image file");
                                mListLocalInterviewData.get(index).setInterviewerPictureLocalURI("");
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {}

                    //Obtain narrator profile pictures from Firebase Storage
                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mListLocalInterviewData.get(index).getNarratorPictureURL());
                        mStorageRef.getFile(fileNarrator).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mListLocalInterviewData.get(index).setNarratorPictureLocalURI(pathNarrator);
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading Interviewer image file");
                                mListLocalInterviewData.get(index).setNarratorPictureLocalURI("");
                                mLocalInterviewDataAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {}
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };

        mDatabaseRef = mDatabaseRootReference.child("interviews");
        mDatabaseRef.addValueEventListener(mValueEventListener);
    }

    @Override
    public void onItemClick(LocalInterviewData dataItem) {
        Intent intent = new Intent(MainOverviewActivity.this, DetailsInterviewActivity.class);
        intent.putExtra("selectedInterviewID", dataItem.getContentID());
        intent.putExtra("interviewTitle", dataItem.getTitle());
        intent.putExtra("dateYear", dataItem.getDateYear());
        intent.putExtra("dateMonth", dataItem.getDateMonth());
        intent.putExtra("dateDay", dataItem.getDateDay());
        intent.putExtra("topic", dataItem.getTopic());
        intent.putExtra("medium", dataItem.getMedium());
        intent.putExtra("length", dataItem.getLength());
        intent.putExtra("pictureLocalURI", dataItem.getPictureLocalURI());
        intent.putExtra("interviewerID", dataItem.getInterviewerID());
        intent.putExtra("interviewerName", dataItem.getInterviewerName());
        intent.putExtra("interviewerPictureLocalURI", dataItem.getInterviewerPictureLocalURI());
        intent.putExtra("narratorID", dataItem.getNarratorID());
        intent.putExtra("narratorName", dataItem.getNarratorName());
        intent.putExtra("narratorPictureLocalURI", dataItem.getNarratorPictureLocalURI());
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
