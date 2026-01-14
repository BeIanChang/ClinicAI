// src/main/java/com/clinicai/encounter/adapters/rest/dto/AvailabilityDtos.java
package com.clinicai.encounter.adapters.rest.dto;

import java.time.Instant;
import java.util.List;

public class AvailabilityDtos {

  public record Action(
      String rel,
      String method,
      String href,
      String contentType,
      List<Field> fields
  ) {
    public record Field(String name, String type, boolean required, String value) {}
  }

  // FreeSlot now includes actions (HATEOAS)
  public record FreeSlot(
      String slotId,
      Instant start,
      Instant end,
      List<Action> actions
  ) {}

  public record ClinicianAvailability(
      String clinicianId,
      List<FreeSlot> slots
  ) {}

  public record BatchListOut(List<ClinicianAvailability> results) {}
}
