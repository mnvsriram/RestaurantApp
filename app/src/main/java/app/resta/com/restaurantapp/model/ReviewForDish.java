package app.resta.com.restaurantapp.model;

import java.io.Serializable;

/**
 * Created by Sriram on 23/06/2017.
 */
public class ReviewForDish implements Serializable {
    private RestaurantItem item;
    private ReviewEnum review;
    private long orderId;
    private String reviewText;


    public ReviewForDish() {

    }

    public ReviewForDish(RestaurantItem item, long orderId) {
        this.item = item;
        this.orderId = orderId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public RestaurantItem getItem() {
        return item;
    }

    public void setItem(RestaurantItem item) {
        this.item = item;
    }

    public ReviewEnum getReview() {
        return review;
    }

    public void setReview(ReviewEnum review) {
        this.review = review;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReviewForDish that = (ReviewForDish) o;

        if (orderId != that.orderId) return false;
        return item != null ? item.equals(that.item) : that.item == null;

    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + (int) (orderId ^ (orderId >>> 32));
        return result;
    }
}
