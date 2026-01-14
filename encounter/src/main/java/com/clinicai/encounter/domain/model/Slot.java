package com.clinicai.encounter.domain.model;

import java.time.Instant;
public record Slot(String slotId, Instant start, Instant end) {
    public Slot {
        if (start == null || end == null || !end.isAfter(start)) throw new IllegalArgumentException("Invalid slot");
    }
}
