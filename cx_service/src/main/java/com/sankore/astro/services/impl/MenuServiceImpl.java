package com.sankore.astro.services.impl;

import com.sankore.astro.enums.ResponseCode;
import com.sankore.astro.enums.RoleType;
import com.sankore.astro.pojo.Menu;
import com.sankore.astro.repository.MenuRepository;
import com.sankore.astro.request.RequestMethod;
import com.sankore.astro.request.menu.MenuRequest;
import com.sankore.astro.response.BaseResponse;
import com.sankore.astro.response.menu.MenuListResponse;
import com.sankore.astro.services.BaseEntityService;
import com.sankore.astro.services.MenuService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Obi on 24/05/2019
 */
@Service
public class MenuServiceImpl implements MenuService, BaseEntityService {

    @Autowired
    MenuRepository menuRepository;

    @Override
    public void processRequest(Object requestPojo, String handlerMethod, Handler<AsyncResult<BaseResponse>> asyncResultHandler) {
        if (RequestMethod.fetchAdminMenus.name().equals(handlerMethod))
            fetchAdminMenus((MenuRequest) requestPojo, asyncResultHandler);
        else
            asyncResultHandler.handle(Future.succeededFuture(new BaseResponse(ResponseCode.Invalid_Request_Target)));
    }

    @Override
    public void fetchAdminMenus(MenuRequest request, Handler<AsyncResult<BaseResponse>> handler) {
        List<Menu> menus = new ArrayList<>();
        menuRepository.findSystemMenusByRoleName(RoleType.ADMIN.getScreenName()).stream().forEach( m -> {
            Menu menu = new Menu();
            menu.setId(m.getId());
            menu.setUrl(m.getUrl());
            menu.setName(m.getName());
            menu.setPosition(m.getPosition());
            menu.setCommands(m.getCommands());
            menus.add(menu);
        });

        MenuListResponse response = new MenuListResponse(ResponseCode.Success);
        response.setMenus(menus);
        handler.handle(Future.succeededFuture(response));
    }

    @Override
    public List<Menu> toMenu(List<com.sankore.astro.entity.Menu> menus) {
        List<Menu> menuList = new ArrayList<>();
        if (CollectionUtils.isEmpty(menus))
            return menuList;

        menus.stream().forEach(p -> menuList.add(toMenu(p)));
        return menuList;
    }

    @Override
    public Menu toMenu(com.sankore.astro.entity.Menu menu) {
        if (menu == null)
            return null;

        Menu menuPojo = new Menu();
        menuPojo.setId(menu.getId());
        menuPojo.setCommands(menu.getCommands());
        menuPojo.setName(menu.getName());
        menuPojo.setPosition(menu.getPosition());
        menuPojo.setUrl(menu.getUrl());
        return menuPojo;
    }
}
