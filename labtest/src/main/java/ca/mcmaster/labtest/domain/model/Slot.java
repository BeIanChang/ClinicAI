package ca.mcmaster.labtest.domain.model;

import java.time.Instant;

public final class Slot {
    private final String slotId;
    private final Instant start;
    private final Instant end;

    public Slot(String slotId, Instant start, Instant end) {
        this.slotId = slotId;
        this.start = start;
        this.end = end;
    }

    public String slotId() { return slotId; }
    public Instant start() { return start; }
    public Instant end() { return end; }
}
