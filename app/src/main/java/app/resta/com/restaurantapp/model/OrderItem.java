package app.resta.com.restaurantapp.model;

import java.io.Serializable;

public class OrderItem implements Serializable {
    private RestaurantItem restaurantItem;
    private String instructions;
    private int quantity;
    private double totalPrice;

    public void increaseQuantity() {
        quantity++;
    }

    public void reduceQuantity() {
        quantity--;
    }

    public OrderItem(){

    }
    public OrderItem(RestaurantItem restaurantItem) {
        this.restaurantItem = restaurantItem;
        this.quantity = 1;
        this.instructions = "";
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

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
