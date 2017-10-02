package de.griot_app.griot.dataclasses;

import android.content.Context;

import java.util.HashMap;



public class LocalData {

    private HashMap<String, LocalUserData> userData;
    private HashMap<String, LocalGuestData> guestData;
    private HashMap<String, LocalGroupData> groupData;
    private HashMap<String, LocalInterviewData> interviewData;
    private HashMap<String, LocalInterviewQuestionData> interviewQuestionData;
    private HashMap<String, LocalCommentData> commentData;

    public LocalData() {
        userData = new HashMap<>();
        guestData = new HashMap<>();
        groupData = new HashMap<>();
        interviewData = new HashMap<>();
        interviewQuestionData = new HashMap<>();
        commentData = new HashMap<>();
    }

    //get-methods
    public HashMap<String, LocalUserData> getUserData() { return userData; }
    public HashMap<String, LocalGuestData> getGuestData() { return guestData; }
    public HashMap<String, LocalGroupData> getGroupData() { return groupData; }
    public HashMap<String, LocalInterviewData> getInterviewData() { return interviewData; }
    public HashMap<String, LocalInterviewQuestionData> getInterviewQuestionData() { return interviewQuestionData; }
    public HashMap<String, LocalCommentData> getCommentData() { return commentData; }

    //set-methods
    public void setUserData(HashMap<String, LocalUserData> userData) { this.userData = userData; }

    public void setGuestData(HashMap<String, LocalGuestData> guestData) { this.guestData = guestData; }

    public void setGroupData(HashMap<String, LocalGroupData> groupData) { this.groupData = groupData; }

    public void setInterviewData(HashMap<String, LocalInterviewData> interviewData) { this.interviewData = interviewData; }

    public void setInterviewQuestionData(HashMap<String, LocalInterviewQuestionData> interviewQuestionData) { this.interviewQuestionData = interviewQuestionData; }

    public void setCommentData(HashMap<String, LocalCommentData> commentData) { this.commentData = commentData; }
}
