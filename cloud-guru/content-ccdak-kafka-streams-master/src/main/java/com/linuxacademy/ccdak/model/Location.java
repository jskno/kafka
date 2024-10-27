package com.linuxacademy.ccdak.model;

public class Location {

    private String Latitude;
    private String longitude;

    public Location() {
    }

    public Location(String latitude, String longitude) {
        Latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitud(String longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "Location{" +
            "Latitude='" + Latitude + '\'' +
            ", longitude='" + longitude + '\'' +
            '}';
    }

}
