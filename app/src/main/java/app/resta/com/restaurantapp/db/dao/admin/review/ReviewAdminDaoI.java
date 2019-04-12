package app.resta.com.restaurantapp.db.dao.admin.review;

import java.util.List;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 06/03/2019.
 */

public interface ReviewAdminDaoI {
    void addReviews(List<ReviewForDish> reviewForDishes);

    void addReview(ReviewForDish reviewForDish, final OnResultListener<ReviewForDish> listener);

    void getLatestFiveComments(final OnResultListener<String> listener);

}
