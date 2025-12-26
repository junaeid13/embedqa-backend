package com.akash.embedqa.model.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: akash
 * Date: 26/12/25
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfoDTO {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private Boolean first;
    private Boolean last;
}
