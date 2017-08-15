package de.griot_app.griot.dataclasses;

/**
 * Bases local data class for local person data classes
 */
public class LocalPersonData extends LocalContactData {

    protected String lastname;
    protected String birthday;
    protected String email;
    protected Boolean isUser = false;

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
            String contactID,
            String firstname,
            String lastname,
            String birthday,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String category,
            Boolean isUser
    ) {
        super(contactID, firstname, pictureURL, pictureLocalURI, category);
        this.lastname = lastname;
        this.birthday = birthday;
        this.email = email;
        this.isUser = false;
    }

    @Override
    public String toString() {
        return "LocalPersonData{" +
                "lastname='" + lastname + '\'' +
                ", birthday='" + birthday + '\'' +
                ", email='" + email + '\'' +
                ", isUser='" + isUser + '\'' +
                '}';
    }

    //get-methods
    public String getLastname() { return lastname; }

    public String getBirthday() { return birthday; }

    public String getEmail() { return email; }

    public Boolean getIsUser() { return isUser; }

    //set-methods
    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public void setEmail(String email) { this.email = email; }

    public void setIsUser(Boolean isUser) { this.isUser = isUser; }
}
