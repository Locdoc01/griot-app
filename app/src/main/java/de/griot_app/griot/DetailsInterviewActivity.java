package de.griot_app.griot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import de.griot_app.griot.adapters.LocalInterviewQuestionDataAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.views.TagView;


public class DetailsInterviewActivity extends GriotBaseActivity {

    private static final String TAG = DetailsInterviewActivity.class.getSimpleName();

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
//    private ImageView mMediaPlayerPlaceholder;
    private ProfileImageView mPivInterviewer;
    private ProfileImageView mPivNarrator;
    private TextView mTextViewInterviewer;
    private TextView mTextViewNarrator;
    private FrameLayout mButtonInterviewer;
    private FrameLayout mButtonNarrator;
    private TextView mButtonComments;
    private ImageView mButtonOptions;
    private TextView mTextViewInterviewTitle;
    private TextView mDate;
    private LinearLayout mLayoutScrollViewTags;
    private TextView mTextViewTopic;
    private LinearLayout mLayoutScrollViewVisibility;
    private TextView mTextViewComments;
    private EditText mEditTextPostComment;
    private ImageView mButtonPostComment;
    private View.OnClickListener clickListener;

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

        mAppBar.setVisibility(View.GONE);
        mLineAppBar.setVisibility(View.GONE);
        //mTitle.setText(interviewTitle);
//        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mListHeader = getLayoutInflater().inflate(R.layout.listheader_details_interview, null);
        mListFooter = getLayoutInflater().inflate(R.layout.listfooter_details_interview, null);

        //Header views
        mMediaPlayer = (ImageView) mListHeader.findViewById(R.id.mediaPlayer);
//        mMediaPlayerPlaceholder = (ImageView) mListHeader.findViewById(R.id.mediaPlayer_placeholder);
        mPivInterviewer = (ProfileImageView) mListHeader.findViewById(R.id.piv_interviewer);
        mPivNarrator = (ProfileImageView) mListHeader.findViewById(R.id.piv_narrator);
        mTextViewInterviewer = (TextView) mListHeader.findViewById(R.id.textView_interviewer);
        mTextViewNarrator = (TextView) mListHeader.findViewById(R.id.textView_narrator);
        mButtonInterviewer = (FrameLayout) mListHeader.findViewById(R.id.button_interviewer);
        mButtonNarrator = (FrameLayout) mListHeader.findViewById(R.id.button_narrator);
        mButtonComments = (TextView) mListHeader.findViewById(R.id.button_comments);
        mButtonOptions = (ImageView) mListHeader.findViewById(R.id.button_options);
        mTextViewInterviewTitle = (TextView) mListHeader.findViewById(R.id.textView_interview_title);
        mDate = (TextView) mListHeader.findViewById(R.id.textView_date);
        mLayoutScrollViewTags = (LinearLayout) mListHeader.findViewById(R.id.layout_scrollView_tags);
        //Footer views
        mTextViewTopic = (TextView) mListFooter.findViewById(R.id.textView_topic);
        mLayoutScrollViewVisibility = (LinearLayout) mListFooter.findViewById(R.id.layout_scrollView_visibility);
        mTextViewComments = (TextView) mListFooter.findViewById(R.id.textView_comments);
        mEditTextPostComment = (EditText) mListFooter.findViewById(R.id.editText_post_comment);
        mButtonPostComment = (ImageView) mListFooter.findViewById(R.id.button_post_comment);

        if (pictureLocalURI != null) {
            if (Uri.parse(pictureLocalURI) != null) {
                ImageView test = new ImageView(this);
                test.setImageURI(Uri.parse(pictureLocalURI));
                if (test.getDrawable() != null) {
                    mMediaPlayer.setImageURI(Uri.parse(pictureLocalURI));
//                    mMediaPlayerPlaceholder.setVisibility(View.GONE);
//                    mMediaPlayer.setVisibility(View.VISIBLE);
                }
            }
        }

