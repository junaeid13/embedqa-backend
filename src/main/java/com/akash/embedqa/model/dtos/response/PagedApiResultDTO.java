package com.akash.embedqa.model.dtos.response;

import com.akash.embedqa.controller.HistoryController;
import lombok.Data;

import java.util.List;

/**
 * Author: akash
 * Date: 26/12/25
 */
@Data
public class PagedApiResultDTO<T> {
    private Boolean success;
    private String message;
    private List<T> data;
    private List<String> errors;
    private PageInfoDTO pageInfo;
}
