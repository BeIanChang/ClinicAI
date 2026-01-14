package com.clinicai.encounter.app.policy;

import com.clinicai.encounter.app.EventPublisher;
import com.clinicai.encounter.adapters.repo.AccountRepository;
import com.clinicai.encounter.adapters.repo.AccountView;
import com.clinicai.encounter.domain.model.Appointment;
import com.clinicai.encounter.domain.model.Encounter;

import java.util.Map;

public class EventDrivenEncounterPolicyHandler implements EncounterPolicyHandler {
    private final EventPublisher events;
    private final AccountRepository accounts;

    public EventDrivenEncounterPolicyHandler(EventPublisher events, AccountRepository accounts) {
        this.events = events;
        this.accounts = accounts;
    }

    @Override
    public void onEncounterCompleted(Encounter encounter) {
        publishDataAnalysisRequest(encounter);
        publishLabUpdate(encounter);
    }

    @Override
    public void onAppointmentBooked(Appointment appointment) {
        events.publish("appointment.booked", Map.of(
                "appointmentId", appointment.appointmentId(),
                "clinicianId", appointment.clinicianId(),
                "patientId", appointment.patientId(),
                "slotId", appointment.slotId(),
                "start", appointment.start().toString(),
                "end", appointment.end().toString(),
                "paymentDueAt", appointment.paymentDueAt().toString()
        ));
    }

    @Override
    public void onAppointmentConfirmed(Appointment appointment) {
        publishAppointmentConfirmedEvent(appointment);
        publishAppointmentConfirmationEmail(appointment);
    }

    private void publishDataAnalysisRequest(Encounter encounter) {
        events.publish("data.analysis.requested", Map.of(
                "encounterId", encounter.encounterId(),
                "appointmentId", encounter.appointmentId(),
                "clinicianId", encounter.clinicianId(),
                "patientId", encounter.patientId()
        ));
    }

    private void publishLabUpdate(Encounter encounter) {
        events.publish("lab.tests.update", Map.of(
                "encounterId", encounter.encounterId(),
                "appointmentId", encounter.appointmentId(),
                "clinicianId", encounter.clinicianId(),
                "patientId", encounter.patientId()
        ));
    }

    private void publishAppointmentConfirmedEvent(Appointment appointment) {
        events.publish("appointment.confirmed", Map.of(
                "appointmentId", appointment.appointmentId(),
                "invoiceId", appointment.invoiceId(),
                "clinicianId", appointment.clinicianId(),
                "patientId", appointment.patientId(),
                "slotId", appointment.slotId(),
                "confirmedAt", appointment.confirmedAt() != null ? appointment.confirmedAt().toString() : null
        ));
    }

    private void publishAppointmentConfirmationEmail(Appointment appointment) {
        String clinicianEmail = accounts.findById(appointment.clinicianId())
                .map(AccountView::getEmail)
                .orElse(null);

        if (clinicianEmail == null || clinicianEmail.isBlank()) {
            return;
        }

        String body = "Encounter appointment confirmed for clinician %s with patient %s at %s – %s."
                .formatted(
                        appointment.clinicianId(),
                        appointment.patientId(),
                        appointment.start().toString(),
                        appointment.end().toString()
                );

        events.publish("notification.email.encounter.confirmed", Map.of(
                "to", clinicianEmail,
                "subject", "Encounter appointment confirmed",
                "body", body
        ));
    }
}
