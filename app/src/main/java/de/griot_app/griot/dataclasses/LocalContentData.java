package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Bases local data class for local content data classes
 */
public class LocalContentData {

    protected String contentID;
    protected String length;
    protected String dateYear;
    protected String dateMonth;
    protected String dateDay;
    protected String pictureURL;
    protected String pictureLocalURI;
    protected HashMap<String, Boolean> associatedUsers;
    protected HashMap<String, Boolean> associatedGuests;
    protected HashMap<String, Boolean> tags;

    //default-constructor
    public LocalContentData() {
        associatedUsers = new HashMap<>();
        associatedGuests = new HashMap<>();
        tags = new HashMap<>();
    }

    //constructor
    public LocalContentData(
            String contentID,
            String length,
            String dateYear,
            String dateMonth,
            String dateDay,
            String pictureURL,
            String pictureLocalURI,
            HashMap<String, Boolean> associatedUsers,
            HashMap<String, Boolean> associatedGuests,
            HashMap<String, Boolean> tags
    ) {
        this.contentID = contentID;
        this.length = length;
        this.dateYear = dateYear;
        this.dateMonth = dateMonth;
        this.dateDay = dateDay;
        this.pictureURL = pictureURL;
        this.pictureLocalURI = pictureLocalURI;
        this.associatedUsers = associatedUsers;
        this.associatedGuests = associatedGuests;
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "LocalContentData{" +
                "contentID='" + contentID + '\'' +
                ", length='" + length + '\'' +
                ", dateYear='" + dateYear + '\'' +
                ", dateMonth='" + dateMonth + '\'' +
                ", dateDay='" + dateDay + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", pictureLocalURI='" + pictureLocalURI + '\'' +
                ", associatedUsers=" + associatedUsers +
                ", associatedGuests=" + associatedGuests +
                ", tags=" + tags +
                '}';
    }

    //get-methods
    public String getContentID() { return contentID; }

    public String getLength() { return length; }

    public String getDateYear() { return dateYear; }
    public String getDateMonth() { return dateMonth; }
    public String getDateDay() { return dateDay; }

    public String getPictureURL() { return pictureURL; }

    public String getPictureLocalURI() { return pictureLocalURI; }

    public HashMap<String, Boolean> getAssociatedUsers() { return associatedUsers; }

    public HashMap<String, Boolean> getAssociatedGuests() { return associatedGuests; }

    public HashMap<String, Boolean> getTags() { return tags; }

    //set-methods
    public void setContentID(String contentID) { this.contentID = contentID; }

    public void setLength(String length) { this.length = length; }

    public void setDateYear(String dateYear) { this.dateYear = dateYear; }
    public void setDateMonth(String dateMonth) { this.dateMonth = dateMonth; }
    public void setDateDay(String dateDay) { this.dateDay = dateDay; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setPictureLocalURI(String pictureLocalURI) { this.pictureLocalURI = pictureLocalURI; }

    public void setAssociatedUsers(HashMap<String, Boolean> associatedUsers) { this.associatedUsers = associatedUsers; }

    public void setAssociatedGuests(HashMap<String, Boolean> associatedGuests) { this.associatedGuests = associatedGuests; }

    public void setTags(HashMap<String, Boolean> tags) { this.tags = tags; }
}
