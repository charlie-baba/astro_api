package com.sankore.astro.services;

import com.sankore.astro.request.BaseRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author Obi on 16/05/2019
 */
public interface DashboardService {

    void fetchDashboardData(BaseRequest request, Handler<AsyncResult<BaseResponse>> handler);
}
