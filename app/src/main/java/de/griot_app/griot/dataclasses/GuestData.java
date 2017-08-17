package de.griot_app.griot.dataclasses;

import java.util.HashMap;


/**
 * Data holding class for guest data
 */
public class GuestData extends PersonData {

    private HashMap<String, Boolean> hostID;
    private String relationshop;

    //default-constructor
    public GuestData() {
        super();
        hostID = new HashMap<>();
    }

    //TODO: evt nicht benötigt
    public GuestData(String category) {
        super(category);
        hostID = new HashMap<>();
    }

    //constructor
    public GuestData(
            String firstname,
            String lastname,
            String birthday,
            Integer year,
            Integer month,
            Integer day,
            String email,
            String pictureURL,
            String pictureLocalURI,
            String category,
            HashMap<String, Boolean> hostID,
            String relationship
    ) {
        super(firstname, lastname, birthday, year, month, day, email, pictureURL, pictureLocalURI, category);
        this.hostID = hostID;
        this.relationshop = relationship;
    }

    @Override
    public String toString() {
        return "GuestData{" +
                "hostID=" + hostID +
                ", relationshop='" + relationshop + '\'' +
                '}';
    }

    //get-methods
    public HashMap<String, Boolean> getHostID() { return hostID; }

    public String getRelationshop() { return relationshop; }


    //set-methods
    public void setHostID(HashMap<String, Boolean> hostID) { this.hostID = hostID; }

    public void setRelationshop(String relationshop) { this.relationshop = relationshop; }
}
