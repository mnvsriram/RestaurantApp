package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Sriram on 23/06/2017.
 */
public class ReviewForOrder implements Serializable {
    private Set<ReviewForDish> reviews;
    private String orderId;

    public ReviewForOrder(List<OrderedItem> items, String orderId) {
        reviews = new HashSet<>();
        for (OrderedItem item : items) {
            RestaurantItem restItem = new RestaurantItem(item);
            ReviewForDish reviewForDish = new ReviewForDish(restItem, orderId);
            reviews.add(reviewForDish);
        }
        this.orderId = orderId;
    }

    public Set<ReviewForDish> getReviews() {
        return reviews;
    }

    public void setReviews(Set<ReviewForDish> reviews) {
        this.reviews = reviews;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
