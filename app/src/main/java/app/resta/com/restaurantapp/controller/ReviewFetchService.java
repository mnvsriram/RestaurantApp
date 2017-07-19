package app.resta.com.restaurantapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import app.resta.com.restaurantapp.db.dao.RatingSummaryDao;
import app.resta.com.restaurantapp.model.RatingSummary;

public class ReviewFetchService {

    private RatingSummaryDao ratingSummaryDao;

    public ReviewFetchService() {
        ratingSummaryDao = new RatingSummaryDao();
    }

    public Map<Long, RatingSummary> getDataGroupByItem(int noOfDaysOld) {
        Map<Integer, Map<Long, RatingSummary>> ratings = getDataGroupByDay(noOfDaysOld);
        return accumulateByItem(ratings);
    }


    public Map<Integer, Map<Long, RatingSummary>> getDataGroupByDay(int noOfDaysOld) {
        return ratingSummaryDao.getRatingsPerDayPerItem(noOfDaysOld);
    }


    public Map<Double, List<RatingSummary>> generateScoreMap(Map<Long, RatingSummary> ratingByItem, boolean goodScore) {
        Map<Double, List<RatingSummary>> scoreMap = new TreeMap(Collections.reverseOrder());
        if (ratingByItem != null) {
            for (long itemId : ratingByItem.keySet()) {
                double score = 0;
                if (goodScore) {
                    score = calculateGoodScore(ratingByItem.get(itemId));
                } else {
                    score = calculateBadScore(ratingByItem.get(itemId));
                }
                List<RatingSummary> ratingsForThisScore = scoreMap.get(score);
                if (ratingsForThisScore == null) {
                    ratingsForThisScore = new ArrayList<>();
                }
                ratingsForThisScore.add(ratingByItem.get(itemId));
                scoreMap.put(score, ratingsForThisScore);
            }
        }
        return scoreMap;
    }

    private Double calculateGoodScore(RatingSummary summary) {
        Integer goodCount = summary.getGoodRatingCount();
        Integer averageCount = summary.getAverageRatingCount();
        double score = (goodCount * 1) + (0.5 * averageCount);
        return score;
    }

    private Double calculateBadScore(RatingSummary summary) {
        Integer badCount = summary.getBadRatingCount();
        Integer averageCount = summary.getAverageRatingCount();
        double score = (badCount * 1) + (0.2 * averageCount);
        return score;
    }

    private Map<Long, RatingSummary> accumulateByItem(Map<Integer, Map<Long, RatingSummary>> ratingsPerDayPerItem) {
        Map<Long, RatingSummary> perItemForAllDays = new HashMap<>();
        if (ratingsPerDayPerItem != null) {
            for (Integer dayOlder : ratingsPerDayPerItem.keySet()) {
                Map<Long, RatingSummary> perDay = ratingsPerDayPerItem.get(dayOlder);
                if (perDay != null) {

                    for (Long itemId : perDay.keySet()) {
                        RatingSummary summaryForThisItem = perDay.get(itemId);
                        RatingSummary existingSummaryForThisItem = perItemForAllDays.get(itemId);
                        if (existingSummaryForThisItem == null) {
                            perItemForAllDays.put(itemId, perDay.get(itemId));
                        } else {
                            summaryForThisItem.mergeFrom(perDay.get(itemId));
                            perItemForAllDays.put(itemId, summaryForThisItem);
                        }

                    }
                }
            }
        }
        return perItemForAllDays;
    }
}
