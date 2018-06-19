package io.ami2018.ntmy.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Color {

    private Integer red;
    private Integer green;
    private Integer blue;
    private int intColor;

    public Color(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color(JSONObject jsonObject) {
        try {
            red = Math.round(Float.valueOf(jsonObject.getString("red")) * 255);
            green = Math.round(Float.valueOf(jsonObject.getString("green")) * 255);
            blue = Math.round(Float.valueOf(jsonObject.getString("blue")) * 255);
            intColor = getIntFromColor(this.red, this.green, this.blue);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getColor() {
        return intColor;
    }

    private int getIntFromColor(int red, int green, int blue) {
        red = (red << 16) & 0x00FF0000;
        green = (green << 8) & 0x0000FF00;
        blue = blue & 0x000000FF;

        return 0xFF000000 | red | green | blue;
    }
}
