// adapters/rest/dto/AppointmentDtos.java
package com.clinicai.encounter.adapters.rest.dto;
public class AppointmentDtos {
  public record CreateIn(String patientId, String slotId) {}
  public record Out(String appointmentId, String clinicianId, String patientId,
                    String slotId, String status, String start, String end,
                    String paymentDueAt, String invoiceId, String confirmedAt) {}
}
