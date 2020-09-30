package com.sankore.astro.services;

import com.sankore.astro.entity.UserGroupMapping;
import com.sankore.astro.pojo.UserDetail;
import com.sankore.astro.request.group.GroupRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

/**
 * @author Obi on 28/05/2019
 */
public interface GroupService {

	void findById(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler);
	
	void saveGroup(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchGroups(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void deleteGroup(GroupRequest requestPojo, Handler<AsyncResult<BaseResponse>> handler);

	void fetchGroupMembers(GroupRequest request, Handler<AsyncResult<BaseResponse>> handler);

	UserDetail fromUserGroup(UserGroupMapping userGroup);
}
