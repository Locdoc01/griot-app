package de.griot_app.griot.dataclasses;

/**
 * Bases local data class for local person data classes
 */
public class LocalPersonData extends LocalContactData {

    protected String lastname;
    protected String birthday;
    protected Integer year;
    protected Integer month;
    protected Integer day;
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
            Integer year,
            Integer month,
            Integer day,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String category
    ) {
        super(contactID, firstname, pictureURL, pictureLocalURI, category);
        this.lastname = lastname;
        this.birthday = birthday;
        this.year = year;
        this.month = month;
        this.day = day;
        this.email = email;
    }

    @Override
    public String toString() {
        return "LocalPersonData{" +
                "lastname='" + lastname + '\'' +
                ", birthday='" + birthday + '\'' +
                ", year='" + year + '\'' +
                ", month='" + month + '\'' +
                ", day='" + day + '\'' +
                ", email='" + email + '\'' +
                ", isUser='" + isUser + '\'' +
                '}';
    }

    //get-methods
    public String getLastname() { return lastname; }

    public String getBirthday() { return birthday; }

    public Integer getYear() { return year; }

    public Integer getMonth() { return month; }

    public Integer getDay() { return day; }

    public String getEmail() { return email; }

    public Boolean getIsUser() { return isUser; }

    //set-methods
    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public void setYear(Integer year) { this.year = year; }

    public void setMonth(Integer month) { this.month = month; }

    public void setDay(Integer day) { this.day = day; }

    public void setEmail(String email) { this.email = email; }

    public void setIsUser(Boolean isUser) { this.isUser = isUser; }
}
