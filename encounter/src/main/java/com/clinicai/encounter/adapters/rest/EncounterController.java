package com.clinicai.encounter.adapters.rest;
import com.clinicai.encounter.adapters.rest.dto.EncounterDtos.*;
import com.clinicai.encounter.domain.model.Encounter;
import com.clinicai.encounter.domain.port.EncounterPort;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/encounters")
public class EncounterController {
    private final EncounterPort encounters;
    public EncounterController(EncounterPort encounters){ this.encounters=encounters; }

    @PostMapping("/start")
    public ResponseEntity<EncounterOut> start(@RequestBody StartRequest req){
        Encounter e = encounters.start(req.appointmentId());
        return ResponseEntity.status(201).body(toOut(e));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<CompletedEventOut> end(@PathVariable String id){
        var e = encounters.end(id);
        return ResponseEntity.ok(new CompletedEventOut(e.encounterId(), e.patientId(), null, e.endedAt().toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncounterOut> get(@PathVariable String id){
        return encounters.get(id).map(e -> ResponseEntity.ok(toOut(e))).orElse(ResponseEntity.notFound().build());
    }

    private EncounterOut toOut(Encounter e){
        return new EncounterOut(e.encounterId(), e.appointmentId(), e.patientId(), e.clinicianId(),
        e.startedAt().toString(), e.endedAt()==null? null : e.endedAt().toString());
    }
}
