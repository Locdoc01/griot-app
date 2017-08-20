package de.griot_app.griot.recordfunctions;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.griot_app.griot.R;


/**
 * Abstract base activity for recording audio or video files for Griot-App. It provides the following views: start/stop-button, finish-button,
 * chronometers for elapsed record time and available remaining record time, a question carousel and a FrameLayout as a
 * screen-filling placeholder for e.g. a camera preview or other background content.
 * It also provides a String List for holding the interview questions and a List of String Lists for holding the created filenames.
 *
 * Abstract methods setup(), startRecording() and stopRecording() have to be implemented by subclasses.
 */
public abstract class RecordActivity extends AppCompatActivity {

    protected static final int MEDIUM_VIDEO = 0;
    protected static final int MEDIUM_AUDIO = 1;

    private static final String TAG = RecordActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST = 6353;

    private static final String APP_DIR_NAME = "Griot";

    //intent-data
    protected int narratorSelectedItemID;
    protected String narratorID;
    protected String narratorName;
    protected String narratorPictureURL;
    protected Boolean narratorIsUser;

    protected int topicSelectedItemID;
    protected int topicKey;
    protected String topic;

    protected String[] interviewQuestions;


    protected View.OnClickListener mClickListener;

    /** The screen-filling placeholder for e.g. camera preview or other background content. */
    protected FrameLayout mBackground;

    protected QuestionCarousel mCarousel;
    protected RecordChronometers mChronometers;
    protected MediaRecorder mMediaRecorder;
    //protected MediaRecorder.OutputFormat mOutputFormat;

    /** mListQuestions holds the interview questions as Strings. */
    protected ArrayList<String> mListQuestions;

    protected int mQuestionCount;

    /**
     * mListFiles stores the filenames of the created media files.
     * Outer list index correspond with index from mListQuestions, thus one element is equivalent to one interview question.
     * One inner list holds all filenames of files, that were recorded for one specific question.
     * regularly there will be just one filename in one list, unless a question was recorded more than once.
     */
    protected ArrayList<ArrayList<String>> mListFilenames;

    protected File mFile;

    protected float mDensity;

    protected FrameLayout mLayoutChronometers;
    protected FrameLayout mLayoutCarousel;
    protected ImageView mButtonRecord;
    protected Button mButtonFinished;

    private String mInterviewTitle;

    private boolean mIsRecording = false;

    /**
     * Abstract method, that returns the appropriate layout id for extending subclass. This method can be used in onCreate()
     * to inflate the appropriate layout for the extending subclass
     * @return  layout id for extending subclass
     */
    protected abstract int getSubClassLayoutId();

