package de.griot_app.griot.dataclasses;

/**
 * Bases data class for person data classes
 */
public class PersonData extends ContactData {

    protected String lastname;
    protected String birthday;
    protected Integer byear;
    protected Integer bmonth;
    protected Integer bday;
    protected String email;
    protected Boolean isUser = false;


    //default-constructor
    public PersonData() {
        super();
    }

/*
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
            String pictureLocalURI
    ) {
        super(firstname, pictureURL, pictureLocalURI);
        this.lastname = lastname;
        this.birthday = birthday;
        this.byear = year;
        this.bmonth = month;
        this.bday = day;
        this.email = email;
    }
*/

    //get-methods
    public String getLastname() { return lastname; }

    public String getBirthday() { return birthday; }

    public Integer getByear() { return byear; }

    public Integer getBmonth() { return bmonth; }

    public Integer getBday() { return bday; }

    public String getEmail() { return email;}

    public Boolean getIsUser() { return isUser; }

    //set-methods
    public void setLastname(String lastname) { this.lastname = lastname; }

    public void setBirthday(String birthday) { this.birthday = birthday; }

    public void setByear(Integer bYear) { this.byear = bYear; }

    public void setBmonth(Integer bMonth) { this.bmonth = bMonth; }

    public void setBday(Integer bDay) { this.bday = bDay; }

    public void setEmail(String email) { this.email = email; }

    public void setIsUser(Boolean isUser) { this.isUser = isUser; }
}
