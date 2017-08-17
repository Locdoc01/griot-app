package de.griot_app.griot.dataclasses;

/**
 * Bases local data class for local person data classes
 */
public class LocalPersonData extends LocalContactData {

    protected String lastname;
    protected String birthday;
    protected Integer bYear;
    protected Integer bMonth;
    protected Integer bDay;
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
        this.bYear = year;
        this.bMonth = month;
        this.bDay = day;
        this.email = email;
    }

    @Override
    public String toString() {
        return "LocalPersonData{" +
                "lastname='" + lastname + '\'' +
                ", birthday='" + birthday + '\'' +
                ", bYear='" + bYear + '\'' +
                ", bMonth='" + bMonth + '\'' +
                ", bDay='" + bDay + '\'' +
                ", email='" + email + '\'' +
                ", isUser='" + isUser + '\'' +
                '}';
    }

    //get-methods
    public String getLastname() { return lastname; }

    public String getBirthday() { return birthday; }

    public Integer getYear() { return bYear; }

    public Integer getMonth() { return bMonth; }

    public Integer getDay() { return bDay; }

    public String getEmail() { return email; }

    public Boolean getIsUser() { return isUser; }

    //set-methods
    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public void setYear(Integer bYear) { this.bYear = bYear; }

    public void setMonth(Integer month) { this.bMonth = month; }

    public void setDay(Integer bDay) { this.bDay = bDay; }

    public void setEmail(String email) { this.email = email; }

    public void setIsUser(Boolean isUser) { this.isUser = isUser; }
}
