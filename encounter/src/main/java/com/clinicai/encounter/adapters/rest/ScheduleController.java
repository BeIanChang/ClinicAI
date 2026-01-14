package com.clinicai.encounter.adapters.rest;
import com.clinicai.encounter.domain.model.Slot;
import com.clinicai.encounter.domain.port.SchedulePort;
import com.clinicai.encounter.adapters.rest.dto.ScheduleDtos.*;
import org.springframework.http.ResponseEntity; import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*; import java.util.*;

@RestController @RequestMapping("/clinicians/{clinicianId}/schedule")
public class ScheduleController {
    private final SchedulePort schedule;
    public ScheduleController(SchedulePort schedule){ this.schedule = schedule; }

    @PutMapping
    public ResponseEntity<ScheduleOut> put(@PathVariable String clinicianId, @RequestBody @Validated SchedulePutRequest req){
        List<Slot> slots = req.slots().stream().map(s -> new Slot(null, s.start(), s.end())).toList();
        var out = schedule.setSchedule(clinicianId, slots).stream().map(s -> new SlotOut(s.slotId(), s.start(), s.end())).toList();
        return ResponseEntity.ok(new ScheduleOut(clinicianId, out));
    }

    @GetMapping
    public ResponseEntity<ScheduleOut> get(@PathVariable String clinicianId){
        var out = schedule.getSchedule(clinicianId).stream().map(s -> new SlotOut(s.slotId(), s.start(), s.end())).toList();
        return ResponseEntity.ok(new ScheduleOut(clinicianId, out));
    }
}
