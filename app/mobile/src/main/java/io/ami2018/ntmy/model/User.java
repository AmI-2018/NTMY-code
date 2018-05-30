package io.ami2018.ntmy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String name;
    private String surname;
    private String email;
    private String phone;
    private int userId;

    public User(String name, String surname, String email, String phone, int userId) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.userId = userId;
    }

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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
