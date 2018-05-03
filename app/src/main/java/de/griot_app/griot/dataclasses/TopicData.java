package de.griot_app.griot.dataclasses;

import java.util.ArrayList;

/**
 * Data holding class for question group of topic catalog
 */

public class TopicData {

    private Integer topicKey;
    private String topic;
    private Boolean topicState;
    private ArrayList<QuestionData> questions;
    private Boolean selected = false;
    private Boolean expanded = false;

    //default-constructor
    public TopicData() {
        questions = new ArrayList<>();
    }


    //get-methods
    public int getTopicKey() { return topicKey; }

    public String getTopic() { return topic; }

    public Boolean getTopicState() { return topicState; }

    public ArrayList<QuestionData> getQuestions() { return questions; }

    public Boolean getSelected() { return selected; }

    public Boolean getExpanded() { return expanded; }

    //set-methods
    public void setTopicKey(int topicKey) { this.topicKey = topicKey; }

    public void setTopic(String topic) { this.topic = topic; }

    public void setTopicState(Boolean topicState) {this.topicState = topicState;}

    public void setQuestions(ArrayList<QuestionData> questions) { this.questions = questions; }

    public void setSelected(Boolean selected) { this.selected = selected; }

    public void setExpanded(Boolean expanded) { this.expanded = expanded; }
}
