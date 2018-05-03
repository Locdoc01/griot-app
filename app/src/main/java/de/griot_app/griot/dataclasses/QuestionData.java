package de.griot_app.griot.dataclasses;

/**
 * Data holding class for standard question data
 */
public class QuestionData {

    public static class QuestionState {
        public static final long ON = 0;
        public static final long OFF = 1;
        public static final long GONE = 2;
    }


    private int topicKey;
    private String questionKey;
    private String question;
    private Long questionState;


    //default-constructor
    public QuestionData() {
        questionState = QuestionState.ON;
    }


    //get-methods
    public int getTopicKey() { return topicKey; }

    public String getQuestionKey() { return questionKey; }

    public String getQuestion() { return question; }

    public Long getQuestionState() { return questionState; }

    //set-methods
    public void setTopicKey(int questionKey) { this.topicKey = questionKey; }

    public void setQuestionKey(String questionKey) { this.questionKey = questionKey; }

    public void setQuestion(String question) { this.question = question; }

    public void setQuestionState(Long questionState) { this.questionState = questionState; }

}
