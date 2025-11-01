package com.akash.embedqa.repository;

import com.akash.embedqa.model.entities.EnvironmentVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Author: akash
 * Date: 29/10/25
 */
@Repository
public interface EnvironmentVariableRepository extends JpaRepository<EnvironmentVariable, Long> {
}
