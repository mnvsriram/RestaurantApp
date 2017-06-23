package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderItem implements Serializable {
    private RestaurantItem restaurantItem;
    private String instructions;
    private int quantity;

    public RestaurantItem getRestaurantItem() {
        return restaurantItem;
    }

    public void setRestaurantItem(RestaurantItem restaurantItem) {
        this.restaurantItem = restaurantItem;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
