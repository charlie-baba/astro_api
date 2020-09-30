package com.sankore.astro.services.impl;

import com.sankore.astro.entity.Client;
import com.sankore.astro.entity.Permission;
import com.sankore.astro.entity.User;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.enums.RoleType;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.generic.util.PasswordGenerator;
import com.sankore.astro.pojo.ActiveUserStore;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.pojo.Role;
import com.sankore.astro.pojo.UserDetail;
import com.sankore.astro.repository.*;
import com.sankore.astro.request.BaseRequest;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.user.EmailRequest;
import com.sankore.astro.request.user.UserRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.ExistsResponse;
import com.sankore.astro.response.user.UserListResponse;
import com.sankore.astro.response.user.UserResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.NotificationService;
import com.sankore.astro.services.PermissionService;
import com.sankore.astro.services.UserService;
import com.sankore.ligare.base.Param;
import com.sankore.ligare.enums.email.EmailType;
import com.sankore.ligare.messaging.email.SendMail;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.*;
import java.util.stream.Collectors;

import static com.sankore.astro.generic.Constants.RequestKey.APP_NAME;

/**
 * @author Obi on 13/05/2019
 */
@Service
public class UserServiceImpl implements UserService, BaseEntityService {

    @Autowired
    AstroLoggerService log;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    PermissionService permissionService;

    @Autowired
    I18nMessagingService messagingService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    PermissionRepository permissionRepository;

    @Value("${astrocx_web_end_point}")
    String url;

