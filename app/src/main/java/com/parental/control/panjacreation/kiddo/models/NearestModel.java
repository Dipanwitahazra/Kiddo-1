package com.parental.control.panjacreation.kiddo.models;

public class NearestModel {
    private final String name;
    private final float distance;
    private final boolean isParent;

    public NearestModel(String name, float distance, boolean isParent) {
        this.name = name;
        this.distance = distance;
        this.isParent = isParent;
    }

    public String getName() {
        return name;
    }

    public float getDistance() {
        return distance;
    }

    public boolean isParent() {
        return isParent;
    }

}
