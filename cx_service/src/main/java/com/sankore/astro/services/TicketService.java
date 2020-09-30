package com.sankore.astro.services;

import com.sankore.astro.entity.EscalationLevel;
import com.sankore.astro.entity.Ticket;
import com.sankore.astro.entity.TicketSLA;
import com.sankore.astro.entity.User;
import com.sankore.astro.request.ticket.CreateTicketRequest;
import com.sankore.astro.request.ticket.FetchTicketsRequest;
import com.sankore.astro.request.ticket.TicketIdRequest;
import com.sankore.astro.request.ticket.UpdateTicketRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;
import java.util.Map;

/**
 * @author Obi on 24/04/2019
 */
public interface TicketService {

    String getUniqueRef();

    void create(CreateTicketRequest ticket, Handler<AsyncResult<BaseResponse>> asyncResultHandler);

    void saveTicket(CreateTicketRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchTicketFilter(FetchTicketsRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void loadTicketDetails(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchTickets(FetchTicketsRequest request, Handler<AsyncResult<BaseResponse>> handler);

    long countTickets(Long userId, boolean isAdmin, String clientCode, Map<String, Object> filters);

    void assignTicket(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void resolveTicket(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void updateTicket(UpdateTicketRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void reopenTicket(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler);

    void fetchTicketTrail(TicketIdRequest request, Handler<AsyncResult<BaseResponse>> handler);

    long convertTicketSLAToDurationInMillis(TicketSLA ticketSLA);

    long getInterval(EscalationLevel escalationLevel);

	List<Object[]> getTickets(Long userId, boolean isAdmin, String clientCode, Map<String, Object> filters, int start, int size);

    void sendEscalationEmail(Ticket tickets, User admin) throws Exception;

    void autoAssignTicket(Ticket ticket, String clientCode, List<String> activeUsers);
}
