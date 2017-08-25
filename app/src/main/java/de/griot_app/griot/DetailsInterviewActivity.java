package de.griot_app.griot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;

import de.griot_app.griot.adapters.LocalInterviewQuestionDataAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.views.TagView;


public class DetailsInterviewActivity extends GriotBaseActivity {

    private static final String TAG = DetailsInterviewActivity.class.getSimpleName();

    private String selectedInterviewQuestionID;

    // intent-data
    private String selectedInterviewID;
    private String interviewTitle;
    private String dateYear;
    private String dateMonth;
    private String dateDay;
    private String topic;
    private String medium;
    private String length;
    private String pictureLocalURI;
    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureLocalURI;
    private String narratorID;
    private String narratorName;
    private String narratorPictureLocalURI;
    private boolean narratorIsUser;
    private String[] associatedUsers;
    private String[] associatedGuests;
    private String[] tags;
    private int numberComments;

    // views
    private View mListHeader;
    private View mListFooter;

    private ImageView mMediaPlayer;
    private ImageView mMediaPlayerPlaceholder;
    private ProfileImageView mPivInterviewer;
    private ProfileImageView mPivNarrator;
    private TextView mTextViewInterviewer;
    private TextView mTextViewNarrator;
    private TextView mTextViewCommentsHeader;
    private ImageView mButtonOptions;
    private TextView mInterviewTitle;
    private TextView mDate;
    private LinearLayout mLayoutScrollViewTags;
    private TextView mTextViewTopic;
    private LinearLayout mLayoutScrollViewVisibility;
    private TextView mTextViewCommentsFooter;
    private EditText mEditTextPostComment;
    private ImageView mButtonPostComment;

    // ListView, that holds the interview items
    private ListView mListViewInterviewQuestions;

    // data list
    private ArrayList<LocalInterviewQuestionData> mListLocalInterviewQuestionData;

    //Data-View-Adapter for the ListView
    private LocalInterviewQuestionDataAdapter mLocalInterviewQuestionDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedInterviewID = getIntent().getStringExtra("selectedInterviewID");

        interviewTitle = getIntent().getStringExtra("interviewTitle");
        dateYear = getIntent().getStringExtra("dateYear");
        dateMonth = getIntent().getStringExtra("dateMonth");
        dateDay = getIntent().getStringExtra("dateDay");
        topic = getIntent().getStringExtra("topic");
        medium = getIntent().getStringExtra("medium");
        length = getIntent().getStringExtra("length");
        pictureLocalURI = getIntent().getStringExtra("pictureLocalURI");
        interviewerID = getIntent().getStringExtra("interviewerID");
        interviewerName = getIntent().getStringExtra("interviewerName");
        interviewerPictureLocalURI = getIntent().getStringExtra("interviewerPictureLocalURI");
        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureLocalURI = getIntent().getStringExtra("narratorPictureLocalURI");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", false);
        associatedUsers = getIntent().getStringArrayExtra("associatedUsers");
        associatedGuests = getIntent().getStringArrayExtra("associatedGuests");
        tags = getIntent().getStringArrayExtra("tags");
        numberComments = getIntent().getIntExtra("numberComments", 0);

        mTitle.setText(interviewTitle);
//        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mListHeader = getLayoutInflater().inflate(R.layout.listheader_details_interview, null);
        mListFooter = getLayoutInflater().inflate(R.layout.listfooter_details_interview, null);

        //Header views
        mMediaPlayer = (ImageView) mListHeader.findViewById(R.id.mediaPlayer);
        mMediaPlayerPlaceholder = (ImageView) mListHeader.findViewById(R.id.mediaPlayer_placeholder);
        mPivInterviewer = (ProfileImageView) mListHeader.findViewById(R.id.piv_interviewer);
        mPivNarrator = (ProfileImageView) mListHeader.findViewById(R.id.piv_narrator);
        mTextViewInterviewer = (TextView) mListHeader.findViewById(R.id.textView_interviewer);
        mTextViewNarrator = (TextView) mListHeader.findViewById(R.id.textView_narrator);
        mTextViewCommentsHeader = (TextView) mListHeader.findViewById(R.id.textView_comments_header);
        mButtonOptions = (ImageView) mListHeader.findViewById(R.id.button_options);
        mInterviewTitle = (TextView) mListHeader.findViewById(R.id.textView_interview_title);
        mDate = (TextView) mListHeader.findViewById(R.id.textView_date);
        mLayoutScrollViewTags = (LinearLayout) mListHeader.findViewById(R.id.layout_scrollView_tags);
        //Footer views
        mTextViewTopic = (TextView) mListFooter.findViewById(R.id.textView_topic);
        mLayoutScrollViewVisibility = (LinearLayout) mListFooter.findViewById(R.id.layout_scrollView_visibility);
        mTextViewCommentsFooter = (TextView) mListFooter.findViewById(R.id.textView_comments_footer);
        mEditTextPostComment = (EditText) mListFooter.findViewById(R.id.editText_post_comment);
        mButtonPostComment = (ImageView) mListFooter.findViewById(R.id.button_post_comment);

