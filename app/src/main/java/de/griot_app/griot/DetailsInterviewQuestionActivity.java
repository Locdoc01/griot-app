package de.griot_app.griot;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
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
    private ImageView mMediaPlayerForeground;
//    private ImageView mMediaPlayerPlaceholder;
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
    private View.OnClickListener clickListener;

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
        mMediaPlayerForeground = (ImageView) findViewById(R.id.mediaPlayer_foreground);
//        mMediaPlayerPlaceholder = (ImageView) findViewById(R.id.mediaPlayer_placeholder);
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

        if (pictureLocalURI != null) {
            if (Uri.parse(pictureLocalURI) != null) {
                ImageView test = new ImageView(this);
                test.setImageURI(Uri.parse(pictureLocalURI));
                if (test.getDrawable() != null) {
                    mMediaPlayer.setImageURI(Uri.parse(pictureLocalURI));
                    if (medium.equals("audio")) {
                        mMediaPlayer.setScaleType(ImageView.ScaleType.CENTER_CROP);

                        ColorMatrix matrix = new ColorMatrix();
                        matrix.setSaturation(0);
                        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
                        mMediaPlayer.setColorFilter(filter);
                        mMediaPlayerForeground.setVisibility(View.VISIBLE);
                    }
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

        clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_interviewer:
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
                        Toast.makeText(DetailsInterviewQuestionActivity.this, "Show Comments", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.button_options:
                        Toast.makeText(DetailsInterviewQuestionActivity.this, "Show Options", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        mButtonInterviewer.setOnClickListener(clickListener);
        mButtonNarrator.setOnClickListener(clickListener);
        mButtonComments.setOnClickListener(clickListener);
        mButtonOptions.setOnClickListener(clickListener);

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
