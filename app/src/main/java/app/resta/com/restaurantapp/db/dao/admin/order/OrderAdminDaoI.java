package app.resta.com.restaurantapp.db.dao.admin.order;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Order;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 06/03/2019.
 */

public interface OrderAdminDaoI {
    void placeOrder(List<OrderedItem> items, String tableNo, String notes, final OnResultListener<Order> listener);

    void modifyOrder(List<OrderedItem> items, String orderId, String comment, String tableNo, final OnResultListener<Order> listener);

    void markOrderAsComplete(String orderId, final OnResultListener<String> listener);

    void getOrdersWithItems(Date fromDate, Date toDate, final OnResultListener<List<Order>> listener);

    void addReviewsAndRatingsToOrder(List<ReviewForDish> reviewForDishes);

    void getReviewsForOrders(Set<String> orderIds, final OnResultListener<Map<String, List<ReviewForDish>>> listener);
}
