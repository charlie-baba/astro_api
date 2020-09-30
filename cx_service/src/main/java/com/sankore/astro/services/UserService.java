package com.sankore.astro.services;

import com.sankore.astro.entity.User;
import com.sankore.astro.pojo.UserDetail;
import com.sankore.astro.request.BaseRequest;
import com.sankore.astro.request.user.EmailRequest;
import com.sankore.astro.request.user.UserRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;
import java.util.Set;

/**
 * @author Obi on 15/05/2019
 */
public interface UserService {

    void findById(BaseRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void emailExists(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void findByEmail(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchLoginUserDetails(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler);

    List<String> fetchActiveUsers();

    void loadClientAgents(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void updateProfile(UserRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void saveUser(UserRequest request, Handler<AsyncResult<BaseResponse>> handler);
    
    void resetPassword(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler);
    
    void changePassword(UserRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void deleteUser(UserRequest request, Handler<AsyncResult<BaseResponse>> handler);

    Set<User> getUsersByUserDetails(Set<UserDetail> userDetails);

    UserDetail fromUser(User user, boolean loadClient);

    Set<UserDetail> fromUsers(Set<User> user);
}
