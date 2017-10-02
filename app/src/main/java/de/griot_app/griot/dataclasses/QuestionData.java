package de.griot_app.griot.dataclasses;

/**
 * Data holding class for standard question data
 */
public class QuestionData {

    private int topicKey;
    private String question;

    //default-constructor
    public QuestionData() {

    }


    //get-methods
    public int getTopicKey() { return topicKey; }

    public String getQuestion() { return question; }

    //set-methods
    public void setTopicKey(int questionKey) { this.topicKey = questionKey; }

    public void setQuestion(String question) { this.question = question; }

}
