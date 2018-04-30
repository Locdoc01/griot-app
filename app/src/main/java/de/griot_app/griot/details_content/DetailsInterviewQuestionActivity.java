package de.griot_app.griot.details_content;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import de.griot_app.griot.R;
import de.griot_app.griot.baseactivities.GriotBaseActivity;
import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.contacts_profiles.OwnProfileInputActivity;
import de.griot_app.griot.contacts_profiles.UserProfileInputActivity;
import de.griot_app.griot.views.ProfileImageView;
import de.griot_app.griot.views.TagView;

/**
 * Activity that shows the details of a selected interview question
 */
public class DetailsInterviewQuestionActivity extends GriotBaseActivity {

    private static final String TAG = DetailsInterviewQuestionActivity.class.getSimpleName();

    //Intent-data
    private String selectedInterviewQuestionID;
    private String question;
    private String dateYear;
    private String dateMonth;
    private String dateDay;
    private String topic;
    private String medium;
    private String length;
    private String pictureLocalURI;
    private String pictureURL;
    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureLocalURI;
    private String interviewerPictureURL;
    private String narratorID;
    private String narratorName;
    private String narratorPictureLocalURI;
    private String narratorPictureURL;
    private boolean narratorIsUser;
    private String[] associatedUsers;
    private String[] associatedGuests;
    private String[] tags;
    private int numberComments;

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
    private TextView mTextViewQuestion;
    private TextView mDate;
    private LinearLayout mLayoutScrollViewTags;
    private TextView mTextViewTopic;
    private LinearLayout mLayoutScrollViewVisibility;
    private View.OnClickListener mClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get intent data
        selectedInterviewQuestionID = getIntent().getStringExtra("selectedInterviewQuestionID");

        question = getIntent().getStringExtra("question");
        dateYear = getIntent().getStringExtra("dateYearQuestion");
        dateMonth = getIntent().getStringExtra("dateMonthQuestion");
        dateDay = getIntent().getStringExtra("dateDayQuestion");
        topic = getIntent().getStringExtra("topic");
        medium = getIntent().getStringExtra("medium");
        length = getIntent().getStringExtra("lengthQuestion");
        pictureLocalURI = getIntent().getStringExtra("pictureLocalURIQuestion");
        pictureURL = getIntent().getStringExtra("pictureURLQuestion");
        interviewerID = getIntent().getStringExtra("interviewerID");
        interviewerName = getIntent().getStringExtra("interviewerName");
        interviewerPictureLocalURI = getIntent().getStringExtra("interviewerPictureLocalURI");
        interviewerPictureURL = getIntent().getStringExtra("interviewerPictureURL");
        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureLocalURI = getIntent().getStringExtra("narratorPictureLocalURI");
        narratorPictureURL = getIntent().getStringExtra("narratorPictureURL");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", false);
        associatedUsers = getIntent().getStringArrayExtra("associatedUsersQuestion");
        associatedGuests = getIntent().getStringArrayExtra("associatedGuestsQuestion");
        tags = getIntent().getStringArrayExtra("tagsQuestion");
        numberComments = getIntent().getIntExtra("numberComments", 0);

        //Hides the app bar
        mAppBar.setVisibility(View.GONE);
        mLineAppBar.setVisibility(View.GONE);
        //mTitle.setText(question);
//        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        //Get references to layout objects
        mMediaPlayer = (ImageView) findViewById(R.id.mediaPlayer);
        mMediaPlayerForeground = (ImageView) findViewById(R.id.mediaPlayer_foreground);
        mPivInterviewer = (ProfileImageView) findViewById(R.id.piv_interviewer);
        mPivNarrator = (ProfileImageView) findViewById(R.id.piv_narrator);
        mTextViewInterviewer = (TextView) findViewById(R.id.textView_interviewer);
        mTextViewNarrator = (TextView) findViewById(R.id.textView_narrator);
        mButtonInterviewer = (FrameLayout) findViewById(R.id.button_interviewer);
        mButtonNarrator = (FrameLayout) findViewById(R.id.button_narrator);
        mButtonComments = (TextView) findViewById(R.id.button_comments);
        mButtonOptions = (ImageView) findViewById(R.id.button_options);
        mTextViewQuestion = (TextView) findViewById(R.id.textView_question);
        mDate = (TextView) findViewById(R.id.textView_date);
        mLayoutScrollViewTags = (LinearLayout) findViewById(R.id.layout_scrollView_tags);
        mTextViewTopic = (TextView) findViewById(R.id.textView_topic);
        mLayoutScrollViewVisibility = (LinearLayout) findViewById(R.id.layout_scrollView_visibility);

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

