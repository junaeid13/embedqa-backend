package com.akash.embedqa.controller;

import com.akash.embedqa.model.dtos.test.AssertionDTO;
import com.akash.embedqa.model.dtos.test.TestRequestDTO;
import com.akash.embedqa.model.dtos.test.TestResultDTO;
import com.akash.embedqa.service.TestRunnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/tests")

public class TestController {
    private final TestRunnerService testRunnerService;

    @Autowired
    public TestController(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }


    @GetMapping("/run")
    public TestResultDTO runTest(
            @RequestParam String url,
            @RequestParam(required = false) Integer expectedStatus
    ) {
        TestRequestDTO request = new TestRequestDTO();
        request.setMethod("GET");
        request.setUrl(url);
        request.setName("Phase-2 Test");
        if (expectedStatus != null) {
            AssertionDTO assertion = new AssertionDTO();
            assertion.setType("statusCode");
            assertion.setValue(expectedStatus.toString());
            request.setAssertions(List.of(assertion));
        }

        return testRunnerService.runSingleTest(request);

    }
}
