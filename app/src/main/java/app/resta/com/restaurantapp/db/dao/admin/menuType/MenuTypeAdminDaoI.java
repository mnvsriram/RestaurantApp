package app.resta.com.restaurantapp.db.dao.admin.menuType;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.MenuTypeAndGroupMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuTypeAdminDaoI {
    void insertOrUpdateMenuType(final MenuType menuType, final OnResultListener<MenuType> listener);

    void getMenuType(final String menuTypeId, final OnResultListener<MenuType> listener);

    void getAllMenuTypes(final OnResultListener<List<MenuType>> listener);

    void getGroupsWithItemsInMenuType(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener);

    void getGroupsInMenuType(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener);

    void updatePositions(List<MenuTypeAndGroupMapping> mappings);



}
