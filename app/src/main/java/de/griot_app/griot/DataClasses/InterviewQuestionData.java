package de.griot_app.griot.DataClasses;

import java.util.HashMap;

/**
 * Data holding class for interview question data
 */
public class InterviewQuestionData extends ContentData {

    private HashMap<String, Boolean> interviewID;
    private String question;
    private String recordURL;

    //default-constructor
    public InterviewQuestionData() {
        super();
        interviewID = new HashMap<>();
        question = "";
        recordURL = "";
    }

    //constructor
    public InterviewQuestionData(
            HashMap<String, Boolean> interviewID,
            String question,
            String length,
            String pictureURL,
            String recordURL,
            HashMap<String, Boolean> associatedUsers,
            HashMap<String, Boolean> associatedGuests,
            HashMap<String, Boolean> tags
    ) {
        super(length, pictureURL, associatedUsers, associatedGuests, tags);
        this.interviewID = interviewID;
        this.question = question;
        this.recordURL = recordURL;
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
