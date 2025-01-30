package com.jskno.f_complex_streams.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PatientWithSickRoom {

    private Patient patient;
    private SickRoom sickRoom;
    private long heartBeat;

    public PatientWithSickRoom(Patient patient, SickRoom sickRoom) {
        this.patient = patient;
        this.sickRoom = sickRoom;
    }
}
