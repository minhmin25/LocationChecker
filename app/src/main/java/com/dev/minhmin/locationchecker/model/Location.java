package com.dev.minhmin.locationchecker.model;

/**
 * Created by Minh min on 1/6/2017.
 */

public class Location {
    private double x;
    private double y;
    private String name;

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }


    public Location() {

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
