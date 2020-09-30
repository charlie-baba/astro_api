package com.sankore.astro.services;

import com.sankore.astro.pojo.Menu;
import com.sankore.astro.request.menu.MenuRequest;
import com.sankore.astro.response.BaseResponse;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

import java.util.List;

/**
 * @author Obi on 24/05/2019
 */
public interface MenuService {

    void fetchAdminMenus(MenuRequest request, Handler<AsyncResult<BaseResponse>> handler);

    List<Menu> toMenu(List<com.sankore.astro.entity.Menu> menus);

    Menu toMenu(com.sankore.astro.entity.Menu menu);
}
