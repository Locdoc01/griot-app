package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for locally holded guest data
 */
public class LocalGuestData extends LocalPersonData {

    private String hostID;
    private String relationship;

    //default-constructor
    public LocalGuestData() {
        super();
    }


    //constructor
    public LocalGuestData(
            String contactID,
            String firstname,
            String lastname,
            String birthday,
            Integer bYear,
            Integer bMonth,
            Integer bDay,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String category,
            String hostID,
            String relationship
    ) {
        super(contactID, firstname, lastname, birthday, bYear, bMonth, bDay, email, pictureURL, pictureLocalURI, category);
        this.hostID = hostID;
        this.relationship = relationship;
    }


    //get-methods
    public String getHostID() { return hostID; }

    public String getRelationship() { return relationship; }

    //set-methods
    public void setHostID(String hostID) { this.hostID = hostID; }

    public void setRelationship(String relationship) { this.relationship = relationship; }
}
