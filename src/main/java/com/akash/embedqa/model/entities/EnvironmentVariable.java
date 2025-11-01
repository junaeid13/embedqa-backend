package com.akash.embedqa.model.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Author: akash
 * Date: 29/10/25
 */
@Entity
@Table(name = "environment_variables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnvironmentVariable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "var_key", nullable = false)
    private String key;

    @Column(name = "var_value", nullable = false)
    private String value;

    @Builder.Default
    @Column(nullable = false)
    private Boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;
}
