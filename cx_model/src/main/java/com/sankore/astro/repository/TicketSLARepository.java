package com.sankore.astro.repository;

import com.sankore.astro.entity.TicketSLA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Obi on 07/06/2019
 */
public interface TicketSLARepository extends JpaRepository<TicketSLA, Long> {

    TicketSLA findTicketSLAById(Long id);

    TicketSLA findTicketSLAByIssueType_NameAndClient_Code(String issueType, String clientCode);

    TicketSLA findTicketSLAByIssueType_IdAndClient_Id(Long issueTypeId, Long clientId);

    List<TicketSLA> findAllByClient_Code(String clientCode);

    boolean existsByClient_CodeAndIssueType_Id(String clientCode, Long issueTypeId);
}
