package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for guest data
 */
public class GuestData extends PersonData {

    private String hostID;
    private String relationship;


    //default-constructor
    public GuestData() {
        super();
    }

/*
    //constructor
    public GuestData(
            String firstname,
            String lastname,
            String birthday,
            Integer bYear,
            Integer bMonth,
            Integer bDay,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String hostID,
            String relationship
    ) {
        super(firstname, lastname, birthday, bYear, bMonth, bDay, email, pictureURL, pictureLocalURI);
        this.hostID = hostID;
        this.relationship = relationship;
    }
*/

    //get-methods
    public String getHostID() { return hostID; }

    public String getRelationship() { return relationship; }


    //set-methods
    public void setHostID(String hostID) { this.hostID = hostID; }

    public void setRelationship(String relationship) { this.relationship = relationship; }
}
