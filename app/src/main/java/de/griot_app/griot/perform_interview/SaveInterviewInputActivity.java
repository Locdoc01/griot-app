package de.griot_app.griot.perform_interview;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.griot_app.griot.contacts_profiles.GuestProfileInputActivity;
import de.griot_app.griot.R;
import de.griot_app.griot.adapters.CombinedPersonListCreator;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.InterviewData;
import de.griot_app.griot.dataclasses.InterviewQuestionData;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.recordfunctions.RecordActivity;
import de.griot_app.griot.views.ProfileImageView;

public class SaveInterviewInputActivity extends GriotBaseInputActivity {

    private static final String TAG = SaveInterviewInputActivity.class.getSimpleName();

    //intent-data
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

    private String title;
    private int medium;
    private String dateYear;
    private String dateMonth;
    private String dateDay;

    private String[] allQuestions;
    private String[] allMediaSingleFilePaths;

    private String[] recordedMediaSingleFilePaths;
    private String[] recordedCoverFilePaths;
    private String[] recordedQuestions;
    private int[] recordedQuestionIndices;
    private String[] recordedQuestionLengths;

    private int recordedQuestionsCount;
    private String interviewDir;
    private String[][] tags;
    private boolean mTitleChosen = false;

    private String interviewID;

    //Necessary to determine, if all uploads are finished (either succefully or not)
    private int mUploadMediaSuccessCount = 0;
    private int mUploadMediaFailureCount = 0;
    private int mUploadMediaCoverSuccessCount = 0;
    private int mUploadMediaCoverFailureCount = 0;

    //Necessary to determine, if a new push-key has to be obtained from Firebase for storing the interview, or not.
    //A new push-key is only obtained on first attempt, so that in case of a faliure or interruption the interview
    //gets saved to the same database-location at further upload attempts. The incomplete data from the failed attempt
    //will be overwritten in that way. Otherwise there would pile up dead data entrys in the database, which can never
    //be accessed.
    private boolean mFirstSaveAttempt = true;

    //Views
    private EditText mEditTextTitle;
    private TextView mTextViewTitle;
    private ImageView mButtonCheckTitle;
    private ImageView mButtonCancelTitle;
    private TextView mTextViewPersonTop;
    private ProfileImageView mPivPerson;
    private TextView mTextViewPerson;
    private ImageView mButtonCancelPerson;
    private ImageView mLinePerson;

    //ListView, that holds the contact list
    private ListView mListViewPersons;

    //Creates a ListView of person contacts as a combination of own user data, guest list data, friend list data and approriate category headings
    private CombinedPersonListCreator mCombinedListCreator;

    //Firebase queries for the CombinedPersonListCreator
    private Query mQueryGuests;
    private Query mQueryFriends;

