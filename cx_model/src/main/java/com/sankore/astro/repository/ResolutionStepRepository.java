package com.sankore.astro.repository;

import com.sankore.astro.entity.ResolutionStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Obi on 22/05/2019
 */
public interface ResolutionStepRepository extends JpaRepository<ResolutionStep, Long> {

    @Query("select r from ResolutionStep r left join fetch r.updatedBy u where r.ticket.id = :ticketId and r.active = true")
    List<ResolutionStep> findAllActiveByTicketId(@Param("ticketId") Long ticketId);
}
