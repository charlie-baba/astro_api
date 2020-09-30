package com.sankore.server.emailreader.services;

import com.sankore.server.emailreader.pojo.EmailExtract;
import com.sankore.server.emailreader.entity.ClientEmail;

import java.util.List;

/**
 * @author Obi on 05/03/2019
 */
public interface EmailReader {

    List<EmailExtract> readEmail(ClientEmail clientEmail);
}
