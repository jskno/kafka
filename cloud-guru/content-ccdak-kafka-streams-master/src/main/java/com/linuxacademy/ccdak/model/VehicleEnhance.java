package com.linuxacademy.ccdak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class VehicleEnhance {

    @JsonProperty("vehicle_id")
    private Long vehicleId;
    private Location location;
    @JsonProperty("ts")
    private Long timeStamp;
    @JsonProperty("engine_temperature")
    private Integer engineTemperature;
    @JsonProperty("average_rpm")
    private Integer averageRpm;

    public VehicleEnhance() {
    }

    public VehicleEnhance(Long vehicleId, Location location, Long timeStamp, Integer engineTemperature, Integer averageRpm) {
        this.vehicleId = vehicleId;
        this.location = location;
        this.timeStamp = timeStamp;
        this.engineTemperature = engineTemperature;
        this.averageRpm = averageRpm;
    }

    public static VehicleEnhance buildVehicleEnhance(VehicleLocation vehicleLocation, VehicleIndicators vehicleIndicators) {
        if (vehicleLocation != null) {
            if (vehicleIndicators != null) {
                return new VehicleEnhance(vehicleLocation.getVehicleId(), vehicleLocation.getLocation(), vehicleLocation.getTimeStamp(),
                    vehicleIndicators.getEngineTemperature(), vehicleIndicators.getAverageRpm());
            } else {
                return new VehicleEnhance(vehicleLocation.getVehicleId(), vehicleLocation.getLocation(), vehicleLocation.getTimeStamp(),
                    null , null);
            }
        } else {
            if (vehicleIndicators != null) {
                return new VehicleEnhance(null, null, null,
                    vehicleIndicators.getEngineTemperature(), vehicleIndicators.getAverageRpm());
            } else {
                return new VehicleEnhance(null, null, null,null , null);
            }
        }
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

    public Integer getEngineTemperature() {
        return engineTemperature;
    }

    public void setEngineTemperature(Integer engineTemperature) {
        this.engineTemperature = engineTemperature;
    }

    public Integer getAverageRpm() {
        return averageRpm;
    }

    public void setAverageRpm(Integer averageRpm) {
        this.averageRpm = averageRpm;
    }

    @Override
    public String toString() {
        return "VehicleEnhance{" +
            "vehicleId=" + vehicleId +
            ", location=" + location +
            ", timeStamp=" + timeStamp +
            ", engineTemperature=" + engineTemperature +
            ", averageRpm=" + averageRpm +
            '}';
    }
}
