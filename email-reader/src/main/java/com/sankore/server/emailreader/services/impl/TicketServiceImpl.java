package com.sankore.server.emailreader.services.impl;

import com.sankore.server.emailreader.pojo.BaseResponse;
import com.sankore.server.emailreader.pojo.CreateTicketRequest;
import com.sankore.server.emailreader.pojo.EmailExtract;
import com.sankore.server.emailreader.services.RestClient;
import com.sankore.server.emailreader.services.TicketService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Obi on 28/05/2019
 */
@Log4j
@Service
public class TicketServiceImpl implements TicketService {

    @Value("${cx_api_end_point}")
    String url;

    @Value("${cx_api_auth}")
    String auth;

    @Override
    public void createTicket(EmailExtract emailExtract) {
        try {
            RestClient<BaseResponse> rest = new RestClient<>();
            rest.setClazz(BaseResponse.class);

            CreateTicketRequest request = new CreateTicketRequest();
            request.setClientCode(emailExtract.getClientCode());
            request.setHandlerMethod("createTicket");
            String[] reporterName = emailExtract.getFullName().get(0).split(" ");
            request.setReporterFirstName(reporterName[0]);
            request.setReporterLastName(reporterName[1]);
            request.setReporterEmail(emailExtract.getEmail().get(0));
            request.setMedia("Email");
            request.setIssueCategory(getIssueCategory(emailExtract.getBody()));
            request.setComment(emailExtract.getBody());
            BaseResponse response = rest.postJson(url, request, auth);
            if (response != null && response.getResponseCode() == 200)
                log.info("Ticket Created...................");
        } catch (Exception e) {
            log.error("Error", e);
        }
    }
    
    private String getIssueCategory(String mailBody) {
    	if (mailBody == null || mailBody.isBlank())
    		return null;
    	
    	mailBody = mailBody.toLowerCase();
    	if (mailBody.contains("complain") || mailBody.contains("not work") || mailBody.contains("issue") || mailBody.contains("fail") || mailBody.contains("unable to"))
    		return "Complaint";
    	
    	else if (mailBody.contains("enquire") || mailBody.contains("find out") || mailBody.contains("to know"))
    		return "Enquiry";

    	else if (mailBody.contains("suggest") || mailBody.contains("recommend"))
    		return "Suggestion";
    	
    	else 
    		return null;
    }
}
