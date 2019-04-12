package app.resta.com.restaurantapp.db.dao.user.menuGroup;

import com.google.firebase.firestore.Source;

import java.util.List;

import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuTypeAndGroupMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuGroupUserFireStoreDao extends MenuGroupAdminFireStoreDao implements MenuGroupUserDaoI {
    private static final String TAG = "MenuGroupUserDao";


    @Override
    public void getGroupsWithItems_u(final List<MenuTypeAndGroupMapping> mappings, final OnResultListener<List<RestaurantItem>> listener) {
        getGroupsAlongWithItems(mappings, Source.CACHE, listener);
    }


    @Override
    public void getItemsInGroup_u(final String groupId, final OnResultListener<List<RestaurantItem>> listener) {
        getItemsInGroup(groupId, Source.CACHE, listener);
    }


}
