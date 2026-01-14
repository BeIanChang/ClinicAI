package com.clinicai.encounter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // scans com.clinicai.encounter.** by default
public class ClinicAiEncounterApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClinicAiEncounterApplication.class, args);
    }
}
