package app.resta.com.restaurantapp.util;

import java.util.Comparator;

import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/07/2017.
 */
public class ItemNameComparator implements Comparator<RestaurantItem> {
    @Override
    public int compare(RestaurantItem o1, RestaurantItem o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
