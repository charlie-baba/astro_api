package com.sankore.astro.services;

import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author Obi on 10/05/2019
 */
public interface BaseEntityService {

    void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler);
}
