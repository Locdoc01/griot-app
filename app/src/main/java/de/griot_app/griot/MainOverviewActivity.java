package de.griot_app.griot;

import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import java.util.ArrayList;

import de.griot_app.griot.dataclasses.InterviewData;
import de.griot_app.griot.adapter.InterviewDataAdapter;

public class MainOverviewActivity extends GriotBaseActivity {

    private static final String TAG = MainOverviewActivity.class.getSimpleName();

    private ListView mListViewInterviews;
    private ArrayList<InterviewData> mListInterviewData;
    private InterviewDataAdapter mInterviewDataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_overview);
        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));

        mListInterviewData = new ArrayList<>();
        mListViewInterviews = (ListView) findViewById(R.id.listView_main_overview);

        //TODO: mValueEventListener bzw. mChildEventListener (Firebase)
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_main_overview;
    }

    @Override
    protected String getSubClassTAG() { return TAG; }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app_bar_main_overview, menu);
        return super.onCreateOptionsMenu(menu);
    }

}
