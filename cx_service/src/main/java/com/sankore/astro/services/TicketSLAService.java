package com.sankore.astro.services;

import com.sankore.astro.request.sla.SlaRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author Obi on 07/06/2019
 */
public interface TicketSLAService {

    void fetchTicketSLAs(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchTicketSLADetail(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void saveSLA(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void deleteSLA(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler);
}
