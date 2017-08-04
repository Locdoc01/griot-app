package de.griot_app.griot;

import android.os.Bundle;

public class TopicCatalogActivity extends GriotBaseActivity {

    private static final String TAG = TopicCatalogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle(R.string.title_topics);
    }

    @Override
    protected int getSubClassLayoutId() {
        return R.layout.activity_topic_catalog;
    }
}
