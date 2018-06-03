package io.ami2018.ntmy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {

    private String name;
    private String description;
    private String start;
    private String end;
    private Integer eventId;

    public Event(String name, String description, String start, String end, Integer eventId) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.eventId = eventId;
    }

    public Event(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.start = jsonObject.getString("start");
            this.end = jsonObject.getString("end");
            this.eventId = jsonObject.getInt("eventID");
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

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }
}
