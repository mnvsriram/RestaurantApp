package app.resta.com.restaurantapp.db.dao.user.menuType;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuTypeUserDaoI {
    void getAllMenuTypes_u(final OnResultListener<List<MenuType>> listener);

    void getMenuType_u(final String menuTypeId, final OnResultListener<MenuType> listener);

    void getGroupsWithItemsInMenuType_u(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener);

    void getGroupsInMenuType_u(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener);
}
