package io.ami2018.ntmy;

import android.content.Context;
import android.widget.Toast;

public class Mode {
    private String name;
    private String icon;
    private String image;
    private String info;
    private int count;

    public Mode(String name, String icon, String image, String info, int count) {
        this.name = name;
        this.icon = icon;
        this.image = image;
        this.info = info;
        this.count = count;
    }

    public Mode(String[] par) {
        this.name = par[0];
        this.icon = par[1];
        this.image = par[2];
        this.info = par[3];
        this.count = Integer.parseInt(par[4]);
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getImage() {
        return image;
    }

    public int getCount() {
        return count;
    }

    public String getInfo() { return info; }

    public void start() {

    }

    public void stop() {

    }

}
