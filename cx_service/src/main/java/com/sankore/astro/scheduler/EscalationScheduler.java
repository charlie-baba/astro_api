package com.sankore.astro.scheduler;

import com.sankore.astro.entity.*;
import com.sankore.astro.enums.TicketStatus;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.repository.ClientRepository;
import com.sankore.astro.repository.TicketRepository;
import com.sankore.astro.repository.TicketSLARepository;
import com.sankore.astro.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Obi on 27/08/2020
 */
@Component
public class EscalationScheduler {

    @Autowired
    AstroLoggerService log;

    @Autowired
    TicketService ticketService;

    @Autowired
    TicketSLARepository slaRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ClientRepository clientRepository;

    @Scheduled(cron="0 0/15 * * * ?")
    public void scheduleEscalationNotification() {
        try {
            List<Client> clients = clientRepository.findValidClients();
            if (CollectionUtils.isEmpty(clients))
                return;

            for (Client client : clients) {
                List<TicketSLA> slas = slaRepository.findAllByClient_Code(client.getCode());
                if (CollectionUtils.isEmpty(slas))
                    continue;

                for (TicketSLA sla : slas) {
                    if (CollectionUtils.isEmpty(sla.getEscalationLevels()))
                        continue;

                    for (EscalationLevel esc : sla.getEscalationLevels()) {
                        if (CollectionUtils.isEmpty(esc.getAdmins()))
                            continue;

                        long interval = ticketService.getInterval(esc);
                        List<Ticket> tickets = ticketRepository.fetchTicketsToEscalate(sla.getIssueType().getId(), esc.getLevel() - 1, TicketStatus.RESOLVED);
                        List<Ticket> removeTickets = new ArrayList<>();
                        for (Ticket ticket : tickets)
                            if (new Date(ticket.getDateCreated().getTime() + interval).compareTo(new Date()) > 0)
                                removeTickets.add(ticket);
                        tickets.removeAll(removeTickets);

                        List<Ticket> updateTickets = new ArrayList<>();
                        if (!CollectionUtils.isEmpty(tickets)) {
                            for (Ticket ticket : tickets) {
                                for (User admin : esc.getAdmins()) {
                                    try {
                                        ticketService.sendEscalationEmail(ticket, admin);
                                        ticket.setLevelEscalated(esc.getLevel());
                                        updateTickets.add(ticket);
                                    } catch (Exception e) {
                                        log.error("Error", e);
                                    }
                                    ticketRepository.saveAll(updateTickets);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            log.error("Error", ex);
        }
    }

    private String getExpDate(EscalationLevel esc) {
        String timeUnit = esc.getPeriod() == 1 ? esc.getTimeUnit().name().replace("s", "") : esc.getTimeUnit().name();
        return "t.date_created + interval '"+ esc.getPeriod() +" "+ timeUnit.toLowerCase() +"'";
    }
}
