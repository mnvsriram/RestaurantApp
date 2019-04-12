package app.resta.com.restaurantapp.util;

import java.util.Comparator;

import app.resta.com.restaurantapp.model.RestaurantItem;


/**
 * Created by Sriram on 29/07/2017.
 */
public class GroupPositionComparator implements Comparator<RestaurantItem> {

    @Override
    public int compare(RestaurantItem item1, RestaurantItem item2) {

        Long position1 = -1l;
        if (item1 != null) {
            position1 = item1.getPosition();
        }

        Long position2 = -1l;
        if (item2 != null) {
            position2 = item2.getPosition();
        }


        return position1.compareTo(position2);
    }
}
