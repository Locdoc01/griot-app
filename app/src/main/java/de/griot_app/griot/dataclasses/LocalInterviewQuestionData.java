package de.griot_app.griot.dataclasses;

import java.util.HashMap;

/**
 * Data holding class for locally holded interview question data
 */
public class LocalInterviewQuestionData extends LocalContentData {

    private String interviewID;
    private String question;
    private String recordURL;

    //default-constructor
    public LocalInterviewQuestionData() {
        super();
    }

    //constructor
    public LocalInterviewQuestionData(
            String contentID,
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
        super(contentID, length, dateYear, dateMonth, dateDay, medium, pictureURL, pictureLocalURI, associatedUsers, associatedGuests, tags);
        this.interviewID = interviewID;
        this.question = question;
        this.recordURL = recordURL;
    }

    @Override
    public String toString() {
        return "LocalInterviewQuestionData{" +
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
