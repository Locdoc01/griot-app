package de.griot_app.griot.dataclasses;


/**
 * Bases local data class for all local contact data classes
 */
public class LocalContactData {

    protected String contactID;
    protected String firstname;
    protected String pictureURL;
    protected String pictureLocalURI;
    protected String category;
    protected Boolean selected = false;

    //default-constructor
    public LocalContactData() {
    }

    //TODO: evt nicht benötigt
    public LocalContactData(String category) {
        this.category = category;
    }


    //constructor
    public LocalContactData(
            String contactID,
            String firstname,
            String pictureURL,
            String pictureLocalURI,
            String category
    ) {
        this.contactID = contactID;
        this.firstname = firstname;
        this.pictureURL = pictureURL;
        this.pictureLocalURI = pictureLocalURI;
        this.category = category;
    }


    @Override
    public String toString() {
        return "LocalContactData{" +
                "contactID='" + contactID + '\'' +
                ", firstname='" + firstname + '\'' +
                ", pictureURL='" + pictureURL + '\'' +
                ", pictureLocalURI='" + pictureLocalURI + '\'' +
                ", category='" + category + '\'' +
                '}';
    }

    //get-methods
    public String getContactID() { return contactID; }

    public String getFirstname() { return firstname; }

    public String getPictureURL() { return pictureURL; }

    public String getPictureLocalURI() { return pictureLocalURI; }

    public String getCategory() { return category; }

    public Boolean getSelected() { return selected; }

    //set-methods
    public void setContactID(String contactID) { this.contactID = contactID; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setPictureLocalURI(String pictureLocalURI) { this.pictureLocalURI = pictureLocalURI; }

    public void setCategory(String category) { this.category = category; }

    public void setSelected(Boolean selected) { this.selected = selected; }
}