    @Value("${cx_api_auth}")
    String auth;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.findById.name().equals(handlerMethod))
            findById((BaseRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.findByEmail.name().equals(handlerMethod))
            findByEmail((EmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.emailExists.name().equals(handlerMethod))
            emailExists((EmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchLoginUserDetails.name().equals(handlerMethod))
            fetchLoginUserDetails((EmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.loadClientAgents.name().equals(handlerMethod))
            loadClientAgents((EmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.updateProfile.name().equals(handlerMethod))
            updateProfile((UserRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.saveUser.name().equals(handlerMethod))
            saveUser((UserRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.resetPassword.name().equals(handlerMethod))
	        resetPassword((EmailRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.changePassword.name().equals(handlerMethod))
	        changePassword((UserRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.deleteUser.name().equals(handlerMethod))
            deleteUser((UserRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
    }

    @Override
    public void findById(BaseRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        UserResponse response;
        User user = userRepository.findByIdAndClientCode(request.getUserId(), request.getClientCode());
        if (user == null)
            response = new UserResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("user_not_found"));
        else {
            response = new UserResponse(ResponseCode.Success);
            response.setUserDetail(fromUser(user, true));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void emailExists(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        ExistsResponse response;
        try {
            response = new ExistsResponse(ResponseCode.Success);
            response.setExists(userRepository.existsByEmailAddress(request.getEmail()));
        } catch (Exception e) {
            log.error("Error", e);
            response = new ExistsResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void findByEmail(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        UserResponse response;
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null)
            response = new UserResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("email_not_found"));
        else {
            response = new UserResponse(ResponseCode.Success);
            response.setUserDetail(fromUser(user, true));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchLoginUserDetails(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        UserResponse response;
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null)
            response = new UserResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("email_not_found"));
        else {
            response = new UserResponse(ResponseCode.Success);
            response.setUserDetail(fromUser(user, true));
            response.setMenus(getUserMenus(user));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public List<String> fetchActiveUsers() {
        ActiveUserStore userStore = null;
        try {
            userStore = ClientBuilder.newClient()
                    .target(url)
                    .path("fetch_active_users")
                    .request(MediaType.APPLICATION_JSON)
                    .header("Authorization", auth)
                    .header("Content-Type", MediaType.APPLICATION_JSON)
                    .get(ActiveUserStore.class);
        } catch (Exception e) {
            log.error("Error", e);
        }
        return userStore == null ? null : userStore.getUsers();
    }

    @Override
    public void loadClientAgents(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        List<User> users = userRepository.findAllByClient_Code(request.getClientCode());
        List<UserDetail> userDetails = new ArrayList<>();
        if (!CollectionUtils.isEmpty(users))
            users.forEach(u -> userDetails.add(fromUser(u, false)));

        UserListResponse response = new UserListResponse(ResponseCode.Success);
        response.setUserDetails(userDetails);
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void updateProfile(UserRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        UserResponse response;
        User user = userRepository.findByIdAndClientCode(request.getUserId(), request.getClientCode());
        if (user == null)
            response = new UserResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("user_not_found"));
        else {
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            //if (user.isSuperAdmin())
            user.setEmailAddress(request.getEmail());
            if (!StringUtils.isEmpty(request.getPassword()) && !request.getPassword().isBlank())
                user.setPassword(request.getPassword());
            userRepository.save(user);

            response = new UserResponse(ResponseCode.Success);
            response.setUserDetail(fromUser(user, true));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Transactional
    @Override
    public void saveUser(UserRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response = new BaseResponse(ResponseCode.Internal_Server_Error);
        boolean isCreate = request.getId() == null || request.getId().equals(0L);
        User user = isCreate ? new User() : userRepository.findUserAndPermissionsById(request.getId());
        Client client = clientRepository.findClientById(request.getClientId());

        boolean emailExists = userRepository.existsByEmailAddressIgnoreCaseAndClient_Code(request.getEmail(), client.getCode());
        if ((isCreate && emailExists) || (!isCreate && emailExists && !request.getEmail().equalsIgnoreCase(user.getEmailAddress()))) {
            response = new BaseResponse(ResponseCode.Info.getCode(),
                    messagingService.getMessage("user.email-exists", new String[] {request.getEmail()}));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        try {
            User createdBy = userRepository.findUserById(request.getUserId());
            user.setSsoId(request.getSsoId());
            user.setGliaUserId(request.getGliaUserId());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setEmailAddress(request.getEmail());
            user.setCreatedBy(createdBy);
            user.setActive(request.isActive());
            String password = request.getPassword();
            if (isCreate) {
                user.setClient(client);
                user.setResetPassword(true);
                user.setPassword(passwordEncoder.encode(password));
            }

            boolean isAdmin = user.getUserRoles().stream().anyMatch(x -> x.getName().equals(RoleType.ADMIN.getScreenName()));
            user.getUserRoles().clear();
            List<com.sankore.astro.entity.Role> roles = roleRepository.findRolesByIdIn(request.getRoleIds());
            if (!CollectionUtils.isEmpty(roles)) {
                user.setUserRoles(new HashSet<>(roles));
            }
            if (createdBy.isSuperAdmin() || isAdmin)
                user.addRole(roleRepository.findRoleByName(RoleType.ADMIN.getScreenName()));
            Set<Permission> oldPermissions = user.getCustomPermissions();
            user.setCustomPermissions(permissionService.createPermissions(request.getMenus(), client));
            userRepository.save(user);
            permissionRepository.deleteAll(oldPermissions);
            response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("user_save_successful"));

            if (isCreate) {
	            if (createdBy.isSuperAdmin())
	            	sendNewAdminEmail(request.getEmail(), request.getFirstName(), password, client.getName());
	            else
            	    sendNewUserEmail(request.getEmail(), request.getFirstName(), password);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        handler.handle(Future.succeededFuture(response));
    }
    
    @Override
    public void resetPassword(EmailRequest request, Handler<AsyncResult<BaseResponse>> handler){
	    BaseResponse response;
	    try {
		    User user = userRepository.findUserByEmailAddressIgnoreCase(request.getEmail().trim());
		    if (user == null) {
			    response = new BaseResponse(ResponseCode.Not_Found.getCode(), "Your email did not return any result");
		    } else {
			    String newPassword = PasswordGenerator.generate();
			    user.setPassword(passwordEncoder.encode(newPassword));
			    user.setResetPassword(true);
			    userRepository.save(user);
			    sendPasswordResetEmail(user, newPassword);
			    response = new BaseResponse(ResponseCode.Success);
		    }
	    } catch (Exception e) {
			log.error("", e);
		    response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("password_reset_failed"));
		}
	    handler.handle(Future.succeededFuture(response));
    }
	
	@Override
	public void changePassword(UserRequest request, Handler<AsyncResult<BaseResponse>> handler){
		UserResponse response;
		try {
			User user = userRepository.findUserAndClientById(request.getId());
			if (user == null) {
				response = new UserResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("user_not_found"));
			} else {
				user.setPassword(request.getPassword());
				user.setResetPassword(false);
				userRepository.save(user);
				response = new UserResponse(ResponseCode.Success);
				response.setUserDetail(fromUser(user, true));
			}
		} catch (Exception e) {
			log.error("Error", e);
			response = new UserResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("password_change_failed"));
		}
		handler.handle(Future.succeededFuture(response));
	}

    @Override
    public void deleteUser(UserRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        UserResponse response;
        try {
            User user = userRepository.findUserById(request.getId());
            if (user == null) {
                response = new UserResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("user_not_found"));
            } else {
                response = new UserResponse(ResponseCode.Success.getCode(), messagingService.getMessage("user_delete_successful"));
                response.setUserDetail(fromUser(user, false));
                userRepository.delete(user);
            }
        } catch (DataIntegrityViolationException dIVEx) {
            log.error("Constraint Violation Exception", dIVEx);
            response = new UserResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("user_attached_to_entities"));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public Set<User> getUsersByUserDetails(Set<UserDetail> userDetails) {
        if (CollectionUtils.isEmpty(userDetails))
            return new HashSet<>();

        List<Long> userIds = userDetails.stream().map(x -> x.getId()).collect(Collectors.toList());
        List<User> users = userRepository.findUsersByIdIn(userIds);
        return new HashSet<>(users);
    }

    @Override
    public UserDetail fromUser(User user, boolean loadClient) {
        UserDetail details = new UserDetail();
        details.setId(user.getId());
        details.setSsoId(user.getSsoId());
        details.setGliaUserId(user.getGliaUserId());
        details.setEmail(user.getEmailAddress());
        details.setPassword(user.getPassword());
        details.setAvatar(user.getAvatar());
        details.setActive(user.isActive());
        details.setFirstName(user.getFirstName());
        details.setLastName(user.getLastName());
        details.setMiddleName(user.getMiddleName());
        details.setPhoneNumber(user.getPhoneNumber());
        details.setSuperAdmin(user.isSuperAdmin());
        details.setAdmin(user.isAdmin());
        details.setResetPassword(user.isResetPassword());
        if (loadClient && user.getClient() != null) {
            details.setClientId(user.getClient().getId());
            details.setClientCode(user.getClient().getCode());
            details.setClientName(user.getClient().getName());
            details.setClientLogo(user.getClient().getLogo());
        }
        details.setRoles(getUserRoles(user));
        return details;
    }

    @Override
    public Set<UserDetail> fromUsers(Set<User> users) {
        if (CollectionUtils.isEmpty(users))
            return new HashSet<>();

        Set<UserDetail> userDetails = new HashSet<>();
        for (User user : users){
            UserDetail details = new UserDetail();
            details.setId(user.getId());
            details.setFirstName(user.getFirstName());
            details.setLastName(user.getLastName());
            userDetails.add(details);
        }
        return userDetails;
    }

    private Set<Role> getUserRoles(User user) {
        Set<Role> roles = new HashSet<>();
        if (!CollectionUtils.isEmpty(user.getUserRoles())) {
            user.getUserRoles().forEach(r -> {
                Role role = new Role();
                role.setId(r.getId());
                role.setName(r.getName());
                role.setSystem(r.isSystem());
                if (r.getClient() != null)
                    role.setClientId(r.getClient().getId());
                roles.add(role);
            });
        }
        return roles;
    }

    private List<Menu> getUserMenus(User user) {
        Set<Menu> menus = new HashSet<>();
        if (!CollectionUtils.isEmpty(user.getUserRoles())) {
            List<Long> roleIds = user.getUserRoles().stream().map(r -> r.getId()).collect(Collectors.toList());
            menuRepository.findByRoleIds(roleIds).forEach(m -> {
                Menu menu = new Menu();
                menu.setId(m.getId());
                menu.setUrl(m.getUrl());
                menu.setName(m.getName());
                menu.setPosition(m.getPosition());
                menus.add(menu);
            });
        }

        List<Permission> permissions = permissionRepository.findAllByUser_Id(user.getId());
        permissions.forEach(p -> {
            Menu menu = new Menu();
            menu.setUrl(p.getMenu().getUrl());
            menu.setName(p.getMenu().getName());
            menu.setPosition(p.getMenu().getPosition());
            menus.add(menu);
        });
        return menus.stream().sorted(Comparator.comparing(Menu::getPosition)).collect(Collectors.toList());
    }

    public void sendNewUserEmail(String emailAddress, String firstName, String password) {
        try {
            SendMail sendMail = new SendMail();
            sendMail.setSenderApp(APP_NAME);
            sendMail.setMailFor("New_User_Notification");
            sendMail.setSender(notificationService.getNoReplySender());
            sendMail.setMailType(EmailType.HTML.name());
            sendMail.setRecipients(Arrays.asList(emailAddress));
            sendMail.setSubject("Welcome to AstroCX");
            sendMail.setTemplate("New_User_Notification.html");
            sendMail.setContentClass(SendMail.class.getSimpleName());
    
            List<Param> paramList = new ArrayList<>();
            paramList.add(new Param("firstName", firstName));
            paramList.add(new Param("username", emailAddress));
            paramList.add(new Param("password", password));
            paramList.add(new Param("endpoint", notificationService.getMessageEndpoint()));
            sendMail.setParams(paramList);
            notificationService.sendMailToQueue(sendMail);
        } catch(Exception err) {
            log.error("Error", err);
        }
    }
	
	public void sendNewAdminEmail(String emailAddress, String firstName, String password, String client) {
		try {
			SendMail sendMail = new SendMail();
			sendMail.setSenderApp(APP_NAME);
			sendMail.setMailFor("Welcome_to_Astro_CX");
			sendMail.setTemplate("Welcome_to_Astro_CX.html");
			sendMail.setContentClass(SendMail.class.getSimpleName());
			sendMail.setSender(notificationService.getNoReplySender());
			sendMail.setMailType(EmailType.HTML.name());
			sendMail.setRecipients(Arrays.asList(emailAddress));
			sendMail.setSubject("Welcome to AstroCX");
			
			List<Param> paramList = new ArrayList<>();
			paramList.add(new Param("endpoint", notificationService.getMessageEndpoint()));
			paramList.add(new Param("firstName", firstName));
			paramList.add(new Param("client", client));
			paramList.add(new Param("username", emailAddress));
			paramList.add(new Param("password", password));
			sendMail.setParams(paramList);
			notificationService.sendMailToQueue(sendMail);
		} catch(Exception err) {
			log.error("Error", err);
		}
	}
	
	public void sendPasswordResetEmail(User user, String newPassword) {
		try {
			SendMail sendMail = new SendMail();
			sendMail.setSenderApp(APP_NAME);
			sendMail.setMailFor("Password_Reset");
			sendMail.setTemplate("Password_Reset.html");
			sendMail.setContentClass(SendMail.class.getSimpleName());
			sendMail.setSender(notificationService.getNoReplySender());
			sendMail.setMailType(EmailType.HTML.name());
			sendMail.setRecipients(Arrays.asList(user.getEmailAddress()));
			sendMail.setSubject("AstroCX account password reset");
			
			List<Param> paramList = new ArrayList<>();
			paramList.add(new Param("endpoint", notificationService.getMessageEndpoint()));
			paramList.add(new Param("firstName", user.getFirstName()));
			paramList.add(new Param("password", newPassword));
			sendMail.setParams(paramList);
			notificationService.sendMailToQueue(sendMail);
		} catch(Exception err) {
			log.error("Error", err);
		}
	}

}
