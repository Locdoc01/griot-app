package de.griot_app.griot.dataclasses;

import java.util.HashMap;

/**
 * Data holding class for interview question data
 */
public class InterviewQuestionData extends ContentData {

    private String interviewID;
    private String question;
    private String recordURL;


    //default-constructor
    public InterviewQuestionData() {
        super();
    }


    //get-methods
    public String getInterviewID() { return interviewID; }

    public String getQuestion() { return question; }

    public String getRecordURL() { return recordURL; }

    //set-methods
    public void setInterviewID(String interviewID) { this.interviewID = interviewID; }

    public void setQuestion(String question) { this.question = question; }

    public void setRecordURL(String recordURL) { this.recordURL = recordURL; }
}
