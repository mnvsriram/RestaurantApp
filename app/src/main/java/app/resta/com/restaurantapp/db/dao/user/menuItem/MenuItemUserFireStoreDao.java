package app.resta.com.restaurantapp.db.dao.user.menuItem;

import com.google.firebase.firestore.Source;

import java.util.List;

import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.GroupAndItemMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuItemUserFireStoreDao extends MenuItemAdminFireStoreDao implements MenuItemUserDaoI {
    private static final String TAG = "MenuItemAdminDao";

    @Override
    public void getAllItems_u(final OnResultListener<List<RestaurantItem>> listener) {
        getAllItems(Source.CACHE, listener);
    }

    @Override
    public void getTagsForItem_u(final String itemId, final OnResultListener<List<String>> listener) {
        getTagsForItem(itemId, Source.CACHE, listener);
    }

    @Override
    public void getItem_u(final String itemId, final OnResultListener<RestaurantItem> listener) {
        getItem(itemId, Source.CACHE, listener);
    }


    @Override
    public void getItems_u(final List<GroupAndItemMapping> mappings, final OnResultListener<List<RestaurantItem>> listener) {
        getItems(mappings, Source.CACHE, listener);
    }

    @Override
    public void getIngredientsForItem_u(String itemId, final OnResultListener<List<String>> listener) {
        getIngredientsForItem(itemId, Source.CACHE, listener);
    }

    @Override
    public void getGGWsForItem_u(String itemId, final OnResultListener<List<String>> listener) {
        getGGWsForItem(itemId, Source.CACHE, listener);
    }
}
