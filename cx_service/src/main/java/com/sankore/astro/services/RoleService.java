package com.sankore.astro.services;

import com.sankore.astro.request.role.RoleRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author Obi on 25/05/2019
 */
public interface RoleService {

    void fetchRoles(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void loadRoleDetails(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void saveRole(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void deleteRole(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler);
}