        mPivInterviewer.getProfileImage().setImageURI(Uri.parse(interviewerPictureLocalURI));
        mPivNarrator.getProfileImage().setImageURI(Uri.parse(narratorPictureLocalURI));
        mTextViewInterviewer.setText(interviewerName);
        mTextViewNarrator.setText(narratorName);
        mButtonComments.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments) + " " + ( numberComments == 1 ? getString(R.string.text_comment) : getString(R.string.text_comments)));
        mTextViewInterviewTitle.setText(interviewTitle);
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

        mTextViewComments.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments));

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_interviewer:
                        Intent intent;
                        if (interviewerID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(DetailsInterviewActivity.this, OwnProfileInputActivity.class);
                        } else {
                            intent = new Intent(DetailsInterviewActivity.this, UserProfileInputActivity.class);
                            intent.putExtra("contactID", interviewerID);
                        }
                        startActivity(intent);
                        break;
                    case R.id.button_narrator:
                        if (narratorID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(DetailsInterviewActivity.this, OwnProfileInputActivity.class);
                        } else if (narratorIsUser) {
                            intent = new Intent(DetailsInterviewActivity.this, UserProfileInputActivity.class);
                            intent.putExtra("contactID", narratorID);
                        } else {
                            intent = new Intent(DetailsInterviewActivity.this, GuestProfileInputActivity.class);
                            intent.putExtra("contactID", narratorID);
                        }
                        startActivity(intent);
                        break;
                    case R.id.button_comments:
                        Toast.makeText(DetailsInterviewActivity.this, "Show Comments", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.button_options:
                        Toast.makeText(DetailsInterviewActivity.this, "Show Options", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mButtonInterviewer.setOnClickListener(clickListener);
        mButtonNarrator.setOnClickListener(clickListener);
        mButtonComments.setOnClickListener(clickListener);
        mButtonOptions.setOnClickListener(clickListener);


        mListLocalInterviewQuestionData = new ArrayList<>();

        mListViewInterviewQuestions = (ListView) findViewById(R.id.listView_interviewQuestions);
        mListViewInterviewQuestions.addHeaderView(mListHeader);
        mListViewInterviewQuestions.addFooterView(mListFooter);

        mListViewInterviewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //adding a ListViewHeader causes an increasing of position by 1, so it has to be decreased to get the right position value
                position--;
                Intent intent = new Intent(DetailsInterviewActivity.this, DetailsInterviewQuestionActivity.class);
                intent.putExtra("selectedInterviewQuestionID", mLocalInterviewQuestionDataAdapter.getItem(position).getContentID());

                intent.putExtra("question", mLocalInterviewQuestionDataAdapter.getItem(position).getQuestion());
                intent.putExtra("dateYearQuestion", mLocalInterviewQuestionDataAdapter.getItem(position).getDateYear());
                intent.putExtra("dateMonthQuestion", mLocalInterviewQuestionDataAdapter.getItem(position).getDateMonth());
                intent.putExtra("dateDayQuestion", mLocalInterviewQuestionDataAdapter.getItem(position).getDateDay());
                intent.putExtra("topic", topic);
                intent.putExtra("medium", medium);
                intent.putExtra("lengthQuestion", mLocalInterviewQuestionDataAdapter.getItem(position).getLength());
                intent.putExtra("pictureLocalURIQuestion", mLocalInterviewQuestionDataAdapter.getItem(position).getPictureLocalURI());
                intent.putExtra("interviewerID", interviewerID);
                intent.putExtra("interviewerName", interviewerName);
                intent.putExtra("interviewerPictureLocalURI", interviewerPictureLocalURI);
                intent.putExtra("narratorID", narratorID);
                intent.putExtra("narratorName", narratorName);
                intent.putExtra("narratorPictureLocalURI", narratorPictureLocalURI);
                intent.putExtra("narratorIsUser", narratorIsUser);

                String[] associatedUsersQuestion = new String[mLocalInterviewQuestionDataAdapter.getItem(position).getAssociatedUsers().size()];
                Iterator<String> iterator = mLocalInterviewQuestionDataAdapter.getItem(position).getAssociatedUsers().keySet().iterator();
                for (int i=0 ; i<associatedUsersQuestion.length ; i++) {
                    associatedUsersQuestion[i] = iterator.next();
                }
                intent.putExtra("associatedUsersQuestion", associatedUsersQuestion);

                String[] associatedGuestsQuestion = new String[mLocalInterviewQuestionDataAdapter.getItem(position).getAssociatedGuests().size()];
                iterator = mLocalInterviewQuestionDataAdapter.getItem(position).getAssociatedGuests().keySet().iterator();
                for (int i=0 ; i<associatedGuestsQuestion.length ; i++) {
                    associatedGuestsQuestion[i] = iterator.next();
                }
                intent.putExtra("associatedGuestsQuestion", associatedGuestsQuestion);

                String[] tagsQuestion = new String[mLocalInterviewQuestionDataAdapter.getItem(position).getTags().size()];
                iterator = mLocalInterviewQuestionDataAdapter.getItem(position).getTags().keySet().iterator();
                for (int i=0 ; i<tagsQuestion.length ; i++) {
                    tagsQuestion[i] = iterator.next();
                }
                intent.putExtra("tagsQuestion", tagsQuestion);

                intent.putExtra("numberComments", numberComments);

                startActivity(intent);
                finish();
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
