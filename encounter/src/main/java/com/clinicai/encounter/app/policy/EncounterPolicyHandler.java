package com.clinicai.encounter.app.policy;

import com.clinicai.encounter.domain.model.Appointment;
import com.clinicai.encounter.domain.model.Encounter;

public interface EncounterPolicyHandler {
    void onEncounterCompleted(Encounter encounter);

    default void onAppointmentBooked(Appointment appointment) {
        // no-op by default
    }

    default void onAppointmentConfirmed(Appointment appointment) {
        // no-op by default
    }
}
