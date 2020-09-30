package com.sankore.astro.services.impl;

import com.sankore.astro.entity.Client;
import com.sankore.astro.entity.Permission;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.enums.RoleType;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.pojo.Role;
import com.sankore.astro.repository.ClientRepository;
import com.sankore.astro.repository.MenuRepository;
import com.sankore.astro.repository.PermissionRepository;
import com.sankore.astro.repository.RoleRepository;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.role.RoleRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.role.RoleDetailResponse;
import com.sankore.astro.response.role.RoleListResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.MenuService;
import com.sankore.astro.services.PermissionService;
import com.sankore.astro.services.RoleService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Obi on 25/05/2019
 */
@Service
public class RoleServiceImpl implements RoleService, BaseEntityService {

    @Autowired
    AstroLoggerService log;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    MenuService menuService;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    PermissionService permissionService;

    @Autowired
    I18nMessagingService messagingService;

    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchRoles.name().equals(handlerMethod))
            fetchRoles((RoleRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.loadRoleDetails.name().equals(handlerMethod))
            loadRoleDetails((RoleRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.saveRole.name().equals(handlerMethod))
            saveRole((RoleRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.deleteRole.name().equals(handlerMethod))
            deleteRole((RoleRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
    }

    @Override
    public void fetchRoles(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        List<Object[]> roleObjs = roleRepository.findRolesAndUserCountByClientId(request.getRole().getClientId());

        List<Role> roles = new ArrayList<>();
        if (!CollectionUtils.isEmpty(roleObjs))
            roleObjs.forEach(r -> roles.add(toRole(r)));

        RoleListResponse response = new RoleListResponse(ResponseCode.Success);
        response.setRoles(roles);
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void loadRoleDetails(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        RoleDetailResponse response = new RoleDetailResponse(ResponseCode.Success);
        long roleId = request.getRole().getId();
        boolean isCreate = roleId == 0;
        com.sankore.astro.entity.Role role = isCreate ? new com.sankore.astro.entity.Role() : roleRepository.findRoleById(roleId);
        if (role == null) {
            response = new RoleDetailResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("role_not_found"));
        } else {
            response.setRoles(toRole(role));
            response.setMenus(menuService.toMenu(menuRepository.findSystemMenusByRoleName(RoleType.ADMIN.getScreenName())));
            if (!isCreate)
                response.setPermissions(permissionService.toPermission(permissionRepository.findAllByRole_Id(roleId)));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Transactional
    @Override
    public void saveRole(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response = new BaseResponse(ResponseCode.Internal_Server_Error);
        try {
            final String roleName = request.getRole().getName().trim();
            final boolean isCreate = request.getRole().getId() == 0;

            com.sankore.astro.entity.Role role = isCreate ? new com.sankore.astro.entity.Role() : roleRepository.findRoleById(request.getRole().getId());
            if (role == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("role_not_found"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            boolean nameExists = roleRepository.existsByNameIgnoreCaseAndClient_Code(roleName, request.getClientCode());
            if (Arrays.asList(RoleType.values()).stream().anyMatch(x -> x.getScreenName().equals(roleName)) ||
                    (isCreate && nameExists) || (!isCreate && nameExists && !role.getId().equals(request.getRole().getId()))) {
                response = new BaseResponse(ResponseCode.Bad_Request.getCode(), messagingService.getMessage("role_name_exists"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            Set<Permission> oldPermissions = role.getPermissions();
            Client client = clientRepository.findByCode(request.getClientCode());
            role.setClient(client);
            role.setActive(request.getRole().isActive());
            role.setName(roleName);
            role.setDescription(request.getRole().getDescription());
            role.setSystem(false);
            role.setMenus(new HashSet<>(menuRepository.findMenusByIdIn(request.getMenus().stream().map(Menu::getId).collect(Collectors.toList()))));
            role.setPermissions(permissionService.createPermissions(request.getMenus(), client));
            roleRepository.save(role);
            permissionRepository.deleteAll(oldPermissions);
            response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("role_save_successful"));
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    @Transactional
    public void deleteRole(RoleRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response;
        try {
            com.sankore.astro.entity.Role role = roleRepository.findRoleById(request.getRole().getId());
            if (role == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("role_not_found"));
            } else {
                roleRepository.delete(role);
                response = new BaseResponse(ResponseCode.Success);
            }
        } catch (DataIntegrityViolationException dIVEx) {
            log.error("Constraint Violation Exception", dIVEx);
            response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("user_attached_to_role"));
        }
        handler.handle(Future.succeededFuture(response));
    }

    public Role toRole(Object[] objArr) {
        Role role = new Role();
        role.setId(((BigInteger) objArr[0]).longValue());
        role.setName((String) objArr[1]);
        role.setDescription((String) objArr[2]);
        role.setActive((boolean) objArr[3]);
        role.setSystem((boolean) objArr[4]);
        role.setClientId(((BigInteger) objArr[5]).longValue());
        role.setNoOfUsers(((BigInteger) objArr[6]).longValue());
        return role;
    }

    public Role toRole(com.sankore.astro.entity.Role role) {
        if (role == null)
            return null;

        if (role.getId() == null)
            return new Role();

        Role rolePojo = new Role();
        rolePojo.setId(role.getId());
        rolePojo.setName(role.getName());
        rolePojo.setDescription(role.getDescription());
        rolePojo.setActive(role.isActive());
        rolePojo.setSystem(role.isSystem());
        return rolePojo;
    }
}
