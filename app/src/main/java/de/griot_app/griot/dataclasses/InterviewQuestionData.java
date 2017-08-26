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

    //constructor
    public InterviewQuestionData(
            String interviewID,
            String question,
            String length,
            String dateYear,
            String dateMonth,
            String dateDay,
            String medium,
            String pictureURL,
            String pictureLocalURI,
            String recordURL,
            HashMap<String, Boolean> associatedUsers,
            HashMap<String, Boolean> associatedGuests,
            HashMap<String, Boolean> tags
    ) {
        super(length, dateYear, dateMonth, medium, dateDay, pictureURL, associatedUsers, associatedGuests, tags);
        this.interviewID = interviewID;
        this.question = question;
        this.recordURL = recordURL;
    }

    @Override
    public String toString() {
        return "InterviewQuestionData{" +
                "interviewID=" + interviewID +
                ", question='" + question + '\'' +
                ", recordURL='" + recordURL + '\'' +
                '}';
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
