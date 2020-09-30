package com.sankore.astro.services;

import com.sankore.ligare.messaging.email.SendMail;
import com.sankore.ligare.messaging.sms.SendSMS;

public interface NotificationService {

    public void sendMailToQueue(SendMail mailModel);

    public void sendSMSToQueue(SendSMS smsModel);

    public String getMessageEndpoint();

    public String getNoReplySender();

    public String getSupportSender();

    public String getSmsSender();
}
