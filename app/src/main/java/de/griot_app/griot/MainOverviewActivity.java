package de.griot_app.griot;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Menu;
import android.view.MenuInflater;

public class MainOverviewActivity extends GriotBaseActivity {

    private static final String TAG = MainOverviewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTitle.setText(R.string.title_overview);
        mButtonHome.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorGriotBlue, null));
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
