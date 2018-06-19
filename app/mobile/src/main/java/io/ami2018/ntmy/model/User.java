package io.ami2018.ntmy.model;

import android.graphics.Bitmap;
import android.util.SparseArray;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String surname;
    private String email;
    private String phone;
    private String name;
    private Integer userId;
    private Bitmap photo;

    private SparseArray<Event> events;

    public User(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.surname = jsonObject.getString("surname");
            this.email = jsonObject.getString("email");
            this.phone = jsonObject.getString("phone");
            this.userId = jsonObject.getInt("userID");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.events = new SparseArray<>();
    }

    public User(String name, String surname, String email, String phone, Integer userId) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
        this.events = new SparseArray<>();
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

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }

    public SparseArray<Event> getEvents() {
        return events;
    }

    public void setEvents(SparseArray<Event> events) {
        this.events = events;
    }

    public void addEvent(Event event) {
        if (this.events.get(event.getEventId()) == null)
            this.events.append(event.getEventId(), event);
    }
}
