package com.sankore.astro.services.impl;

import com.sankore.astro.entity.Client;
import com.sankore.astro.entity.User;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.enums.RoleType;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.pojo.ClientDetail;
import com.sankore.astro.pojo.UserDetail;
import com.sankore.astro.repository.ClientRepository;
import com.sankore.astro.repository.UserRepository;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.client.ClientRequest;
import com.sankore.astro.request.client.FetchClientRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.client.ClientListResponse;
import com.sankore.astro.response.client.ClientResponse;
import com.sankore.astro.response.user.UserListResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.ClientService;
import com.sankore.astro.services.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Obi on 23/05/2019
 */
@Service
public class ClientServiceImpl implements ClientService, BaseEntityService {

    @Autowired
    AstroLoggerService log;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    I18nMessagingService messagingService;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchValidClients.name().equals(handlerMethod))
            fetchValidClients((FetchClientRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.loadClientDetails.name().equals(handlerMethod))
            loadClientDetails((FetchClientRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchClientByCode.name().equals(handlerMethod))
            fetchClientByCode((FetchClientRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.saveClient.name().equals(handlerMethod))
            saveClient((ClientRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchClientAdmins.name().equals(handlerMethod))
            fetchClientAdmins((FetchClientRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.deleteClient.name().equals(handlerMethod))
            deleteClient((ClientRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
    }

    @Override
    public void fetchValidClients(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        List<Client> clients = clientRepository.findValidClients();
        ClientListResponse response = new ClientListResponse(ResponseCode.Success);

        List<ClientDetail> clientDetails = new ArrayList<>();
        if (!CollectionUtils.isEmpty(clients)) {
            for (Client client : clients) {
                clientDetails.add(toClientDetail(client));
            }
        }
        response.setClientDetails(clientDetails);
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchClientByCode(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        ClientResponse response = new ClientResponse(ResponseCode.Success);
        Client client = clientRepository.findByCode(request.getClientCode());
        response.setClientDetail(toClientDetail(client));
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void loadClientDetails(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        ClientResponse response = new ClientResponse(ResponseCode.Success);
        User user = userRepository.findUserById(request.getUserId());
        if (user != null && user.isSuperAdmin()) {
            Client client = clientRepository.findClientById(request.getClientId());
            response.setClientDetail(toClientDetail(client));
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void saveClient(ClientRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        User user = userRepository.findUserById(request.getUserId());
        if (user == null || !user.isSuperAdmin()) {
            BaseResponse response = new BaseResponse(ResponseCode.Unauthorized);
            handler.handle(Future.succeededFuture(response));
            return;
        }

        long clientId = request.getClientDetail().getId();
        boolean isCreate = clientId == 0;
        Client client = isCreate ? new Client() : clientRepository.findClientById(clientId);
        if (client == null) {
            BaseResponse response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("client.not-found"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        String clientName = request.getClientDetail().getName().trim();
        String shortName = request.getClientDetail().getShortName().trim();
        String email = request.getClientDetail().getEmail().trim().toLowerCase();
        String code = request.getClientDetail().getCode().trim();
        String errorMessage = "";
        if (clientRepository.existsByNameIgnoreCase(clientName) && !clientName.equalsIgnoreCase(client.getName()))
            errorMessage = messagingService.getMessage("client_name_exist");
        else if (clientRepository.existsByShortNameIgnoreCase(shortName) && !shortName.equalsIgnoreCase(client.getShortName()))
            errorMessage = messagingService.getMessage("client_shortName_exist");
        else if (clientRepository.existsByEmail(email) && !email.equalsIgnoreCase(client.getEmail()))
            errorMessage = messagingService.getMessage("client.email-exists");
        else if (clientRepository.existsByCode(code) && !code.equalsIgnoreCase(client.getCode()))
            errorMessage = messagingService.getMessage("client_code_exist");

        if (!errorMessage.isBlank()) {
            BaseResponse response = new BaseResponse(ResponseCode.Bad_Request.getCode(), errorMessage);
            handler.handle(Future.succeededFuture(response));
            return;
        }

        client.setCode(code);
        client.setEmail(email);
        client.setName(clientName);
        client.setShortName(shortName);
        client.setTemplate(request.getClientDetail().getTemplate());
        if (!request.getClientDetail().getLogo().isBlank())
            client.setLogo(request.getClientDetail().getLogo());
        clientRepository.save(client);
        BaseResponse response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("client_save_successful"));
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchClientAdmins(FetchClientRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        List<User> admins = userRepository.findUserByClientIdAndRole(request.getClientId(), RoleType.ADMIN.getScreenName());
        List<UserDetail> userDetails = new ArrayList<>();
        if (!CollectionUtils.isEmpty(admins))
            admins.forEach(u -> userDetails.add(userService.fromUser(u, false)));

        UserListResponse response = new UserListResponse(ResponseCode.Success);
        response.setUserDetails(userDetails);
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void deleteClient(ClientRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response;
        try {
            Client client = clientRepository.findClientById(request.getClientDetail().getId());
            if (client == null) {
                response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("client.not-found"));
            } else {
                clientRepository.delete(client);
                response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("client_delete_successful"));
            }
        } catch (DataIntegrityViolationException dIVEx) {
            log.error("Constraint Violation Exception", dIVEx);
            response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("client_attached_to_entities"));
        }
        handler.handle(Future.succeededFuture(response));
    }

    private ClientDetail toClientDetail(Client client) {
        if (client == null)
            return null;

        ClientDetail detail = new ClientDetail();
        detail.setActive(client.isActive());
        detail.setName(client.getName());
        detail.setCode(client.getCode());
        detail.setId(client.getId());
        detail.setLogo(client.getLogo());
        detail.setShortName(client.getShortName());
        detail.setEmail(client.getEmail());
        detail.setTemplate(client.getTemplate());
        return detail;
    }
}
