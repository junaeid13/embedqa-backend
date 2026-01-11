package com.akash.embedqa;

import com.akash.embedqa.model.dtos.test.AssertionDTO;
import com.akash.embedqa.model.dtos.test.TestRequestDTO;
import com.akash.embedqa.model.dtos.test.TestResultDTO;
import com.akash.embedqa.service.TestRunnerService;
import com.akash.embedqa.service.impl.TestRunnerServiceImpl;
import org.hibernate.validator.internal.constraintvalidators.bv.AssertTrueValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.Assert.*;


@SpringBootTest
public class TestRunnerServiceUnitTest {

    @Autowired
    private TestRunnerService service;


    @Test
    void testGoogleHomepage_StatusCode200() {
        TestRequestDTO request = new TestRequestDTO();
        request.setName("Google Test");
        request.setMethod("GET");
        request.setUrl("google.com");

        AssertionDTO assertion = new AssertionDTO();
        assertion.setType("statusCode");
        assertion.setValue("200");

        request.setAssertions(List.of(assertion));

        TestResultDTO result = service.runSingleTest(request);

        Assertions.assertTrue(result.isPassed());
        Assertions.assertEquals(200, result.getStatusCode());
        Assertions.assertTrue(result.getMessages().stream().anyMatch(m -> m.contains("PASS")));
    }

    @Test
    void testInvalidUrl_ShouldFail() {
        TestRequestDTO request = new TestRequestDTO();
        request.setName("Invalid URL Test");
        request.setMethod("GET");
        request.setUrl("http://invalid.url.test");

        TestResultDTO result = service.runSingleTest(request);

        Assertions.assertFalse(result.isPassed());
        Assertions.assertTrue(result.getMessages().stream().anyMatch(m -> m.contains("Exception")));
    }
}

