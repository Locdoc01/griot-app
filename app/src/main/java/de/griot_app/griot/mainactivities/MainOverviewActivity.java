package de.griot_app.griot.mainactivities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;

import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.adapter.LocalInterviewDataAdapter;
import de.griot_app.griot.dataclasses.LocalInterviewData;


/**
 * Main activity that provides an overview over all interviews, the user got access to.
 */
public class MainOverviewActivity extends GriotBaseActivity {

    private static final String TAG = MainOverviewActivity.class.getSimpleName();

    // ListView, that holds the interview items
    private ListView mListViewInterviews;

    // data list
    private ArrayList<LocalInterviewData> mListLocalInterviewData;

    //Data-View-Adapter for the ListView
    private LocalInterviewDataAdapter mLocalInterviewDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_overview);
        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mListLocalInterviewData = new ArrayList<>();

        mListViewInterviews = (ListView) findViewById(R.id.listView_main_overview);

        // Obtains all necessary data from Firebase
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListLocalInterviewData.clear();
                //obtain interview data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final LocalInterviewData localInterviewData = ds.getValue(LocalInterviewData.class);
                    mListLocalInterviewData.add(localInterviewData);
                }
                // set the adapter
                mLocalInterviewDataAdapter = new LocalInterviewDataAdapter(MainOverviewActivity.this, mListLocalInterviewData);
                mListViewInterviews.setAdapter(mLocalInterviewDataAdapter);

                //create temporary files to store the pictures from Firebase Storage
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

                    //TODO: try-catch wahrscheinlich nötig, wenn beim Upload der Bilder was schief gelaufen ist.
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
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_overview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_bar_main_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseRef = mDatabaseRootReference.child("interviews");
        mDatabaseRef.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: muss mDatabaseRef hier gesetzt werden?
        //TODO: kann man auf einen Schlag alle Listener entfernen?
        mDatabaseRef = mDatabaseRootReference.child("interviews");
        mDatabaseRef.removeEventListener(mValueEventListener);
    }
}
