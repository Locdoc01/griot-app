package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for locally holded guest data
 */
public class LocalGuestData extends LocalPersonData {

    private HashMap<String, Boolean> hostID;
    private String relationshop;

    //default-constructor
    public LocalGuestData() {
        super();
        hostID = new HashMap<>();
    }

    //TODO: evt nicht benötigt
    public LocalGuestData(String category) {
        super(category);
        hostID = new HashMap<>();
    }

    //constructor
    public LocalGuestData(
            String firstname,
            String lastname,
            String birthday,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String category,
            HashMap<String, Boolean> hostID,
            String relationship
    ) {
        super(firstname, lastname, birthday, email, pictureURL, pictureLocalURI, category);
        this.hostID = hostID;
        this.relationshop = relationship;
    }

    //get-methods
    public HashMap<String, Boolean> getHostID() { return hostID; }

    public String getRelationshop() { return relationshop; }


    //set-methods
    public void setHostID(HashMap<String, Boolean> hostID) { this.hostID = hostID; }

    public void setRelationshop(String relationshop) { this.relationshop = relationshop; }
}
