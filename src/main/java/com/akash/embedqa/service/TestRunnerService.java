package com.akash.embedqa.service;

import com.akash.embedqa.model.dtos.test.TestRequestDTO;
import com.akash.embedqa.model.dtos.test.TestResultDTO;


public interface TestRunnerService {
    TestResultDTO runSingleTest(TestRequestDTO request);
}
