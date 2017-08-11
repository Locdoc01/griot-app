package de.griot_app.griot.dataclasses;

/**
 * Bases data class for person data classes
 */
public class PersonData extends ContactData {

    protected String lastname;
    protected String birthday;
    protected String email;

    //default-constructor
    public PersonData() {
        super();
        lastname = "";
        birthday = "";
        email = "";
    }

    //TODO: evt nicht benötigt
    public PersonData(String category) {
        super(category);
        lastname = "";
        birthday = "";
        email = "";
    }

    //constructor
    public PersonData(
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
