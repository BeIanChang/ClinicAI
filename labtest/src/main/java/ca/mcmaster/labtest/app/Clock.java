package ca.mcmaster.labtest.app;

import java.time.Instant;

@FunctionalInterface
public interface Clock {
    Instant now();
}
