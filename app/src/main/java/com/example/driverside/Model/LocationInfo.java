package com.example.driverside.Model;

public class LocationInfo {

    private String lat;
    private String log;

    public LocationInfo(String lat, String log) {
        this.lat = lat;
        this.log = log;
    }

    public LocationInfo() {
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
