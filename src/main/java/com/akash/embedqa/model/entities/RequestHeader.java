package com.akash.embedqa.model.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Entity
@Table(name = "request_headers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestHeader extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String headerName;

    @Column(nullable = false)
    private String headerValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_request_id", nullable = false)
    private ApiRequest apiRequest;
}
