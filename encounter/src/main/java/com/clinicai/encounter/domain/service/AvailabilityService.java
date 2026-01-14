

package com.clinicai.encounter.domain.service;
import com.clinicai.encounter.domain.model.Slot;
import com.clinicai.encounter.domain.port.AvailabilityPort;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailabilityService implements AvailabilityPort {
    private final Map<String, List<Slot>> schedules;   // clinicianId -> slots (UTC)
    private final Map<String, String> bookedSlots;     // slotId -> appointmentId

    public AvailabilityService(Map<String, List<Slot>> schedules,
                              Map<String, String> bookedSlots) {
      this.schedules = schedules;
      this.bookedSlots = bookedSlots;
    }

    @Override
    public List<Slot> freeSlots(String clinicianId, Instant from, Instant to) {
      var all = schedules.getOrDefault(clinicianId, List.of());
      var out = new ArrayList<Slot>();
      for (Slot s : all) {
        boolean overlapsWindow = !s.end().isBefore(from) && !s.start().isAfter(to);
        boolean isFree = !bookedSlots.containsKey(s.slotId());
        if (overlapsWindow && isFree) out.add(s);
      }
      return out;
    }

    // (optional) You can override the batch for efficiency, but not required:
    // @Override
    // public Map<String, List<Slot>> freeSlotsBatch(List<String> ids, Instant from, Instant to) {
    //   var out = new java.util.HashMap<String, List<Slot>>();
    //   for (var id : ids) out.put(id, freeSlots(id, from, to));
    //   return out;
    // }
}
