package com.akash.embedqa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Getter
@AllArgsConstructor
public enum AuthType {
    NONE, BEARER_TOKEN, BASIC_AUTH, API_KEY, OAUTH2
}
