package com.akash.embedqa.service;

import com.akash.embedqa.model.dtos.test.TestRequestDTO;
import com.akash.embedqa.model.dtos.test.TestResultDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


public interface TestRunnerService {
    public TestResultDTO runSingleTest(TestRequestDTO request);
}
