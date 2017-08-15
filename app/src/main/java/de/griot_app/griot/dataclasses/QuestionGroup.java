package de.griot_app.griot.dataclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Data holding class for question group of local topic catalog
 */

public class QuestionGroup {

    private String topic;
    private List<String> questions;
    protected Boolean selected = false;

    //default-constructor
    public QuestionGroup() {
        questions = new ArrayList<>();
    }

    //constructor
    public QuestionGroup(String topic) {
        this.topic = topic;
        this.questions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "QuestionGroup{" +
                ", topic=" + topic + '\'' +
                ", questions=" + questions +
                '}';
    }

    //get-methods
    public String getTopic() { return topic; }

    public List<String> getQuestions() { return questions; }

    public Boolean getSelected() { return selected; }

    //set-methods
    public void setTopic(String topic) { this.topic = topic; }

    public void setQuestions(List<String> questions) { this.questions = questions; }

    public void setSelected(Boolean selected) { this.selected = selected; }
}
