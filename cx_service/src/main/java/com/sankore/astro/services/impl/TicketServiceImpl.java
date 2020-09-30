package com.sankore.astro.services.impl;

import com.sankore.astro.entity.*;
import com.sankore.astro.enums.*;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.generic.services.I18nMessagingService;
import com.sankore.astro.pojo.FilterParams;
import com.sankore.astro.pojo.TicketDetail;
import com.sankore.astro.pojo.TicketMessage;
import com.sankore.astro.pojo.Trail;
import com.sankore.astro.repository.*;
import com.sankore.astro.repository.dataaccesslayer.AstroDataAccessLayer;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.ticket.CreateTicketRequest;
import com.sankore.astro.request.ticket.FetchTicketsRequest;
import com.sankore.astro.request.ticket.TicketIdRequest;
import com.sankore.astro.request.ticket.UpdateTicketRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.ticket.TicketDetailsResponse;
import com.sankore.astro.response.ticket.TicketResponse;
import com.sankore.astro.response.ticket.TicketTrailResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.NotificationService;
import com.sankore.astro.services.TicketService;
import com.sankore.astro.services.UserService;
import com.sankore.ligare.base.Param;
import com.sankore.ligare.enums.email.EmailType;
import com.sankore.ligare.messaging.email.SendMail;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.sankore.astro.generic.Constants.RequestKey.APP_NAME;

/**
 * @author Obi on 24/04/2019
 */
@Service
public class TicketServiceImpl implements TicketService, BaseEntityService {

    @Autowired
    AstroLoggerService log;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AstroDataAccessLayer dataAccessLayer;

    @Autowired
    I18nMessagingService messagingService;

    @Autowired
    IssueTypeRepository issueTypeRepository;

    @Autowired
    TicketSLARepository ticketSLARepository;

    @Autowired
    TicketTrailRepository ticketTrailRepository;

    @Autowired
    IssueCategoryRepository issueCategoryRepository;

    @Autowired
    ResolutionStepRepository resolutionStepRepository;

