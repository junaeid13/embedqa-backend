package com.akash.embedqa.service.impl;

import com.akash.embedqa.model.dtos.test.AssertionDTO;
import com.akash.embedqa.model.dtos.test.TestRequestDTO;
import com.akash.embedqa.model.dtos.test.TestResultDTO;
import com.akash.embedqa.service.TestRunnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public TestResultDTO runSingleTest(TestRequestDTO request) {
        TestResultDTO result = new TestResultDTO();
        result.setRequestName(result.getRequestName());
        result.setMessages(new ArrayList<>());

        try {
            String url = normalizerUrl(request.getUrl());
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            result.setStatusCode(response.getStatusCodeValue());
            boolean allPassed = true;

            if (request.getAssertions() != null && !request.getAssertions().isEmpty()) {
                for (AssertionDTO assertion : request.getAssertions()) {
                    boolean passed = false;
                    if ("StatusCode".equalsIgnoreCase(assertion.getType())) {
                        int expected = Integer.parseInt(assertion.getValue());
                        int actual = response.getStatusCodeValue();
                        passed = actual == expected;
                        result.getMessages().add(
                                "Excepted status " + expected +
                                        ", actual " + actual +
                                        " -> " + (passed ? "PASS" : "FAIL")
                        );
                    }
                    allPassed = allPassed && passed;
                }
            } else {
                allPassed = response.getStatusCodeValue() == 200;
                result.getMessages().add("Default check status == 200");
            }

            result.setPassed(allPassed);
            
        } catch (Exception e) {
            result.setPassed(false);
            result.getMessages().add("Exception: " + e.getMessage());
        }
        return result;
    }

    private String normalizerUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        return url;
    }
}
