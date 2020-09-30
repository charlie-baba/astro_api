package com.sankore.astro.scheduler;

import com.sankore.astro.entity.Client;
import com.sankore.astro.entity.Ticket;
import com.sankore.astro.enums.TicketStatus;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.repository.ClientRepository;
import com.sankore.astro.repository.TicketRepository;
import com.sankore.astro.services.TicketService;
import com.sankore.astro.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Obi on 02/09/2020
 */
@Component
public class TicketAutoAssignScheduler {

    @Autowired
    AstroLoggerService log;

    @Autowired
    UserService userService;

    @Autowired
    TicketService ticketService;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ClientRepository clientRepository;

    @Scheduled(cron="0 */5 * * * ?")
    public void autoAssignUnassignedTickets() {
        try {
            List<Client> clients = clientRepository.findValidClients();
            if (CollectionUtils.isEmpty(clients))
                return;

            List<String> users = userService.fetchActiveUsers();
            log.logJSON("active users: ", users);
            for (Client client : clients) {
                List<Ticket> tickets = ticketRepository.fetchUnassignedTickets(client.getCode(), TicketStatus.RESOLVED);
                if (CollectionUtils.isEmpty(tickets) || CollectionUtils.isEmpty(users))
                    return;

                for (Ticket ticket : tickets)
                    ticketService.autoAssignTicket(ticket, client.getCode(), users);
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
}
