package de.griot_app.griot.DataClasses;

import java.util.HashMap;

/**
 * Data holding class for user data
 */
public class UserData extends PersonData {

    private HashMap<String, Boolean> interviewsOwn;
    private HashMap<String, Boolean> interviewsAssociated;
    private HashMap<String, String> guests;
    private HashMap<String, String> friends;
    private HashMap<String, String> groups;
    private HashMap<String, Boolean> standardTopics;
    private HashMap<String, Boolean> extraTopics;
    private HashMap<String, String> standardQuestions;
    private HashMap<String, String> extraQuestions;

    //default-constructor
    public UserData() {
        super();
        interviewsOwn = new HashMap<>();
        interviewsAssociated = new HashMap<>();
        guests = new HashMap<>();
        friends = new HashMap<>();
        groups = new HashMap<>();
        standardTopics = new HashMap<>();
        extraTopics = new HashMap<>();
        standardQuestions = new HashMap<>();
        extraQuestions = new HashMap<>();
    }

    //TODO: evt nicht benötigt
    public UserData(String category) {
        super(category);
        interviewsOwn = new HashMap<>();
        interviewsAssociated = new HashMap<>();
        guests = new HashMap<>();
        friends = new HashMap<>();
        groups = new HashMap<>();
        standardTopics = new HashMap<>();
        extraTopics = new HashMap<>();
        standardQuestions = new HashMap<>();
        extraQuestions = new HashMap<>();
    }

    //constructor
    public UserData(
            String firstname,
            String lastname,
            String birthday,
            String email,
            String pictureURL,
            String category,
            HashMap<String, Boolean> interviewsOwn,
            HashMap<String, Boolean> interviewsAssociated,
            HashMap<String, String> guests,
            HashMap<String, String> friends,
            HashMap<String, String> groups,
            HashMap<String, Boolean> standardTopics,
            HashMap<String, Boolean> extraTopics,
            HashMap<String, String> standardQuestions,
            HashMap<String, String> extraQuestions
    ) {
        super(firstname, lastname, birthday, email, pictureURL, category);
        this.interviewsOwn = interviewsOwn;
        this.interviewsAssociated = interviewsAssociated;
        this.guests = guests;
        this.friends = friends;
        this.groups = groups;
        this.standardTopics = standardTopics;
        this.extraTopics = extraTopics;
        this.standardQuestions = standardQuestions;
        this.extraQuestions = extraQuestions;
    }

    //get-methods
    public HashMap<String, Boolean> getInterviewsOwn() { return interviewsOwn; }

    public HashMap<String, Boolean> getInterviewsAssociated() { return interviewsAssociated; }

    public HashMap<String, String> getGuests() { return guests; }

    public HashMap<String, String> getFriends() { return friends; }

    public HashMap<String, String> getGroups() { return groups; }

    public HashMap<String, Boolean> getStandardTopics() { return standardTopics; }

    public HashMap<String, Boolean> getExtraTopics() { return extraTopics; }

    public HashMap<String, String> getStandardQuestions() { return standardQuestions; }

    public HashMap<String, String> getExtraQuestions() { return extraQuestions; }

    //set-methods
    public void setInterviewsOwn(HashMap<String, Boolean> interviewsOwn) { this.interviewsOwn = interviewsOwn; }

    public void setInterviewsAssociated(HashMap<String, Boolean> interviewsAssociated) { this.interviewsAssociated = interviewsAssociated; }

    public void setGuests(HashMap<String, String> guests) { this.guests = guests; }

    public void setFriends(HashMap<String, String> friends) { this.friends = friends; }

    public void setGroups(HashMap<String, String> groups) { this.groups = groups; }

    public void setStandardTopics(HashMap<String, Boolean> standardTopics) { this.standardTopics = standardTopics; }

    public void setExtraTopics(HashMap<String, Boolean> extraTopics) { this.extraTopics = extraTopics; }

    public void setStandardQuestions(HashMap<String, String> standardQuestions) { this.standardQuestions = standardQuestions; }

    public void setExtraQuestions(HashMap<String, String> extraQuestions) { this.extraQuestions = extraQuestions; }
}
