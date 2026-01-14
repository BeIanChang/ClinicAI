package com.clinicai.encounter.domain.port;
import com.clinicai.encounter.domain.model.Encounter;
import java.util.Optional;
public interface EncounterPort {
  Encounter start(String appointmentId);
  Encounter end(String encounterId);
  Optional<Encounter> get(String encounterId);
}
