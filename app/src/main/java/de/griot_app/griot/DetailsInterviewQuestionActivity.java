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


public class DetailsInterviewQuestionActivity extends GriotBaseActivity {

    private static final String TAG = DetailsInterviewQuestionActivity.class.getSimpleName();

    // intent-data
    private String selectedInterviewQuestionID;
    private String question;
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
    private ImageView mMediaPlayer;
    private ImageView mMediaPlayerPlaceholder;
    private ProfileImageView mPivInterviewer;
    private ProfileImageView mPivNarrator;
    private TextView mTextViewInterviewer;
    private TextView mTextViewNarrator;
    private TextView mTextViewComments;
    private ImageView mButtonOptions;
    private TextView mTextViewQuestion;
    private TextView mDate;
    private LinearLayout mLayoutScrollViewTags;
    private TextView mTextViewTopic;
    private LinearLayout mLayoutScrollViewVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedInterviewQuestionID = getIntent().getStringExtra("selectedInterviewQuestionID");

        question = getIntent().getStringExtra("question");
        dateYear = getIntent().getStringExtra("dateYearQuestion");
        dateMonth = getIntent().getStringExtra("dateMonthQuestion");
        dateDay = getIntent().getStringExtra("dateDayQuestion");
        topic = getIntent().getStringExtra("topic");
        medium = getIntent().getStringExtra("medium");
        length = getIntent().getStringExtra("lengthQuestion");
        pictureLocalURI = getIntent().getStringExtra("pictureLocalURIQuestion");
        interviewerID = getIntent().getStringExtra("interviewerID");
        interviewerName = getIntent().getStringExtra("interviewerName");
        interviewerPictureLocalURI = getIntent().getStringExtra("interviewerPictureLocalURI");
        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureLocalURI = getIntent().getStringExtra("narratorPictureLocalURI");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", false);
        associatedUsers = getIntent().getStringArrayExtra("associatedUsersQuestion");
        associatedGuests = getIntent().getStringArrayExtra("associatedGuestsQuestion");
        tags = getIntent().getStringArrayExtra("tagsQuestion");
        numberComments = getIntent().getIntExtra("numberComments", 0);

        mAppBar.setVisibility(View.GONE);
        mLineAppBar.setVisibility(View.GONE);
        //mTitle.setText(question);
//        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));


        mMediaPlayer = (ImageView) findViewById(R.id.mediaPlayer);
        mMediaPlayerPlaceholder = (ImageView) findViewById(R.id.mediaPlayer_placeholder);
        mPivInterviewer = (ProfileImageView) findViewById(R.id.piv_interviewer);
        mPivNarrator = (ProfileImageView) findViewById(R.id.piv_narrator);
        mTextViewInterviewer = (TextView) findViewById(R.id.textView_interviewer);
        mTextViewNarrator = (TextView) findViewById(R.id.textView_narrator);
        mTextViewComments = (TextView) findViewById(R.id.textView_comments);
        mButtonOptions = (ImageView) findViewById(R.id.button_options);
        mTextViewQuestion = (TextView) findViewById(R.id.textView_question);
        mDate = (TextView) findViewById(R.id.textView_date);
        mLayoutScrollViewTags = (LinearLayout) findViewById(R.id.layout_scrollView_tags);
        mTextViewTopic = (TextView) findViewById(R.id.textView_topic);
        mLayoutScrollViewVisibility = (LinearLayout) findViewById(R.id.layout_scrollView_visibility);

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
        mTextViewComments.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments) + " " + ( numberComments == 1 ? getString(R.string.text_comment) : getString(R.string.text_comments)));
        mTextViewQuestion.setText(question);
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
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_details_interview_question;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

}
