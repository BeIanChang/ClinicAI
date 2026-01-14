package ca.mcmaster.labtest.adapters.rest;

import ca.mcmaster.labtest.domain.model.Slot;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clinicians")
public class ClinicianController {

    private final Map<String, List<Slot>> schedules;
    private final Map<String, Slot> slotIndex;

    public ClinicianController(Map<String, List<Slot>> schedules,
                               Map<String, Slot> slotIndex) {
        this.schedules = schedules;
        this.slotIndex = slotIndex;
    }

    public record SlotInput(String start, String end) {}
    public record ScheduleInput(List<SlotInput> slots) {}
    public record SlotOutput(String slotId, String start, String end) {}
    public record ScheduleOutput(String labTechnicianId, List<SlotOutput> slots) {}

    @PutMapping("/{labTechnicianId}/schedule")
    public ResponseEntity<ScheduleOutput> updateSchedule(
            @PathVariable String labTechnicianId,
            @RequestBody ScheduleInput input) {

        // Convert input slots to domain Slots with proper slotId format: labTechnicianId#start
        List<Slot> newSlots = input.slots().stream()
                .map(s -> {
                    Instant start = Instant.parse(s.start());
                    Instant end = Instant.parse(s.end());
                    // slotId format: labTechnicianId#startInstant
                    String slotId = labTechnicianId + "#" + start.toEpochMilli();
                    return new Slot(slotId, start, end);
                })
                .collect(Collectors.toList());

        // Update the schedules map
        schedules.put(labTechnicianId, newSlots);

        // Also update the slotIndex for appointment booking
        newSlots.forEach(slot -> slotIndex.put(slot.slotId(), slot));

        // Build response
        List<SlotOutput> outputSlots = newSlots.stream()
                .map(s -> new SlotOutput(s.slotId(), s.start().toString(), s.end().toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ScheduleOutput(labTechnicianId, outputSlots));
    }

    @GetMapping("/{labTechnicianId}/schedule")
    public ResponseEntity<ScheduleOutput> getSchedule(@PathVariable String labTechnicianId) {
        List<Slot> slots = schedules.getOrDefault(labTechnicianId, List.of());

        List<SlotOutput> outputSlots = slots.stream()
                .map(s -> new SlotOutput(s.slotId(), s.start().toString(), s.end().toString()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new ScheduleOutput(labTechnicianId, outputSlots));
    }
}
