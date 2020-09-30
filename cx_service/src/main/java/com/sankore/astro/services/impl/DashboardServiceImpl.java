package com.sankore.astro.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.enums.TicketStatus;
import com.sankore.astro.generic.services.AstroLoggerService;
import com.sankore.astro.pojo.DashboardData;
import com.sankore.astro.repository.TicketRepository;
import com.sankore.astro.request.BaseRequest;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.DashboardResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.DashboardService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author Obi on 16/05/2019
 */
@Service
public class DashboardServiceImpl implements DashboardService, BaseEntityService {

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    AstroLoggerService log;
    
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchDashboardData.name().equals(handlerMethod)) {
            fetchDashboardData((BaseRequest) requestPojo, asyncResultHandler);
        } else {
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
        }
    }

    @Override
    public void fetchDashboardData(BaseRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        String clientCode = request.getClientCode();
        DashboardData data = new DashboardData();
        data.setDueToday(ticketRepository.countDueToday(clientCode, new Date(), TicketStatus.RESOLVED));
        data.setInProgress(ticketRepository.countByClient_CodeAndTicketStatus(clientCode, TicketStatus.IN_PROGRESS));
        data.setOverdue(ticketRepository.countOverDue(clientCode, new Date(), TicketStatus.RESOLVED));
        data.setResolved(ticketRepository.countByClient_CodeAndTicketStatus(clientCode, TicketStatus.RESOLVED));
        data.setUnresolved(ticketRepository.countByOtherThanTicketStatus(clientCode, TicketStatus.RESOLVED));
        data.setCategories(ticketRepository.countByCategory(clientCode));
        try {
        	String channels = "[['Tickets by Category', 'Amounts'], "+ mapper.writeValueAsString(ticketRepository.countByCategory(clientCode)).substring(1);
        	String media = "[['Tickets by Media', 'Amounts'], "+ mapper.writeValueAsString(ticketRepository.countByMedia(clientCode)).substring(1);
			data.setChannels(channels);
	        data.setMedia(media);
		} catch (JsonProcessingException e) {
			log.error("Error: ", e);
		}

        DashboardResponse response = new DashboardResponse(ResponseCode.Success);
        response.setData(data);
        handler.handle(Future.succeededFuture(response));
    }
}
