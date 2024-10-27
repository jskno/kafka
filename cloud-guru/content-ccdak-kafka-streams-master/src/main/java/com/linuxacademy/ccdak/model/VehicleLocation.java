package com.linuxacademy.ccdak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
  "vehicle_id": 8566,
  "location": {
    "latitude": 0.41420080431773376,
    "longitude": 0.7571994830560752
  },
  "ts": 1609541400000
}
 */
public class VehicleLocation {

    @JsonProperty("vehicle_id")
    private Long vehicleId;
    private Location location;
    @JsonProperty("ts")
    private Long timeStamp;

    public VehicleLocation() {
    }

    public VehicleLocation(Long vehicleId, Location location, Long timeStamp) {
        this.vehicleId = vehicleId;
        this.location = location;
        this.timeStamp = timeStamp;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Long timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "VehicleLocation{" +
            "vehicleId=" + vehicleId +
            ", location=" + location +
            ", timeStamp=" + timeStamp +
            '}';
    }

}
