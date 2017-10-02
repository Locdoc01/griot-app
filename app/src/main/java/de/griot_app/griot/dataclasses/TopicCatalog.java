package de.griot_app.griot.dataclasses;

import android.util.SparseArray;

/**
 * Data holding class for the users topic catalog
 */

public class TopicCatalog {

    private SparseArray<LocalTopicData> topics;

    //default-constructor
    public TopicCatalog() {
        topics = new SparseArray<>();
    }

    //get-methods:
    public SparseArray<LocalTopicData> getTopics() { return topics; }

    //set-methods:
    public void setTopics(SparseArray<LocalTopicData> topics) { this.topics = topics; }
}
