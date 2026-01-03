package com.akash.embedqa.model.dtos.test;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
    private String requestName;
    private boolean passed;
    private int statusCode;
    private List<String> messages = new ArrayList<>();
}
