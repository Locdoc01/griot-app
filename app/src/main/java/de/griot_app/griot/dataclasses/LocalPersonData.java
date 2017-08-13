package de.griot_app.griot.dataclasses;

/**
 * Bases local data class for local person data classes
 */
public class LocalPersonData extends LocalContactData {

    protected String lastname;
    protected String birthday;
    protected String email;

    //default-constructor
    public LocalPersonData() {
        super();
    }

    //TODO: evt nicht benötigt
    public LocalPersonData(String category) {
        super(category);
    }

    //constructor
    public LocalPersonData(
            String firstname,
            String lastname,
            String birthday,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String category
    ) {
        super(firstname, pictureURL, pictureLocalURI, category);
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
    }

    //get-methods
    public String getLastname() { return lastname; }

    public String getBirthday() { return birthday; }

    public String getEmail() { return email;}

    //set-methods
    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public void setEmail(String email) { this.email = email; }
}
