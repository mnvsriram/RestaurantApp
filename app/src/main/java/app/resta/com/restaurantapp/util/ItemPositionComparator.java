package app.resta.com.restaurantapp.util;

import java.util.Comparator;

import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/07/2017.
 */
public class ItemPositionComparator implements Comparator<RestaurantItem> {
    @Override
    public int compare(RestaurantItem o1, RestaurantItem o2) {
        Long position1 = o1.getPosition();
        Long position2 = o2.getPosition();

        return position1.compareTo(position2);
    }
}
