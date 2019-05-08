package app.resta.com.restaurantapp.db.dao.user.menuType;

import com.google.firebase.firestore.Source;

import java.util.List;

import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuTypeUserFireStoreDao extends MenuTypeAdminFireStoreDao implements MenuTypeUserDaoI {
    private static final String TAG = "MenuGroupUserDao";


    @Override
    public void getAllMenuTypes_u(final OnResultListener<List<MenuType>> listener) {
        getAllMenuTypes(listener, Source.CACHE);
    }

    @Override
    public void getGroupsInMenuType_u(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener) {
        getGroupsInMenuType(menuTypeId, Source.CACHE, listener);
    }


    @Override
    public void getGroupsWithItemsInMenuType_u(final String menuTypeId, final OnResultListener<List<RestaurantItem>> listener) {
        getGroupsWithItemsInMenuType(menuTypeId, Source.CACHE, listener);
    }


    @Override
    public void getMenuType_u(final String menuTypeId, final OnResultListener<MenuType> listener) {
        if (menuTypeId != null) {
            getMenuType(menuTypeId, Source.CACHE, listener);
        } else {
            listener.onCallback(null);
        }
    }

}
