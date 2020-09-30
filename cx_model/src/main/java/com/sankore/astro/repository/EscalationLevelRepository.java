package com.sankore.astro.repository;

import com.sankore.astro.entity.EscalationLevel;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Obi on 14/08/2020
 */
public interface EscalationLevelRepository extends JpaRepository<EscalationLevel, Long> {

    EscalationLevel findByLevelAndTicketSLA_Id(int level, Long slaId);

}
