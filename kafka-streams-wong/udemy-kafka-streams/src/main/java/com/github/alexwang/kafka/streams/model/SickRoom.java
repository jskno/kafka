package com.github.alexwang.kafka.streams.model;

public class SickRoom {
    private String sickRoomID;
    private String doctorName;

    public SickRoom() {
    }

    public SickRoom(String sickRoomID, String doctorName) {
        this.sickRoomID = sickRoomID;
        this.doctorName = doctorName;
    }

    public String getSickRoomID() {
        return sickRoomID;
    }

    public void setSickRoomID(String sickRoomID) {
        this.sickRoomID = sickRoomID;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    @Override
    public String toString() {
        return "SickRoom{" +
                "sickRoomID='" + sickRoomID + '\'' +
                ", doctorName='" + doctorName + '\'' +
                '}';
    }
}
