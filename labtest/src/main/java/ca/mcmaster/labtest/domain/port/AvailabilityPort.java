package ca.mcmaster.labtest.domain.port;

import ca.mcmaster.labtest.domain.model.Slot;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface AvailabilityPort {
    Map<String, List<Slot>> freeSlotsBatch(List<String> labTechnicianIds, Instant from, Instant to);
}
