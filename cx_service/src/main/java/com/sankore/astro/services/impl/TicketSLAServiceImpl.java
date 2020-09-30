package com.sankore.astro.services.impl;

import com.sankore.astro.entity.EscalationLevel;
import com.sankore.astro.entity.IssueType;
import com.sankore.astro.entity.TicketSLA;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.enums.SLATimeUnit;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.pojo.Sla;
import com.sankore.astro.repository.*;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.sla.SlaRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.sla.SlaListResponse;
import com.sankore.astro.response.sla.SlaResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.IssueCategoryService;
import com.sankore.astro.services.TicketSLAService;
import com.sankore.astro.services.UserService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Obi on 07/06/2019
 */
@Service
public class TicketSLAServiceImpl implements TicketSLAService, BaseEntityService {

    @Autowired
    AstroLoggerService log;

    @Autowired
    UserService userService;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    I18nMessagingService messagingService;

    @Autowired
    TicketSLARepository ticketSLARepository;

    @Autowired
    IssueTypeRepository issueTypeRepository;

    @Autowired
    IssueCategoryService issueCategoryService;

    @Autowired
    IssueCategoryRepository issueCategoryRepository;

    @Autowired
    EscalationLevelRepository escalationLevelRepository;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchTicketSLAs.name().equals(handlerMethod))
            fetchTicketSLAs((SlaRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchTicketSLADetail.name().equals(handlerMethod))
            fetchTicketSLADetail((SlaRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.saveSLA.name().equals(handlerMethod))
            saveSLA((SlaRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.deleteSLA.name().equals(handlerMethod))
            deleteSLA((SlaRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Not_Found)));
    }

    @Override
    public void fetchTicketSLAs(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        SlaListResponse response = new SlaListResponse(ResponseCode.Success);
        try {
            List<TicketSLA> ticketSLAs = ticketSLARepository.findAllByClient_Code(request.getClientCode());
            response.setSlas(toSLAs(ticketSLAs));
        } catch (Exception e) {
            log.error("Error", e);
            response = new SlaListResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchTicketSLADetail(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        SlaResponse response = new SlaResponse(ResponseCode.Success);
        try {
            if (request.getSla().getId() != 0) {
                TicketSLA ticketSLA = ticketSLARepository.findTicketSLAById(request.getSla().getId());
                if (ticketSLA == null) {
                    response = new SlaResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("sla_not_found"));
                    handler.handle(Future.succeededFuture(response));
                    return;
                }
                response.setSla(toSLA(ticketSLA));
            }
            response.setIssueTypes(issueCategoryService.toIssueTypes(issueTypeRepository.findAllByActiveTrue()));
            response.setCategories(issueCategoryService.toCategories(issueCategoryRepository.loadAllCategories(), true));
            response.setTimeUnits(Arrays.asList(SLATimeUnit.values()).stream().map(x -> x.name()).collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Error", e);
            response = new SlaResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void saveSLA(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        long issueTypeId = request.getSla().getIssueTypeId();
        boolean isCreate = request.getSla().getId() == 0;
        IssueType issueType = issueTypeRepository.findIssueTypeById(issueTypeId);
        if (issueType == null) {
            BaseResponse response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("sla_issueType_not_found"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        TicketSLA ticketSLA = isCreate ? new TicketSLA() : ticketSLARepository.findTicketSLAById(request.getSla().getId());
        boolean slaExists = ticketSLARepository.existsByClient_CodeAndIssueType_Id(request.getClientCode(), issueTypeId);
        if ((isCreate && slaExists) || (!isCreate && slaExists && issueTypeId != ticketSLA.getIssueType().getId())) {
            BaseResponse response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("sla_issue_type_exists"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        if (isCreate)
            ticketSLA.setClient(clientRepository.findByCode(request.getClientCode()));
        ticketSLA.setIssueType(issueType);
        ticketSLARepository.save(ticketSLA);

        saveEscalationLevels(request, ticketSLA);
        BaseResponse response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("ticket-sla.set-sla.success"));
        handler.handle(Future.succeededFuture(response));
    }

    private void saveEscalationLevels(SlaRequest request, TicketSLA ticketSLA) {
        if (!CollectionUtils.isEmpty(request.getSla().getEscalationLevels())) {
            List<EscalationLevel> escalationLevels = new ArrayList<>();
            for (com.sankore.astro.pojo.EscalationLevel esc : request.getSla().getEscalationLevels()) {
                EscalationLevel escalationLevel = escalationLevelRepository.findByLevelAndTicketSLA_Id(esc.getLevel(), esc.getSlaId());
                if (escalationLevel == null)
                    escalationLevel = new EscalationLevel();
                escalationLevel.setTicketSLA(ticketSLA);
                escalationLevel.setLevel(esc.getLevel());
                escalationLevel.setPeriod(esc.getPeriod());
                escalationLevel.setTimeUnit(SLATimeUnit.valueOf(esc.getTimeUnit()));
                escalationLevel.setAdmins(userService.getUsersByUserDetails(esc.getAdmins()));
                escalationLevels.add(escalationLevel);
            }
            escalationLevelRepository.saveAll(escalationLevels);
        }
    }

    @Override
    public void deleteSLA(SlaRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response;
        try {
            TicketSLA ticketSLA = ticketSLARepository.findTicketSLAById(request.getSla().getId());
            if (ticketSLA == null) {
                response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("sla_not_found"));
                handler.handle(Future.succeededFuture(response));
                return;
            }
            ticketSLARepository.delete(ticketSLA);
            response = new BaseResponse(ResponseCode.Success);
        } catch (Exception ex) {
            log.error("Error", ex);
            response = new BaseResponse(ResponseCode.Internal_Server_Error.getCode(), messagingService.getMessage("sla_delete_error"));
        }
        handler.handle(Future.succeededFuture(response));
    }

    public List<Sla> toSLAs(List<TicketSLA> ticketSLAs) {
        List<Sla> slaList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ticketSLAs)) {
            for (TicketSLA ticketSLA : ticketSLAs) {
                Sla sla = toSLA(ticketSLA);
                if (sla != null)
                    slaList.add(sla);
            }
        }
        return slaList;
    }

    public Sla toSLA(TicketSLA ticketSLA) {
        if (ticketSLA == null)
            return null;

        Sla sla = new Sla();
        sla.setId(ticketSLA.getId());
        sla.setClientId(ticketSLA.getClient().getId());
        sla.setIssueTypeId(ticketSLA.getIssueType().getId());
        sla.setIssueTypeName(ticketSLA.getIssueType().getName());
        sla.setCategoryId(ticketSLA.getIssueType().getIssueCategory().getId());
        sla.setCategoryName(ticketSLA.getIssueType().getIssueCategory().getName());

        for (EscalationLevel esc : ticketSLA.getEscalationLevels()) {
            com.sankore.astro.pojo.EscalationLevel escalationLevel = new com.sankore.astro.pojo.EscalationLevel();
            escalationLevel.setLevel(esc.getLevel());
            escalationLevel.setPeriod(esc.getPeriod());
            escalationLevel.setSlaId(esc.getTicketSLA().getId());
            escalationLevel.setTimeUnit(esc.getTimeUnit().name());
            escalationLevel.setAdmins(userService.fromUsers(esc.getAdmins()));
            sla.getEscalationLevels().add(escalationLevel);
        }
        return sla;
    }
}
