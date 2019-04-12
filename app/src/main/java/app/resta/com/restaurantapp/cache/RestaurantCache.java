package app.resta.com.restaurantapp.cache;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 28/07/2017.
 */
public class RestaurantCache {
    public static Map<String, RestaurantItem> allChildItemsByName = new HashMap<>();
    public static Map<Long, RestaurantItem> allItemsById = new HashMap<>();
}