    /**
     * Abstract method for setting up the chosen recording device and the screen background filling.
     * All steps, that are necessary to be done BEFORE recording starts have to be performed in this method.
     * Since these steps are dependant on the chosen medium, it has to implemented by the specific subclass.
     * Background filling would usually be a camera preview for video recording. In case of audio recording
     * it could be a picture, some kind of animation or anything else, that could be hold by a FrameLayout.
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
        //the layout for the appropriate subclass of RecordActivity has to be inflated here, because some views has to be initialized yet.
        //thus RecordActivity.onCreate() has to know, which layout for which subclass has to be inflated.
        //This is achieved through the abstract method getSubClassLayoutId(), which has to be implemented in the subclasses to return there
        //appropriate layout ids
        setContentView(getSubClassLayoutId());

        // gets intent-data about previous selections
        narratorSelectedItemID = getIntent().getIntExtra("narratorSelectedItemID", -1);
        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureURL = getIntent().getStringExtra("narratorPictureURL");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", true);

        topicSelectedItemID = getIntent().getIntExtra("topicSelectedItemID", -1);
        topicKey = getIntent().getIntExtra("topicKey", -1);
        topic = getIntent().getStringExtra("topic");

        interviewQuestions = getIntent().getStringArrayExtra("interviewQuestions");


        mBackground = (FrameLayout) findViewById(R.id.record_background);
        mButtonRecord = (ImageView) findViewById(R.id.button_record);

        mChronometers = (RecordChronometers) findViewById(R.id.layout_chronometers);
        mButtonFinished = (Button) findViewById(R.id.button_finished);

        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.button_record:
                        if (!mIsRecording) {
                            if (startRecording()) {
                                mIsRecording = true;
                                mCarousel.setRecordOn(mCarousel.getCurrentIndex());
                                //TODO: Farbe als Resource einpflegen
                                mButtonFinished.setTextColor(Color.parseColor("#505154"));
                                mButtonRecord.setImageResource(R.drawable.record_stop);
                            }
                        } else {
                            stopRecording();
                            mIsRecording = false;
                            mCarousel.setRecordOff();
                            //TODO: Farbe als Resource einpflegen
                            mButtonFinished.setTextColor(Color.parseColor("#FFFFFF"));
                            mButtonRecord.setImageResource(R.drawable.record_start);
                            Log.d(TAG, "File recorded: " + mFile.exists());
                            scanMedia();
                        }
                        break;
                    case R.id.button_finished:
                        //switches the carousels finishedMode
                        // TODO: ACHTUNG: Dies kann so nicht verwendet werden. Es wird beim Wechsel ein neues Carousel angelegt wobei der bisherige Status der Fragen nicht berücksichtigt wird
                        // NUR ZU VORFÜHRZWECKEN
                        if (mCarousel.getFinishedMode() == QuestionCarousel.CHECKED_WHEN_FINISHED) {
                            //mCarousel = new QuestionCarousel(RecordActivity.this);
                            mCarousel.setQuestionList(mListQuestions);
                            //mLayoutCarousel.removeAllViews();
                            //mLayoutCarousel.addView(mCarousel);
                            mCarousel.setFinishedMode(QuestionCarousel.BLUE_WHEN_FINISHED);
                        } else {
                            //mCarousel = new QuestionCarousel(RecordActivity.this);
                            mCarousel.setQuestionList(mListQuestions);
                            //mLayoutCarousel.removeAllViews();
                            //mLayoutCarousel.addView(mCarousel);
                            mCarousel.setFinishedMode(QuestionCarousel.CHECKED_WHEN_FINISHED);
                        }

                        //Intent i = new Intent(RecordActivity.this, RecordAudioActivity.class);
                        //startActivity(i);

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
                    Log.d(TAG, "onSystemUIVisibilityChange: visibility: " + visibility);
                    int vis = visibility & (View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    if (vis == 0) {
                        Log.d(TAG, "Visibility: status bar visible");
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
                        Log.d(TAG, "Visibility: status bar hidden");
                    }
                }
            });
        }

        mInterviewTitle = "Omas Kindheit";

        mListQuestions = new ArrayList<>();
        for (String question : interviewQuestions) {
            mListQuestions.add(question);
        }

        mDensity = getResources().getDisplayMetrics().density;
/*
        mListQuestions.add("Welchen Berufswunsch hattest du als Kind?");
        mListQuestions.add("Warst du in der Schule glücklich?");
        mListQuestions.add("Erinnerst du dich an dein erstes Spielzeug?");
        mListQuestions.add("Was war deine Lieblingskindersendung?");
        mListQuestions.add("Was war dein Lieblingsmärchen?");
        mListQuestions.add("Was sind deine schönsten Kindheitserinnerungen?");
        mListQuestions.add("Haben dir deine Eltern früher Geschichten erzählt?");
        mListQuestions.add("Kannst du mir etwas über die Ortschaft erzählen, in der du aufgewachsen bist und wie es für dich war, ohne Bruder aufzuwachsen?");
        mListQuestions.add("Was sind deine schlimmsten Kindheitserinnerungen?");
        mListQuestions.add("Kannst du mir etwas über die Verhältnisse erzählen, in denen du aufgewachsen bist, insbesondere wie es für dich war, getrennt von deinen Geschwistern aufzuwachsen?");
        mListQuestions.add("Wann und wie hast du Schwimmen gelernt?");
*/
        mQuestionCount = mListQuestions.size();

        mListFilenames = new ArrayList<>();

        for (int i = 0; i < mQuestionCount; i++) {
            mListFilenames.add(new ArrayList<String>());
        }

        //findViewById(R.id.stub_chronometers).setVisibility(View.VISIBLE);
        //or
        //View importPanel = ((ViewStub) findViewById(R.id.stub_chronometers)).inflate();
        //importPanel.addView(mChronometers);


