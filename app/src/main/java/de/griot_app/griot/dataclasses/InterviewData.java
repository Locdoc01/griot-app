package de.griot_app.griot.dataclasses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data holding class for interview data
 */
public class InterviewData extends ContentData {

    private String title;
    private String topic;
    private String interviewerID;
    private String interviewerName;
    private String interviewerPictureURL;
    private String narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private Boolean narratorIsUser;
    private Integer numberComments;
    private HashMap<String, CommentData> comments;
    private ArrayList<String> interviewQuestionIDs;

    //default-constructor
    public InterviewData() {
        super();
        narratorIsUser = false;
        numberComments = 0;
        comments = new HashMap<>();
        interviewQuestionIDs = new ArrayList<>();
    }


    //get-methods
    public String getTitle() { return title; }

    public String getTopic() { return topic; }

    public String getInterviewerID() { return interviewerID; }

    public String getInterviewerName() { return interviewerName; }

    public String getInterviewerPictureURL() { return interviewerPictureURL; }

    public String getNarratorID() { return narratorID; }

    public String getNarratorName() { return narratorName; }

    public String getNarratorPictureURL() { return narratorPictureURL; }

    public Boolean getNarratorIsUser() { return narratorIsUser; }

    public Integer getNumberComments() { return numberComments; }

    public HashMap<String, CommentData> getComments() { return comments; }

    public ArrayList<String> getInterviewQuestionIDs() { return interviewQuestionIDs; }


    //set-methods
    public void setTitle(String title) { this.title = title; }

    public void setTopic(String topic) { this.topic = topic; }

    public void setInterviewerID(String interviewerID) { this.interviewerID = interviewerID; }

    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }

    public void setInterviewerPictureURL(String interviewerPictureURL) { this.interviewerPictureURL = interviewerPictureURL; }

    public void setNarratorID(String narratorID) { this.narratorID = narratorID; }

    public void setNarratorName(String narratorName) { this.narratorName = narratorName; }

    public void setNarratorPictureURL(String narratorPictureURL) { this.narratorPictureURL = narratorPictureURL; }

    public void setNarratorIsUser(Boolean narratorIsUser) { this.narratorIsUser = narratorIsUser; }

    public void setNumberComments(Integer numberComments) { this.numberComments = numberComments; }

    public void setComments(HashMap<String, CommentData> comments) { this.comments = comments; }

    public void setInterviewQuestionIDs(ArrayList<String> interviewQuestionIDs) { this.interviewQuestionIDs = interviewQuestionIDs; }
}
