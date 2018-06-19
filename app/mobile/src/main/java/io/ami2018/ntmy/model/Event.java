package io.ami2018.ntmy.model;

import android.util.SparseArray;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {

    private String name;
    private String description;
    private String start;
    private String end;
    private Integer eventId;
    private User creator;

    private Room room;
    private SparseArray<Category> categories;
    private Color color;

    public Event(String name, String description, String start, String end, Integer eventId, User creator) {
        this.name = name;
        this.description = description;
        this.start = start;
        this.end = end;
        this.eventId = eventId;
        this.creator = creator;
        this.room = null;
        this.color = null;
        this.categories = new SparseArray<>();
    }

    public Event(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.start = jsonObject.getString("start");
            this.end = jsonObject.getString("end");
            this.eventId = jsonObject.getInt("eventID");
            this.creator = new User(jsonObject.getJSONObject("creator"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.room = null;
        this.color = null;
        this.categories = new SparseArray<>();
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

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public SparseArray<Category> getCategories() {
        return categories;
    }

    public void setCategories(SparseArray<Category> categories) {
        this.categories = categories;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void addCategory(Category category) {
        if (this.categories.get(category.getCategoryId()) == null) {
            this.categories.append(category.getCategoryId(), category);
        }
    }
}