        mCarousel = (QuestionCarousel) findViewById(R.id.layout_carousel);
        mCarousel.setQuestionList(mListQuestions);
        //mCarousel = new QuestionCarousel(RecordActivity.this, mListQuestions);
        //mLayoutCarousel = (FrameLayout) findViewById(R.id.layout_carousel_OLD_OLD_OLD_OLD_OLD_OLD_OLD_OLD_OLD_OLD_OLD);
        //mLayoutCarousel.addView(mCarousel);

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
     * In that case the app will return to the MainActivity. All other Functions of the app will be usable.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult: ");
        boolean grantedVideo = true;
        boolean grantedAudio = true;

        if (requestCode == PERMISSION_REQUEST) {
            for (int i=0 ; i<grantResults.length ; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    if (permissions[i].equals("android.permission.CAMERA")) {
                        grantedVideo = false;
                        Log.w(TAG, "onRequestPermissionResult: " + permissions[i] + ": Permission denied !!");
                    } else {
                        grantedVideo = false;
                        grantedAudio = false;
                        Log.e(TAG, "onRequestPermissionResult: " + permissions[i] + ": Permission denied !!");
                    }
                }
            }
        } else {
            grantedVideo = false;
            grantedAudio = false;
        }
        if (!grantedAudio) {
            showToastLong("Für die Benutzung sind mindestens Berechtigungen für Mikrofon und Speicherzugriff nötig!");
            // TODO: Intent zu MainActivity
        } else if (!grantedVideo) {
            showToastLong("Ohne Berechtigung für die Kamera ist nur Audio-Aufzeichnung möglich!");
            // TODO: Intent zu RecordAudioActivity
        } else {
            Log.d(TAG, "onRequestPermissionResult: All permissions granted");

            setup();
        }
    }


    // since Android 6.0 it's necessary to check for Permissons during run-time.
    private void checkForPermissions() {
        Log.d(TAG, "checkForPermissions: ");

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
                // dialog-Abfrage ob wirklich fertig
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
        Log.d(TAG, "hideStatusBar: ");
        // sorgt für Ausblendung der StatusBar.
        View decorView = getWindow().getDecorView();
        Log.d(TAG, "hideStatusBar: SDK: " + android.os.Build.VERSION.SDK_INT);
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
     * has the format "AUD_yyyy-MM-dd_HHmmss.mp3" for audio files or "VID_yyyy-MM-dd_HHmmss.mp4" for video files. The file type is dependant on
     * the given int value representing a medium.
     * If not existant, a subdirectory for the specific interview will also be created into the Movies-directory of external storages public directory,
     * so that the recorded files will be available to other apps including the systems gallery app.
     *
     * @param medium    int value that represents the medium. Accepted are MEDIUM_AUDIO for an audio file and MEDIUM_VIDEO for a video file.
     *                  Any other value will cause an Exception.
     * @return          File object holding the filename for the next recorded file.
     * @throws Exception
     */
    protected File getOutputFile(int medium) throws Exception {
        Log.d(TAG, "getOutputFile: ");
        String fileBeginning;
        String fileEnding;
        if (medium == MEDIUM_AUDIO) {
            fileBeginning = "AUD_";
            fileEnding = ".m4a";
        } else if (medium == MEDIUM_VIDEO) {
            fileBeginning = "VID_";
            fileEnding = ".mp4";
        } else {
            throw new Exception("Error: Unknown Medium");
        }
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File interviewDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), APP_DIR_NAME + File.separator + date + "_" + mInterviewTitle);
        if (!interviewDir.exists()) {
            if (!interviewDir.mkdirs()) {
                Log.e(TAG, "Error creating interview directory");
                return null;
            }
        }
        String dateTime = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date());
        return new File(interviewDir.getPath() + File.separator + fileBeginning + dateTime + fileEnding);
    }

/*
    protected Uri getOutputFileUri() {
        return Uri.fromFile(getOutputFile());
    }
*/

    /**
     * Makes sure, that the recorded files will be scanned by MediaScanner, so that they will be available from other apps like the systems gallery.
     */
    private void scanMedia() {
        //makes sure, that the file will be shown in gallery
        Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScannerIntent.setData(Uri.fromFile(mFile));
        sendBroadcast(mediaScannerIntent);
    }


    public void showToastLong(String text) {
        Toast.makeText(RecordActivity.this, text, Toast.LENGTH_LONG).show();
    }

}
