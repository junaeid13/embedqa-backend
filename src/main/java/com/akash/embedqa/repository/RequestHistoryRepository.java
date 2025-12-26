package com.akash.embedqa.repository;

import com.akash.embedqa.model.entities.RequestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * Author: akash
 * Date: 26/12/25
 */
@Repository
public interface RequestHistoryRepository extends JpaRepository<RequestHistory, Long>, JpaSpecificationExecutor<RequestHistory> {

    @Modifying
    @Query("DELETE FROM RequestHistory h WHERE h.executedAt < :date")
    void deleteOlderThan(@Param("date") LocalDateTime date);

    @Modifying
    @Query("DELETE FROM RequestHistory h")
    void deleteAllHistory();
}
