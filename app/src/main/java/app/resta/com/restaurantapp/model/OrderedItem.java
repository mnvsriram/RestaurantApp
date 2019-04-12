package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

public class OrderedItem implements Serializable {
    public static final String FIRESTORE_ITEM_NAME_KEY = "itemName";
    public static final String FIRESTORE_ITEM_ID_KEY = "itemId";
    public static final String FIRESTORE_INSTRUCTIONS_KEY = "instructions";
    public static final String FIRESTORE_PRICE_KEY = "price";
    public static final String FIRESTORE_REVIEW_KEY = "review";
    public static final String FIRESTORE_RATING_KEY = "rating";
    public static final String FIRESTORE_QUANTITY_KEY = "quantity";


    private String orderId;
    private String orderComment;
    private String orderStatus;
    private String itemName;
    private String parentName;
    private String itemId;
    private String review;
    private int rating;
    private int quantity;
    private Double price;
    private String instructions;
    private double totalPrice;
    private String orderDate;
    private String menuTypeId;
    private int setMenuGroup;

    public OrderedItem() {
    }

    public OrderedItem(RestaurantItem restaurantItem) {
        this.itemName = restaurantItem.getName();
        this.itemId = restaurantItem.getId();
        try {
            this.price = Double.parseDouble(restaurantItem.getPrice());
        } catch (Exception e) {
            this.price = null;
        }
        this.quantity = 1;
        this.instructions = "";
        this.setSetMenuGroup(restaurantItem.getSetMenuGroup());
        this.setMenuTypeId(restaurantItem.getMenuTypeId());
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(String menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public int getSetMenuGroup() {
        return setMenuGroup;
    }

    public void setSetMenuGroup(int setMenuGroup) {
        this.setMenuGroup = setMenuGroup;
    }

    public String getOrderComment() {
        return orderComment;
    }

    public void setOrderComment(String orderComment) {
        this.orderComment = orderComment;
    }

    public static OrderedItem prepareOrderedItem(QueryDocumentSnapshot documentSnapshot) {
        return getOrderedItem(documentSnapshot);
    }


    public static OrderedItem prepareOrderedItem(DocumentSnapshot documentSnapshot) {
        return getOrderedItem(documentSnapshot);
    }

    @NonNull
    private static OrderedItem getOrderedItem(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        OrderedItem orderedItem = new OrderedItem();
        orderedItem.setInstructions(FireStoreUtil.getString(keyValueMap, FIRESTORE_INSTRUCTIONS_KEY));
        orderedItem.setItemId(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_ID_KEY));
        orderedItem.setItemName(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_NAME_KEY));
        orderedItem.setPrice(FireStoreUtil.getDouble(keyValueMap, FIRESTORE_PRICE_KEY));
        orderedItem.setQuantity(FireStoreUtil.getInt(keyValueMap, FIRESTORE_QUANTITY_KEY));
        orderedItem.setRating(FireStoreUtil.getInt(keyValueMap, FIRESTORE_RATING_KEY));
        orderedItem.setReview(FireStoreUtil.getString(keyValueMap, FIRESTORE_REVIEW_KEY));
        return orderedItem;
    }

}
