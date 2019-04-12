package app.resta.com.restaurantapp.util;

import java.util.Comparator;

import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;

/**
 * Created by Sriram on 29/07/2017.
 */
public class TagNameComparator implements Comparator<Tag> {
    @Override
    public int compare(Tag o1, Tag o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
