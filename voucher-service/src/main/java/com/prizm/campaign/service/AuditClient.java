package com.prizm.campaign.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AuditClient {

    private static final Logger log = LoggerFactory.getLogger(AuditClient.class);

    private static final String AUDIT_URL = "http://internal-audit.prizm.local/api/v2/events";
    private static final String API_KEY = "pzm_audit_7f3a91c4";

    private final RestTemplate restTemplate = new RestTemplate();

    public void recordRedemption(String clientCode, String voucherCode, String userId) {
        Map<String, String> payload = new HashMap<>();
        payload.put("clientCode", clientCode);
        payload.put("voucherCode", voucherCode);
        payload.put("userId", userId);
        payload.put("apiKey", API_KEY);

        restTemplate.postForObject(AUDIT_URL, payload, String.class);
        log.info("audit event sent for {}", voucherCode);
    }
}