    @Autowired
    NotificationService notificationService;

    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
    private DateFormat fullDF = new SimpleDateFormat("dd-MMM-yyyy h:mm a");

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.createTicket.name().equals(handlerMethod))
            create((CreateTicketRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.saveTicket.name().equals(handlerMethod))
            saveTicket((CreateTicketRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchTicketFilter.name().equals(handlerMethod))
            fetchTicketFilter((FetchTicketsRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchTickets.name().equals(handlerMethod))
            fetchTickets((FetchTicketsRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.loadTicketDetails.name().equals(handlerMethod))
            loadTicketDetails((TicketIdRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.assignTicket.name().equals(handlerMethod))
            assignTicket((TicketIdRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.resolveTicket.name().equals(handlerMethod))
            resolveTicket((TicketIdRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.updateTicket.name().equals(handlerMethod))
            updateTicket((UpdateTicketRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.reopenTicket.name().equals(handlerMethod))
            reopenTicket((TicketIdRequest) requestPojo, asyncResultHandler);
        else if (RequestMethod.fetchTicketTrail.name().equals(handlerMethod))
            fetchTicketTrail((TicketIdRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Not_Found)));
    }

    @Override
    public String getUniqueRef() {
        return UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
    }

    @Override
    @Transactional
    public void saveTicket(CreateTicketRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("ticket.update.successful"));
        try {
            Ticket ticket = ticketRepository.findTicketById(request.getTicketId());
            if (ticket == null)
                response = new BaseResponse(ResponseCode.Not_Found);
            else {
                ticket.setCategory(issueCategoryRepository.findIssueCategoryById(request.getIssueCategoryId()));
                ticket.setIssueType(issueTypeRepository.findIssueTypeById(request.getIssueTypeId()));
                ticket.setReporterFirstName(request.getReporterFirstName());
                ticket.setReporterLastName(request.getReporterLastName());
                ticket.setReporterPhoneNumber(request.getReporterPhoneNumber());
                ticket.setReporterAcctNumber(request.getAccountNumber());
                ticket.setMedia(Media.valueOf(request.getMedia()));
                ticketRepository.save(ticket);
            }
        } catch (Exception e) {
            log.error("Error", e);
            response = new BaseResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    @Transactional
    public void create(CreateTicketRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("ticket.create.successful"));
        try {
            Ticket ticket = new Ticket();
            ticket.setReference(getUniqueRef());
            ticket.setTicketStatus(TicketStatus.OPEN);
            if (request.getIssueType() != null && !request.getIssueType().isBlank()) {
                ticket.setIssueType(issueTypeRepository.findIssueTypeByNameIgnoreCase(request.getIssueType().trim()));
                ticket.setCategory(ticket.getIssueType().getIssueCategory());
            } else if (request.getIssueCategory() != null && !request.getIssueCategory().isBlank()) {
                ticket.setCategory(issueCategoryRepository.findIssueCategoryByNameIgnoreCase(request.getIssueCategory()));
            } else
                ticket.setCategory(issueCategoryRepository.findIssueCategoryByNameIgnoreCase(TicketCategory.Complaint.name()));

            ticket.setReporterEmail(request.getReporterEmail());
            ticket.setReporterFirstName(request.getReporterFirstName());
            ticket.setReporterLastName(request.getReporterLastName());
            ticket.setReporterPhoneNumber(request.getReporterPhoneNumber());
            ticket.setReporterAcctNumber(request.getAccountNumber());
            ticket.setMedia(Media.valueOf(request.getMedia()));
            ticket.setComment(request.getComment());
            ticket.setClient(clientRepository.findByCode(request.getClientCode()));
            if (ticket.getIssueType() != null)
                ticket.setDueDate(getDueDate(new Date(), ticket.getIssueType().getName(), request.getClientCode()));
            ticketRepository.save(ticket);

            autoAssignTicket(ticket, request.getClientCode(), request.getActiveUsers());
            sendNewTicketToClient(ticket);
            createTicketTrail(ticket, request.getReporterFirstName(), request.getReporterLastName(),
                    request.getReporterEmail(), "Opened", UserType.Customer);
        } catch (Exception e) {
            log.error("Error", e);
            response = new BaseResponse(ResponseCode.Internal_Server_Error);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void autoAssignTicket(Ticket ticket, String clientCode, List<String> activeUsers) {
        if (ticket.getMedia() != Media.AstroCX && CollectionUtils.isEmpty(activeUsers))
            activeUsers = userService.fetchActiveUsers();
        if (CollectionUtils.isEmpty(activeUsers) || ticket == null || ticket.getCategory() == null)
            return;

        try {
            List<User> users = findMatchingUsersForCategory(ticket.getCategory().getName(), clientCode, activeUsers);
            if (!CollectionUtils.isEmpty(users)) {
                log.logJSON("auto assigning tickets to users ", users.size());
                int randomNum = ThreadLocalRandom.current().nextInt(0, users.size());
                ticket.setAssignedTo(users.get(randomNum));
                ticket.setTicketStatus(TicketStatus.IN_PROGRESS);
                ticketRepository.save(ticket);
            }
        } catch (Exception e){
            log.error("Error", e);
        }
    }

    private Date getDueDate(Date dateCreated, String issueTypeName, String clientCode) {
        try {
            TicketSLA sla = ticketSLARepository.findTicketSLAByIssueType_NameAndClient_Code(issueTypeName, clientCode);
            if (sla == null)
                return null;

            return new Date(dateCreated.getTime() + convertTicketSLAToDurationInMillis(sla));
        } catch (Exception e) {
            log.error("Error", e);
            return null;
        }
    }

    @Override
    public void fetchTicketFilter(FetchTicketsRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        TicketResponse response = new TicketResponse(ResponseCode.Success);
        List<IssueCategory> categories = issueCategoryRepository.loadAllCategories();
        Set<IssueType> issueTypes = new HashSet<>();
        categories.stream().forEach(c -> issueTypes.addAll(c.getIssueTypes()));

        FilterParams filter = new FilterParams();
        filter.setMedia(Arrays.stream(Media.values()).map(Enum::name).collect(Collectors.toList()));
        filter.setStatus(Arrays.stream(TicketStatus.values()).map(TicketStatus::getDisplayName).collect(Collectors.toList()));
        filter.setCategories(categories.stream().map(IssueCategory::getName).collect(Collectors.toList()));
        filter.setIssueTypes(issueTypes.stream().map(IssueType::getName).collect(Collectors.toList()));

        response.setFilterParams(filter);
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void loadTicketDetails(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        Ticket ticket = ticketRepository.findByIdAndClientCode(request.getTicketId(), request.getClientCode());
        TicketDetailsResponse response = new TicketDetailsResponse(ResponseCode.Success);
        if (ticket != null && ticket.getDueDate() == null && ticket.getIssueType() != null) {
            ticket.setDueDate(getDueDate(ticket.getDateCreated(), ticket.getIssueType().getName(), request.getClientCode()));
            ticketRepository.save(ticket);
        }
        response.setTicketDetail(transformToTicketDetail(ticket));

        List<ResolutionStep> resolutionSteps = resolutionStepRepository.findAllActiveByTicketId(ticket.getId());
        response.setMessages(toTicketMessages(ticket, resolutionSteps));
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchTickets(FetchTicketsRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        TicketResponse response = new TicketResponse(ResponseCode.Success);
        List<TicketDetail> details = transformToTicketDetail(getTickets(request.getUserId(), request.isAdmin(), 
        		request.getClientCode(), request.getFilters(), request.getStart(), request.getSize()));
        response.setTicketDetails(details);
        response.setTotalCount(countTickets(request.getUserId(), request.isAdmin(), request.getClientCode(), request.getFilters()));
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public long countTickets(Long userId, boolean isAdmin, String clientCode, Map<String, Object> filters) {
        String query = "select count(t) from Ticket t where t.client.code = :clientCode";
        if (!isAdmin) {
            filters.put("userId", userId);
            query += " and (t.assignedTo.id = :userId or t.category.id in " +
                    "(select c.id from Group g left join g.issueCategories c where g.id in " +
                    "(select m.group.id from UserGroupMapping m where m.user.id = :userId))) ";
        }
        query = populateQueryFilters(clientCode, filters, query);
        try {
            return dataAccessLayer.countWithDynamicJPAQueryString(query, filters);
        } catch (Exception e) {
            log.error("Error: ", e);
        }
        return 0L;
    }

    @Override
    public List<Object[]> getTickets(Long userId, boolean isAdmin, String clientCode, Map<String, Object> filters, int start, int size) {
        List<Object[]> tickets = new ArrayList<>();
        String query = "select t.id, t.reference, t.reporterEmail, c.name, t.ticketStatus, t.dueDate, t.dateCreated " +
                "from Ticket t left join t.category c where t.client.code = :clientCode ";
        if (!isAdmin) {
            filters.put("userId", userId);
            query += " and (t.assignedTo.id = :userId or t.category.id in " +
                    "(select c.id from Group g left join g.issueCategories c where g.id in " +
                    "(select m.group.id from UserGroupMapping m where m.user.id = :userId))) ";
        }

        query = populateQueryFilters(clientCode, filters, query);
        query += " order by t.id desc";
        try {
            tickets = (List<Object[]>) dataAccessLayer.findWithDynamicJPAQueryString(query, filters, start, size);
        } catch (Exception e) {
            log.error("Error", e);
        }
        return tickets;
    }

    private List<User> findMatchingUsersForCategory(String category, String clientCode,  List<String> ssoIds) {
        String query = "select m.user from UserGroupMapping m left join m.group g left join g.issueCategories c " +
                "where m.user.ssoId in :ssoIds and g.client.code = :clientCode and c.name = :category";

        List<User> users = new ArrayList<>();
        Map<String, Object> filters = new HashMap<>();
        filters.put("ssoIds", ssoIds);
        filters.put("category", category);
        filters.put("clientCode", clientCode);
        try {
            users = (List<User>) dataAccessLayer.findWithDynamicJPAQueryString(query, filters);
        } catch (Exception e) {
            log.error("Error", e);
        }
        return users;
    }

    @Override
    public void assignTicket(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        BaseResponse response;
        Ticket ticket = ticketRepository.findByIdAndClientCode(request.getTicketId(), request.getClientCode());
        if (ticket == null) {
            response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("ticket-not-found"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        if (ticket.getTicketStatus() == TicketStatus.RESOLVED) {
            response = new BaseResponse(ResponseCode.Info.getCode(), messagingService.getMessage("ticket_already_resolved"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        User user = userRepository.findByIdAndClientCode(request.getAssignToUserId(), request.getClientCode());
        if (user == null) {
            response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("user_not_found"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        if (request.getUserId().equals(request.getAssignToUserId()) &&
                (ticket.getAssignedTo() != null && user.getEmailAddress().equals(ticket.getAssignedTo().getEmailAddress()))) {
            response = new BaseResponse(ResponseCode.Info.getCode(), messagingService.getMessage("ticket_already_assigned_to_you"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        ticket.setTicketStatus(TicketStatus.IN_PROGRESS);
        ticket.setAssignedTo(user);
        ticketRepository.save(ticket);
        response = new BaseResponse(ResponseCode.Success.getCode(),
                messagingService.getMessage("ticket_assign_successful", new String[]{user.getFirstName()} ));
        createTicketTrail(ticket, user.getFirstName(), user.getLastName(), user.getEmailAddress(), "Assigned", UserType.Agent);
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void resolveTicket(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        Ticket ticket = ticketRepository.findByIdAndClientCode(request.getTicketId(), request.getClientCode());
        BaseResponse response;
        if (ticket == null)
            response =  new BaseResponse(ResponseCode.Not_Found.getCode(), "Ticket not found");
        else {
            ticket.setTicketStatus(TicketStatus.RESOLVED);
            ticketRepository.save(ticket);
            response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("ticket_resolve_successful"));
            User user = userRepository.findByIdAndClientCode(request.getUserId(), request.getClientCode());
            createTicketTrail(ticket, user.getFirstName(), user.getLastName(), user.getEmailAddress(), "Resolved", UserType.Agent);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void updateTicket(UpdateTicketRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        Ticket ticket = ticketRepository.findByIdAndClientCode(request.getTicketId(), request.getClientCode());
        if (ticket == null) {
            BaseResponse response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("ticket-not-found"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        User user = userRepository.findByIdAndClientCode(request.getUserId(), request.getClientCode());
        if (user == null) {
            BaseResponse response = new BaseResponse(ResponseCode.Not_Found.getCode(), messagingService.getMessage("user_not_found"));
            handler.handle(Future.succeededFuture(response));
            return;
        }

        ResolutionStep resolutionStep = new ResolutionStep();
        resolutionStep.setComment(request.getComment());
        resolutionStep.setFromClient(false);
        resolutionStep.setSendToClient(request.isSendToClient());
        resolutionStep.setStatus(ticket.getTicketStatus());
        resolutionStep.setTicket(ticket);
        resolutionStep.setUpdateDate(new Date());
        resolutionStep.setUpdatedBy(user);
        resolutionStepRepository.save(resolutionStep);

        if (ticket.getTicketStatus() == TicketStatus.OPEN) {
            ticket.setTicketStatus(TicketStatus.IN_PROGRESS);
            ticket.setAssignedTo(user);
            ticketRepository.save(ticket);
        }
        BaseResponse response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("ticket_Comment_successful"));
        createTicketTrail(ticket, user.getFirstName(), user.getLastName(), user.getEmailAddress(), "Updated", UserType.Agent);
        if (request.isSendToClient()) {
            sendTicketUpdateToClient(ticket, request.getComment());
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void reopenTicket(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        Ticket ticket = ticketRepository.findByIdAndClientCode(request.getTicketId(), request.getClientCode());
        BaseResponse response;
        if (ticket == null)
            response =  new BaseResponse(ResponseCode.Not_Found.getCode(), "Ticket not found");
        else {
            ticket.setTicketStatus(TicketStatus.IN_PROGRESS);
            ticketRepository.save(ticket);
            response = new BaseResponse(ResponseCode.Success.getCode(), messagingService.getMessage("ticket_reopen_successful"));
            User user = userRepository.findByIdAndClientCode(request.getUserId(), request.getClientCode());
            createTicketTrail(ticket, user.getFirstName(), user.getLastName(), user.getEmailAddress(), "Re-opened", UserType.Agent);
        }
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public void fetchTicketTrail(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        TicketTrailResponse response;
        try {
            List<TicketTrail> ticketTrails = ticketTrailRepository.findAllByTicket_IdOrderByDateCreated(request.getTicketId());
            response = new TicketTrailResponse(ResponseCode.Success);
            response.setTrails(toTrail(ticketTrails));
        } catch (Exception e) {
            response = new TicketTrailResponse(ResponseCode.Internal_Server_Error);
            log.error("Error", e);
        }
        handler.handle(Future.succeededFuture(response));
    }

    private String populateQueryFilters(String clientCode, Map<String, Object> filters, String query) {
        if (filters.containsKey("fromDate")) {
            try {
                filters.replace("fromDate", df.parse(filters.get("fromDate").toString()));
            } catch (ParseException e) {  }
            query += " and DATE(t.dateCreated) >= :fromDate ";
        }
        if (filters.containsKey("toDate")) {
            try {
                filters.put("toDate", df.parse(filters.get("toDate").toString()));
            } catch (ParseException e) {  }
            query += " and DATE(t.dateCreated) <= :toDate ";
        }
        if (filters.containsKey("search"))
            query += " and (lower(t.reference) like :search or lower(t.reporterFirstName) like :search or lower(t.reporterLastName) like :search or lower(t.reporterAcctNumber) like :search) ";
        
        if (filters.containsKey("ticketStatus")) {
            filters.put("ticketStatus", TicketStatus.fromDisplayName(filters.get("ticketStatus").toString()));
            query += " and t.ticketStatus = :ticketStatus ";
        }
        if (filters.containsKey("media")) {
            filters.put("media", Media.valueOf(filters.get("media").toString()));
            query += " and t.media = :media ";
        }
        if (filters.containsKey("category")) {
            query += " and t.category.name = :category";
        }

        filters.put("clientCode", clientCode);
        return query;
    }

    private List<TicketDetail> transformToTicketDetail(List<Object[]> tickets) {
        if (CollectionUtils.isEmpty(tickets))
            return null;

        List<TicketDetail> ticketDetails = new ArrayList<>();
        for (Object[] ticket : tickets) {
            TicketDetail detail = new TicketDetail();
            detail.setTicketId((Long)ticket[0]);
            detail.setReference((String)ticket[1]);
            detail.setReporterEmail((String)ticket[2]);
            detail.setCategory((String)ticket[3]);
            detail.setTicketStatus(((TicketStatus)ticket[4]).getDisplayName());
            detail.setDueDate((Date)ticket[5]);
            detail.setDateCreated((Date)ticket[6]);
            ticketDetails.add(detail);
        }
        return ticketDetails;
    }

    private TicketDetail transformToTicketDetail(Ticket ticket) {
        if (ticket == null)
            return null;

        TicketDetail detail = new TicketDetail();
        detail.setTicketId(ticket.getId());
        detail.setReference(ticket.getReference());
        detail.setComment(ticket.getComment());
        detail.setMedia(ticket.getMedia().name());
        detail.setCategory(ticket.getCategory().getName());
        detail.setTicketStatus(ticket.getTicketStatus().getDisplayName());
        detail.setReporterEmail(ticket.getReporterEmail());
        detail.setReporterFirstName(ticket.getReporterFirstName());
        detail.setReporterLastName(ticket.getReporterLastName());
        detail.setReporterPhoneNumber(ticket.getReporterPhoneNumber());
        detail.setAccountNumber(ticket.getReporterAcctNumber());
        detail.setDateCreated(ticket.getDateCreated());
        detail.setDueDate(ticket.getDueDate());
        if (ticket.getIssueType() != null)
            detail.setIssueType(ticket.getIssueType().getName());
        if (ticket.getAssignedTo() != null) {
            detail.setAssignedToName(ticket.getAssignedTo().getFirstName() + " " + ticket.getAssignedTo().getLastName());
            detail.setAssignedToEmail(ticket.getAssignedTo().getEmailAddress());
        }
        return detail;
    }

    private List<TicketMessage> toTicketMessages(Ticket ticket, List<ResolutionStep> resolutionSteps) {
        List<TicketMessage> ticketMessages = new ArrayList<>();

        TicketMessage message = new TicketMessage();
        message.setComment(ticket.getComment());
        message.setDateCreated(fullDF.format(ticket.getDateCreated()));
        message.setFromClient(true);
        message.setStatus(ticket.getTicketStatus().getDisplayName());
        message.setUsername(ticket.getReporterFirstName() + " " + ticket.getReporterLastName());
        message.setSendToClient(true);
        ticketMessages.add(message);

        if (!CollectionUtils.isEmpty(resolutionSteps)) {
            resolutionSteps.forEach(r -> {
                TicketMessage ticketMessage = new TicketMessage();
                ticketMessage.setComment(r.getComment());
                ticketMessage.setDateCreated(fullDF.format(r.getUpdateDate()));
                ticketMessage.setFromClient(r.isFromClient());
                ticketMessage.setStatus(r.getStatus().getDisplayName());
                ticketMessage.setSendToClient(r.isSendToClient());
                if (r.isFromClient())
                    ticketMessage.setUsername(ticket.getReporterFirstName() + " " + ticket.getReporterLastName());
                else
                    ticketMessage.setUsername(r.getUpdatedBy().getFirstName() + " " + r.getUpdatedBy().getLastName());
                ticketMessages.add(ticketMessage);
            });
        }

        return ticketMessages;
    }

    private List<Trail> toTrail(List<TicketTrail> ticketTrails) {
        List<Trail> trails = new ArrayList<>();
        if (!CollectionUtils.isEmpty(ticketTrails)) {
            for (TicketTrail ticketTrail : ticketTrails) {
                Trail trail = new Trail();
                trail.setTitle(ticketTrail.getTitle());
                trail.setUserFirstName(ticketTrail.getUserFirstName());
                trail.setUserLastName(ticketTrail.getUserLastName());
                trail.setUserType(ticketTrail.getUserType().name());
                trail.setDateCreated(fullDF.format(ticketTrail.getDateCreated()));
                trails.add(trail);
            }
        }
        return trails;
    }

    private void createTicketTrail(Ticket ticket, String firstName, String lastName,
                                   String email, String title, UserType userType) {
        try {
            TicketTrail ticketTrail = new TicketTrail();
            ticketTrail.setTicket(ticket);
            ticketTrail.setUserFirstName(firstName);
            ticketTrail.setUserLastName(lastName);
            ticketTrail.setUserEmail(email);
            ticketTrail.setTitle(title);
            ticketTrail.setUserType(userType);
            ticketTrailRepository.save(ticketTrail);
        } catch (Exception e) {
            log.error("Error", e);
        }
    }

    @Override
    public long convertTicketSLAToDurationInMillis(TicketSLA ticketSLA) {
        if (ticketSLA == null)
            return 0L;

        if (!CollectionUtils.isEmpty(ticketSLA.getEscalationLevels())) {
            EscalationLevel escalationLevel = ticketSLA.getEscalationLevels().stream()
                    .sorted(Comparator.comparingInt(EscalationLevel::getLevel)).findFirst().orElse(null);
            if (escalationLevel != null) return getInterval(escalationLevel);
        }
        return 0L;
    }

    @Override
    public long getInterval(EscalationLevel escalationLevel) {
        if (escalationLevel != null) {
            if (escalationLevel.getTimeUnit().equals(SLATimeUnit.Hours)) {
                return escalationLevel.getPeriod() * 60 * 60 * 1000;
            } else {
                return escalationLevel.getPeriod() * 24 * 60 * 60 * 1000;
            }
        }
        return 0L;
    }

    public void sendTicketUpdateToClient(Ticket ticket, String message) {
        try {
            SendMail sendMail = new SendMail();
            sendMail.setSenderApp(APP_NAME);
            sendMail.setMailFor("Ticket_Update");
            sendMail.setTemplate("Ticket_Update.html");
            sendMail.setSender(notificationService.getNoReplySender());
            sendMail.setMailType(EmailType.HTML.name());
            sendMail.setRecipients(Arrays.asList(ticket.getReporterEmail()));
            sendMail.setSubject(getTicketMailSubject(ticket));
            sendMail.setContentClass(SendMail.class.getSimpleName());
    
            List<Param> paramList = new ArrayList<>();
            paramList.add(new Param("firstName", ticket.getReporterFirstName()));
            paramList.add(new Param("ref", ticket.getReference()));
            paramList.add(new Param("message", message));
	        paramList.add(new Param("initialComment", ticket.getComment()));
            sendMail.setParams(paramList);
            notificationService.sendMailToQueue(sendMail);
        } catch(Exception err) {
            log.error("Error", err);
        }
    }
    
    public void sendNewTicketToClient(Ticket ticket) {
        try {
            SendMail sendMail = new SendMail();
            sendMail.setSenderApp(APP_NAME);
            sendMail.setMailFor("New_Ticket_Notification");
            sendMail.setTemplate("New_Ticket_Notification.html");
            sendMail.setContentClass(SendMail.class.getSimpleName());
            sendMail.setSender(notificationService.getNoReplySender());
            sendMail.setMailType(EmailType.HTML.name());
            sendMail.setRecipients(Arrays.asList(ticket.getReporterEmail()));
            sendMail.setSubject(getTicketMailSubject(ticket));
            
            List<Param> paramList = new ArrayList<>();
            paramList.add(new Param("firstName", ticket.getReporterFirstName()));
            paramList.add(new Param("client", ticket.getClient().getName()));
            paramList.add(new Param("category", ticket.getCategory().getName()));
            paramList.add(new Param("issueType", ticket.getIssueType() != null ? ticket.getIssueType().getName() : ""));
            paramList.add(new Param("createDate", fullDF.format(ticket.getDateCreated())));
            paramList.add(new Param("ref", ticket.getReference()));
            paramList.add(new Param("comment", ticket.getComment()));
            paramList.add(new Param("endpoint", notificationService.getMessageEndpoint()));
            sendMail.setParams(paramList);
            notificationService.sendMailToQueue(sendMail);
        } catch(Exception err) {
            log.error("Error", err);
        }
    }

    @Override
    public void sendEscalationEmail(Ticket ticket, User admin)  throws Exception {
        SendMail sendMail = new SendMail();
        sendMail.setSenderApp(APP_NAME);
        sendMail.setMailFor("Ticket_Escalation");
        sendMail.setTemplate("Ticket_Escalation.html");
        sendMail.setContentClass(SendMail.class.getSimpleName());
        sendMail.setSender(notificationService.getNoReplySender());
        sendMail.setMailType(EmailType.HTML.name());

        sendMail.setSubject("Ticket Escalation - [#" + ticket.getReference() + "]");
        List<Param> paramList = new ArrayList<>();
        paramList.add(new Param("fullName", ticket.getReporterFirstName() + " " + ticket.getReporterLastName()));
        paramList.add(new Param("ref", ticket.getReference()));
        paramList.add(new Param("issueType", ticket.getIssueType() != null ? ticket.getIssueType().getName() : ""));
        paramList.add(new Param("createDate", fullDF.format(ticket.getDateCreated())));
        paramList.add(new Param("dueDate", fullDF.format(ticket.getDueDate())));
        paramList.add(new Param("comment", ticket.getComment()));
        paramList.add(new Param("assignedTo", ticket.getAssignedFullName()));
        paramList.add(new Param("endpoint", notificationService.getMessageEndpoint()));

        paramList.add(new Param("adminFirstName", admin.getFirstName()));
        sendMail.setRecipients(Arrays.asList(admin.getEmailAddress()));
        sendMail.setParams(paramList);
        log.info("sending escalation to "+ admin.getEmailAddress());
        notificationService.sendMailToQueue(sendMail);
    }
    
    private String getTicketMailSubject(Ticket ticket) {
    	if (ticket == null)
    		return null;
    	return "[#" + ticket.getReference() + "]: "+ (ticket.getCategory() != null ? ticket.getCategory().getName() : "");
    }

}
