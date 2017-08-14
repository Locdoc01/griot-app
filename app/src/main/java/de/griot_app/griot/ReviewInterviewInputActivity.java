package de.griot_app.griot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.R;

public class ReviewInterviewInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ReviewInterviewInputActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_review_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
        mButtonRight.setEnabled(false);
        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));

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

        // Abfrage-dialog
        //wenn ja, interview verwerfen und aktivity beenden
    }

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        //zurück zur Aufnahme mit den bisherigen Daten
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        startActivity(new Intent(this, SaveInterviewInputActivity.class));
        //Daten weiterreichen
    }

}