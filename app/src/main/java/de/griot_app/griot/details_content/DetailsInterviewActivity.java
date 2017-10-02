package de.griot_app.griot.details_content;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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

import de.griot_app.griot.R;
import de.griot_app.griot.adapters.LocalInterviewQuestionDataAdapter;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.contacts_profiles.OwnProfileInputActivity;
import de.griot_app.griot.contacts_profiles.UserProfileInputActivity;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.views.TagView;

/**
 * Activity that shows the details of a selected interview, including al of its belonging interview questions
 */
public class DetailsInterviewActivity extends GriotBaseActivity {

    private static final String TAG = DetailsInterviewActivity.class.getSimpleName();

    //Intent-data
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

    //List header & footer
    private View mListHeader;
    private View mListFooter;

    //Views
    private ImageView mMediaPlayer;
    private ImageView mMediaPlayerForeground;
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
    private View.OnClickListener mClickListener;

    //ListView, that holds the interview question items
    private ListView mListViewInterviewQuestions;

    //ArrayList containing the data of interview questions
    private ArrayList<LocalInterviewQuestionData> mListLocalInterviewQuestionData;

    //Data-View-Adapter for the ListView
    private LocalInterviewQuestionDataAdapter mLocalInterviewQuestionDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get intent data
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

        //Hides the app bar
        mAppBar.setVisibility(View.GONE);
        mLineAppBar.setVisibility(View.GONE);
        //mTitle.setText(interviewTitle);
//        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        //Inflates ListView header & footer layouts
        mListHeader = getLayoutInflater().inflate(R.layout.listheader_details_interview, null);
        mListFooter = getLayoutInflater().inflate(R.layout.listfooter_details_interview, null);

        //Get references to header layout objects
        mMediaPlayer = (ImageView) mListHeader.findViewById(R.id.mediaPlayer);
        mMediaPlayerForeground = (ImageView) mListHeader.findViewById(R.id.mediaPlayer_foreground);
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
        //Get references to footer layout objects
        mTextViewTopic = (TextView) mListFooter.findViewById(R.id.textView_topic);
        mLayoutScrollViewVisibility = (LinearLayout) mListFooter.findViewById(R.id.layout_scrollView_visibility);
        mTextViewComments = (TextView) mListFooter.findViewById(R.id.textView_comments);
        mEditTextPostComment = (EditText) mListFooter.findViewById(R.id.editText_post_comment);
        mButtonPostComment = (ImageView) mListFooter.findViewById(R.id.button_post_comment);

        //Initialize mediaPlayer
        if (pictureLocalURI != null) {
            if (Uri.parse(pictureLocalURI) != null) {
                ImageView test = new ImageView(this);
                test.setImageURI(Uri.parse(pictureLocalURI));
                if (test.getDrawable() != null) {
                    mMediaPlayer.setImageURI(Uri.parse(pictureLocalURI));
                    //if the interview got recorded as audio, the mediaCover will show the narrator profile picture in black/white and darkened
                    if (medium.equals("audio")) {
                        mMediaPlayer.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        mMediaPlayer.setColorFilter(filter);
                        mMediaPlayerForeground.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

        //Initialize other header & footer views
        mPivInterviewer.getProfileImage().setImageURI(Uri.parse(interviewerPictureLocalURI));
        mPivNarrator.getProfileImage().setImageURI(Uri.parse(narratorPictureLocalURI));
        mTextViewInterviewer.setText(interviewerName);
        mTextViewNarrator.setText(narratorName);
        mButtonComments.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments) + " " + ( numberComments == 1 ? getString(R.string.text_comment) : getString(R.string.text_comments)));
        mTextViewInterviewTitle.setText(interviewTitle);
        mDate.setText(dateDay + "." + dateMonth + "." + dateYear);

        // create TagViews and add them to ScrollView
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
        //TODO: add associated users & guests to the ScrollView

        mTextViewComments.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments));

        // set OnClickListener to button views
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_interviewer:
                        Log.d(TAG, "interviewer clicked: ");
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
                        Log.d(TAG, "narrator clicked: ");
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
                        Log.d(TAG, "comments clicked: ");
                        Toast.makeText(DetailsInterviewActivity.this, "Show Comments", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.button_options:
                        Log.d(TAG, "options clicked: ");
                        Toast.makeText(DetailsInterviewActivity.this, "Show Options", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mButtonInterviewer.setOnClickListener(mClickListener);
        mButtonNarrator.setOnClickListener(mClickListener);
        mButtonComments.setOnClickListener(mClickListener);
        mButtonOptions.setOnClickListener(mClickListener);


        mListLocalInterviewQuestionData = new ArrayList<>();

        //get reference to ListView and add header & footer
        mListViewInterviewQuestions = (ListView) findViewById(R.id.listView_interviewQuestions);
        mListViewInterviewQuestions.addHeaderView(mListHeader);
        mListViewInterviewQuestions.addFooterView(mListFooter);

        // set OnItemClickListener to the ListView to start DetailsInterviewQuestionDataActivity for the clicked Question
        mListViewInterviewQuestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //adding a ListViewHeader causes an increasing of position by 1, so it has to be decreased to get the right position value
                position--;

                //create Intent and put extra data to it
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


        // Set ValueEventListener to obtains all interview question data from Firebase
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