package de.griot_app.griot.dataclasses;


/**
 * Bases data class for all contact data classes
 */
public class ContactData {

    protected String firstname;
    protected String pictureURL;
    protected String pictureLocalURI;

    //default-constructor
    public ContactData() {
    }


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


    //get-methods
    public String getFirstname() { return firstname; }

    public String getPictureURL() { return pictureURL; }

    public String getPictureLocalURI() { return pictureLocalURI; }

    //set-methods
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setPictureLocalURI(String pictureLocalURI) { this.pictureLocalURI = pictureLocalURI; }
}
