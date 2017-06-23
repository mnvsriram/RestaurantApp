package app.resta.com.restaurantapp.controller;

import java.util.HashSet;
import java.util.Set;

import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 22/06/2017.
 */
public class ItemsOnPlate {

    Set<RestaurantItem> itemsOnPlate;

    public ItemsOnPlate() {
        this.itemsOnPlate = new HashSet<>();
    }

    public boolean addToPlate(RestaurantItem item) {
        return itemsOnPlate.add(item);
    }

    public void removeFromPlate(RestaurantItem item) {
        itemsOnPlate.remove(item);
    }
}
