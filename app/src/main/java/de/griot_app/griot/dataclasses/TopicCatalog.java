package de.griot_app.griot.dataclasses;

import android.util.SparseArray;

/**
 * Data holding class for the users topic catalog
 */

public class TopicCatalog {

    private SparseArray<TopicData> topics;

    //default-constructor
    public TopicCatalog() {
        topics = new SparseArray<>();
    }

    //get-methods:
    public SparseArray<TopicData> getTopics() { return topics; }

    //set-methods:
    public void setTopics(SparseArray<TopicData> topics) { this.topics = topics; }
}
