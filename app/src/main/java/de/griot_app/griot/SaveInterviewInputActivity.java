package de.griot_app.griot;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.griot_app.griot.adapters.CombinedPersonListCreator;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.InterviewData;
import de.griot_app.griot.dataclasses.InterviewQuestionData;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.LocalUserData;
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

    private String interviewDir;

    private int recordedQuestionsCount;

    private String[][] tags;

    private boolean mTitleChosen = false;

    private int mUploadMediaSuccessCount = 0;
    private int mUploadMediaFailureCount = 0;
    private int mUploadMediaCoverSuccessCount = 0;
    private int mUploadMediaCoverFailureCount = 0;

    private String interviewID;

    private boolean firstSaveAttempt = true;

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

    //Creates the PersonlistView as a combination of own user data, guest list data, friend list data and approriate headings
    private CombinedPersonListCreator mCombinedListCreator;

    //firebase-queries for the CombinedListCreator
    private Query mQueryYou;
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

        interviewDir = getIntent().getStringExtra("interviewDir");

        recordedQuestionsCount = getIntent().getIntExtra("recordedQuestionsCount", 0);

        tags = new String[recordedQuestionsCount][];
        for (int i=0 ; i<recordedQuestionsCount ; i++) {
            tags[i] = getIntent().getStringArrayExtra("tags" + i);
        }

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

        mClicklistener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_check_title:
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
                    case R.id.button_cancel_title:
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
                    case R.id.button_cancel_person:
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

        // manages the narrator selection along with the next-button functionality. The selection gets stored in the appropriate LocalPersonData-object
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

    @Override
    protected void onStart() {
        super.onStart();

        mEditTextTitle.setText(title);
        if (!TextUtils.isEmpty(mEditTextTitle.getText().toString().trim())) {
            mButtonCheckTitle.performClick();
        }

        //TODO: Auslagern, ober überarbeiten oder vereinheitlichen

        // Obtain own user data from Firebase
        mUser = mAuth.getCurrentUser();
        mUserID = mUser.getUid();

        mQueryYou = mDatabaseRootReference.child("users").orderByKey().equalTo(mUserID);
        mQueryGuests = mDatabaseRootReference.child("guests");   //TODO genauer spezifizieren
        mQueryFriends = mDatabaseRootReference.child("users");  //TODO genauer spezifizieren

        mQueryYou.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(getSubClassTAG(), "getValueEventListener: onDataChange:");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mLocalUserData = ds.getValue(LocalUserData.class);
                    mLocalUserData.setContactID(ds.getKey());
                    mLocalUserData.setCategory(getString(R.string.text_yourself));
                }

                File file = null;
                try {
                    file = File.createTempFile("profile_image" + "_", ".jpg");
                } catch (Exception e) {
                }
                final String path = file.getPath();

                try {
                    mStorageRef = mStorage.getReferenceFromUrl(mLocalUserData.getPictureURL());
                    mStorageRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            mLocalUserData.setPictureLocalURI(path);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(getSubClassTAG(), "Error downloading user profile image file");
                            mLocalUserData.setPictureLocalURI("");
                        }
                    });
                } catch (Exception e) {}

                //create the Combined ListView
                mCombinedListCreator = new CombinedPersonListCreator(SaveInterviewInputActivity.this, -1, mLocalUserData, mListViewPersons);
                mCombinedListCreator.setMode(CombinedPersonListCreator.PERSONS_CHOOSE_MODE);
                mCombinedListCreator.add(mQueryGuests);
                mCombinedListCreator.add(mQueryFriends);
                mCombinedListCreator.loadData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_save_interview;
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

        intent.putExtra("medium", medium);      // TODO evt. überflüssig

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

        intent.putExtra("interviewDir", interviewDir);

        intent.putExtra("recordedQuestionsCount", recordedQuestions.length);

        for (int i=0 ; i<recordedQuestionsCount ; i++) {
            intent.putExtra("tags" + i, tags[i]);
        }

        startActivity(intent);
        finish();

    }

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

    private void saveInterview() {

        showProgressBar(getString(R.string.progress_uploading_interview));

        final DatabaseReference interviewsReference = mDatabaseRootReference.child("interviews");
        // the boolean firstSaveAttempt ensures, that in case of an interrupted upload the interview will be saved to the same key at further attempts.
        // Thus the potentially incomplete upload will be overwritten by the complete one.
        // Otherwise there could pile up dead interview fragments in the database, which cannot be accessed.
        if (firstSaveAttempt) {
            interviewID = interviewsReference.push().getKey();
        }

        final ArrayList<String> interviewQuestionIDs = new ArrayList<>();
        final DatabaseReference interviewQuestionsReference = mDatabaseRootReference.child("interviewQuestions");
        if (firstSaveAttempt) {
            for (int i = 0; i < recordedQuestionsCount; i++) {
                interviewQuestionIDs.add(interviewQuestionsReference.push().getKey());
            }
        }
        firstSaveAttempt = false;
        for (int i=0 ; i<recordedQuestionsCount ; i++) {
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
        // number of media cover files is one larger than recordedQuestionsCount, because one cover file is uploaded extra for the interview itself
        boolean allMediaCoverUploadsAttempted = mUploadMediaCoverSuccessCount+mUploadMediaCoverFailureCount>recordedQuestionsCount;
        boolean allMediaUploadsAttempted = mUploadMediaSuccessCount+mUploadMediaFailureCount>=recordedQuestionsCount;
        boolean allUploadsAttempted = allMediaCoverUploadsAttempted && allMediaUploadsAttempted;
        boolean allUploadsSuccessful = mUploadMediaCoverFailureCount+mUploadMediaFailureCount==0;
        if (allUploadsAttempted) {
            if (allUploadsSuccessful) {
                //add interview to user in Database
                mDatabaseRef = mDatabaseRootReference.child("users").child(mUserID).child("interviewsAll").child(interviewID);
                mDatabaseRef.setValue(true);
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