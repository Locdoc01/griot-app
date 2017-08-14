package de.griot_app.griot.dataclasses;

import java.util.HashMap;

/**
 * Data holding class for locally holded interview question data
 */
public class LocalInterviewQuestionData extends LocalContentData {

    private HashMap<String, Boolean> interviewID;
    private String question;
    private String recordURL;

    //default-constructor
    public LocalInterviewQuestionData() {
        super();
        interviewID = new HashMap<>();
    }

    //constructor
    public LocalInterviewQuestionData(
            HashMap<String, Boolean> interviewID,
            String question,
            String length,
            String pictureURL,
            String pictureLocalURI,
            String recordURL,
            HashMap<String, Boolean> associatedUsers,
            HashMap<String, Boolean> associatedGuests,
            HashMap<String, Boolean> tags
    ) {
        super(length, pictureURL, pictureLocalURI, associatedUsers, associatedGuests, tags);
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
    public HashMap<String, Boolean> getInterviewID() { return interviewID; }

    public String getQuestion() { return question; }

    public String getRecordURL() { return recordURL; }

    //set-methods
    public void setInterviewID(HashMap<String, Boolean> interviewID) { this.interviewID = interviewID; }

    public void setQuestion(String question) { this.question = question; }

    public void setRecordURL(String recordURL) { this.recordURL = recordURL; }
}
