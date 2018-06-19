package io.ami2018.ntmy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Room {

    private String name;
    private String description;
    private Integer size;
    private Integer roomId;

    public Room(String name, String description, Integer size, Integer roomId) {
        this.name = name;
        this.description = description;
        this.size = size;
        this.roomId = roomId;
    }

    public Room(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.size = jsonObject.getInt("size");
            this.roomId = jsonObject.getInt("roomID");
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
