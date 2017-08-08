package de.griot_app.griot.DataClasses;


/**
 * Bases data class for all contact data classes
 */
public class ContactData {

    protected String firstname;
    protected String pictureURL;
    protected String category;

    //default-constructor
    public ContactData() {
        firstname = "";
        pictureURL = "";
        category = "";
    }

    //TODO: evt nicht benötigt
    public ContactData(String category) {
        firstname = "";
        pictureURL = "";
        this.category = category;
    }

    //constructor
    public ContactData(
            String firstname,
            String pictureURL,
            String category
    ) {
        this.firstname = firstname;
        this.pictureURL = pictureURL;
        this.category = category;
    }

    //get-methods
    public String getFirstname() { return firstname; }

    public String getPictureURL() { return pictureURL; }

    public String getCategory() { return category; }

    //set-methods
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setCategory(String category) { this.category = category; }
}
