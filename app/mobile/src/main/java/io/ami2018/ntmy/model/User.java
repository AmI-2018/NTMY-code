package io.ami2018.ntmy.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class User {

    // Instance
    private static final User mInstance = new User();
    private String surname;
    private String email;
    private String phone;
    // Attributes
    private String name;
    private Integer userId;
    // Relationships
    private Map<Integer, Event> events;

    private User() {
        this.events = new HashMap<>();
    }

    public static User getInstance() {
        return mInstance;
    }

    public void setInstance(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.surname = jsonObject.getString("surname");
            this.email = jsonObject.getString("email");
            this.phone = jsonObject.getString("phone");
            this.userId = jsonObject.getInt("userID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.events = new HashMap<>();
    }

    public void setInstance(String name, String surname, String email, String phone, Integer userId) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
        this.events = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Map<Integer, Event> getEvents() {
        return events;
    }

    public void setEvents(Map<Integer, Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        if (!this.events.containsKey(event.getEventId()))
            this.events.put(event.getEventId(), event);
    }
}
