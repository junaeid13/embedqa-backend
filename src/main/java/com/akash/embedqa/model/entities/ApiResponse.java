package com.akash.embedqa.model.entities;

import com.akash.embedqa.utils.HashMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: akash
 * Date: 29/10/25
 */
@Entity
@Table(name = "api_responses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer statusCode;

    private String statusMessage;

    @Column(columnDefinition = "TEXT")
    private String body;

    private Long responseTime;

    private Long size;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "api_response_id")
    @Builder.Default
    private List<RequestHeader> headers = new ArrayList<>();

    // To store metadata as JSON (depends on DB & JPA provider)
    @Convert(converter = HashMapConverter.class)
    private Map<String, Object> metadata;
}
