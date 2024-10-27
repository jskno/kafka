package com.linuxacademy.ccdak.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
{
  "vehicle_id": 6989,
  "engine_temperature": 155,
  "average_rpm": 2904
}
 */
public class VehicleIndicators {

    @JsonProperty("vehicle_id")
    private Long vehicleId;

    @JsonProperty("engine_temperature")
    private Integer engineTemperature;

    @JsonProperty("average_rpm")
    private Integer averageRpm;

    public VehicleIndicators() {
    }

    public VehicleIndicators(Long vehicleId, Integer engineTemperature, Integer averageRpm) {
        this.vehicleId = vehicleId;
        this.engineTemperature = engineTemperature;
        this.averageRpm = averageRpm;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
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
        return "VehicleIndicators{" +
            "vehicleId=" + vehicleId +
            ", engineTemperature=" + engineTemperature +
            ", averageRpm=" + averageRpm +
            '}';
    }
}
