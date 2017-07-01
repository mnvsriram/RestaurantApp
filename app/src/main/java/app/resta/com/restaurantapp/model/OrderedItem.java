package app.resta.com.restaurantapp.model;

/**
 * Created by Sriram on 01/07/2017.
 */
public class OrderedItem {
    private long orderId;
    private String itemName;
    private long itemId;
    private long review;
    private long quantity;

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
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
}
