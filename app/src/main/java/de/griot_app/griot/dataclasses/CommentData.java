package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for comment data
 */
public class CommentData {

    private String comment;
    private String time;
    private HashMap<String, Boolean> commentatorID;
    private String commentatorName;
    private String commentatorPictureURL;

    //default-constructor
    public CommentData() {
        commentatorID = new HashMap<>();
    }

    //constructor
    public CommentData(
            String comment,
            String time,
            HashMap<String, Boolean> commentatorID,
            String commentatorName,
            String commentatorPictureURL
    ) {
        this.comment = comment;
        this.time = time;
        this.commentatorID = commentatorID;
        this.commentatorName = commentatorName;
        this.commentatorPictureURL = commentatorPictureURL;
    }

    @Override
    public String toString() {
        return "CommentData{" +
                "comment='" + comment + '\'' +
                ", time='" + time + '\'' +
                ", commentatorID=" + commentatorID +
                ", commentatorName='" + commentatorName + '\'' +
                ", commentatorPictureURL='" + commentatorPictureURL + '\'' +
                '}';
    }

    //get-methods
    public String getComment() { return comment; }

    public String getTime() { return time; }

    public HashMap<String, Boolean> getCommentatorID() { return commentatorID; }

    public String getCommentatorName() { return commentatorName; }

    public String getCommentatorPictureURL() { return commentatorPictureURL; }

    //set-methods
    public void setComment(String comment) { this.comment = comment; }

    public void setTime(String time) { this.time = time; }

    public void setCommentatorID(HashMap<String, Boolean> commentatorID) { this.commentatorID = commentatorID; }

    public void setCommentatorName(String commentatorName) { this.commentatorName = commentatorName; }

    public void setCommentatorPictureURL(String commentatorPictureURL) { this.commentatorPictureURL = commentatorPictureURL; }
}
