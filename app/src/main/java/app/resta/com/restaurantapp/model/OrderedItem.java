package app.resta.com.restaurantapp.model;

import java.io.Serializable;

public class OrderedItem implements Serializable {
    private long orderId;
    private String orderStatus;
    private String itemName;
    private String parentName;
    private long itemId;
    private long review;
    private int quantity;
    private double price;
    private String instructions;
    private double totalPrice;
    private String orderDate;

    public OrderedItem() {
    }

    public OrderedItem(RestaurantItem restaurantItem) {
        this.itemName = restaurantItem.getName();
        this.itemId = restaurantItem.getId();
        try {
            this.price = Double.parseDouble(restaurantItem.getPrice());
        } catch (Exception e) {
            this.price = 0;
        }
        this.quantity = 1;
        this.instructions = "";
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void reduceQuantity() {
        quantity--;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getReview() {
        return review;
    }

    public void setReview(long review) {
        this.review = review;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
