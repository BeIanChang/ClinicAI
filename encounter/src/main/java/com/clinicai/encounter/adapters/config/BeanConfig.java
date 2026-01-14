package com.clinicai.encounter.adapters.config;

import com.clinicai.encounter.app.EventPublisher;
import com.clinicai.encounter.app.Clock;
import com.clinicai.encounter.app.IdGenerator;
import com.clinicai.encounter.app.policy.EncounterPolicyHandler;
import com.clinicai.encounter.app.policy.EventDrivenEncounterPolicyHandler;
import com.clinicai.encounter.adapters.repo.AccountRepository;
import com.clinicai.encounter.domain.model.Slot;
import com.clinicai.encounter.domain.port.*;
import com.clinicai.encounter.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class BeanConfig {

    // Core utilities
    @Bean public Clock clock()          { return Instant::now; }
    @Bean public IdGenerator ids()      { return () -> UUID.randomUUID().toString(); }

    // Shared in-memory stores
    @Bean public Map<String, List<Slot>> schedules()        { return new ConcurrentHashMap<>(); }
    @Bean public Map<String, Slot>       slotIndex()        { return new ConcurrentHashMap<>(); }
    @Bean public Map<String, String>     bookedSlots()      { return new ConcurrentHashMap<>(); }

    // ---- THE IMPORTANT ONE: SchedulePort bean ----
    @Bean
    public SchedulePort schedulePort(Map<String, List<Slot>> schedules,
                                    Map<String, Slot> slotIndex,
                                    EventPublisher events) {
        return new ScheduleService(schedules, slotIndex, events);
    }

    @Bean
    public AvailabilityPort availabilityPort(Map<String, List<Slot>> schedules,
                                            Map<String, String> bookedSlots) {
        return new AvailabilityService(schedules, bookedSlots);
    }

    @Bean
    public AppointmentPort appointmentPort(Map<String, Slot> slotIndex,
                                           Map<String, String> bookedSlots,
                                           com.clinicai.encounter.domain.port.AppointmentStorePort appointments,
                                           IdGenerator ids,
                                           Clock clock,
                                           EncounterPolicyHandler policies) {
        return new AppointmentService(slotIndex, bookedSlots, appointments, ids, clock, policies);
    }

    @Bean
    public EncounterPolicyHandler encounterPolicyHandler(EventPublisher events, AccountRepository accounts) {
        return new EventDrivenEncounterPolicyHandler(events, accounts);
    }

    @Bean
    public EncounterPort encounterPort(com.clinicai.encounter.domain.port.AppointmentStorePort appointments,
                                       com.clinicai.encounter.domain.port.EncounterStorePort encounters,
                                       IdGenerator ids, EventPublisher events, Clock clock,
                                       EncounterPolicyHandler encounterPolicyHandler) {
        return new EncounterService(appointments, encounters, ids, events, clock, encounterPolicyHandler);
    }
}
