package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Bases data class for content data classes
 */
public class ContentData {

    protected String length;
    protected String pictureURL;
    protected HashMap<String, Boolean> associatedUsers;
    protected HashMap<String, Boolean> associatedGuests;
    protected HashMap<String, Boolean> tags;

    //default-constructor
    public ContentData() {
        length = "";
        pictureURL = "";
        associatedUsers = new HashMap<>();
        associatedGuests = new HashMap<>();
        tags = new HashMap<>();
    }

    //constructor
    public ContentData(
            String length,
            String pictureURL,
            HashMap<String, Boolean> associatedUsers,
            HashMap<String, Boolean> associatedGuests,
            HashMap<String, Boolean> tags
    ) {
        this.length = length;
        this.pictureURL = pictureURL;
        this.associatedUsers = associatedUsers;
        this.associatedGuests = associatedGuests;
        this.tags = tags;
    }

    //get-methods
    public String getLength() { return length; }

    public String getPictureURL() { return pictureURL; }

    public HashMap<String, Boolean> getAssociatedUsers() { return associatedUsers; }

    public HashMap<String, Boolean> getAssociatedGuests() { return associatedGuests; }

    public HashMap<String, Boolean> getTags() { return tags; }

    //set-methods
    public void setLength(String length) { this.length = length; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setAssociatedUsers(HashMap<String, Boolean> associatedUsers) { this.associatedUsers = associatedUsers; }

    public void setAssociatedGuests(HashMap<String, Boolean> associatedGuests) { this.associatedGuests = associatedGuests; }

    public void setTags(HashMap<String, Boolean> tags) { this.tags = tags; }
}
