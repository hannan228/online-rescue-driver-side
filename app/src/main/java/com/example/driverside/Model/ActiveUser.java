package com.example.driverside.Model;

public class ActiveUser {

    private String lat;
    private String log;
    private String driverType;

    public ActiveUser() {
    }

    public ActiveUser(String lat, String log, String driverType) {
        this.lat = lat;
        this.log = log;
        this.driverType = driverType;
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

    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }
}

