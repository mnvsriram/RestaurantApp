package app.resta.com.restaurantapp.db.dao.admin.menuGroup;

import com.google.firebase.firestore.Source;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.MenuTypeAndGroupMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/12/2018.
 */

public interface MenuGroupAdminDaoI {
    void addItemToGroup(GroupAndItemMapping mapping, final OnResultListener<String> listener);

    void insertOrUpdateGroup(final RestaurantItem group, final OnResultListener<RestaurantItem> listener);

    void deleteGroup(final String groupId, final String menuTypeId, final OnResultListener<String> listener);

    void getGroups(List<MenuTypeAndGroupMapping> mappings, Source source, final OnResultListener<List<RestaurantItem>> listener);

    void getGroupsAlongWithItems(List<MenuTypeAndGroupMapping> mappings, Source source, final OnResultListener<List<RestaurantItem>> listener);

    void getItemsInGroup(final String groupId, final OnResultListener<List<RestaurantItem>> listener);

    void getGroup(String groupId, final OnResultListener<RestaurantItem> listener);

//    void updateItemPositions(List<GroupAndItemMapping> mappings);

    void deleteItemFromGroup(String itemId, String groupId, final OnResultListener<String> listener);

    void deleteItemFromAllGroups(String itemId, final OnResultListener<String> listener);

    void removeGroupFromMenuType(String menuTypeId, String groupId, final OnResultListener<String> listener);

    void addGroupToMenuType(MenuTypeAndGroupMapping mapping, final OnResultListener<MenuTypeAndGroupMapping> listener);

    void updateItemsInGroup(final String groupId, final List<GroupAndItemMapping> mappings, final OnResultListener<String> listener);
}
