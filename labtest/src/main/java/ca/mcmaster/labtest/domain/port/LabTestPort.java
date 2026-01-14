package ca.mcmaster.labtest.domain.port;

import ca.mcmaster.labtest.domain.model.LabTest;
import ca.mcmaster.labtest.domain.model.TestResultMetadata;
import java.util.Optional;

public interface LabTestPort {
    LabTest start(String appointmentId);
    LabTest end(String labTestId, TestResultMetadata result);
    Optional<LabTest> get(String id);
}
