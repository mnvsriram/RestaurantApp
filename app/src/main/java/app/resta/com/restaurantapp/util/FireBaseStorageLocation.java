package app.resta.com.restaurantapp.util;

/**
 * Created by Sriram on 31/12/2018.
 */

import static app.resta.com.restaurantapp.util.RestaurantMetadata.getRestaurantId;

public class FireBaseStorageLocation {

    public static String getIngredientImagesLocation() {
        return "restaurants/" + getRestaurantId() + "/" + "images/ingredients/";
    }

    public static String getTagImagesLocation() {
        return "restaurants/" + getRestaurantId() + "/" + "images/tags/";
    }

    public static String getItemImagesLocation() {
        return "restaurants/" + getRestaurantId() + "/" + "images/items/";
    }

    public static String getNoImageLocation() {
        return "restaurants/" + getRestaurantId() + "/" + "images/noImage/noImage.jpg";
    }

    public static String getMenuCardImagesLocation() {
        return "restaurants/" + getRestaurantId() + "/" + "images/menuCards/";
    }

}
