package com.akash.embedqa.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: akash
 * Date: 29/10/25
 */

@Entity
@Table(name = "environments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Environment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "environment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApiRequest> apiRequests = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String variablesJson; // store environment variables as JSON string

    // Helper
    public void addApiRequest(ApiRequest request) {
        apiRequests.add(request);
        request.setEnvironment(this);
    }
}
