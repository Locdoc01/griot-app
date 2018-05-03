package de.griot_app.griot.dataclasses;


import com.google.firebase.database.Exclude;

/**
 * Base data class for all contact data classes
 */
public class ContactData {

    protected String firstname;
    protected String pictureURL;
    protected String category;

    //following attributes are excluded from uploading to Firebase Database
    @Exclude
    protected String contactID;
    @Exclude
    protected Boolean selected = false;


    //default-constructor
    public ContactData() {
    }



    //get-methods
    public String getContactID() { return contactID; }

    public String getFirstname() { return firstname; }

    public String getPictureURL() { return pictureURL; }

    public String getCategory() { return category; }

    public Boolean getSelected() { return selected; }

    //set-methods
    public void setContactID(String contactID) { this.contactID = contactID; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setCategory(String category) { this.category = category; }

    public void setSelected(Boolean selected) { this.selected = selected; }
}
