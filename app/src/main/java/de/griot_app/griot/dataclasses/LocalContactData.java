package de.griot_app.griot.dataclasses;


/**
 * Bases local data class for all local contact data classes
 */
public class LocalContactData {

    protected String firstname;
    protected String pictureURL;
    protected String pictureLocalURI;
    protected String category;

    //default-constructor
    public LocalContactData() {
    }

    //TODO: evt nicht benötigt
    public LocalContactData(String category) {
        this.category = category;
    }

    //constructor
    public LocalContactData(
            String firstname,
            String pictureURL,
            String pictureLocalURI,
            String category
    ) {
        this.firstname = firstname;
        this.pictureURL = pictureURL;
        this.pictureLocalURI = pictureLocalURI;
        this.category = category;
    }

    //get-methods
    public String getFirstname() { return firstname; }

    public String getPictureURL() { return pictureURL; }

    public String getPictureLocalURI() { return pictureLocalURI; }

    public String getCategory() { return category; }

    //set-methods
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public void setPictureURL(String pictureURL) { this.pictureURL = pictureURL; }

    public void setPictureLocalURI(String pictureLocalURI) { this.pictureLocalURI = pictureLocalURI; }

    public void setCategory(String category) { this.category = category; }
}
