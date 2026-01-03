package com.akash.embedqa.model.dtos.test;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestRequestDTO {
    private String name;
    private String method;
    private String url;
    private Map<String, String> headers;
    private List<AssertionDTO> assertions;
}
