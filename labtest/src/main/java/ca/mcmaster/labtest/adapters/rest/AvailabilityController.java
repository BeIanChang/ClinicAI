package ca.mcmaster.labtest.adapters.rest;

import ca.mcmaster.labtest.adapters.rest.dto.AvailabilityDtos.*;
import ca.mcmaster.labtest.domain.model.Slot;
import ca.mcmaster.labtest.domain.port.AvailabilityPort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    private final AvailabilityPort availability;

    public AvailabilityController(AvailabilityPort availability) {
        this.availability = availability;
    }

    @GetMapping
    public ResponseEntity<BatchListOut> list(
            @RequestParam String labTechnicianIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {

        List<String> ids = Arrays.stream(labTechnicianIds.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        Map<String, List<Slot>> map = availability.freeSlotsBatch(ids, from, to);

        List<ClinicianAvailability> results = ids.stream().map(id -> {
            List<FreeSlot> slots = map.getOrDefault(id, List.of()).stream()
                    .map(s -> {
                        // Build HATEOAS action: POST /appointments?slotId=ENCODED
                        String href = "/appointments?slotId=" +
                                URLEncoder.encode(s.slotId(), StandardCharsets.UTF_8);

                        List<Field> fields = List.of(
                                new Field("patientId", "string", true, null) // client provides only patientId
                        );

                        List<Action> actions = List.of(
                                new Action("book", "POST", href, "application/json", fields)
                        );

                        return new FreeSlot(s.slotId(), s.start().toString(), s.end().toString(), actions);
                    })
                    .collect(Collectors.toList());

            return new ClinicianAvailability(id, slots);
        }).toList();

        return ResponseEntity.ok(new BatchListOut(results));
    }
}
