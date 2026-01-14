package ca.mcmaster.labtest.domain.service;

import ca.mcmaster.labtest.domain.model.Slot;
import ca.mcmaster.labtest.domain.port.AvailabilityPort;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AvailabilityService implements AvailabilityPort {
    private final Map<String, List<Slot>> schedules;      // labTechnicianId -> List<Slot>
    private final Map<String, String> bookedSlots;        // slotId -> appointmentId

    public AvailabilityService(Map<String, List<Slot>> schedules,
                               Map<String, String> bookedSlots) {
        this.schedules = schedules;
        this.bookedSlots = bookedSlots;
    }

    @Override
    public Map<String, List<Slot>> freeSlotsBatch(List<String> labTechnicianIds, Instant from, Instant to) {
        return labTechnicianIds.stream().collect(Collectors.toMap(
            id -> id,
            id -> schedules.getOrDefault(id, List.of()).stream()
                .filter(slot -> !slot.start().isBefore(from) && !slot.end().isAfter(to))
                .filter(slot -> !bookedSlots.containsKey(slot.slotId()))
                .collect(Collectors.toList())
        ));
    }
}
