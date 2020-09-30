package com.sankore.astro.services.impl;

import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.services.NotificationService;
import com.sankore.ligare.messaging.email.SendMail;
import com.sankore.ligare.messaging.sms.SendSMS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    @Qualifier("emailQueueJmsTemplate")
    JmsTemplate emailJmsTemplate;

    @Autowired
    @Qualifier("smsQueueJmsTemplate")
    JmsTemplate smsJmsTemplate;

    @Autowired
    AstroLoggerService log;

    @Autowired
    Environment env;

    public void sendMailToQueue(SendMail mailModel){
         try {
            emailJmsTemplate.convertAndSend(mailModel);
         } catch(Exception err) {
             log.error("Error", err);
         }
    }

    public void sendSMSToQueue(SendSMS smsModel){
        try {
            smsJmsTemplate.convertAndSend(smsModel);
        } catch(Exception err) {
            log.error("Error", err);
        }
    }

    public String getMessageEndpoint() {
        return env.getProperty("astro_endpoint") ;
    }

    public String getNoReplySender() {
        return env.getProperty("noreply.email");
    }

    public String getSupportSender() {
        return env.getProperty("support.email");
    }

    public String getSmsSender() {
        return env.getProperty("sms.gateway.sender","astroCX");
    }
}