package com.sankore.astro.services;

import com.sankore.astro.entity.Client;
import com.sankore.astro.entity.Permission;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.request.permission.PermissionRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;
import java.util.Set;

/**
 * @author Obi on 25/05/2019
 */
public interface PermissionService {

    void fetchUserPermissions(PermissionRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchRolesPermissions(PermissionRequest request, Handler<AsyncResult<BaseResponse>> handler);

    Set<Permission> createPermissions(List<Menu> menus, Client client);

    List<com.sankore.astro.pojo.Permission> toPermission(List<Permission> permissions);

}
