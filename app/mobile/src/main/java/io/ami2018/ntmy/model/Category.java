package io.ami2018.ntmy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {

    private String name;
    private String description;
    private Integer categoryId;

    public Category(String name, String description, Integer categoryId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
    }

    public Category(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.categoryId = jsonObject.getInt("categoryID");
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

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
