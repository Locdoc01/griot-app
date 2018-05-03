package de.griot_app.griot.dataclasses;

import java.util.HashMap;

/**
 * Data holding class for extra question data
 */
public class ExtraQuestionData {

    private String topic;
    private String question;

    //default-constructor
    public ExtraQuestionData() {
    }


    //constructor
    public ExtraQuestionData(String topic, String question) {
        this.topic = topic;
        this.question = question;
    }


    //get-methods
    public String getTopic() { return topic; }

    public String getQuestion() { return question; }

    //set-methods
    public void setTopic(String topic) { this.topic = topic; }

    public void setQuestion(String question) { this.question = question; }
}
