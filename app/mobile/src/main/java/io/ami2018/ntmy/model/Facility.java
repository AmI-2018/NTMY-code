package io.ami2018.ntmy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Facility {

    private String name;
    private String description;
    private Integer facilityId;

    public Facility(String name, String description, Integer facilityId) {
        this.name = name;
        this.description = description;
        this.facilityId = facilityId;
    }

    public Facility(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.facilityId = jsonObject.getInt("facilityID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }
}
