package de.griot_app.griot;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.griot_app.griot.adapters.LocalInterviewQuestionDataAdapter;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.LocalInterviewQuestionData;
import de.griot_app.griot.recordfunctions.RecordActivity;
import de.griot_app.griot.recordfunctions.RecordAudioActivity;
import de.griot_app.griot.recordfunctions.RecordVideoActivity;
import de.griot_app.griot.views.TagView;

public class ReviewInterviewInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ReviewInterviewInputActivity.class.getSimpleName();

    //intent-data
    protected int narratorSelectedItemID;
    protected String narratorID;
    protected String narratorName;
    protected String narratorPictureURL;
    protected Boolean narratorIsUser;

    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureURL;

    protected int topicSelectedItemID;
    protected int topicKey;
    protected String topic;

    protected int medium;

    protected String dateYear;
    protected String dateMonth;
    protected String dateDay;

    protected String[] allQuestions;

    protected String[] allMediaSingleFilePaths;

    protected String[] recordedMediaSingleFilePaths;
    protected String[] recordedCoverFilePaths;
    protected String[] recordedQuestions;
    protected String[] recordedQuestionLengths;

    protected String interviewDir;

    // ListView, that holds the interview questions
    private ListView mListViewInterviewQuestions;

    // data list
    private ArrayList<LocalInterviewQuestionData> mListInterviewQuestionData;

    //Data-View-Adapter for the ListView
    private LocalInterviewQuestionDataAdapter mLocalInterviewQuestionDataAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_review_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);

        // gets intent-data about previous selections
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

        medium = getIntent().getIntExtra("medium", -1);

        dateYear = getIntent().getStringExtra("dateYear");
        dateMonth = getIntent().getStringExtra("dateMonth");
        dateDay = getIntent().getStringExtra("dateDay");

        allQuestions = getIntent().getStringArrayExtra("allQuestions");
        allMediaSingleFilePaths = getIntent().getStringArrayExtra("allMediaSingleFilePaths");

        recordedQuestions = getIntent().getStringArrayExtra("recordedQuestions");
        recordedQuestionLengths = getIntent().getStringArrayExtra("recordedQuestionLengths");

        recordedMediaSingleFilePaths = getIntent().getStringArrayExtra("recordedMediaSingleFilePaths");
        recordedCoverFilePaths = getIntent().getStringArrayExtra("recordedCoverFilePaths");

        interviewDir = getIntent().getStringExtra("interviewDir");

        mListViewInterviewQuestions = (ListView) findViewById(R.id.listView_review_interviews);

        mListInterviewQuestionData = new ArrayList<>();

        for ( int i=0 ; i<recordedQuestions.length ; i++ ) {
            //DataClass-object here serves only as a holder for the ListView data (NOT for Firebase)
            LocalInterviewQuestionData data = new LocalInterviewQuestionData();
            data.setQuestion(recordedQuestions[i]);

            int seconds=0, hours, minutes;
            try { seconds = (int) (Double.parseDouble(recordedQuestionLengths[i]) / 1000.0); } catch (Exception e) {}
            hours = (int)((double)seconds/(60*60));
            seconds = seconds - hours * 60*60;
            minutes = (int)((double)seconds/60);
            seconds = seconds - minutes * 60;
            data.setLength("" + (hours==0 ? "" : hours + ":") + (minutes<10 ? "0" + minutes : minutes) + ":" + (seconds<10 ? "0" + seconds : seconds));

            data.setDateYear(dateYear);
            data.setDateMonth(dateMonth);
            data.setDateDay(dateDay);

            data.setPictureLocalURI(recordedCoverFilePaths[i]);
            mListInterviewQuestionData.add(data);
        }

        // set the adapter
        mLocalInterviewQuestionDataAdapter = new LocalInterviewQuestionDataAdapter(ReviewInterviewInputActivity.this, mListInterviewQuestionData);
        mListViewInterviewQuestions.setAdapter(mLocalInterviewQuestionDataAdapter);

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_review_interview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }


    @Override
    protected void buttonLeftPressed() {
        Log.d(TAG, "buttonLeftPressed: ");
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_input, null);

        final TextView textViewInputDialog = (TextView) dialogView.findViewById(R.id.textView_inputDialog);
        final EditText editTextInputDialog = (EditText) dialogView.findViewById(R.id.editText_inputDialog);

        textViewInputDialog.setText(getString(R.string.dialog_title_cancel_interview));
        editTextInputDialog.setVisibility(View.GONE);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomDialogTheme));
        // set dialog view
        alertDialogBuilder.setView(dialogView);
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(getString(R.string.button_yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                finish();
                            }
                        })
                .setNegativeButton(getString(R.string.button_no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create and show alert dialog
        alertDialogBuilder.create().show();
    }

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        Intent intent = new Intent();
        if (medium==RecordActivity.MEDIUM_AUDIO) {
            intent.setClass(this, RecordAudioActivity.class);
        } else if (medium==RecordActivity.MEDIUM_VIDEO) {
            intent.setClass(this, RecordVideoActivity.class);
        }
        // Navigates to next page of "prepare interview"-dialog
        // All relevant data for the interview or the dialog-pages get sent to the next page.
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

        intent.putExtra("dateYear", dateYear);
        intent.putExtra("dateMonth", dateMonth);
        intent.putExtra("dateDay", dateDay);

        intent.putExtra("allQuestions", allQuestions);
        intent.putExtra("allMediaSingleFilePaths", allMediaSingleFilePaths);

        intent.putExtra("recordedQuestions", recordedQuestions);
        intent.putExtra("recordedQuestionLengths", recordedQuestionLengths);
        intent.putExtra("recordedMediaSingleFilePaths", recordedMediaSingleFilePaths);
        intent.putExtra("recordedCoverFilePaths", recordedCoverFilePaths);

        intent.putExtra("interviewDir", interviewDir);

        intent.putExtra("recordedQuestionsCount", recordedQuestions.length);

        //TODO: Daten zu den einzeln InterviewFragen weiterreichen (Tags, etc)

        startActivity(intent);
        finish();

    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        Intent intent = new Intent(this, SaveInterviewInputActivity.class);

        // Navigates to next page of "prepare interview"-dialog
        // All relevant data for the interview or the dialog-pages get sent to the next page.
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

        intent.putExtra("medium", medium);      // TODO evt. überflüssig

        intent.putExtra("dateYear", dateYear);
        intent.putExtra("dateMonth", dateMonth);
        intent.putExtra("dateDay", dateDay);

        intent.putExtra("allQuestions", allQuestions);
        intent.putExtra("allMediaSingleFilePaths", allMediaSingleFilePaths);

        intent.putExtra("recordedQuestions", recordedQuestions);
        intent.putExtra("recordedQuestionLengths", recordedQuestionLengths);
        intent.putExtra("recordedMediaSingleFilePaths", recordedMediaSingleFilePaths);
        intent.putExtra("recordedCoverFilePaths", recordedCoverFilePaths);

        //TODO: Daten zu den einzeln InterviewFragen weiterreichen (Tags, etc)

        startActivity(intent);
        finish();
    }

}