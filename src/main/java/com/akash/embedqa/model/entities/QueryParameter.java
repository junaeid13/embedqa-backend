package com.akash.embedqa.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryParameter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "api_request_id") // foreign key column
    private ApiRequest apiRequest;
}
