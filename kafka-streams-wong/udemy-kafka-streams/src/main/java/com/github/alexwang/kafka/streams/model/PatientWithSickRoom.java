package com.github.alexwang.kafka.streams.model;

public class PatientWithSickRoom {
    private Patient patient;
    private SickRoom sickRoom;
    private long heartBeat;

    public PatientWithSickRoom() {
    }

    public PatientWithSickRoom(Patient patient, SickRoom sickRoom) {
        this.patient = patient;
        this.sickRoom = sickRoom;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public SickRoom getSickRoom() {
        return sickRoom;
    }

    public void setSickRoom(SickRoom sickRoom) {
        this.sickRoom = sickRoom;
    }

    public long getHeartBeat() {
        return heartBeat;
    }

    public void setHeartBeat(long heartBeat) {
        this.heartBeat = heartBeat;
    }

    @Override
    public String toString() {
        return "PatientWithSickRoom{" +
                "patient=" + patient +
                ", sickRoom=" + sickRoom +
                ", heartBeat=" + heartBeat +
                '}';
    }
}
