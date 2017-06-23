package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Sriram on 23/06/2017.
 */
public class ReviewForDish implements Serializable {
    private RestaurantItem item;
    private ReviewEnum review;

    public ReviewForDish(RestaurantItem item) {
        this.item = item;
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
}
