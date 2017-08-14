package de.griot_app.griot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;

import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.R;

public class ChooseMediumInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ChooseMediumInputActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
        mButtonRight.setEnabled(false);
        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_choose_medium;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }


    @Override
    protected void buttonLeftPressed() {
        Log.d(TAG, "buttonLeftPressed: ");

        //TODO: Diese Activity und alle anderen InputActivities schließen und so zur MainOverview zurückkehren
    }

    @Override
    protected void buttonCenterPressed() {
        Log.d(TAG, "buttonCenterPressed: ");

        finish();
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");
        /*
        if (video ausgewählt) {
            startActivity(new Intent(this, RecordVideoActivity.class));
            //daten weiterreichen
        } else {
            startActivity(new Intent(this, RecordAudioActivity.class));
            //daten weiterreichen
        }
        finish();
        */
    }

}