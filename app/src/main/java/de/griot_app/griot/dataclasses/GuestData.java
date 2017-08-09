package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for guest data
 */
public class GuestData extends PersonData {

    private String visibility;
    private HashMap<String, String> members;

    //default-constructor
    public GuestData() {
        super();
        visibility = "";
        members = new HashMap<>();
    }

    //TODO: evt nicht benötigt
    public GuestData(String category) {
        super(category);
        visibility = "";
        members = new HashMap<>();
    }

    //constructor
    public GuestData(
            String firstname,
            String lastname,
            String birthday,
            String email,
            String pictureURL,
            String category,
            String visibility,
            HashMap<String, String> members
    ) {
        super(firstname, lastname, birthday, email, pictureURL, category);
        this.visibility = visibility;
        this.members = members;
    }

    //get-methods
    public String getVisibility() { return visibility; }

    public HashMap<String, String> getMembers() { return members; }

    //set-methods
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public void setMembers(HashMap<String, String> members) { this.members = members; }
}
