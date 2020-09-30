package com.sankore.server.emailreader.scheduler;

import com.sankore.server.emailreader.entity.ClientEmail;
import com.sankore.server.emailreader.pojo.EmailExtract;
import com.sankore.server.emailreader.repository.ClientEmailRepository;
import com.sankore.server.emailreader.services.EmailReader;
import com.sankore.server.emailreader.services.TicketService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author Obi on 05/03/2019
 */
@Log4j
@Component
public class GenericScheduler {

    @Autowired
    EmailReader emailReader;

    @Autowired
    TicketService ticketService;

    @Autowired
    ClientEmailRepository clientEmailRepository;

    @Scheduled(cron="*/30 * * * * ?")
    public void scheduleEmailRead() {
        List<ClientEmail> clientEmails = clientEmailRepository.findAllByActiveTrue();
        if (CollectionUtils.isEmpty(clientEmails))
            return;

        for (ClientEmail clientEmail: clientEmails) {
            log.info(".............. about to read "+ clientEmail.getEmailAddress());
            List<EmailExtract> emailExtracts = emailReader.readEmail(clientEmail);
            if (!CollectionUtils.isEmpty(emailExtracts)) {
                emailExtracts.stream().forEach(e -> ticketService.createTicket(e));
            }
        }

    }
}
