package app.resta.com.restaurantapp.util;

import java.util.Comparator;

import app.resta.com.restaurantapp.model.ItemParentMapping;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/07/2017.
 */
public class ItemPositionComparator implements Comparator<RestaurantItem> {
    @Override
    public int compare(RestaurantItem o1, RestaurantItem o2) {
        Integer o1Position = -1;
        Integer o2Position = -1;
        if (o1.getItemToParentMappings() != null && o2.getItemToParentMappings() != null) {

            ItemParentMapping o1Mapping = o1.getItemToParentMappings().get(o1.getParent().getId());
            ItemParentMapping o2Mapping = o2.getItemToParentMappings().get(o2.getParent().getId());
            if (o1Mapping != null && o1Mapping.getPosition() != null) {
                o1Position = o1Mapping.getPosition();
            }

            if (o2Mapping != null && o2Mapping.getPosition() != null) {
                o2Position = o2Mapping.getPosition();
            }
        }
        return o1Position.compareTo(o2Position);
    }
}
