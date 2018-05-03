package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Base data class for content data classes
 */
public class ContentData {

    protected String contentID;
    protected String length;
    protected String dateYear;
    protected String dateMonth;
    protected String dateDay;
    protected String medium;
    protected String pictureURL;
    protected HashMap<String, Boolean> associatedUsers;
    protected HashMap<String, Boolean> associatedGuests;
    protected HashMap<String, Boolean> tags;

    //default-constructor
    public ContentData() {
        associatedUsers = new HashMap<>();
        associatedGuests = new HashMap<>();
        tags = new HashMap<>();
    }


    //get-methods
    public String getContentID() { return contentID; }

    public String getLength() { return length; }

    public String getDateYear() { return dateYear; }
    public String getDateMonth() { return dateMonth; }
    public String getDateDay() { return dateDay; }

    public String getMedium() { return medium; }

    public String getPictureURL() { return pictureURL; }

    public HashMap<String, Boolean> getAssociatedUsers() { return associatedUsers; }

    public HashMap<String, Boolean> getAssociatedGuests() { return associatedGuests; }

    public HashMap<String, Boolean> getTags() { return tags; }

    //set-methods
    public void setContentID(String contentID) { this.contentID = contentID; }

    public void setLength(String length) { this.length = length; }

    public void setDateYear(String dateYear) { this.dateYear = dateYear; }
    public void setDateMonth(String dateMonth) { this.dateMonth = dateMonth; }
    public void setDateDay(String dateDay) { this.dateDay = dateDay; }

    public void setMedium(String medium) { this.medium = medium; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setAssociatedUsers(HashMap<String, Boolean> associatedUsers) { this.associatedUsers = associatedUsers; }

    public void setAssociatedGuests(HashMap<String, Boolean> associatedGuests) { this.associatedGuests = associatedGuests; }

    public void setTags(HashMap<String, Boolean> tags) { this.tags = tags; }
}
