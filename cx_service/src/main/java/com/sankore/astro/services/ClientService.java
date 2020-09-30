package com.sankore.astro.services;

import com.sankore.astro.request.client.ClientRequest;
import com.sankore.astro.request.client.FetchClientRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author Obi on 23/05/2019
 */
public interface ClientService {

    void fetchValidClients(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchClientByCode(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void loadClientDetails(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void saveClient(ClientRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchClientAdmins(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void deleteClient(ClientRequest request, Handler<AsyncResult<BaseResponse>> handler);
}
