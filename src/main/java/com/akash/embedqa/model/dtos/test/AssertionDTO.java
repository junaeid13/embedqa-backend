package com.akash.embedqa.model.dtos.test;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssertionDTO {
    private String type;
    private String key;
    private String value;
}
