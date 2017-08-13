package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for locally holded group data
 */
public class LocalGroupData extends LocalContactData {

    private String visibility;
    private HashMap<String, String> members;

    //default-constructor
    public LocalGroupData() {
        super();
        members = new HashMap<>();
    }

    //TODO: evt nicht benötigt
    public LocalGroupData(String category) {
        super(category);
        members = new HashMap<>();
    }

    //constructor
    public LocalGroupData(
            String firstname,
            String pictureURL,
            String pictureLocalURI,
            String visibility,
            HashMap<String, String> members,
            String category
    ) {
        super(firstname, pictureURL, pictureLocalURI, category);
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
