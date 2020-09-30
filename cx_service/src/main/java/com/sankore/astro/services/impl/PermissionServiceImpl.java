package com.sankore.astro.services.impl;

import com.sankore.astro.entity.Client;
import com.sankore.astro.entity.Menu;
import com.sankore.astro.entity.Permission;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.repository.MenuRepository;
import com.sankore.astro.repository.PermissionRepository;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.permission.PermissionRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.permission.PermissionListResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.MenuService;
import com.sankore.astro.services.PermissionService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Obi on 25/05/2019
 */
@Service
public class PermissionServiceImpl implements PermissionService, BaseEntityService {

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuService menuService;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchUserPermissions.name().equals(handlerMethod))
            fetchUserPermissions((PermissionRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchRolesPermissions.name().equals(handlerMethod))
            fetchRolesPermissions((PermissionRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
    }

    @Override
    public void fetchUserPermissions(PermissionRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        PermissionListResponse response;
        try {
            response = new PermissionListResponse(ResponseCode.Success);
            response.setPermissions(toPermission(permissionRepository.findAllByUser_Id(request.getUserId())));
        } catch (Exception e) {
            response = new PermissionListResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchRolesPermissions(PermissionRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        PermissionListResponse response = new PermissionListResponse(ResponseCode.Success);
        List<Long> roleIds = request.getRoles().stream().map(r -> r.getId()).collect(Collectors.toList());
        response.setPermissions(toPermission(permissionRepository.findAllByRole_IdIn(roleIds)));
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public Set<Permission> createPermissions(List<com.sankore.astro.pojo.Menu> menus, Client client) {
        if (CollectionUtils.isEmpty(menus))
            return null;

        Set<Permission> permissionSet = new HashSet<>();
        for (com.sankore.astro.pojo.Menu menuPojo : menus) {
            Menu menu = menuRepository.findMenuById(menuPojo.getId());
            Permission permission = new Permission();
            permission.setCommands(menuPojo.getCommands());
            permission.setMenu(menu);
            permissionSet.add(permission);
        }
        permissionRepository.saveAll(permissionSet);
        return permissionSet;
    }

    @Override
    public List<com.sankore.astro.pojo.Permission> toPermission(List<com.sankore.astro.entity.Permission> permissions) {
        List<com.sankore.astro.pojo.Permission> permissionList = new ArrayList<>();
        if (CollectionUtils.isEmpty(permissions))
            return permissionList;

        permissions.stream().forEach(p -> {
            com.sankore.astro.pojo.Permission permission = new com.sankore.astro.pojo.Permission();
            permission.setId(p.getId());
            permission.setCommands(p.getCommands());
            permission.setMenu(menuService.toMenu(p.getMenu()));
            if (p.getRole() != null)
                permission.setRoleId(p.getRole().getId());
            if (p.getUser() != null)
                permission.setUserId(p.getUser().getId());
            permissionList.add(permission);
        });
        return permissionList;
    }
}
