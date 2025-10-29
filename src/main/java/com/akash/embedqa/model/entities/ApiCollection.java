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
@Table(name = "api_collections")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiCollection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ApiRequest> requests = new ArrayList<>();

    public void addRequest(ApiRequest request) {
        requests.add(request);
        request.setCollection(this);
    }
}
