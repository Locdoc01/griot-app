package de.griot_app.griot.recordfunctions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.griot_app.griot.R;
import de.griot_app.griot.perform_interview.ReviewInterviewInputActivity;


/**
 * Abstract base activity for recording audio or video files for Griot-App. It provides the following views: start/stop-button, finish-button,
 * chronometers for elapsed record time and available remaining record time, a question carousel and a FrameLayout as a
 * screen-filling placeholder for e.g. a camera preview or other background content.
 * It also provides a String List for holding the interview questions and a List of String Lists for holding the created filenames.
 *
 * Abstract methods setup(), startRecording() and stopRecording() have to be implemented by subclasses.
 */
public abstract class RecordActivity extends AppCompatActivity {

    public static final int MEDIUM_VIDEO = 0;
    public static final int MEDIUM_AUDIO = 1;

    private static final int PERMISSION_REQUEST = 6353;

    private static final String APP_DIR_NAME = "Griot";

    protected int mMedium = -1;

    //Intent-data
    protected int narratorSelectedItemID;
    protected String narratorID;
    protected String narratorName;
    protected String narratorPictureURL;
    protected Boolean narratorIsUser;

    protected String interviewerID;
    protected String interviewerName;
    protected String interviewerPictureURL;

    protected int topicSelectedItemID;
    protected int topicKey;
    protected String topic;

    protected String title;
    protected String dateYear;
    protected String dateMonth;
    protected String dateDay;

    /**
     * allQuestions holds all questions, which were selected before the interview started
     */
    protected String[] allQuestions;

    /**
     * addedQuestions holds questions, which were added, when reviewing the interview
     */
    protected String[] addedQuestions;

    protected int allQuestionsCount;

    /**
     * allMediaMultiFilePaths holds the file paths of the created media files.
     * Outer ArrayList index correspond with allQuestions index, thus one element is equivalent to one interview question.
     * One inner list holds all filenames of files, that were recorded for one specific question.
     * regularly there will be just one filename in one list, unless a question was recorded more than once.
     */
    protected ArrayList<ArrayList<String>> allMediaMultiFilePaths;

    /**
     * allMediaSingleFilePaths holds the file paths of the media files, after all files of one question were combined to one file.
     * Thus the index corresponds with allQestions index.
     */
    protected String[] allMediaSingleFilePaths;

    /**
     * recordedMediaSingleFilePaths also holds combined files, but only for those questions, which were actually recorded.
     * Thus the index does not correspond with allQuestions index.
     */
    protected String[] recordedMediaSingleFilePaths;

    /**
     * The following Arrays refer only to the recorded Files. Their index corresponds with the index of recordedMediaSingleFilePaths
     */
    protected String[] recordedCoverFilePaths;
    protected String[] recordedQuestions;
    protected String[] recordedQuestionLengths;

    /**
     * recordedQuestionIndices maps the indices from recordedMediaSingleFilePaths to the indices of allQuestions
     */
    protected int[] recordedQuestionIndices;

    protected int recordedQuestionsCount;

    protected View.OnClickListener mClickListener;

    /** The screen-filling placeholder for e.g. camera preview or other background content. */
    protected FrameLayout mBackground;

    //Views
    protected QuestionCarousel mCarousel;
    protected RecordChronometers mChronometers;
    protected MediaRecorder mMediaRecorder;
    protected ImageView mButtonRecord;
    protected Button mButtonFinished;

    protected String mInterviewDir;
    protected File mMediaFile;
    protected File mCoverFile;

    //switch and control variables
    protected float mDensity;
    protected int mFirstShownQuestion = 0;
    protected boolean mIsRecording = false;
    protected int mCurrentRecordingIndex = -1;

    /**
     * Abstract method, that returns the appropriate layout id for extending subclass. This method can be used in onCreate()
     * to inflate the appropriate layout for the extending subclass
     * @return  layout id for extending subclass
     */
    protected abstract int getSubClassLayoutId();

    /**
     * Abstract method, which returns the TAG of the extending subclass.
     * This method can be used, when the TAG of the concrete subclass is needed.
     * Note, that RecordActivity itself doesn't provide a TAG field.
     * @return  TAG of the extending subclass
     */
    protected abstract String getSubClassTAG();

    /**
     * Abstract method for setting up the chosen recording device and the screen background filling.
     * All steps, that are necessary to be done BEFORE recording starts have to be performed in this method.
     * Since these steps are dependant on the chosen medium, it has to be implemented by the specific subclass.
     * Background filling would usually be a camera preview for video recording. In case of audio recording
     * it could be a picture, some kind of animation or anything else.
     */
    protected abstract void setup();

