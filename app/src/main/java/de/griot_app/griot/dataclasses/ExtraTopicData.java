package de.griot_app.griot.dataclasses;

/**
 * Data holding class for extra topic data
 */
public class ExtraTopicData {

    private String extraTopic;

    //default-constructor
    public ExtraTopicData() {
    }

    //constructor
    public ExtraTopicData(String extraTopic) {
        this.extraTopic = extraTopic;
    }


    //get-methods
    public String getExtraTopic() { return extraTopic; }

    //set-methods
    public void setExtraTopic(String extraTopic) { this.extraTopic = extraTopic; }
}
