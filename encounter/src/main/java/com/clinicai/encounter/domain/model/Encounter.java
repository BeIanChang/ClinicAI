package com.clinicai.encounter.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("encounters")
public class Encounter {
    @Id
    private String encounterId;
    private String appointmentId;
    private String patientId;
    private String clinicianId;
    private Instant startedAt;
    private Instant endedAt;

    public Encounter() {
        // for MongoDB
    }

    public Encounter(String id, String apptId, String patientId, String clinicianId, Instant startedAt) {
      this.encounterId=id; this.appointmentId=apptId; this.patientId=patientId; this.clinicianId=clinicianId; this.startedAt=startedAt;
    }
    public void end(Instant ended){ if(endedAt!=null) throw new IllegalStateException("Ended");
      this.endedAt=ended; }
    public String encounterId(){return encounterId;} public String appointmentId(){return appointmentId;}
    public String patientId(){return patientId;} public String clinicianId(){return clinicianId;}
    public Instant startedAt(){return startedAt;} public Instant endedAt(){return endedAt;}
}
