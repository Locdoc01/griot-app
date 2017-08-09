package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for comment data
 */
public class CommentData {

    private String comment;
    private String time;
    private HashMap<String, Boolean> commentatorID;

    //default-constructor
    public CommentData() {
        comment = "";
        time = "";
        commentatorID = new HashMap<>();
    }

    //constructor
    public CommentData(
            String comment,
            String time,
            HashMap<String, Boolean> commentatorID
    ) {
        this.comment = comment;
        this.time = time;
        this.commentatorID = commentatorID;
    }

    //get-methods
    public String getComment() { return comment; }

    public String getTime() { return time; }

    public HashMap<String, Boolean> getCommentatorID() { return commentatorID; }

    //set-methods
    public void setComment(String comment) { this.comment = comment; }

    public void setTime(String time) { this.time = time; }

    public void setCommentatorID(HashMap<String, Boolean> commentatorID) { this.commentatorID = commentatorID; }
}
