package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

/**
 * Created by Sriram on 06/03/2019.
 */

public class Order implements Comparable {

    public static final String FIRESTORE_TABLE_NO_KEY = "tableNumber";
    public static final String FIRESTORE_INSTRUCTIONS_KEY = "instructions";
    public static final String FIRESTORE_PRICE_KEY = "totalPrice";
    public static final String FIRESTORE_ACTIVE_KEY = "isActive";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    private List<OrderedItem> items;
    private String tableNumber;
    private String instructions;
    private String orderId;
    private String totalPrice;
    private String active;

    private Date orderCreatedAt;

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public List<OrderedItem> getItems() {
        return items;
    }

    public void setItems(List<OrderedItem> items) {
        this.items = items;
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String calculateTotalPrice() {
        String totalPrice = "";
        for (OrderedItem item : items) {
            totalPrice += item.getPrice();
        }
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getOrderCreatedAt() {
        return orderCreatedAt;
    }

    public void setOrderCreatedAt(Date orderCreatedAt) {
        this.orderCreatedAt = orderCreatedAt;
    }

    public static Order prepareOrder(QueryDocumentSnapshot documentSnapshot) {
        return getOrder(documentSnapshot);
    }


    public static Order prepareOrder(DocumentSnapshot documentSnapshot) {
        return getOrder(documentSnapshot);
    }

    @NonNull
    private static Order getOrder(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        Order order = new Order();
        order.setOrderId(documentSnapshot.getId());
        order.setTotalPrice(FireStoreUtil.getString(keyValueMap, FIRESTORE_PRICE_KEY));
        order.setTableNumber(FireStoreUtil.getString(keyValueMap, FIRESTORE_TABLE_NO_KEY));
        order.setInstructions(FireStoreUtil.getString(keyValueMap, FIRESTORE_INSTRUCTIONS_KEY));
        order.setActive(FireStoreUtil.getString(keyValueMap, FIRESTORE_ACTIVE_KEY));
        order.setOrderCreatedAt(documentSnapshot.getDate(FIRESTORE_CREATED_AT_KEY));
        return order;
    }

    @Override
    public int compareTo(@NonNull Object o) {

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(this.getOrderCreatedAt());

        Calendar cal2 = Calendar.getInstance();
        Order o2 = (Order) o;
        cal2.setTime(o2.getOrderCreatedAt());


        int compare =
                cal1.compareTo(cal2);
        if (compare == 0)
            return 1;
        else
            return compare;
    }
}
