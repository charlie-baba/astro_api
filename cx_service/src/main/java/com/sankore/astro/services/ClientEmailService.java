package com.sankore.astro.services;

import com.sankore.astro.entity.ClientEmail;
import com.sankore.astro.pojo.EmailDetail;
import com.sankore.astro.request.email.ClientEmailRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 * @author Obi on 30/05/2019
 */
public interface ClientEmailService {

    void fetchClientEmails(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler);

    List<EmailDetail> toEmailDetails(List<ClientEmail> clientEmails);

    EmailDetail toEmailDetail(ClientEmail clientEmail);

    void fetchClientEmail(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void saveClientEmail(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void deleteClientEmail(ClientEmailRequest request, Handler<AsyncResult<BaseResponse>> handler);
}
