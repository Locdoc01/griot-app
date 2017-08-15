package de.griot_app.griot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import de.griot_app.griot.adapter.TopicCatalogAdapter;
import de.griot_app.griot.baseactivities.GriotBaseInputActivity;
import de.griot_app.griot.dataclasses.LocalPersonData;
import de.griot_app.griot.dataclasses.QuestionGroup;
import de.griot_app.griot.dataclasses.TopicCatalog;

public class ChooseTopicInputActivity extends GriotBaseInputActivity {

    private static final String TAG = ChooseTopicInputActivity.class.getSimpleName();

    private String narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private Boolean narratorIsUser;

    TopicCatalog mTopicCatalog;
    ExpandableListView mExpandListView;
    TopicCatalogAdapter mAdapter;

    private QuestionGroup mSelectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_record_interview);

        mButtonLeft.setText(R.string.button_cancel);
        mButtonCenter.setText(R.string.button_back);
        mButtonRight.setText(R.string.button_next);
        mButtonRight.setEnabled(false);
        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));

        narratorID = getIntent().getStringExtra("narratorID");
        narratorName = getIntent().getStringExtra("narratorName");
        narratorPictureURL = getIntent().getStringExtra("narratorPictureURL");
        narratorIsUser = getIntent().getBooleanExtra("narratorIsUser", true);

        mExpandListView = (ExpandableListView) findViewById(R.id.expandListView_input_choose_topic);

        /*
        mExpandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSelectedItem==null) {
                    mSelectedItem = mAdapter.getGroup(position);
                    mSelectedItem.setSelected(true);
                    mButtonRight.setEnabled(true);
                    mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));
                } else {
                    if (mSelectedItem==mCombinedListCreator.getAdapter().getItem(position)) {
                        mSelectedItem.setSelected(false);
                        mSelectedItem = null;
                        mButtonRight.setEnabled(false);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotLightgrey, null));
                    } else {
                        mSelectedItem.setSelected(false);
                        mSelectedItem = mCombinedListCreator.getAdapter().getItem(position);
                        mSelectedItem.setSelected(true);
                        mButtonRight.setEnabled(true);
                        mButtonRight.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorGriotDarkgrey, null));

                    }
                }
                mCombinedListCreator.getAdapter().notifyDataSetChanged();
            }
        });
*/

        mTopicCatalog = new TopicCatalog();

        // TODO Daten einlesen

        mAdapter = new TopicCatalogAdapter(this, mTopicCatalog);
        mExpandListView.setAdapter(mAdapter);

    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_input_choose_topic;
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

        finish();   // TODO: prüfen, ob gewünschtes Verhalten erfolgt
    }

    @Override
    protected void buttonRightPressed() {
        Log.d(TAG, "buttonRightPressed: ");

        startActivity(new Intent(this, ChooseMediumInputActivity.class));
        //daten weiterreichen
    }

}