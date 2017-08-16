package de.griot_app.griot.dataclasses;

/**
 * Data holding class for standard question data
 */
public class LocalQuestionData {

    public static class QuestionState {
        public static final int ON = 0;
        public static final int OFF = 1;
        public static final int GONE = 2;
    }

    private int topicKey;
    private String question;
    private Integer questionState;

    //default-constructor
    public LocalQuestionData() {
        questionState = QuestionState.ON;
    }

    @Override
    public String toString() {
        return "QuestionData{" +
                "topicKey='" + topicKey + '\'' +
                ", question=" + question +
                ", questionState=" + questionState +
                '}';
    }

    //get-methods
    public int getTopicKey() { return topicKey; }

    public String getQuestion() { return question; }

    public Integer getQuestionState() { return questionState; }

    //set-methods
    public void setTopicKey(int questionKey) { this.topicKey = questionKey; }

    public void setQuestion(String question) { this.question = question; }

    public void setQuestionState(Integer questionState) { this.questionState = questionState; }

}
