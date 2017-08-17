package de.griot_app.griot.dataclasses;

/**
 * Bases data class for person data classes
 */
public class PersonData extends ContactData {

    protected String lastname;
    protected String birthday;
    protected Integer bYear;
    protected Integer bMonth;
    protected Integer bDay;
    protected String email;
    protected Boolean isUser = false;

    //default-constructor
    public PersonData() {
        super();
    }

    //TODO: evt nicht benötigt
    public PersonData(String category) {
        super(category);
    }

    //constructor
    public PersonData(
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
        super(firstname, pictureURL, pictureLocalURI, category);
        this.lastname = lastname;
        this.birthday = birthday;
        this.bYear = year;
        this.bMonth = month;
        this.bDay = day;
        this.email = email;
    }

    @Override
    public String toString() {
        return "PersonData{" +
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

    public Integer getbYear() { return bYear; }

    public Integer getbMonth() { return bMonth; }

    public Integer getbDay() { return bDay; }

    public String getEmail() { return email;}

    public Boolean getIsUser() { return isUser; }

    //set-methods
    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public void setbYear(Integer bYear) { this.bYear = bYear; }

    public void setbMonth(Integer bMonth) { this.bMonth = bMonth; }

    public void setbDay(Integer bDay) { this.bDay = bDay; }

    public void setEmail(String email) { this.email = email; }

    public void setIsUser(Boolean isUser) { this.isUser = isUser; }
}