    /**
     * Abstract method to start recording. This method should hold all necessary steps to setup and start the MediaRecorder,
     * and (together with stopRecording()) to control any additional views like extra buttons, whose appearance or functionality depends on recording state.
     * @return  true if start was successful, otherwise false.
     */
    protected abstract boolean startRecording();

    /**
     * Abstract method to stop recording. This method should stop the MediaRecorder and call setup() so that another
     * recording could be performed.Together with startRecording() it should control any additional views like extra buttons,
     * whose appearance or functionality depends on recording state.
     */
    protected abstract void stopRecording();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //The layout for the appropriate subclass of RecordActivity has to be inflated here, because some views has to be initialized yet.
        //Thus onCreate() has to know, which layout for which subclass has to be inflated.
        //This is achieved by the abstract method getSubClassLayoutId(), which has to be implemented in the subclasses to return there
        //appropriate layout ids
        setContentView(getSubClassLayoutId());

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
        dateYear = getIntent().getStringExtra("dateYear");
        dateMonth = getIntent().getStringExtra("dateMonth");
        dateDay = getIntent().getStringExtra("dateDay");

        allQuestions = getIntent().getStringArrayExtra("allQuestions");
        addedQuestions = getIntent().getStringArrayExtra("addedQuestions");
        allMediaSingleFilePaths = getIntent().getStringArrayExtra("allMediaSingleFilePaths");

        recordedQuestionsCount = getIntent().getIntExtra("recordedQuestionsCount", 0);
        recordedQuestionIndices = getIntent().getIntArrayExtra("recordedQuestionIndices");

        mInterviewDir = getIntent().getStringExtra("interviewDir");

        //If questions were added in ReviewInterviewInputActivity, a new Array is created, which holds
        //the previous questions and the added questions. mFirstShownQuestion is set to the first question,
        //that has been added
        //TODO: not testet. Add-function is not implemented yet.
        if (addedQuestions != null) {
            mFirstShownQuestion = allQuestions.length;
            String[] tmp = new String[allQuestions.length + addedQuestions.length];
            for (int i=0 ; i<allQuestions.length+addedQuestions.length ; i++) {
                if (i < allQuestions.length) {
                    tmp[i] = allQuestions[i];
                } else {
                    tmp[i] = addedQuestions[i - allQuestions.length];
                }
            }
            allQuestions = tmp;
        }
        allQuestionsCount = allQuestions.length;

        allMediaMultiFilePaths = new ArrayList<>();

        //initializes allMediaMultiFilePaths
        for (int i = 0; i < allQuestionsCount; i++) {
            allMediaMultiFilePaths.add(new ArrayList<String>());
            //If there were some recordings done before, getting from the Intent, they get added
            if (allMediaSingleFilePaths != null) {
                if (allMediaSingleFilePaths[i] != null) {
                    allMediaMultiFilePaths.get(i).add(allMediaSingleFilePaths[i]);
                }
            }
        }

        //initializes the question carousel
        mCarousel = (QuestionCarousel) findViewById(R.id.layout_carousel);
        mCarousel.setQuestionList(allQuestions);

        //Set information about previously recorded questions to question carousel
        if (recordedQuestionIndices != null) {
            mCarousel.setRecordedQuestions(recordedQuestionIndices);
        }

        //TODO: set mFirstShownQuestion to the first question, that hasn't been answered yet
        mCarousel.setFirstShownQuestion(mFirstShownQuestion);

        //Get references to other layout objects
        mBackground = (FrameLayout) findViewById(R.id.record_background);
        mChronometers = (RecordChronometers) findViewById(R.id.layout_chronometers);
        mButtonRecord = (ImageView) findViewById(R.id.button_record);
        mButtonFinished = (Button) findViewById(R.id.button_finished);