        if (pictureLocalURI != null) {
            if (Uri.parse(pictureLocalURI) != null) {
                ImageView test = new ImageView(this);
                test.setImageURI(Uri.parse(pictureLocalURI));
                if (test.getDrawable() != null) {
                    mMediaPlayer.setImageURI(Uri.parse(pictureLocalURI));
                    mMediaPlayerPlaceholder.setVisibility(View.GONE);
                    mMediaPlayer.setVisibility(View.VISIBLE);
                }
            }
        }

        mPivInterviewer.getProfileImage().setImageURI(Uri.parse(interviewerPictureLocalURI));
        mPivNarrator.getProfileImage().setImageURI(Uri.parse(narratorPictureLocalURI));
        mTextViewInterviewer.setText(interviewerName);
        mTextViewNarrator.setText(narratorName);
        mTextViewCommentsHeader.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments) + " " + ( numberComments == 1 ? getString(R.string.text_comment) : getString(R.string.text_comments)));
        mInterviewTitle.setText(interviewTitle);
        mDate.setText(dateDay + "." + dateMonth + "." + dateYear);

        for (int i=0 ; i<tags.length ; i++) {
            TagView tagView = new TagView(this);
            tagView.setTag(tags[i]);
            tagView.setVisibilityDeleteButton(false);
            mLayoutScrollViewTags.addView(tagView);
        }

        mTextViewTopic.setText(topic);

        int width = getResources().getDimensionPixelSize(R.dimen.dimen_piv_visibility);
        int height = getResources().getDimensionPixelSize(R.dimen.dimen_piv_visibility);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        ProfileImageView pivInterviewer = new ProfileImageView(this);
        pivInterviewer.getProfileImage().setImageURI(Uri.parse(interviewerPictureLocalURI));
        mLayoutScrollViewVisibility.addView(pivInterviewer);
        pivInterviewer.setLayoutParams(params);
        if (!interviewerID.equals(narratorID)) {
            ProfileImageView pivNarrator = new ProfileImageView(this);
            pivNarrator.getProfileImage().setImageURI(Uri.parse(narratorPictureLocalURI));
            mLayoutScrollViewVisibility.addView(pivNarrator);
            pivNarrator.setLayoutParams(params);
        }
        //TODO: associated users & guests zur ScrollView hinzufügen

        mTextViewCommentsFooter.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments));

        mListLocalInterviewQuestionData = new ArrayList<>();

        mListViewInterviewQuestions = (ListView) findViewById(R.id.listView_interviewQuestions);
        mListViewInterviewQuestions.addHeaderView(mListHeader);
        mListViewInterviewQuestions.addFooterView(mListFooter);

        mListViewInterviewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedInterviewQuestionID = mLocalInterviewQuestionDataAdapter.getItem(position).getContentID();
                Intent intent = new Intent(DetailsInterviewActivity.this, DetailsInterviewQuestionActivity.class);
                intent.putExtra("selectedInterviewQuestionID", selectedInterviewQuestionID);
                startActivity(intent);
            }
        });


        // Obtains all necessary data from Firebase
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mListLocalInterviewQuestionData.clear();
                //obtain interview data
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    final LocalInterviewQuestionData localInterviewQuestionData = ds.getValue(LocalInterviewQuestionData.class);
                    localInterviewQuestionData.setContentID(ds.getKey());
                    mListLocalInterviewQuestionData.add(localInterviewQuestionData);
                }
                // set the adapter
                mLocalInterviewQuestionDataAdapter = new LocalInterviewQuestionDataAdapter(DetailsInterviewActivity.this, mListLocalInterviewQuestionData);
                mLocalInterviewQuestionDataAdapter.setShowTags(false);
                mListViewInterviewQuestions.setAdapter(mLocalInterviewQuestionDataAdapter);

                //create temporary files to store the pictures from Firebase Storage

                for ( int i=0 ; i<mListLocalInterviewQuestionData.size() ; i++ ) {
                    final int index = i;
                    File fileMediaCover = null;
                    try {
                        fileMediaCover = File.createTempFile("mediaCover" + i + "_", ".jpg");
                    } catch (Exception e) {
                    }
                    final String pathMediaCover = fileMediaCover.getPath();

                    //TODO: try-catch wahrscheinlich nötig, wenn beim Upload der Bilder was schief gelaufen ist.
                    //Obtain pictures for interview media covers from Firebase Storage
                    try {
                        mStorageRef = mStorage.getReferenceFromUrl(mListLocalInterviewQuestionData.get(index).getPictureURL());
                        mStorageRef.getFile(fileMediaCover).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mListLocalInterviewQuestionData.get(index).setPictureLocalURI(pathMediaCover);
                                mLocalInterviewQuestionDataAdapter.notifyDataSetChanged();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error downloading MediaCover image file");
                                mListLocalInterviewQuestionData.get(index).setPictureLocalURI("");
                                mLocalInterviewQuestionDataAdapter.notifyDataSetChanged();
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
        return R.layout.activity_details_interview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseRef = mDatabaseRootReference.child("interviewQuestions");
        mDatabaseRef.addValueEventListener(mValueEventListener);
        //mQuery = mDatabaseRootReference.child("interviewQuestions").orderByChild("interviewID").equalTo(selectedInterviewID);
        //mQuery.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: muss mDatabaseRef hier gesetzt werden?
        //TODO: kann man auf einen Schlag alle Listener entfernen?
//        mQuery.removeEventListener(mValueEventListener);
    }
}
