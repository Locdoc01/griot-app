package de.griot_app.griot.perform_interview;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.griot_app.griot.R;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.mainactivities.MainChooseFriendInputActivity;
import de.griot_app.griot.recordfunctions.RecordAudioActivity;
import de.griot_app.griot.recordfunctions.RecordVideoActivity;

/**
 * This Activity allows the user to choose a medium for an interview.
 * It's the third step of the "prepare-interview"-dialog.
 * It also shows selections of narrator and topic and allow to cancel them,
 * which would lead the user back to the appropriate page
 */
public class ChooseMediumInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ChooseMediumInputActivity.class.getSimpleName();

    //Constants for medium selection
    private static final int NONE = 0;
    private static final int VIDEO = 1;
    private static final int AUDIO = 2;

    //Holds the medium selection
    private int selectedMedium = NONE;

    //Intent-data
    private int narratorSelectedItemID;
    private String narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private Boolean narratorIsUser;

    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureURL;

    private int topicSelectedItemID;
    private int topicKey;
    private String topic;

    private String[] interviewQuestions;

    //Views
    private TextView mTextViewPerson;
    private ImageView mButtonCancelPerson;
    private TextView mTextViewTopic;
    private ImageView mButtonCancelTopic;
    private ImageView mButtonVideo;
    private ImageView mButtonAudio;

    private View.OnClickListener mClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);

        //Next-button is disabled at start, until a medium got selected
        mButtonRight.setEnabled(false);
        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));

        //Get intent-data
        narratorSelectedItemID = getIntent().getIntExtra("narratorSelectedItemID", -1);
        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureURL = getIntent().getStringExtra("narratorPictureURL");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", true);

        interviewerID = getIntent().getStringExtra("interviewerID");
        interviewerName = getIntent().getStringExtra("interviewerName");
        interviewerPictureURL = getIntent().getStringExtra("interviewerPictureURL");

        topicSelectedItemID = getIntent().getIntExtra("topicSelectedItemID", -1);
        topicKey = getIntent().getIntExtra("topicKey", -1);
        topic = getIntent().getStringExtra("topic");

        interviewQuestions = getIntent().getStringArrayExtra("allQuestions");

        //Get references to layout objects
        mTextViewPerson = (TextView) findViewById(R.id.textView_person);
        mButtonCancelPerson = (ImageView) findViewById(R.id.button_cancel_person);
        mTextViewTopic = (TextView) findViewById(R.id.textView_topic);
        mButtonCancelTopic = (ImageView) findViewById(R.id.button_cancel_topic);
        mButtonVideo = (ImageView) findViewById(R.id.button_video);
        mButtonAudio = (ImageView) findViewById(R.id.button_audio);

        //Show selections of narrator and topic
        mTextViewPerson.setText(getString(R.string.text_choosed_person) + ":  " + narratorName);
        mTextViewTopic.setText(getString(R.string.text_choosed_topic) + ":  " + topic);

        //Cancel-person-button (if button is pressed, the selection of narrator is canceled and the user got back to narrator selection.
        //Selection of topic will be kept
        mButtonCancelPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseMediumInputActivity.this, MainChooseFriendInputActivity.class);
                intent.putExtra("topicSelectedItemID", topicSelectedItemID);
                intent.putExtra("topicKey", topicKey);
                intent.putExtra("topic", topic);
                startActivity(intent);
                finish();
            }
        });

        //cancel-topic-button (if button is pressed, the selection of topic is canceled and the user got back to topic selection
        //Selection of narrator will be kept
        mButtonCancelTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseMediumInputActivity.this, ChooseTopicInputActivity.class);
                intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
                intent.putExtra("narratorID", narratorID);
                intent.putExtra("narratorName", narratorName);
                intent.putExtra("narratorPictureURL", narratorPictureURL);
                intent.putExtra("narratorIsUser", narratorIsUser);
                startActivity(intent);
                finish();
            }
        });

        //Manage the medium selection along with the next-button functionality.
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (selectedMedium) {
                    case NONE:
                        ((ImageView)v).setColorFilter(ContextCompat.getColor(ChooseMediumInputActivity.this, R.color.colorGriotBlue));
                        if (v.getId()==R.id.button_video) {
                            selectedMedium = VIDEO;
                        } else {
                            selectedMedium = AUDIO;
                        }
                        mButtonRight.setEnabled(true);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                        break;
                    case VIDEO:
                        if (v.getId()==R.id.button_video) {
                            mButtonVideo.setColorFilter(ContextCompat.getColor(ChooseMediumInputActivity.this, R.color.colorGriotDarkgrey));
                            selectedMedium = NONE;
                            mButtonRight.setEnabled(false);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                        } else {
                            mButtonVideo.setColorFilter(ContextCompat.getColor(ChooseMediumInputActivity.this, R.color.colorGriotDarkgrey));
                            mButtonAudio.setColorFilter(ContextCompat.getColor(ChooseMediumInputActivity.this, R.color.colorGriotBlue));
                            selectedMedium = AUDIO;
                            mButtonRight.setEnabled(true);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                        }
                        break;
                    case AUDIO:
                        if (v.getId()==R.id.button_video) {
                            mButtonVideo.setColorFilter(ContextCompat.getColor(ChooseMediumInputActivity.this, R.color.colorGriotBlue));
                            mButtonAudio.setColorFilter(ContextCompat.getColor(ChooseMediumInputActivity.this, R.color.colorGriotDarkgrey));
                            selectedMedium = VIDEO;
                            mButtonRight.setEnabled(true);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                        } else {
                            mButtonAudio.setColorFilter(ContextCompat.getColor(ChooseMediumInputActivity.this, R.color.colorGriotDarkgrey));
                            selectedMedium = NONE;
                            mButtonRight.setEnabled(false);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                        }
                        break;
                }
            }
        };

        mButtonVideo.setOnClickListener(mClickListener);
        mButtonAudio.setOnClickListener(mClickListener);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_choose_medium;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {}


    @Override
    protected void buttonLeftPressed() {
        Log.d(TAG, "buttonLeftPressed: ");

        finish();
    }

    //Navigates back to previous page of "prepare interview"-dialog.
    //Previous selections get sent to previous page by intent
    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        Intent intent = new Intent(ChooseMediumInputActivity.this, ChooseTopicInputActivity.class);
        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        intent.putExtra("topicSelectedItemID", topicSelectedItemID);
        startActivity(intent);
        finish();
    }

    //Starts the record activity for the selected medium
    //All relevant data for the interview or the dialog-pages get sent by intent
    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");
        Intent intent = new Intent();

        intent.putExtra("narratorSelectedItemID", narratorSelectedItemID);
        intent.putExtra("narratorID", narratorID);
        intent.putExtra("narratorName", narratorName);
        intent.putExtra("narratorPictureURL", narratorPictureURL);
        intent.putExtra("narratorIsUser", narratorIsUser);

        intent.putExtra("interviewerID", interviewerID);
        intent.putExtra("interviewerName", interviewerName);
        intent.putExtra("interviewerPictureURL", interviewerPictureURL);

        intent.putExtra("topicSelectedItemID", topicSelectedItemID);
        intent.putExtra("topicKey", topicKey);
        intent.putExtra("topic", topic);

        intent.putExtra("allQuestions", interviewQuestions);

        switch (selectedMedium) {
            case VIDEO:
                intent.setClass(this, RecordVideoActivity.class);
                startActivity(intent);
                break;
            case AUDIO:
                intent.setClass(this, RecordAudioActivity.class);
                startActivity(intent);
                break;
        }
        finish();
    }

}