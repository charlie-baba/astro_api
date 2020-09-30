package com.sankore.server.emailreader.services.impl;

import com.sankore.server.emailreader.pojo.EmailExtract;
import com.sankore.server.emailreader.entity.ClientEmail;
import com.sankore.server.emailreader.services.EmailReader;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.search.FlagTerm;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Obi on 05/03/2019
 */
@Log4j
@Service
public class EmailReaderImpl implements EmailReader {

    @Override
    public List<EmailExtract> readEmail(ClientEmail clientEmail) {
        final String username = clientEmail.getEmailAddress();
        final String password = clientEmail.getPassword();
        final String server = clientEmail.getServer();
        final int port = clientEmail.getPort();
        final String protocol = clientEmail.getProtocol();
        List<EmailExtract> emailExtracts = new ArrayList<>();

        try {
            EmailExtract emailExtract = new EmailExtract();
            emailExtract.setClientCode(clientEmail.getClient().getCode());
            Properties props = new Properties();
            props.put("mail.store.protocol", protocol);

            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            };

            Session session = Session.getDefaultInstance(props, auth);
            Store store = session.getStore(protocol);
            store.connect(server, port, username, password);

            log.info("email address connected............");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            for (Message message : messages) {
                Address[] addresses = message.getFrom();
                for (Address address : addresses) {
                    String from = address.toString();
                    log.info("Raw address:           " + from);
                    emailExtract.getFullName().add(extractNameAndEmail(address)[0]);
                    emailExtract.getEmail().add(extractNameAndEmail(address)[1]);
                }

                emailExtract.setDate(message.getSentDate());
                emailExtract.setSubject(message.getSubject());
                Part messagePart = message;
                Object content = messagePart.getContent();
                if (content instanceof Multipart) {
                    messagePart = ((Multipart) content).getBodyPart(0);
                }

                StringBuilder myMail = new StringBuilder();
                String contentType = messagePart.getContentType();

                if (contentType.startsWith("TEXT/PLAIN") || contentType.startsWith("TEXT/HTML")) {
                    InputStream is = messagePart.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String thisLine = reader.readLine();
                    while (thisLine != null) {
                        myMail.append(thisLine).append("\n");
                        thisLine = reader.readLine();
                    }
                }
                emailExtract.setBody(myMail.toString());
                emailExtracts.add(emailExtract);

                //mark message as read
                inbox.setFlags(new Message[] {message}, new Flags(Flags.Flag.SEEN), true);
            }

            inbox.close(false);
            store.close();
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return emailExtracts;
    }

    private String[] extractNameAndEmail(Address address) {
        String from = address.toString();
        String[] nameAndEmail = new String[2];
        try {
            if (from.contains("<")) {
                String[] nameParts = from.split("<");
                String[] emailParts = nameParts[1].split(">");
                nameAndEmail[0] = nameParts[0];
                nameAndEmail[1] = emailParts[0];
            }
        } catch (Exception e) {
            log.error("Error", e);
        }
        return nameAndEmail;
    }
}
