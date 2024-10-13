package com.github.alexwang.kafka.streams.model;

public class Patient {
    private String patientID;
    private String patientName;
    private int patientAge;
    private String sickRoomID;

    public Patient() {
    }

    public Patient(String patientID, String patientName, int patientAge, String sickRoomID) {
        this.patientID = patientID;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.sickRoomID = sickRoomID;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public int getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(int patientAge) {
        this.patientAge = patientAge;
    }

    public String getSickRoomID() {
        return sickRoomID;
    }

    public void setSickRoomID(String sickRoomID) {
        this.sickRoomID = sickRoomID;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientID='" + patientID + '\'' +
                ", patientName='" + patientName + '\'' +
                ", patientAge=" + patientAge +
                ", sickRoomID='" + sickRoomID + '\'' +
                '}';
    }
}
