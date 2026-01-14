package ca.mcmaster.labtest.adapters.rest.dto;

import java.util.List;

public class AvailabilityDtos {
    public record Action(String name, String method, String href, String contentType, List<Field> fields) {}
    public record Field(String name, String type, boolean required, String value) {}
    public record FreeSlot(String slotId, String start, String end, List<Action> actions) {}
    public record ClinicianAvailability(String labTechnicianId, List<FreeSlot> slots) {}
    public record BatchListOut(List<ClinicianAvailability> availabilities) {}
}
