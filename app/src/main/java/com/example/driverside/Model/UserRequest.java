package com.example.driverside.Model;

public class UserRequest {

    private String lat;
    private String log;
    private String email;

    public UserRequest() {
    }

    public UserRequest(String lat, String log, String email) {
        this.lat = lat;
        this.log = log;
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
