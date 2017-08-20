package de.griot_app.griot.dataclasses;

import java.util.HashMap;

/**
 * Data holding class for extra question data
 */
public class ExtraQuestionData {

    private HashMap<String, Boolean> topic;
    private String question;

    //default-constructor
    public ExtraQuestionData() {
    }

    //constructor
    public ExtraQuestionData(HashMap<String, Boolean> topic, String question) {
        this.topic = topic;
        this.question = question;
    }

    @Override
    public String toString() {
        return "ExtraQuestionData{" +
                "topic=" + topic +
                ", question='" + question + '\'' +
                '}';
    }

    //get-methods
    public HashMap<String, Boolean> getTopic() { return topic; }

    public String getQuestion() { return question; }

    //set-methods
    public void setTopic(HashMap<String, Boolean> topic) { this.topic = topic; }

    public void setQuestion(String question) { this.question = question; }
}
