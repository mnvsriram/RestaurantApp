package app.resta.com.restaurantapp.db.dao.admin.ratingSummary;

import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 06/03/2019.
 */

public interface RatingSummaryAdminDaoI {
    void modifyRatingSummary(List<ReviewForDish> reviewForDishes);

    void getRatingsPerDayPerItem(int noOfDaysOld, final OnResultListener<Map<Long, Map<String, RatingSummary>>> listener);
}
