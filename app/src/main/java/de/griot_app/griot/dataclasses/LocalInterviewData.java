package de.griot_app.griot.dataclasses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data holding class for locally holded interview data
 */
public class LocalInterviewData extends LocalContentData {

    private String title;
    private String topic;
    private String medium;
    private HashMap<String, Boolean> interviewerID;
    private String interviewerName;
    private String interviewerPictureURL;
    private String interviewerPictureLocalURI;
    private HashMap<String, Boolean> narratorID;
    private String narratorName;
    private String narratorPictureURL;
    private String narratorPictureLocalURI;
    private Boolean narratorIsUser;
    private Integer numberComments;
    private HashMap<String, CommentData> comments;
    private ArrayList<HashMap<String, Boolean>> interviewQuestions;

    //default-constructor
    public LocalInterviewData() {
        super();
        interviewerID = new HashMap<>();
        narratorID = new HashMap<>();
        narratorIsUser = false;
        numberComments = 0;
        comments = new HashMap<>();
        interviewQuestions = new ArrayList<>();
    }

    //constructor
    public LocalInterviewData(
            String contentID,
            String title,
            String date,
            String topic,
            String medium,
            String length,
            String pictureURL,
            String pictureLocalURI,
            HashMap<String, Boolean> interviewerID,
            String interviewerName,
            String interviewerPictureURL,
            String interviewerPictureLocalURI,
            HashMap<String, Boolean> narratorID,
            String narratorName,
            String narratorPictureURL,
            String narratorPictureLocalURI,
            Boolean narratorIsUser,
            HashMap<String, Boolean> associatedUsers,
            HashMap<String, Boolean> associatedGuests,
            HashMap<String, Boolean> tags,
            Integer numberComments,
            HashMap<String, CommentData> comments,
            ArrayList<HashMap<String, Boolean>> interviewQuestions
    ) {
        super(contentID, length, date, pictureURL, pictureLocalURI, associatedUsers, associatedGuests, tags);
        this.title = title;
        this.topic = topic;
        this.medium = medium;
        this.interviewerID = interviewerID;
        this.interviewerName = interviewerName;
        this.interviewerPictureURL = interviewerPictureURL;
        this.interviewerPictureLocalURI = interviewerPictureLocalURI;
        this.narratorID = narratorID;
        this.narratorName = narratorName;
        this.narratorPictureURL = narratorPictureURL;
        this.narratorPictureLocalURI = narratorPictureLocalURI;
        this.narratorIsUser = narratorIsUser;
        this.numberComments = numberComments;
        this.comments = comments;
        this.interviewQuestions = interviewQuestions;
    }

    @Override
    public String toString() {
        return "LocalInterviewData{" +
                "title='" + title + '\'' +
                ", topic='" + topic + '\'' +
                ", medium='" + medium + '\'' +
                ", interviewerID=" + interviewerID +
                ", interviewerName='" + interviewerName + '\'' +
                ", interviewerPictureURL='" + interviewerPictureURL + '\'' +
                ", interviewerPictureLocalURI='" + interviewerPictureLocalURI + '\'' +
                ", narratorID=" + narratorID +
                ", narratorName='" + narratorName + '\'' +
                ", narratorPictureURL='" + narratorPictureURL + '\'' +
                ", narratorPictureLocalURI='" + narratorPictureLocalURI + '\'' +
                ", narratorIsUser=" + narratorIsUser +
                ", numberComments=" + numberComments +
                ", comments=" + comments +
                ", interviewQuestions=" + interviewQuestions +
                '}';
    }

    //get-methods
    public String getTitle() { return title; }

    public String getTopic() { return topic; }

    public String getMedium() { return medium; }

    public HashMap<String, Boolean> getInterviewerID() { return interviewerID; }

    public String getInterviewerName() { return interviewerName; }

    public String getInterviewerPictureURL() { return interviewerPictureURL; }

    public String getInterviewerPictureLocalURI() { return interviewerPictureLocalURI; }

    public HashMap<String, Boolean> getNarratorID() { return narratorID; }

    public String getNarratorName() { return narratorName; }

    public String getNarratorPictureURL() { return narratorPictureURL; }

    public String getNarratorPictureLocalURI() { return narratorPictureLocalURI; }

    public Boolean getNarratorIsUser() { return narratorIsUser; }

    public Integer getNumberComments() { return numberComments; }

    public HashMap<String, CommentData> getComments() { return comments; }

    public ArrayList<HashMap<String, Boolean>> getInterviewQuestions() { return interviewQuestions; }

    //set-methods
    public void setTitle(String title) { this.title = title; }

    public void setTopic(String topic) { this.topic = topic; }

    public void setMedium(String medium) { this.medium = medium; }

    public void setInterviewerID(HashMap<String, Boolean> interviewerID) { this.interviewerID = interviewerID; }

    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }

    public void setInterviewerPictureURL(String interviewerPictureURL) { this.interviewerPictureURL = interviewerPictureURL; }

    public void setInterviewerPictureLocalURI(String interviewerPictureLocalURI) { this.interviewerPictureLocalURI = interviewerPictureLocalURI; }

    public void setNarratorID(HashMap<String, Boolean> narratorID) { this.narratorID = narratorID; }

    public void setNarratorName(String narratorName) { this.narratorName = narratorName; }

    public void setNarratorPictureURL(String narratorPictureURL) { this.narratorPictureURL = narratorPictureURL; }

    public void setNarratorPictureLocalURI(String narratorPictureLocalURI) { this.narratorPictureLocalURI = narratorPictureLocalURI; }

    public void setNarratorIsUser(Boolean narratorIsUser) { this.narratorIsUser = narratorIsUser; }

    public void setNumberComments(Integer numberComments) { this.numberComments = numberComments; }

    public void setComments(HashMap<String, CommentData> comments) { this.comments = comments; }

    public void setInterviewQuestions(ArrayList<HashMap<String, Boolean>> interviewQuestions) { this.interviewQuestions = interviewQuestions; }
}