        //set OnClickListener for the controls
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_record:
                        if (!mIsRecording) {
                            if (startRecording()) {
                                Log.d(getSubClassTAG(), "record clicked: ");
                                mIsRecording = true;
                                mCarousel.setRecordOn(mCarousel.getCurrentIndex());
                                mButtonFinished.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                                mButtonRecord.setImageResource(R.drawable.record_stop);
                            }
                        } else {
                            Log.d(getSubClassTAG(), "stop clicked: ");
                            stopRecording();
                            mIsRecording = false;
                            mCarousel.setRecordOff();
                            mButtonFinished.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotWhite, null));
                            mButtonRecord.setImageResource(R.drawable.record_start);
                            scanMedia();
                        }
                        break;
                    case R.id.button_finished:
                        Log.d(getSubClassTAG(), "finished clicked: ");
                        finishInterview();
                        break;
                }
            }
        };

        mButtonRecord.setOnClickListener(mClickListener);
        mButtonFinished.setOnClickListener(mClickListener);

        // Makes sure on Android API level < 19 that the status bar will be fade out after 2 seconds, if it got visible by the user
        // (since API level 19 this is obsolete, thanks to the immersive layout functionality (see hideStatusBar() for more details)
        if (android.os.Build.VERSION.SDK_INT < 19) {
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    Log.d(getSubClassTAG(), "onSystemUIVisibilityChange: visibility: " + visibility);
                    int vis = visibility & (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    if (vis == 0) {
                        Log.d(getSubClassTAG(), "Visibility: status bar visible");
                        Handler h = decorView.getHandler();
                        if (h != null) {
                            h.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    hideStatusBar();
                                }
                            }, 2000);
                        }
                    } else {
                        Log.d(getSubClassTAG(), "Visibility: status bar hidden");
                    }
                }
            });
        }

        mDensity = getResources().getDisplayMetrics().density;

        checkForPermissions();
    }


    @Override
    protected void onResume() {
        super.onResume();
        hideStatusBar();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mIsRecording) {
            stopRecording();
            scanMedia();
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }


    /**
     * Will be called, when the user responds to permission requests. The functionality of the app will be dependant on
     * the given permissions. If permissions for CAMERA are denied but for RECORD_AUDIO and WRITE_EXTERNAL_STORAGE are allowed then
     * at least audio recording can be used. In that case the app redirects automatically from RecordVideoActivity to RecordAudioActivity.
     * If one of RECORD_AUDIO or WRITE_EXTERNAL_STORAGE are denied, recording functionality can not be used at all.
     * In that case the app will return to MainOverviewActivity. All other Functions of the app will be usable.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(getSubClassTAG(), "onRequestPermissionResult: ");

        boolean grantedVideo = true;
        boolean grantedAudio = true;

        if (requestCode == PERMISSION_REQUEST) {
            for (int i=0 ; i<grantResults.length ; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (permissions[i].equals("android.permission.CAMERA")) {
                        grantedVideo = false;
                        Log.w(getSubClassTAG(), "onRequestPermissionResult: " + permissions[i] + ": Permission denied !!");
                    } else {
                        grantedVideo = false;
                        grantedAudio = false;
                        Log.e(getSubClassTAG(), "onRequestPermissionResult: " + permissions[i] + ": Permission denied !!");
                    }
                }
            }
        } else {
            grantedVideo = false;
            grantedAudio = false;
        }
        if (!grantedAudio) {
            Toast.makeText(RecordActivity.this, getString(R.string.text_permission_micro_storage), Toast.LENGTH_SHORT).show();
            finish();
        } else if (!grantedVideo) {
            Toast.makeText(RecordActivity.this, getString(R.string.text_permission_camera), Toast.LENGTH_SHORT).show();
            // TODO: Intent to RecordAudioActivity
        } else {
            Log.d(getSubClassTAG(), "onRequestPermissionResult: All permissions granted");

            setup();
        }
    }


    // since Android 6.0 it's necessary to check for permissons during run-time.
    private void checkForPermissions() {
        Log.d(getSubClassTAG(), "checkForPermissions: ");

        ArrayList<String> manifestPermissons = new ArrayList<>();
        manifestPermissons.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        manifestPermissons.add(Manifest.permission.RECORD_AUDIO);
        manifestPermissons.add(Manifest.permission.CAMERA);
        ArrayList<Integer> permissionList = new ArrayList<>();
        for (String manifestPermission : manifestPermissons) {
            permissionList.add(ContextCompat.checkSelfPermission(RecordActivity.this, manifestPermission));
        }

        for (Integer i : permissionList) {
            if (i == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(RecordActivity.this, manifestPermissons.toArray(new String[manifestPermissons.size()]), PERMISSION_REQUEST);
                return;
            }
        }
        setup();
    }


    /**
     * hides the status bar. Before Android API level 19 it only hides it once. If it will be shown by users action it has to be fade out
     * programatically again (see appropriate section in onCreate()-method for more details)
     * Since API level 19 the flag View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY can be set to SystemUiVisibility which will fade out
     * the status bar automatically after it got visible.
     */
    private void hideStatusBar() {
        Log.d(getSubClassTAG(), "hideStatusBar: SDK: " + android.os.Build.VERSION.SDK_INT);

        View decorView = getWindow().getDecorView();
        if (android.os.Build.VERSION.SDK_INT < 19) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        } else {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }


    /**
     * Creates and returns a File object, which holds a filename for the next recorded file. The filename includes the record date and time and
     * has the format "AUD_yyyy-MM-dd_HHmmss.m4a" for audio files or "VID_yyyy-MM-dd_HHmmss.mp4" for video files.
     * If not existant, a subdirectory for the specific interview will also be created into the Movies-directory of external storage public directory,
     * so that the recorded files will be available to other apps including the systems gallery app.
     *
     * @return          File object holding the filename for the next recorded file.
     */
    protected File getOutputFile() throws Exception {
        Log.d(getSubClassTAG(), "getOutputFile: ");

        String fileBeginning;
        String fileEnding;
        if (mMedium == MEDIUM_AUDIO) {
            fileBeginning = "AUD_";
            fileEnding = ".m4a";
        } else if (mMedium == MEDIUM_VIDEO) {
            fileBeginning = "VID_";
            fileEnding = ".mp4";
        } else {
            throw new Exception("unexpected media type");
        }
        String dateTime = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        File dir;
        if (recordedQuestionsCount ==0) {
            dateYear = new SimpleDateFormat("yyyy").format(new Date());
            dateMonth = new SimpleDateFormat("MM").format(new Date());
            dateDay = new SimpleDateFormat("dd").format(new Date());
            dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), APP_DIR_NAME + File.separator + dateTime);
            mInterviewDir = dir.getPath();
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    Log.e(getSubClassTAG(), "Error creating interview directory");
                    return null;
                }
            }
        }
        return new File(mInterviewDir + File.separator + fileBeginning + dateTime + fileEnding);
    }


    /**
     * Makes sure, that the recorded files will be scanned by MediaScanner, so that they will be available from other apps like the systems gallery.
     */
    private void scanMedia() {
        Log.d(getSubClassTAG(), "scanMedia: ");

        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScannerIntent.setData(Uri.fromFile(mMediaFile));
        sendBroadcast(mediaScannerIntent);
    }

    /**
     * Creates arrays, which holds only the recorded questions, their media files paths, cover file paths, length and indices (refering to all questions).
     * Also puts all media files for one question together to one file, obtains the lengths of the files, and a thumb picture as cover file (in case of video)
     * Finally creates an Intent to start ReviewInterviewActivity and put all collected data to it.
     */
    protected void finishInterview() {
        Log.d(getSubClassTAG(), "finishedInterview: ");

        //arrays to hold data of recorded questions
        recordedQuestions = new String[recordedQuestionsCount];
        recordedMediaSingleFilePaths = new String[recordedQuestionsCount];
        recordedCoverFilePaths = new String[recordedQuestionsCount];
        recordedQuestionLengths = new String[recordedQuestionsCount];
        recordedQuestionIndices = new int[recordedQuestionsCount];


        allMediaSingleFilePaths = new String[allMediaMultiFilePaths.size()];
        int recordedIndex=0;
        for (int i = 0; i< allMediaMultiFilePaths.size() ; i++) {
            if (!allMediaMultiFilePaths.get(i).isEmpty()) {

                //TODO: combine all single media files which belong to one question to one file and put it in allMediaMuiltiFilePaths.get(i).get(0)

                allMediaSingleFilePaths[i] = allMediaMultiFilePaths.get(i).get(0);
                String videoPath = allMediaSingleFilePaths[i];

                MediaMetadataRetriever media = new MediaMetadataRetriever();
                media.setDataSource(Uri.parse(videoPath).getPath());

                //Get Length of a media file
                String length = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
               /*
                if (Integer.parseInt(length)<2000) {    // TODO: works only, if ArrayLists are used instead of arrays
                    continue;
                }
                */

                recordedQuestionLengths[recordedIndex] = length;
                if (mMedium==MEDIUM_AUDIO) {
                    recordedCoverFilePaths[recordedIndex] = Uri.fromFile(mCoverFile).toString();
                } else {
                    //Get a thumb bitmap of the video at a specific frame and store it to a file
                    Bitmap cover = media.getFrameAtTime((Integer.parseInt(length) < 2000) ? Integer.parseInt(length) : 2000);
                    if (cover != null) {
                        File coverFile = new File(mInterviewDir, "thumb_" + i + ".jpg");
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        cover.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                        byte[] bitmapData = bos.toByteArray();
                        try {
                            FileOutputStream fos = new FileOutputStream(coverFile);
                            fos.write(bitmapData);
                            fos.flush();
                            fos.close();
                        } catch (Exception e) {
                        }
                        String coverFilePath = Uri.fromFile(coverFile).toString();
                        recordedCoverFilePaths[recordedIndex] = coverFilePath;
                    } else {
                        recordedCoverFilePaths[recordedIndex] = null;
                    }
                }

                recordedQuestions[recordedIndex] = allQuestions[i];
                recordedQuestionIndices[recordedIndex] = i;
                recordedMediaSingleFilePaths[recordedIndex] = allMediaMultiFilePaths.get(i).get(0);
                recordedIndex++;
            }
        }

        //Create an Intent to start ReviewInterviewActivity and put all collected data of the interview to it
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
        intent.putExtra("medium", mMedium);
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

        intent.putExtra("recordedQuestionsCount", recordedQuestionsCount);
        intent.putExtra("interviewDir", mInterviewDir);

        startActivity(intent);
        finish();
    }
}
