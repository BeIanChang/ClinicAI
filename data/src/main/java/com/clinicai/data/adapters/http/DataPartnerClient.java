package com.clinicai.data.adapters.http;

import com.clinicai.data.domain.port.PartnerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class DataPartnerClient implements PartnerPort {
    private static final Logger log = LoggerFactory.getLogger(DataPartnerClient.class);

    private final RestTemplate rest;
    private final String startPath;
    private final String endPath;
    private final String labPath;

    public DataPartnerClient(RestTemplateBuilder builder,
                             @Value("${partner.base-url:http://localhost:8090}") String baseUrl,
                             @Value("${partner.paths.start:/partner/encounters/start}") String startPath,
                             @Value("${partner.paths.end:/partner/encounters/end}") String endPath,
                             @Value("${partner.paths.lab:/partner/labs}") String labPath) {
        this.rest = builder.rootUri(baseUrl).build();
        this.startPath = startPath;
        this.endPath = endPath;
        this.labPath = labPath;
    }

    @Override
    public boolean startProcessing(String encounterId, String clinicianId, String patientId) {
        Map<String, String> body = Map.of(
                "encounterId", encounterId,
                "clinicianId", clinicianId,
                "patientId", patientId
        );
        return postSafe(startPath, body, "startProcessing");
    }

    @Override
    public boolean completeProcessing(String encounterId) {
        Map<String, String> body = Map.of("encounterId", encounterId);
        return postSafe(endPath, body, "completeProcessing");
    }

    @Override
    public boolean pushLabData(String encounterId, String labPayload) {
        Map<String, String> body = Map.of("encounterId", encounterId, "results", labPayload);
        return postSafe(labPath, body, "pushLabData");
    }

    private boolean postSafe(String path, Object payload, String action) {
        try {
            ResponseEntity<Void> resp = rest.postForEntity(path, payload, Void.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                log.warn("Partner {} returned status {}", action, resp.getStatusCode());
                return false;
            }
            return true;
        } catch (RestClientException ex) {
            log.warn("Partner {} failed: {}", action, ex.getMessage());
            return false;
        }
    }
}
