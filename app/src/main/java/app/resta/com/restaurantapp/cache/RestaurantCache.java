package app.resta.com.restaurantapp.cache;

import android.view.MenuItem;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 28/07/2017.
 */
public class RestaurantCache {
    public static Map<String, RestaurantItem> allChildItemsByName = new HashMap<>();
    public static Map<Long, RestaurantItem> allItemsById = new HashMap<>();
    public static Map<Long, RestaurantItem> allParentItemsById = new HashMap<>();
    public static Map<Long, RestaurantImage[]> imageMap = new HashMap<>();
    public static boolean dataFetched = false;


    public static void refreshCache() {
        dataFetched = false;
        MenuItemDao menuItemDao = new MenuItemDao();
        menuItemDao.fetchMenuItems(-1l);
    }
}
