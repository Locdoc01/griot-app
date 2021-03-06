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
        members = new HashMap<>();
    }



    //get-methods
    public String getVisibility() { return visibility; }

    public HashMap<String, String> getMembers() { return members; }

    //set-methods
    public void setVisibility(String visibility) { this.visibility = visibility; }

    public void setMembers(HashMap<String, String> members) { this.members = members; }
}
