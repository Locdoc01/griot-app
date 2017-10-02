package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for comment data
 */
public class CommentData {

    private String comment;
    private String time;
    private String commentatorID;
    private String commentatorName;
    private String commentatorPictureURL;

    //default-constructor
    public CommentData() {
    }

    //constructor
    public CommentData(
            String comment,
            String time,
            String commentatorID,
            String commentatorName,
            String commentatorPictureURL
    ) {
        this.comment = comment;
        this.time = time;
        this.commentatorID = commentatorID;
        this.commentatorName = commentatorName;
        this.commentatorPictureURL = commentatorPictureURL;
    }


    //get-methods
    public String getComment() { return comment; }

    public String getTime() { return time; }

    public String getCommentatorID() { return commentatorID; }

    public String getCommentatorName() { return commentatorName; }

    public String getCommentatorPictureURL() { return commentatorPictureURL; }

    //set-methods
    public void setComment(String comment) { this.comment = comment; }

    public void setTime(String time) { this.time = time; }

    public void setCommentatorID(String commentatorID) { this.commentatorID = commentatorID; }

    public void setCommentatorName(String commentatorName) { this.commentatorName = commentatorName; }

    public void setCommentatorPictureURL(String commentatorPictureURL) { this.commentatorPictureURL = commentatorPictureURL; }
}
