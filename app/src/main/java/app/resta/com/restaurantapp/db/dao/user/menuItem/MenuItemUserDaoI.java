package app.resta.com.restaurantapp.db.dao.user.menuItem;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuItemUserDaoI {
    void getTagsForItem_u(String itemId, final OnResultListener<List<String>> listener);

    void getItem_u(final String itemId, final OnResultListener<RestaurantItem> listener);

    void getAllItems_u(final OnResultListener<List<RestaurantItem>> listener);

    void getItems_u(final List<GroupAndItemMapping> mappings, final OnResultListener<List<RestaurantItem>> listener);

}
