package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for locally holded guest data
 */
public class LocalGuestData extends LocalPersonData {

    private HashMap<String, Boolean> hostID;
    private String relationship;

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
            HashMap<String, Boolean> hostID,
            String relationship
    ) {
        super(contactID, firstname, lastname, birthday, bYear, bMonth, bDay, email, pictureURL, pictureLocalURI, category);
        this.hostID = hostID;
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return "LocalGuestData{" +
                "hostID=" + hostID +
                ", relationship='" + relationship + '\'' +
                '}';
    }

    //get-methods
    public HashMap<String, Boolean> getHostID() { return hostID; }

    public String getRelationship() { return relationship; }

    //set-methods
    public void setHostID(HashMap<String, Boolean> hostID) { this.hostID = hostID; }

    public void setRelationship(String relationship) { this.relationship = relationship; }
}
