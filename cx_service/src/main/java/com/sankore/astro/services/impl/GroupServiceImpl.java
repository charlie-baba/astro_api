package com.sankore.astro.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import com.sankore.astro.services.IssueCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.sankore.astro.entity.Permission;
import com.sankore.astro.entity.User;
import com.sankore.astro.entity.UserGroupMapping;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.pojo.Group;
import com.sankore.astro.pojo.UserDetail;
import com.sankore.astro.repository.ClientRepository;
import com.sankore.astro.repository.GroupRepository;
import com.sankore.astro.repository.UserGroupRepository;
import com.sankore.astro.repository.UserRepository;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.group.GroupRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.group.GroupListResponse;
import com.sankore.astro.response.group.GroupResponse;
import com.sankore.astro.response.user.UserListResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.GroupService;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

/**
 * @author Obi on 28/05/2019
 */
@Service
public class GroupServiceImpl implements GroupService, BaseEntityService {

    @Autowired
    AstroLoggerService log;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    ClientRepository clientRepository;
    
    @Autowired
    GroupRepository groupRepository;

    @Autowired
    I18nMessagingService messagingService;
    
    @Autowired
    UserGroupRepository userGroupRepository;

    @Autowired
    IssueCategoryService categoryService;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.saveGroup.name().equals(handlerMethod))
            saveGroup((GroupRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchGroups.name().equals(handlerMethod))
        	fetchGroups((GroupRequest)requestPojo, asyncResultHandler);
        else if (RequestMethod.findById.name().equals(handlerMethod))
        	findById((GroupRequest)requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchGroupMembers.name().equals(handlerMethod))
        	fetchGroupMembers((GroupRequest)requestPojo, asyncResultHandler);
        else if (RequestMethod.deleteGroup.name().equals(handlerMethod))
            deleteGroup((GroupRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
    }

	@Override
    public void findById(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        GroupResponse response;
        com.sankore.astro.entity.Group group = groupRepository.findByIdAndClientCode(request.getGroup().getId(), request.getClientCode());
        if (group == null)
            response = new GroupResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("group_not_found"));
        else {
            response = new GroupResponse(ResponseCode.Success);
            response.setGroup(toGroup(group));
        }
        handler.handle(Future.succeededFuture(response));
    }                  
    
	@Transactional
    @Override
    public void saveGroup(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler) {
    	BaseResponse response;
        try {
            long groupId = request.getGroup().getId();
            Group group = request.getGroup();
            boolean isCreate = groupId == 0;
            com.sankore.astro.entity.Group currentGroup = isCreate ? new com.sankore.astro.entity.Group() : groupRepository.findGroupAndUserMappingsById(groupId);
            if (currentGroup == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("group_not_found"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            com.sankore.astro.entity.Group existingGroup = groupRepository.findGroupByNameIgnoreCaseAndClient_Code(group.getName(), request.getClientCode());
            boolean groupExists = existingGroup != null;
            if ((isCreate && groupExists) || (!isCreate && groupExists && groupId != existingGroup.getId())) {
                response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("group_name_exists"));
                handler.handle(Future.succeededFuture(response));
                return;
            }

            if (isCreate)
                currentGroup.setClient(clientRepository.findByCode(request.getClientCode()));
            currentGroup.setActive(group.isActive());
            currentGroup.setName(group.getName());
            currentGroup.setDescription(group.getDescription());
            currentGroup.setIssueCategories(categoryService.fromCategories(group.getCategories()));
            groupRepository.save(currentGroup);

            userGroupRepository.deleteAll(currentGroup.getUserGroupMapping());
            createMappings(request.getMembers(), currentGroup);
            response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("group.save.successful"));
        } catch (Exception e) {
            log.error("Error", e);
            response = new BaseResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

	private void createMappings(List<UserDetail> members, com.sankore.astro.entity.Group currentGroup) {
		if (CollectionUtils.isEmpty(members))
			return;
		
		List<UserGroupMapping> mappings = new ArrayList<UserGroupMapping>();
		for(UserDetail userDetail : members) {
			UserGroupMapping mapping = new UserGroupMapping();
        	mapping.setGroup(currentGroup);
        	mapping.setUser(userRepository.findUserById(userDetail.getId()));
        	mapping.setSupervisor(userDetail.isAdmin());
            mappings.add(mapping);
		}
		userGroupRepository.saveAll(mappings);
	}

	@Override
	public void fetchGroups(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler) {
		List<Object[]> groupsEntities = groupRepository.findAllByClientCode(request.getGroup().getClientCode());

        List<Group> groups = new ArrayList<>();
        if (!CollectionUtils.isEmpty(groupsEntities))
        	groupsEntities.forEach(g -> groups.add(toGroup(g)));

        GroupListResponse response = new GroupListResponse(ResponseCode.Success);
        response.setGroups(groups);
        handler.handle(Future.succeededFuture(response));
	}

	@Override
	public void fetchGroupMembers(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler) {
		List<UserGroupMapping> userGroups = groupRepository.findAllUsersByGroup(request.getGroup().getId());

        List<UserDetail> userDetails = new ArrayList<>();
        if (!CollectionUtils.isEmpty(userGroups))
        	userGroups.forEach(u -> userDetails.add(fromUserGroup(u)));

        UserListResponse response = new UserListResponse(ResponseCode.Success);
        response.setUserDetails(userDetails);
        handler.handle(Future.succeededFuture(response));
	}

	@Override
    public void deleteGroup(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler) {
		BaseResponse response;
        try {
            com.sankore.astro.entity.Group group = groupRepository.findGroupById(request.getGroup().getId());
            if (group == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("group_not_found"));
            } else {
                groupRepository.delete(group);
                response = new BaseResponse(ResponseCode.Success);
            }
        } catch (DataIntegrityViolationException dIVEx) {
            log.error("Constraint Violation Exception", dIVEx);
            response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("user_attached_to_group"));
        }
        handler.handle(Future.succeededFuture(response));		
	}

    public Group toGroup(com.sankore.astro.entity.Group group) {
        if (group == null)
            return null;

        if (group.getId() == null)
            return new Group();

        Group groupPojo = new Group();
        groupPojo.setId(group.getId());
        groupPojo.setName(group.getName());
        groupPojo.setDescription(group.getDescription());
        groupPojo.setActive(group.isActive());
        groupPojo.setCategories(categoryService.toCategories(group.getIssueCategories(), false));
        return groupPojo;
    }
    
    public Group toGroup(Object[] group) {
        if (group == null)
            return null;

        if (group[0] == null)
            return new Group();

        Group groupPojo = new Group();
        groupPojo.setId((Long)group[0]);
        groupPojo.setName((String)group[1]);
        groupPojo.setDescription((String)group[2]);
        groupPojo.setActive((boolean)group[3]);
        groupPojo.setUsersCount((long)group[4]);
        return groupPojo;
    }
    
    @Override
    public UserDetail fromUserGroup(UserGroupMapping userGroup) {
    	User user = userGroup.getUser();
        UserDetail details = new UserDetail();
        details.setId(user.getId());
        details.setEmail(user.getEmailAddress());
        details.setAvatar(user.getAvatar());
        details.setActive(user.isActive());
        details.setFirstName(user.getFirstName());
        details.setLastName(user.getLastName());
        details.setMiddleName(user.getMiddleName());
        details.setPhoneNumber(user.getPhoneNumber());
        details.setAdmin(userGroup.isSupervisor());
        return details;
    }

}
