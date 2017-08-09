package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for guest data
 */
public class GroupData extends ContactData {

    private String visibility;
    private HashMap<String, String> members;

    //default-constructor
    public GroupData() {
        super();
        visibility = "";
        members = new HashMap<>();
    }

    //TODO: evt nicht benötigt
    public GroupData(String category) {
        super(category);
        visibility = "";
        members = new HashMap<>();
    }

    //constructor
    public GroupData(
            String firstname,
            String pictureURL,
            String visibility,
            HashMap<String, String> members,
            String category
            ) {
        super(firstname, pictureURL, category);
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
