package com.sankore.astro.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sankore.astro.entity.Ticket;
import com.sankore.astro.enums.TicketStatus;

/**
 * @author Obi on 26/04/2019
 */
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Ticket findTicketById(Long ticketId);

    long countByClient_Code(String clientCode);

    @Query("select t from Ticket t left join fetch t.assignedTo a where t.id = :ticketId and t.client.code = :clientCode")
    Ticket findByIdAndClientCode(@Param("ticketId") Long ticketId, @Param("clientCode") String clientCode);

    @Query("select t.category.name, count(t) from Ticket t where t.client.code = :clientCode group by t.category.name")
    List<Object[]> countByCategory(@Param("clientCode") String clientCode);

    @Query("select count(t) from Ticket t where t.client.code = :clientCode and t.dueDate = :today and t.ticketStatus <> :resolved")
    long countDueToday(@Param("clientCode") String clientCode, @Param("today") Date today, @Param("resolved") TicketStatus resolved);

    long countByClient_CodeAndTicketStatus(String clientCode, TicketStatus status);

    @Query("select count(t) from Ticket t where t.client.code = :clientCode and t.dueDate < :today and t.ticketStatus <> :resolved")
    long countOverDue(@Param("clientCode") String clientCode, @Param("today") Date today, @Param("resolved") TicketStatus resolved);

    @Query("select count(t) from Ticket t where t.ticketStatus <> :ticketStatus and t.client.code = :clientCode")
    long countByOtherThanTicketStatus(@Param("clientCode") String clientCode, @Param("ticketStatus") TicketStatus ticketStatus);

    @Query("select t.media, count(t) from Ticket t where t.client.code = :clientCode group by t.media")
    List<Object[]> countByMedia(@Param("clientCode") String clientCode);

    @Query("select t.issueType.name, count(t) from Ticket t where t.client.code = :clientCode group by t.issueType.name")
    List<Object[]> countByIssueType(@Param("clientCode") String clientCode);

    @Query("select distinct t from Ticket t left join fetch t.assignedTo a where t.issueType.id = :issueTypeId and t.levelEscalated = :level and t.ticketStatus <> :resolved")
    List<Ticket> fetchTicketsToEscalate(@Param("issueTypeId") Long issueTypeId, @Param("level") int level, @Param("resolved") TicketStatus resolved);

    @Query("select t from Ticket t where t.client.code = :clientCode and t.ticketStatus <> :resolved and t.assignedTo is null")
    List<Ticket> fetchUnassignedTickets(@Param("clientCode") String clientCode, @Param("resolved") TicketStatus resolved);
}
