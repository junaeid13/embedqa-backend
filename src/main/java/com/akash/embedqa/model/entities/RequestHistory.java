package com.akash.embedqa.model.entities;

import com.akash.embedqa.enums.HttpMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2048)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HttpMethod method;

    @Column(columnDefinition = "TEXT")
    private String  requestHeaders;

    @Column(columnDefinition = "TEXT")
    private String requestBody;

    private String bodyType;

    private String authType;

    @Column(columnDefinition = "TEXT")
    private String authConfig;

    @Column(nullable = false)
    private Integer statusCode;

    private String statusText;

    @Column(columnDefinition = "TEXT")
    private String responseHeaders;

    @Column(columnDefinition = "TEXT")
    private String responseBody;

    @Column(columnDefinition = "TEXT")
    private String queryParams;

    @Column(nullable = false)
    private Long responseTime; // in milliseconds

    private Long responseSize; // in bytes

    @Column(nullable = false)
    private LocalDateTime executedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_request_id", nullable = false)
    private ApiRequest apiRequest;

    @Column(name = "api_request_id", insertable = false, updatable = false)
    private Long apiRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collection_id")
    private ApiCollection collection;

    @Column(name = "collection_id", insertable = false, updatable = false)
    private Long collectionId;

    @PrePersist
    public void prePersist() {
        if (executedAt == null) {
            executedAt = LocalDateTime.now();
        }
    }
}
