package com.clinicai.encounter.adapters.rest.dto;
import jakarta.validation.constraints.NotNull;
import java.time.Instant; import java.util.List;
public class ScheduleDtos {
  public record SlotIn(@NotNull Instant start, @NotNull Instant end) {}
  public record SchedulePutRequest(List<SlotIn> slots) {}
  public record SlotOut(String slotId, Instant start, Instant end) {}
  public record ScheduleOut(String clinicianId, List<SlotOut> slots) {}
}
