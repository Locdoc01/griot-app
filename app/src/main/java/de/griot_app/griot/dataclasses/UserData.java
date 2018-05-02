package de.griot_app.griot.dataclasses;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data holding class for user data
 */
public class UserData extends PersonData {

    private HashMap<String, Boolean> interviewsOwn;
    private HashMap<String, Boolean> interviewsAll;
    private HashMap<String, String> guests;
    private HashMap<String, String> friends;
    private HashMap<String, String> groups;
    private ArrayList<Boolean> standardTopics;
    private HashMap<String, Boolean> extraTopics;
    private HashMap<String, Integer> standardQuestions;
    private HashMap<String, String> extraQuestions;


    //default-constructor
    public UserData() {
        super();
        isUser = true;
        interviewsOwn = new HashMap<>();
        interviewsAll = new HashMap<>();
        guests = new HashMap<>();
        friends = new HashMap<>();
        groups = new HashMap<>();
        standardTopics = new ArrayList<>();
        extraTopics = new HashMap<>();
        standardQuestions = new HashMap<>();
        extraQuestions = new HashMap<>();
    }

    /*
    //constructor
    public UserData(
            String firstname,
            String lastname,
            String birthday,
            Integer bYear,
            Integer bMonth,
            Integer bDay,
            String email,
            String pictureURL,
            String pictureLocalURI,
            HashMap<String, Boolean> interviewsOwn,
            HashMap<String, Boolean> interviewsAll,
            HashMap<String, String> guests,
            HashMap<String, String> friends,
            HashMap<String, String> groups,
            ArrayList<Boolean> standardTopics,
            HashMap<String, Boolean> extraTopics,
            HashMap<String, Integer> standardQuestions,
            HashMap<String, String> extraQuestions
    ) {
        super(firstname, lastname, birthday, bYear, bMonth, bDay, email, pictureURL, pictureLocalURI);
        isUser = true;
        this.interviewsOwn = interviewsOwn;
        this.interviewsAll = interviewsAll;
        this.guests = guests;
        this.friends = friends;
        this.groups = groups;
        this.standardTopics = standardTopics;
        this.extraTopics = extraTopics;
        this.standardQuestions = standardQuestions;
        this.extraQuestions = extraQuestions;
    }
*/

    //get-methods
    public HashMap<String, Boolean> getInterviewsOwn() { return interviewsOwn; }

    public HashMap<String, Boolean> getInterviewsAll() { return interviewsAll; }

    public HashMap<String, String> getGuests() { return guests; }

    public HashMap<String, String> getFriends() { return friends; }

    public HashMap<String, String> getGroups() { return groups; }

    public ArrayList<Boolean> getStandardTopics() { return standardTopics; }

    public HashMap<String, Boolean> getExtraTopics() { return extraTopics; }

    public HashMap<String, Integer> getStandardQuestions() { return standardQuestions; }

    public HashMap<String, String> getExtraQuestions() { return extraQuestions; }

    //set-methods
    public void setInterviewsOwn(HashMap<String, Boolean> interviewsOwn) { this.interviewsOwn = interviewsOwn; }

    public void setInterviewsAll(HashMap<String, Boolean> interviewsAll) { this.interviewsAll = interviewsAll; }

    public void setGuests(HashMap<String, String> guests) { this.guests = guests; }

    public void setFriends(HashMap<String, String> friends) { this.friends = friends; }

    public void setGroups(HashMap<String, String> groups) { this.groups = groups; }

    public void setStandardTopics(ArrayList<Boolean> standardTopics) { this.standardTopics = standardTopics; }

    public void setExtraTopics(HashMap<String, Boolean> extraTopics) { this.extraTopics = extraTopics; }

    public void setStandardQuestions(HashMap<String, Integer> standardQuestions) { this.standardQuestions = standardQuestions; }

    public void setExtraQuestions(HashMap<String, String> extraQuestions) { this.extraQuestions = extraQuestions; }
}