        //Initialize other views
        mPivInterviewer.loadImageFromSource(interviewerPictureURL);
        mPivNarrator.loadImageFromSource(narratorPictureURL);
        mTextViewInterviewer.setText(interviewerName);
        mTextViewNarrator.setText(narratorName);
        mButtonComments.setText("" + (numberComments==0 ? getString(R.string.text_none) : numberComments) + " " + ( numberComments == 1 ? getString(R.string.text_comment) : getString(R.string.text_comments)));
        mTextViewQuestion.setText(question);
        mDate.setText(dateDay + "." + dateMonth + "." + dateYear);

        // create TagViews and add them to ScrollView
        for (int i=0 ; i<tags.length ; i++) {
            TagView tagView = new TagView(this);
            tagView.setTag(tags[i]);
            tagView.setVisibilityDeleteButton(false);
            mLayoutScrollViewTags.addView(tagView);
        }

        mTextViewTopic.setText(topic);

        int width = getResources().getDimensionPixelSize(R.dimen.dimen_piv);
        int height = getResources().getDimensionPixelSize(R.dimen.dimen_piv);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        ProfileImageView pivInterviewer = new ProfileImageView(this);
        pivInterviewer.loadImageFromSource(interviewerPictureURL);
        mLayoutScrollViewVisibility.addView(pivInterviewer);
        pivInterviewer.setLayoutParams(params);
        if (!interviewerID.equals(narratorID)) {
            ProfileImageView pivNarrator = new ProfileImageView(this);
            pivNarrator.loadImageFromSource(narratorPictureURL);
            mLayoutScrollViewVisibility.addView(pivNarrator);
            pivNarrator.setLayoutParams(params);
        }
        //TODO: add associated users & guests to the ScrollView

        // set OnClickListener to button views
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_interviewer:
                        Log.d(TAG, "interviewer clicked: ");
                        Intent intent;
                        if (interviewerID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(DetailsInterviewQuestionActivity.this, OwnProfileInputActivity.class);
                        } else {
                            intent = new Intent(DetailsInterviewQuestionActivity.this, UserProfileInputActivity.class);
                            intent.putExtra("contactID", interviewerID);
                        }
                        startActivity(intent);
                        break;
                    case R.id.button_narrator:
                        Log.d(TAG, "narrator clicked: ");
                        if (narratorID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            intent = new Intent(DetailsInterviewQuestionActivity.this, OwnProfileInputActivity.class);
                        } else if (narratorIsUser) {
                            intent = new Intent(DetailsInterviewQuestionActivity.this, UserProfileInputActivity.class);
                            intent.putExtra("contactID", narratorID);
                        } else {
                            intent = new Intent(DetailsInterviewQuestionActivity.this, GuestProfileInputActivity.class);
                            intent.putExtra("contactID", narratorID);
                        }
                        startActivity(intent);
                        break;
                    case R.id.button_comments:
                        Log.d(TAG, "comments clicked: ");
                        Toast.makeText(DetailsInterviewQuestionActivity.this, "Show Comments", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.button_options:
                        Log.d(TAG, "options clicked: ");
                        Toast.makeText(DetailsInterviewQuestionActivity.this, "Show Options", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mButtonInterviewer.setOnClickListener(mClickListener);
        mButtonNarrator.setOnClickListener(mClickListener);
        mButtonComments.setOnClickListener(mClickListener);
        mButtonOptions.setOnClickListener(mClickListener);

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_details_interview_question;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}

}
