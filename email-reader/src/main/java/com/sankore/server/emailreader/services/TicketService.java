package com.sankore.server.emailreader.services;

import com.sankore.server.emailreader.pojo.EmailExtract;

/**
 * @author Obi on 28/05/2019
 */
public interface TicketService {

    void createTicket(EmailExtract emailExtract);
}
