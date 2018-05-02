package de.griot_app.griot.dataclasses;


/**
 * Bases data class for all contact data classes
 */
public class ContactData {

    protected String contactID;
    protected String firstname;
    protected String pictureURL;
    protected String pictureLocalURI;
    protected String category;
    protected Boolean selected = false;

    //default-constructor
    public ContactData() {
    }

/*
    //constructor
    public ContactData(
            String firstname,
            String pictureURL,
            String pictureLocalURI
    ) {
        this.firstname = firstname;
        this.pictureURL = pictureURL;
        this.pictureLocalURI = pictureLocalURI;
    }
*/

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