    private View.OnClickListener mClicklistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_save_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_save);
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

        title = getIntent().getStringExtra("title");
        medium = getIntent().getIntExtra("medium", -1);
        dateYear = getIntent().getStringExtra("dateYear");
        dateMonth = getIntent().getStringExtra("dateMonth");
        dateDay = getIntent().getStringExtra("dateDay");

        allQuestions = getIntent().getStringArrayExtra("allQuestions");
        allMediaSingleFilePaths = getIntent().getStringArrayExtra("allMediaSingleFilePaths");

        recordedQuestions = getIntent().getStringArrayExtra("recordedQuestions");
        recordedQuestionIndices = getIntent().getIntArrayExtra("recordedQuestionIndices");
        recordedQuestionLengths = getIntent().getStringArrayExtra("recordedQuestionLengths");

        recordedMediaSingleFilePaths = getIntent().getStringArrayExtra("recordedMediaSingleFilePaths");
        recordedCoverFilePaths = getIntent().getStringArrayExtra("recordedCoverFilePaths");

        recordedQuestionsCount = getIntent().getIntExtra("recordedQuestionsCount", 0);
        interviewDir = getIntent().getStringExtra("interviewDir");

        tags = new String[recordedQuestionsCount][];
        for (int i=0 ; i<recordedQuestionsCount ; i++) {
            tags[i] = getIntent().getStringArrayExtra("tags" + i);
        }

        //Get references to layout objects
        mEditTextTitle = (EditText) findViewById(R.id.editText_title);
        mTextViewTitle = (TextView) findViewById(R.id.textView_title);
        mButtonCheckTitle = (ImageView) findViewById(R.id.button_check_title);
        mButtonCancelTitle = (ImageView) findViewById(R.id.button_cancel_title);
        mTextViewPersonTop = (TextView) findViewById(R.id.textView_person_top);
        mPivPerson = (ProfileImageView) findViewById(R.id.piv_person);
        mTextViewPerson = (TextView) findViewById(R.id.textView_person);
        mButtonCancelPerson = (ImageView) findViewById(R.id.button_cancel_person);
        mLinePerson = (ImageView) findViewById(R.id.line_person);
        mListViewPersons = (ListView) findViewById(R.id.listView_persons);
        mListViewPersons.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        //Get the profile image for the narrator
        File file = null;
        try { file = File.createTempFile("narrator" + "_", ".jpg"); } catch (Exception e) {}
        final String path = file.getPath();
        try {
            mStorageRef = mStorage.getReferenceFromUrl(narratorPictureURL);
            mStorageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    mPivPerson.getProfileImage().setImageURI(Uri.parse(path));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error downloading narrator profile image file");
                }
            });
        } catch (Exception e) {}
        mTextViewPerson.setText(narratorName);

        //Set the OnClickListener for clickable views
        mClicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    //If check title is clicked, the title will be validated. If valid, the EditText will be exchanged with a TextView, which shows the title,
                    // and check title button will be exchanged with a cancel title button. If the title is not valid, an error message is shown at the EditText
                    case R.id.button_check_title:
                        Log.e(TAG, "check title clicked");
                        if (validateTitle()) {
                            try { ((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); } catch (Exception e){}
                            mTextViewTitle.setText(mEditTextTitle.getText().toString().trim());
                            mTextViewTitle.setVisibility(View.VISIBLE);
                            mEditTextTitle.setVisibility(View.GONE);
                            title = mEditTextTitle.getText().toString().trim();
                            mTitleChosen = true;
                            mButtonCheckTitle.setVisibility(View.GONE);
                            mButtonCancelTitle.setVisibility(View.VISIBLE);
                            if (narratorSelectedItemID>=0 && mButtonRight.getText().toString().equals(getString(R.string.button_save))) {
                                mButtonRight.setEnabled(true);
                                mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                            }
                        }
                        break;
                    //If cancel title is clicked, the title will be deleted and the TextView will be exchanged with the EditText again. Also the buttons are exchanged again.
                    case R.id.button_cancel_title:
                        Log.e(TAG, "cancel title clicked");
                        mEditTextTitle.setText("");
                        mEditTextTitle.setVisibility(View.VISIBLE);
                        mTextViewTitle.setVisibility(View.GONE);
                        title = null;
                        mTitleChosen = false;
                        mButtonCheckTitle.setVisibility(View.VISIBLE);
                        mButtonCancelTitle.setVisibility(View.GONE);
                        if (narratorSelectedItemID>=0 && mButtonRight.getText().toString().equals(getString(R.string.button_save))) {
                            mButtonRight.setEnabled(false);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                        }
                        break;
                    //If cancel person is clicked, the narrator will be canceled. A ListView will show up, which allows the user to choose another person, just like in MainChoosePersonInputActivity
                    case R.id.button_cancel_person:
                        Log.e(TAG, "cancel person clicked");
                        mTextViewPersonTop.setText(getString(R.string.question_which_person_recorded));
                        mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(false);
                        mCombinedListCreator.getAdapter().notifyDataSetChanged();
                        narratorSelectedItemID = -1;
                        narratorID = null;
                        narratorName = null;
                        narratorPictureURL = null;
                        mListViewPersons.setVisibility(View.VISIBLE);
                        mPivPerson.setVisibility(View.GONE);
                        mTextViewPerson.setVisibility(View.GONE);
                        mButtonCancelPerson.setVisibility(View.GONE);
                        mLinePerson.setVisibility(View.GONE);
                        mButtonRight.setText(getString(R.string.button_next));
                        mButtonRight.setEnabled(false);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                        break;
                }
            }
        };
        mButtonCheckTitle.setOnClickListener(mClicklistener);
        mButtonCancelTitle.setOnClickListener(mClicklistener);
        mButtonCancelPerson.setOnClickListener(mClicklistener);

        //If the "done" button on the keyboard is clicked, a click to check title button is performed, so that the EditText for the title will be exchanged with a TextView
        mEditTextTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE) {
                    mButtonCheckTitle.performClick();
                    return true;
                }
                return false;
            }
        });

        //Manages the narrator selection along with the next-button functionality. The selection gets stored in the appropriate LocalPersonData-object
        mListViewPersons.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCombinedListCreator.getAdapter().getItem(position).getFirstname().equals(getString(R.string.text_add_guest))) {
                    Intent intent = new Intent(SaveInterviewInputActivity.this, GuestProfileInputActivity.class);
                    startActivity(intent);
                } else {
                    if (narratorSelectedItemID < 0) {
                        narratorSelectedItemID = position;
                        mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(true);
                        mButtonRight.setEnabled(true);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                    } else {
                        if (narratorSelectedItemID == position) {
                            mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(false);
                            narratorSelectedItemID = -1;
                            mButtonRight.setEnabled(false);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                        } else {
                            mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(false);
                            narratorSelectedItemID = position;
                            mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID).setSelected(true);
                            mButtonRight.setEnabled(true);
                            mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                        }
                    }
                    mCombinedListCreator.getAdapter().notifyDataSetChanged();
                }
            }
        });
    }

    //checks, if the EditText for the title is not empty
    private boolean validateTitle() {
        Log.d(TAG, "validateTitle: ");

        boolean valid = true;
        if (TextUtils.isEmpty(mEditTextTitle.getText().toString().trim())) {
            mEditTextTitle.setError(getResources().getString(R.string.error_required_field));
            valid = false;
        } else {
            mEditTextTitle.setError(null);
        }
        return valid;
    }

    //If a title is already given on start, it will be shown
    @Override
    protected void onStart() {
        super.onStart();

        mEditTextTitle.setText(title);
        if (!TextUtils.isEmpty(mEditTextTitle.getText().toString().trim())) {
            mButtonCheckTitle.performClick();
        }
    }


    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_save_interview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    protected void doOnStartAfterLoadingUserInformation() {
        mQueryGuests = mDatabaseRootReference.child("guests");   //TODO: specify to get only guests of the current user
        mQueryFriends = mDatabaseRootReference.child("users");  //TODO: specify to get only users who are in a friendship with the current user

        //Create the combined ListView of person contacts
        mCombinedListCreator = new CombinedPersonListCreator(SaveInterviewInputActivity.this, -1, mOwnUserData, mListViewPersons);
        mCombinedListCreator.setMode(CombinedPersonListCreator.PERSONS_CHOOSE_MODE);
        mCombinedListCreator.add(mQueryGuests);
        mCombinedListCreator.add(mQueryFriends);
        mCombinedListCreator.loadData();
    }


    //Button cancels the interview, after a security inquiry. The interview data will be lost, but the recorded media files will be kept on the device
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

    //Button brings the user one step back to ReviewINterviewActivity. The interview data get sent by intent
    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        Intent intent = new Intent(this, ReviewInterviewInputActivity.class);

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

        intent.putExtra("title", title);
        intent.putExtra("medium", medium);
        intent.putExtra("dateYear", dateYear);
        intent.putExtra("dateMonth", dateMonth);
        intent.putExtra("dateDay", dateDay);

        intent.putExtra("allQuestions", allQuestions);
        intent.putExtra("allMediaSingleFilePaths", allMediaSingleFilePaths);

        intent.putExtra("recordedQuestions", recordedQuestions);
        intent.putExtra("recordedQuestionIndices", recordedQuestionIndices);
        intent.putExtra("recordedQuestionLengths", recordedQuestionLengths);
        intent.putExtra("recordedMediaSingleFilePaths", recordedMediaSingleFilePaths);
        intent.putExtra("recordedCoverFilePaths", recordedCoverFilePaths);

        intent.putExtra("recordedQuestionsCount", recordedQuestions.length);
        intent.putExtra("interviewDir", interviewDir);

        for (int i=0 ; i<recordedQuestionsCount ; i++) {
            intent.putExtra("tags" + i, tags[i]);
        }

        startActivity(intent);
        finish();
    }

    //Button has two different functions, depending on the state of narrator selection.
    //If the selection of narrator was cancelled and the contact list is visible, then the button serves as a next-button to accept a new selection.
    //In that state it is disabled, until a new person got selected.
    //If a narrator is selected, the button serves as save-button, which uploads the interview data to Firebase Database and the media files to Firebase Storage.

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        if (mButtonRight.getText().toString().equals(getString(R.string.button_next))) {
            mTextViewPersonTop.setText(getString(R.string.text_recorded_person));

            LocalPersonData item = mCombinedListCreator.getAdapter().getItem(narratorSelectedItemID);
            narratorID = item.getContactID();
            narratorName = item.getFirstname() + (item.getLastname() == null ? "" : " " + item.getLastname());
            narratorPictureURL = item.getPictureURL();
            narratorIsUser = item.getIsUser();
            mPivPerson.getProfileImage().setImageURI(Uri.parse(item.getPictureLocalURI()));
            mTextViewPerson.setText(narratorName);

            mListViewPersons.setVisibility(View.GONE);
            mPivPerson.setVisibility(View.VISIBLE);
            mTextViewPerson.setVisibility(View.VISIBLE);
            mButtonCancelPerson.setVisibility(View.VISIBLE);
            mLinePerson.setVisibility(View.VISIBLE);
            mButtonRight.setText(getString(R.string.button_save));
            if (mTitleChosen) {
                mButtonRight.setEnabled(true);
                mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
            } else {
                mButtonRight.setEnabled(false);
                mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
            }
        } else if (mButtonRight.getText().toString().equals(getString(R.string.button_save))) {
            saveInterview();
        }
    }

    //saves the interview to Firebase. The interview data & interview question data are uploaded to Firebase Database.
    // The media files are uploaded to Firebase Storage. During that process a progress bar is shown

    private void saveInterview() {
        Log.d(TAG, "saveInterview: ");
        showProgressBar(getString(R.string.progress_uploading_interview));

        final DatabaseReference interviewsReference = mDatabaseRootReference.child("interviews");

        // the boolean mFirstSaveAttempt ensures, that in case of an interrupted upload the interview will be saved to the same push-key at further attempts.
        // Thus the potentially incomplete upload will be overwritten by the complete one.
        // Otherwise there could pile up dead interview fragments in the database, which cannot be accessed.
        if (mFirstSaveAttempt) {
            interviewID = interviewsReference.push().getKey();
        }


        final ArrayList<String> interviewQuestionIDs = new ArrayList<>();
        final DatabaseReference interviewQuestionsReference = mDatabaseRootReference.child("interviewQuestions");
        if (mFirstSaveAttempt) {
            for (int i = 0; i < recordedQuestionsCount; i++) {
                interviewQuestionIDs.add(interviewQuestionsReference.push().getKey());
            }
        }
        mFirstSaveAttempt = false;
        for (int i=0 ; i<recordedQuestionsCount ; i++) {
            //Creates an data-object for every interview question and fills it with the collected data
            final String interviewQuestionID = interviewQuestionIDs.get(i);
            final InterviewQuestionData interviewQuestionData = new InterviewQuestionData();
            interviewQuestionData.setInterviewID(interviewID);
            interviewQuestionData.setQuestion(recordedQuestions[i]);
            interviewQuestionData.setLength(recordedQuestionLengths[i]);
            interviewQuestionData.setDateYear(dateYear);
            interviewQuestionData.setDateMonth(dateMonth);
            interviewQuestionData.setDateDay(dateDay);
            interviewQuestionData.setMedium(medium == RecordActivity.MEDIUM_VIDEO ? "video" : "audio");
            for (int j=0 ; j<tags[i].length ; j++) {
                interviewQuestionData.getTags().put(tags[i][j], true);
            }
            //Uploads the media file and media cover picture to Firebase Storage. After the upload is completed
            //the data-object is uploaded to Firebase Database. After that doAfterUpload() is called.
            //Count-variables measure the success of the upload process.
            final int index = i;
            mStorageRef = mStorageRootReference
                    .child("interviews")
                    .child(interviewID)
                    .child(interviewQuestionIDs.get(index) + (medium==RecordActivity.MEDIUM_VIDEO ? ".mp4" : ".m4a"));
            mStorageRef.putFile(Uri.parse(recordedMediaSingleFilePaths[index])).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    interviewQuestionData.setRecordURL(taskSnapshot.getDownloadUrl().toString());
                    mUploadMediaSuccessCount++;

                    if (medium==RecordActivity.MEDIUM_VIDEO) {
                        mStorageRef = mStorageRootReference
                                .child("interviews")
                                .child(interviewID)
                                .child(interviewQuestionID + ".jpg");
                        mStorageRef.putFile(Uri.parse(recordedCoverFilePaths[index])).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                interviewQuestionData.setPictureURL(taskSnapshot.getDownloadUrl().toString());
                                mUploadMediaCoverSuccessCount++;
                                interviewQuestionsReference
                                        .child(interviewQuestionIDs.get(index))
                                        .setValue(interviewQuestionData);
                                doAfterUpload();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(getSubClassTAG(), "Error uploading media cover image");
                                mUploadMediaCoverFailureCount++;
                                doAfterUpload();
                            }
                        });
                    } else {
                        interviewQuestionData.setPictureURL(narratorPictureURL);
                        mUploadMediaCoverSuccessCount = recordedQuestionsCount+1;
                        interviewQuestionsReference
                                .child(interviewQuestionIDs.get(index))
                                .setValue(interviewQuestionData);
                        doAfterUpload();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(getSubClassTAG(), "Error uploading media file");
                    mUploadMediaFailureCount++;
                    doAfterUpload();
                }
            });
        }

        //Create a data-object for the interview itself and fill it with the collected data.
        final InterviewData interviewData = new InterviewData();
        interviewData.setTitle(title);
        interviewData.setDateYear(dateYear);
        interviewData.setDateMonth(dateMonth);
        interviewData.setDateDay(dateDay);
        interviewData.setTopic(topic);
        interviewData.setMedium(medium == RecordActivity.MEDIUM_VIDEO ? "video" : "audio");

        int interviewLength = 0;
        for (int i=0 ; i<recordedQuestionsCount ; i++) {
            try { interviewLength += Double.parseDouble(recordedQuestionLengths[i]); } catch (Exception e) {}
        }
        interviewData.setLength("" + interviewLength);
        interviewData.setInterviewerID(interviewerID);
        interviewData.setInterviewerName(interviewerName);
        interviewData.setInterviewerPictureURL(interviewerPictureURL);
        interviewData.setNarratorID(narratorID);
        interviewData.setNarratorName(narratorName);
        interviewData.setNarratorPictureURL(narratorPictureURL);
        interviewData.setNarratorIsUser(narratorIsUser);
        for (int i=0 ; i<tags.length ; i++) {
            for (int j=0 ; j<tags[i].length ; j++) {
                interviewData.getTags().put(tags[i][j], true);
            }
        }
        interviewData.setInterviewQuestionIDs(interviewQuestionIDs);

        //Uploads the media cover picture for the interview. After the upload is completed
        //the data-object is uploaded to Firebase Database. After that doAfterUpload() is called.
        if (medium==RecordActivity.MEDIUM_VIDEO) {
            mStorageRef = mStorageRootReference
                    .child("interviews")
                    .child(interviewID)
                    .child(interviewID + ".jpg");
            mStorageRef.putFile(Uri.parse(recordedCoverFilePaths[0])).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    interviewData.setPictureURL(taskSnapshot.getDownloadUrl().toString());
                    interviewsReference
                            .child(interviewID)
                            .setValue(interviewData);
                    mUploadMediaCoverSuccessCount++;
                    doAfterUpload();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(getSubClassTAG(), "Error uploading media cover image");
                    mUploadMediaCoverFailureCount++;
                    doAfterUpload();
                }
            });
        } else {
            interviewData.setPictureURL(narratorPictureURL);
            interviewsReference
                    .child(interviewID)
                    .setValue(interviewData);
            mUploadMediaCoverSuccessCount = recordedQuestionsCount+1;
            doAfterUpload();
        }
    }


    private void doAfterUpload() {
        Log.d(TAG, "doAfterUpload: ");
        //These count-variables measure the success of the upload processes. Only, if all uploads were successfull, the interview is references in the interviewers and narrators
        //user-node and the app returns to MainOverviewActivity.
        //In case of a failure the progress bar just hides, so that the user can try to upload again.
        boolean allMediaCoverUploadsAttempted = mUploadMediaCoverSuccessCount+mUploadMediaCoverFailureCount>recordedQuestionsCount;
        //Number of media cover files is one larger than recordedQuestionsCount, because one cover file is uploaded extra for the interview itself
        boolean allMediaUploadsAttempted = mUploadMediaSuccessCount+mUploadMediaFailureCount>=recordedQuestionsCount;
        boolean allUploadsAttempted = allMediaCoverUploadsAttempted && allMediaUploadsAttempted;
        boolean allUploadsSuccessful = mUploadMediaCoverFailureCount+mUploadMediaFailureCount==0;
        if (allUploadsAttempted) {
            if (allUploadsSuccessful) {
                //Add interview to userID/interviewsOwn & userID/interviewsAll in Database
                mDatabaseRef = mDatabaseRootReference.child("users").child(mUserID).child("interviewsOwn").child(interviewID);
                mDatabaseRef.setValue(true);
                mDatabaseRef = mDatabaseRootReference.child("users").child(mUserID).child("interviewsAll").child(interviewID);
                mDatabaseRef.setValue(true);
                //Add interview to narratorID/interviewsOwn & narratorID/interviewsAll in Database, if narrator and interviewer aren't the same person and if narrator is a user
                if (!narratorID.equals(mUserID) && narratorIsUser) {
                    mDatabaseRef = mDatabaseRootReference.child("users").child(narratorID).child("interviewsOwn").child(interviewID);
                    mDatabaseRef.setValue(true);
                    mDatabaseRef = mDatabaseRootReference.child("users").child(narratorID).child("interviewsAll").child(interviewID);
                    mDatabaseRef.setValue(true);
                }
                showProgressBarFinishMessage(getString(R.string.progress_upload_success));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);
            } else {
                showProgressBarFinishMessage(getString(R.string.progress_upload_failure));
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        hideProgressBar();
                    }
                }, 1500);
            }
        }
    }
}