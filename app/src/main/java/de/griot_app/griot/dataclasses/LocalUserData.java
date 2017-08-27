package de.griot_app.griot.dataclasses;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data holding class for locally holded user data
 */
public class LocalUserData extends LocalPersonData /*implements Parcelable*/ {

    private HashMap<String, Boolean> interviewsAll;
    private HashMap<String, Boolean> interviewsAssociated;
    private HashMap<String, String> guests;
    private HashMap<String, String> friends;
    private HashMap<String, String> groups;
    private ArrayList<Boolean> standardTopics;
    private HashMap<String, Boolean> extraTopics;
    private HashMap<String, Integer> standardQuestions;
    private HashMap<String, String> extraQuestions;

    /*
    //necessary for Parcelable-interface-implementation
    public static final Parcelable.Creator<LocalUserData> CREATOR = new Parcelable.Creator<LocalUserData>() {
        @Override
        public LocalUserData createFromParcel(Parcel source) {
            return new LocalUserData(source);
        }

        @Override
        public LocalUserData[] newArray(int size) {
            return new LocalUserData[size];
        }
    };
    */

    //default-constructor
    public LocalUserData() {
        super();
        isUser = true;
        interviewsAll = new HashMap<>();
        interviewsAssociated = new HashMap<>();
        guests = new HashMap<>();
        friends = new HashMap<>();
        groups = new HashMap<>();
        standardTopics = new ArrayList<>();
        extraTopics = new HashMap<>();
        standardQuestions = new HashMap<>();
        extraQuestions = new HashMap<>();
    }

    //TODO: evt nicht benötigt
    public LocalUserData(String category) {
        super(category);
        isUser = true;
        interviewsAll = new HashMap<>();
        interviewsAssociated = new HashMap<>();
        guests = new HashMap<>();
        friends = new HashMap<>();
        groups = new HashMap<>();
        standardTopics = new ArrayList<>();
        extraTopics = new HashMap<>();
        standardQuestions = new HashMap<>();
        extraQuestions = new HashMap<>();
    }

    //constructor
    public LocalUserData(
            String contactID,
            String firstname,
            String lastname,
            String birthday,
            Integer bYear,
            Integer bMonth,
            Integer bDay,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String category,
            HashMap<String, Boolean> interviewsAll,
            HashMap<String, Boolean> interviewsAssociated,
            HashMap<String, String> guests,
            HashMap<String, String> friends,
            HashMap<String, String> groups,
            ArrayList<Boolean> standardTopics,
            HashMap<String, Boolean> extraTopics,
            HashMap<String, Integer> standardQuestions,
            HashMap<String, String> extraQuestions
    ) {
        super(contactID, firstname, lastname, birthday, bYear, bMonth, bDay, email, pictureURL, pictureLocalURI, category);
        isUser = true;
        this.interviewsAll = interviewsAll;
        this.interviewsAssociated = interviewsAssociated;
        this.guests = guests;
        this.friends = friends;
        this.groups = groups;
        this.standardTopics = standardTopics;
        this.extraTopics = extraTopics;
        this.standardQuestions = standardQuestions;
        this.extraQuestions = extraQuestions;
    }

    /*
    //following methods are necessary for Parcelable-interface-implementation
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstname);
        dest.writeString(lastname);
        dest.writeString(birthday);
        dest.writeString(email);
        dest.writeString(pictureURL);
        dest.writeString(pictureLocalURI);
        dest.writeString(category);
        dest.writeMap(interviewsOwn);
        dest.writeMap(interviewsAssociated);
        dest.writeMap(guests);
        dest.writeMap(friends);
        dest.writeMap(groups);
        dest.writeMap(standardTopics);
        dest.writeMap(extraTopics);
        dest.writeMap(standardQuestions);
        dest.writeMap(extraQuestions);
    }

    private void readFromParcel(Parcel in) {
        firstname = in.readString();
        lastname = in.readString();
        birthday = in.readString();
        email = in.readString();
        pictureURL = in.readString();
        pictureLocalURI = in.readString();
        category = in.readString();
        interviewsOwn = in.readHashMap(HashMap.class.getClassLoader());         //TODO korrigieren
        interviewsAssociated = in.readHashMap(HashMap.class.getClassLoader());  //...
        guests = in.readHashMap(HashMap.class.getClassLoader());                //
        friends = in.readHashMap(HashMap.class.getClassLoader());
        groups = in.readHashMap(HashMap.class.getClassLoader());
        standardTopics = in.readHashMap(HashMap.class.getClassLoader());
        extraTopics = in.readHashMap(HashMap.class.getClassLoader());
        standardQuestions = in.readHashMap(HashMap.class.getClassLoader());
        extraQuestions = in.readHashMap(HashMap.class.getClassLoader());
    }

    private LocalUserData(Parcel in){
        readFromParcel(in);
    }
    */

    @Override
    public String toString() {
        return "LocalUserData{" +
                "interviewsAll=" + interviewsAll +
                ", interviewsAssociated=" + interviewsAssociated +
                ", guests=" + guests +
                ", friends=" + friends +
                ", groups=" + groups +
                ", standardTopics=" + standardTopics +
                ", extraTopics=" + extraTopics +
                ", standardQuestions=" + standardQuestions +
                ", extraQuestions=" + extraQuestions +
                '}';
    }

    //get-methods
    public HashMap<String, Boolean> getInterviewsAll() { return interviewsAll; }

    public HashMap<String, Boolean> getInterviewsAssociated() { return interviewsAssociated; }

    public HashMap<String, String> getGuests() { return guests; }

    public HashMap<String, String> getFriends() { return friends; }

    public HashMap<String, String> getGroups() { return groups; }

    public ArrayList<Boolean> getStandardTopics() { return standardTopics; }

    public HashMap<String, Boolean> getExtraTopics() { return extraTopics; }

    public HashMap<String, Integer> getStandardQuestions() { return standardQuestions; }

    public HashMap<String, String> getExtraQuestions() { return extraQuestions; }

    //set-methods
    public void setInterviewsAll(HashMap<String, Boolean> interviewsAll) { this.interviewsAll = interviewsAll; }

    public void setInterviewsAssociated(HashMap<String, Boolean> interviewsAssociated) { this.interviewsAssociated = interviewsAssociated; }

    public void setGuests(HashMap<String, String> guests) { this.guests = guests; }

    public void setFriends(HashMap<String, String> friends) { this.friends = friends; }

    public void setGroups(HashMap<String, String> groups) { this.groups = groups; }

    public void setStandardTopics(ArrayList<Boolean> standardTopics) { this.standardTopics = standardTopics; }

    public void setExtraTopics(HashMap<String, Boolean> extraTopics) { this.extraTopics = extraTopics; }

    public void setStandardQuestions(HashMap<String, Integer> standardQuestions) { this.standardQuestions = standardQuestions; }

    public void setExtraQuestions(HashMap<String, String> extraQuestions) { this.extraQuestions = extraQuestions; }
}
