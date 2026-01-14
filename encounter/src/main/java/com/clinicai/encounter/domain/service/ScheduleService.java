// domain/service/ScheduleService.java
package com.clinicai.encounter.domain.service;

import com.clinicai.encounter.app.EventPublisher;
import com.clinicai.encounter.domain.model.Slot;
import com.clinicai.encounter.domain.port.SchedulePort;

import java.util.*;

public class ScheduleService implements SchedulePort {
    private final Map<String, List<Slot>> schedules;   // shared bean
    private final Map<String, Slot> slotIndex;         // shared bean
    private final EventPublisher events;

    public ScheduleService(Map<String, List<Slot>> schedules,
                            Map<String, Slot> slotIndex,
                            EventPublisher events) {
        this.schedules = schedules;
        this.slotIndex = slotIndex;
        this.events = events;
    }

    @Override
    public List<Slot> setSchedule(String clinicianId, List<Slot> slotsUtc) {
        // 1) Validate non-overlap
        var sorted = new ArrayList<>(slotsUtc);
        sorted.sort(Comparator.comparing(Slot::start));
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i).start().isBefore(sorted.get(i - 1).end())) {
                throw new IllegalArgumentException("Overlapping slots");
            }
        }

        // 2) Remove prior slotIndex entries for this clinician
        slotIndex.keySet().removeIf(k -> clinicianId.equals(extractClinicianId(k)));

        // 3) Normalize: create slotIds and store to BOTH schedules and slotIndex
        List<Slot> normalized = new ArrayList<>(sorted.size());
        for (Slot s : sorted) {
        String slotId = clinicianId + "#" + s.start().toEpochMilli();
        Slot norm = new Slot(slotId, s.start(), s.end());
        normalized.add(norm);
        slotIndex.put(slotId, norm);
        }
        schedules.put(clinicianId, List.copyOf(normalized));
        return normalized;
    }

    @Override
    public List<Slot> getSchedule(String clinicianId) {
        return schedules.getOrDefault(clinicianId, List.of());
    }

  private String extractClinicianId(String slotId) {
    int i = slotId.indexOf('#');
    return i > 0 ? slotId.substring(0, i) : "";
    }
}
