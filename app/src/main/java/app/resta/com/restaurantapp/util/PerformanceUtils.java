package app.resta.com.restaurantapp.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.ReviewCount;

/**
 * Created by Sriram on 26/09/2017.
 */
public class PerformanceUtils {

    public static Map<Long, ReviewCount> getRatingCountByDay(Map<Long, Map<String, RatingSummary>> ratingByDayAndByItem) {
        Map<Long, ReviewCount> ratingCountByDay = new TreeMap<>();
        if (ratingByDayAndByItem != null) {
            for (Long daysOld : ratingByDayAndByItem.keySet()) {
                Map<String, RatingSummary> ratingForAllItemsOnOneDay = ratingByDayAndByItem.get(daysOld);
                int goodCount = 0;
                int badCount = 0;
                int averageCount = 0;
                if (ratingForAllItemsOnOneDay != null) {
                    for (String itemId : ratingForAllItemsOnOneDay.keySet()) {
                        RatingSummary ratingSummaryForOneItem = ratingForAllItemsOnOneDay.get(itemId);
                        goodCount += ratingSummaryForOneItem.getGoodRatingCount();
                        badCount += ratingSummaryForOneItem.getBadRatingCount();
                        averageCount += ratingSummaryForOneItem.getAverageRatingCount();
                    }
                }
                ratingCountByDay.put(daysOld, new ReviewCount(goodCount, averageCount, badCount));
            }
        }
        return ratingCountByDay;
    }

    public static Map<Long, Double> getPerformanceScoreMap(Map<Long, ReviewCount> ratingCountByDay, int forNoOfDays) {
        Map<Long, Double> scoreMapByDay = new HashMap<>();
        if (ratingCountByDay != null) {
            for (Long daysOld : ratingCountByDay.keySet()) {
                if (daysOld <= forNoOfDays || forNoOfDays != -1) {
                    scoreMapByDay.put(daysOld, getDayPerformanceScore(ratingCountByDay.get(daysOld)));
                }

            }
        }
        return scoreMapByDay;
    }

    public static Double getDayPerformanceScore(ReviewCount count) {
        Double score = 0d;
        if (count != null) {
            score = count.getGoodReviewCount() * 1 + (count.getAverageReviewCount() * 0.5) - count.getBadReviewCount();
        }
        return score;
    }


}
