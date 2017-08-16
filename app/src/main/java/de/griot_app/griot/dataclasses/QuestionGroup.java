package de.griot_app.griot.dataclasses;

import java.util.ArrayList;
import java.util.List;

/**
 * Data holding class for question group of local topic catalog
 */

public class QuestionGroup {

    private int topicKey;
    private String topic;
    private ArrayList<LocalQuestionData> questions;
    protected Boolean selected = false;
    protected Boolean expanded = false;

    //default-constructor
    public QuestionGroup() {
        questions = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "QuestionGroup{" +
                ", topicKey=" + topicKey + '\'' +
                ", topic=" + topic + '\'' +
                ", questions=" + questions +
                '}';
    }

    //get-methods
    public int getTopicKey() { return topicKey; }

    public String getTopic() { return topic; }

    public ArrayList<LocalQuestionData> getQuestions() { return questions; }

    public Boolean getSelected() { return selected; }

    public Boolean getExpanded() { return expanded; }

    //set-methods
    public void setTopicKey(int topicKey) { this.topicKey = topicKey; }

    public void setTopic(String topic) { this.topic = topic; }

    public void setQuestions(ArrayList<LocalQuestionData> questions) { this.questions = questions; }

    public void setSelected(Boolean selected) { this.selected = selected; }

    public void setExpanded(Boolean expanded) { this.expanded = expanded; }
}
