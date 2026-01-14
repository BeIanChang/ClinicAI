package ca.mcmaster.labtest.adapters.config;

import ca.mcmaster.labtest.app.EventPublisher;
import ca.mcmaster.labtest.app.Clock;
import ca.mcmaster.labtest.app.IdGenerator;
import ca.mcmaster.labtest.adapters.repo.AccountRepository;
import ca.mcmaster.labtest.adapters.repo.AppointmentRepository;
import ca.mcmaster.labtest.domain.model.Appointment;
import ca.mcmaster.labtest.domain.model.Slot;
import ca.mcmaster.labtest.domain.port.*;
import ca.mcmaster.labtest.domain.service.*;
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

    @Bean
    public AvailabilityPort availabilityPort(Map<String, List<Slot>> schedules,
                                             Map<String, String> bookedSlots) {
        return new AvailabilityService(schedules, bookedSlots);
    }

    @Bean
    public AppointmentPort appointmentPort(Map<String, Slot> slotIndex,
                                           Map<String, String> bookedSlots,
                                           AppointmentRepository appointments,
                                           AccountRepository accounts,
                                           IdGenerator ids,
                                           EventPublisher events,
                                           Clock clock) {
        return new AppointmentService(slotIndex, bookedSlots, appointments, accounts, ids, events, clock);
    }

    @Bean
    public LabTestPort labTestPort(AppointmentRepository appointments,
                                   IdGenerator ids,
                                   EventPublisher events,
                                   Clock clock) {
        return new LabTestService(appointments, ids, events, clock);
    }
}
