package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for locally holded comment data
 */
public class LocalCommentData {

    private String commentID;
    private String comment;
    private String time;
    private HashMap<String, Boolean> commentatorID;
    private String commentatorName;
    private String commentatorPictureURL;
    private String commentatorPictureLocalURI;

    //default-constructor
    public LocalCommentData() {
        commentatorID = new HashMap<>();
    }

    //constructor
    public LocalCommentData(
            String commentID,
            String comment,
            String time,
            HashMap<String, Boolean> commentatorID,
            String commentatorName,
            String commentatorPictureURL,
            String commentatorPictureLocalURI
    ) {
        this.commentID = commentID;
        this.comment = comment;
        this.time = time;
        this.commentatorID = commentatorID;
        this.commentatorName = commentatorName;
        this.commentatorPictureURL = commentatorPictureURL;
        this.commentatorPictureLocalURI = commentatorPictureLocalURI;
    }

    @Override
    public String toString() {
        return "LocalCommentData{" +
                "commentID='" + commentID + '\'' +
                ", comment='" + comment + '\'' +
                ", time='" + time + '\'' +
                ", commentatorID=" + commentatorID +
                ", commentatorName='" + commentatorName + '\'' +
                ", commentatorPictureURL='" + commentatorPictureURL + '\'' +
                ", commentatorPictureLocalURI='" + commentatorPictureLocalURI + '\'' +
                '}';
    }

    //get-methods
    public String getCommentID() { return commentID; }

    public String getComment() { return comment; }

    public String getTime() { return time; }

    public HashMap<String, Boolean> getCommentatorID() { return commentatorID; }

    public String getCommentatorName() { return commentatorName; }

    public String getCommentatorPictureURL() { return commentatorPictureURL; }

    public String getCommentatorPictureLocalURI() { return commentatorPictureLocalURI; }

    //set-methods
    public void setCommentID(String commentID) { this.commentID = commentID; }

    public void setComment(String comment) { this.comment = comment; }

    public void setTime(String time) { this.time = time; }

    public void setCommentatorID(HashMap<String, Boolean> commentatorID) { this.commentatorID = commentatorID; }

    public void setCommentatorName(String commentatorName) { this.commentatorName = commentatorName; }

    public void setCommentatorPictureURL(String commentatorPictureURL) { this.commentatorPictureURL = commentatorPictureURL; }

    public void setCommentatorPictureLocalURI(String commentatorPictureLocalURI) { this.commentatorPictureLocalURI = commentatorPictureLocalURI; }
}
