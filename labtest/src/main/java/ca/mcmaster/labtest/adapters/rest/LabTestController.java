package ca.mcmaster.labtest.adapters.rest;

import ca.mcmaster.labtest.adapters.rest.dto.LabTestDtos.*;
import ca.mcmaster.labtest.domain.model.LabTest;
import ca.mcmaster.labtest.domain.model.TestResultMetadata;
import ca.mcmaster.labtest.domain.port.LabTestPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/labtests")
public class LabTestController {
    private final LabTestPort labTests;

    public LabTestController(LabTestPort labTests) {
        this.labTests = labTests;
    }

    @PostMapping("/start")
    public ResponseEntity<LabTestOut> start(@RequestBody StartRequest req) {
        LabTest labTest = labTests.start(req.appointmentId());
        return ResponseEntity.status(201).body(toOut(labTest));
    }

    @PostMapping("/{id}/end")
    public ResponseEntity<CompletedEventOut> end(@PathVariable String id, @RequestBody TestResultIn resultIn) {
        var labTest = labTests.end(id, new TestResultMetadata(resultIn.uri(), resultIn.testType(), resultIn.status(), resultIn.doctorReport()));
        return ResponseEntity.ok(new CompletedEventOut(labTest.labTestId(), labTest.patientId(), resultIn, labTest.endedAt().toString()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabTestOut> get(@PathVariable String id) {
        return labTests.get(id).map(lt -> ResponseEntity.ok(toOut(lt))).orElse(ResponseEntity.notFound().build());
    }

    private LabTestOut toOut(LabTest lt) {
        return new LabTestOut(lt.labTestId(), lt.appointmentId(), lt.patientId(), lt.labTechnicianId(),
                lt.startedAt().toString(), lt.endedAt() == null ? null : lt.endedAt().toString());
    }

    public record StartRequest(String appointmentId) {}
}
