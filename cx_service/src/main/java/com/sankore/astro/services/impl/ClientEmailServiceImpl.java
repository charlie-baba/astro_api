package com.sankore.astro.services.impl;

import com.sankore.astro.entity.ClientEmail;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.pojo.EmailDetail;
import com.sankore.astro.repository.ClientEmailRepository;
import com.sankore.astro.repository.ClientRepository;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.email.ClientEmailRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.clientEmail.ClientEmailListResponse;
import com.sankore.astro.response.clientEmail.ClientEmailResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.ClientEmailService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Obi on 30/05/2019
 */
@Service
public class ClientEmailServiceImpl implements ClientEmailService, BaseEntityService {

    @Autowired
    ClientEmailRepository clientEmailRepository;

    @Autowired
    I18nMessagingService messagingService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AstroLoggerService log;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchClientEmails.name().equals(handlerMethod))
            fetchClientEmails((ClientEmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchClientEmail.name().equals(handlerMethod))
            fetchClientEmail((ClientEmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.saveClientEmail.name().equals(handlerMethod))
            saveClientEmail((ClientEmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.deleteClientEmail.name().equals(handlerMethod))
            deleteClientEmail((ClientEmailRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
    }

    @Override
    public void fetchClientEmails(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        ClientEmailListResponse response;
        try {
            List<ClientEmail> emails = clientEmailRepository.findAllByClient_Code(request.getClientCode());
            response = new ClientEmailListResponse(ResponseCode.Success);
            response.setEmailDetails(toEmailDetails(emails));
        } catch (Exception e) {
            log.error("Error", e);
            response = new ClientEmailListResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchClientEmail(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        ClientEmailResponse response;
        try {
            ClientEmail email = clientEmailRepository.findClientEmailById(request.getEmailDetail().getId());
            response = new ClientEmailResponse(ResponseCode.Success);
            response.setEmailDetail(toEmailDetail(email));
        } catch (Exception e) {
            log.error("Error", e);
            response = new ClientEmailResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void saveClientEmail(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response;
        try {
            long emailId = request.getEmailDetail().getId();
            EmailDetail emailDetail = request.getEmailDetail();
            boolean isCreate = emailId == 0;
            ClientEmail clientEmail = isCreate ? new ClientEmail() : clientEmailRepository.findClientEmailById(emailId);
            if (clientEmail == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("client-email_not_found"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            ClientEmail existingEmail = clientEmailRepository.findClientEmailByEmailAddress(emailDetail.getEmail());
            boolean emailExists = existingEmail != null;
            if ((isCreate && emailExists) || (!isCreate && emailExists && emailId != existingEmail.getId())) {
                response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("client.email-exists"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            if (isCreate)
                clientEmail.setClient(clientRepository.findByCode(request.getClientCode()));
            clientEmail.setActive(emailDetail.isActive());
            clientEmail.setEmailAddress(emailDetail.getEmail());
            clientEmail.setPassword(emailDetail.getPassword());
            clientEmail.setServer(emailDetail.getServer());
            clientEmail.setProtocol(emailDetail.getProtocol());
            clientEmail.setPort(emailDetail.getPort());
            clientEmailRepository.save(clientEmail);
            response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("client-email.save.successful"));
        } catch (Exception e) {
            log.error("Error", e);
            response = new BaseResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void deleteClientEmail(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response;
        try {
            ClientEmail clientEmail = clientEmailRepository.findClientEmailById(request.getEmailDetail().getId());
            if (clientEmail == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("client-email_not_found"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            clientEmailRepository.delete(clientEmail);
            response = new BaseResponse(ResponseCode.Success);
        } catch (Exception e) {
            log.error("Error", e);
            response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("client-email_delete_error"));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public List<EmailDetail> toEmailDetails(List<ClientEmail> clientEmails) {
        List<EmailDetail> emailDetails = new ArrayList<>();
        if (CollectionUtils.isEmpty(clientEmails))
            return emailDetails;

        for (ClientEmail clientEmail : clientEmails) {
            emailDetails.add(toEmailDetail(clientEmail));
        }
        return emailDetails;
    }

    @Override
    public EmailDetail toEmailDetail(ClientEmail clientEmail) {
        if (clientEmail == null)
            return null;

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setId(clientEmail.getId());
        emailDetail.setClientCode(clientEmail.getClient().getCode());
        emailDetail.setClientId(clientEmail.getClient().getId());
        emailDetail.setEmail(clientEmail.getEmailAddress());
        emailDetail.setPassword(clientEmail.getPassword());
        emailDetail.setPort(clientEmail.getPort());
        emailDetail.setProtocol(clientEmail.getProtocol());
        emailDetail.setServer(clientEmail.getServer());
        emailDetail.setActive(clientEmail.isActive());
        return emailDetail;
    }
}
