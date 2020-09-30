package com.sankore.astro.repository;

import com.sankore.astro.entity.TicketTrail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author Obi on 06/06/2019
 */
public interface TicketTrailRepository extends JpaRepository<TicketTrail, Long> {

    List<TicketTrail> findAllByTicket_IdOrderByDateCreated(Long id);

}
