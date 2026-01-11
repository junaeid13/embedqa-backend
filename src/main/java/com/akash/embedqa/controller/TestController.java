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
@RequestMapping("/api/v1/test")

public class TestController {

    private final TestRunnerService testRunnerService;

    @Autowired
    public TestController(TestRunnerService testRunnerService) {
        this.testRunnerService = testRunnerService;
    }

    @PostMapping("/run")
    public TestResultDTO runTest(@RequestBody TestRequestDTO request) {
        return testRunnerService.runSingleTest(request);
    }

}
