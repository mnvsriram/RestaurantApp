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

    public static Map<Integer, ReviewCount> getRatingCountByDay(Map<Integer, Map<Long, RatingSummary>> ratingByDayAndByItem) {
        Map<Integer, ReviewCount> ratingCountByDay = new TreeMap<>();
        if (ratingByDayAndByItem != null) {
            for (Integer daysOld : ratingByDayAndByItem.keySet()) {
                Map<Long, RatingSummary> ratingForAllItemsOnOneDay = ratingByDayAndByItem.get(daysOld);
                int goodCount = 0;
                int badCount = 0;
                int averageCount = 0;
                if (ratingForAllItemsOnOneDay != null) {
                    for (Long itemId : ratingForAllItemsOnOneDay.keySet()) {
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

    public static Map<Integer, Double> getPerformanceScoreMap(Map<Integer, ReviewCount> ratingCountByDay, int forNoOfDays) {
        Map<Integer, Double> scoreMapByDay = new HashMap<>();
        if (ratingCountByDay != null) {
            for (Integer daysOld : ratingCountByDay.keySet()) {
                if (daysOld <= forNoOfDays || forNoOfDays != -1) {
                    scoreMapByDay.put(daysOld, getDayPerformanceScore(ratingCountByDay.get(daysOld)));
                }

            }
        }
        return scoreMapByDay;
    }

    private static Double getDayPerformanceScore(ReviewCount count) {
        Double score = 0d;
        if (count != null) {
            score = count.getGoodReviewCount() * 1 + (count.getAverageReviewCount() * 0.5) - count.getBadReviewCount();
        }
        return score;
    }

}
