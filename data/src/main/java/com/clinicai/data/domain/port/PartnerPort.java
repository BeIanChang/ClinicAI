package com.clinicai.data.domain.port;

public interface PartnerPort {
    boolean startProcessing(String encounterId, String clinicianId, String patientId);
    boolean completeProcessing(String encounterId);
    boolean pushLabData(String encounterId, String labPayload);
}
