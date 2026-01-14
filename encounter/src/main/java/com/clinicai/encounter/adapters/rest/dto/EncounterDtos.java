package com.clinicai.encounter.adapters.rest.dto;
public class EncounterDtos {
  public record StartRequest(String appointmentId) {}
  public record RecordingIn(String uri, double durationSec, String format) {}
  public record EncounterOut(String encounterId, String appointmentId, String patientId,
                             String clinicianId, String startedAt, String endedAt) {}
  public record CompletedEventOut(String encounterId, String patientId, RecordingIn recording, String endedAt) {}
}
