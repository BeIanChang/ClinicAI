package com.clinicai.encounter.domain.port;

import com.clinicai.encounter.domain.model.Slot;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface AvailabilityPort {
    // REQUIRED: single-clinician query
    List<Slot> freeSlots(String clinicianId, Instant from, Instant to);

    // OPTIONAL: batch helper with default impl
    default Map<String, List<Slot>> freeSlotsBatch(List<String> clinicianIds, Instant from, Instant to) {
        return clinicianIds.stream()
            .distinct()
            .collect(Collectors.toMap(id -> id, id -> freeSlots(id, from, to)));
    }
}