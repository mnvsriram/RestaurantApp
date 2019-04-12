package app.resta.com.restaurantapp.db.dao.user.menuGroup;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuTypeAndGroupMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuGroupUserDaoI {
    void getGroupsWithItems_u(final List<MenuTypeAndGroupMapping> mappings, final OnResultListener<List<RestaurantItem>> listener);

    void getItemsInGroup_u(final String groupId, final OnResultListener<List<RestaurantItem>> listener);

}
