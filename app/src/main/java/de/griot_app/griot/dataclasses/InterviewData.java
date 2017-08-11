package de.griot_app.griot.dataclasses;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data holding class for interview data
 */
public class InterviewData extends ContentData {

    private String title;
    private String date;
    private String topic;
    private String medium;
    private HashMap<String, Boolean> interviewerID;
    private HashMap<String, Boolean> narratorID;
    private Boolean narratorIsUser;
    private Integer numberComments;
    private HashMap<String, CommentData> comments;
    private ArrayList<HashMap<String, Boolean>> interviewQuestions;

    //default-constructor
    public InterviewData() {
        super();
        title = "";
        date = "";
        topic = "";
        medium = "";
        interviewerID = new HashMap<>();
        narratorID = new HashMap<>();
        narratorIsUser = false;
        numberComments = 0;
        comments = new HashMap<>();
        interviewQuestions = new ArrayList<>();
    }

    //constructor
    public InterviewData(
            String title,
            String date,
            String topic,
            String medium,
            String length,
            String pictureURL,
            String pictureLocalURI,
            HashMap<String, Boolean> interviewerID,
            HashMap<String, Boolean> narratorID,
            Boolean narratorIsUser,
            HashMap<String, Boolean> associatedUsers,
            HashMap<String, Boolean> associatedGuests,
            HashMap<String, Boolean> tags,
            Integer numberComments,
            HashMap<String, CommentData> comments,
            ArrayList<HashMap<String, Boolean>> interviewQuestions
    ) {
        super(length, pictureURL, pictureLocalURI, associatedUsers, associatedGuests, tags);
        this.title = title;
        this.date = date;
        this.topic = topic;
        this.medium = medium;
        this.interviewerID = interviewerID;
        this.narratorID = narratorID;
        this.narratorIsUser = narratorIsUser;
        this.numberComments = numberComments;
        this.comments = comments;
        this.interviewQuestions = interviewQuestions;
    }

    //get-methods
    public String getTitle() { return title; }

    public String getDate() { return date; }

    public String getTopic() { return topic; }

    public String getMedium() { return medium; }

    public HashMap<String, Boolean> getInterviewerID() { return interviewerID; }

    public HashMap<String, Boolean> getNarratorID() { return narratorID; }

    public Boolean getNarratorIsUser() { return narratorIsUser; }

    public Integer getNumberComments() { return numberComments; }

    public HashMap<String, CommentData> getComments() { return comments; }

    public ArrayList<HashMap<String, Boolean>> getInterviewQuestions() { return interviewQuestions; }

    //set-methods
    public void setTitle(String title) { this.title = title; }

    public void setDate(String date) { this.date = date; }

    public void setTopic(String topic) { this.topic = topic; }

    public void setMedium(String medium) { this.medium = medium; }

    public void setInterviewerID(HashMap<String, Boolean> interviewerID) { this.interviewerID = interviewerID; }

    public void setNarratorID(HashMap<String, Boolean> narratorID) { this.narratorID = narratorID; }

    public void setNarratorIsUser(Boolean narratorIsUser) { this.narratorIsUser = narratorIsUser; }

    public void setNumberComments(Integer numberComments) { this.numberComments = numberComments; }

    public void setComments(HashMap<String, CommentData> comments) { this.comments = comments; }

    public void setInterviewQuestions(ArrayList<HashMap<String, Boolean>> interviewQuestions) { this.interviewQuestions = interviewQuestions; }
}
