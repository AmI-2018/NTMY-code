package io.ami2018.ntmy;

import org.json.JSONException;
import org.json.JSONObject;

public class Color {
    private String name;
    private int red;
    private int green;
    private int blue;

    public int getIntColor() {
        return intColor;
    }

    private int intColor;

    public String getName() {
        return name;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public Color(String name, int red, int green, int blue) {
        this.name = name;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public Color (JSONObject color){
        try{
        red = Math.round(Float.valueOf(color.getString("red")) * 255);
        green = Math.round(Float.valueOf(color.getString("green"))* 255);
        blue = Math.round(Float.valueOf(color.getString("blue"))* 255);
        intColor = getIntFromColor(red,green,blue);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String getHexId(int red, int green, int blue){
        String id ="#";
        id = id + Integer.toHexString(red);
        id = id + Integer.toHexString(green);
        id = id + Integer.toHexString(blue);
        return id;
    }

    public int getIntFromColor(int Red, int Green, int Blue){
        Red = (Red << 16) & 0x00FF0000; //Shift red 16-bits and mask out other stuff
        Green = (Green << 8) & 0x0000FF00; //Shift Green 8-bits and mask out other stuff
        Blue = Blue & 0x000000FF; //Mask out anything not blue.

        return 0xEF000000 | Red | Green | Blue; //0xFF000000 for 100% Alpha. Bitwise OR everything together.
    }
}
