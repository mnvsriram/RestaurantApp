package app.resta.com.restaurantapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminFirestoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RatingSummary;

public class ReviewFetchService {

    private RatingSummaryAdminDaoI ratingSummaryAdminDao;

    public ReviewFetchService() {
        ratingSummaryAdminDao = new RatingSummaryAdminFirestoreDao();
    }

    public void getDataGroupByItem(int noOfDaysOld, final OnResultListener<Map<String, RatingSummary>> listener) {
        ratingSummaryAdminDao.getRatingsPerDayPerItem(noOfDaysOld, new OnResultListener<Map<Long, Map<String, RatingSummary>>>() {
            @Override
            public void onCallback(Map<Long, Map<String, RatingSummary>> summary) {
                accumulateByItem(summary, listener);
            }
        });
    }


    public Map<Double, List<RatingSummary>> generateScoreMap(Map<String, RatingSummary> ratingByItem, boolean goodScore) {
        Map<Double, List<RatingSummary>> scoreMap = new TreeMap(Collections.reverseOrder());
        if (ratingByItem != null) {
            for (String itemId : ratingByItem.keySet()) {
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

    private void accumulateByItem(Map<Long, Map<String, RatingSummary>> ratingsPerDayPerItem, final OnResultListener<Map<String, RatingSummary>> listener) {
        Map<String, RatingSummary> perItemForAllDays = new HashMap<>();
        if (ratingsPerDayPerItem != null) {
            for (Long dayOlder : ratingsPerDayPerItem.keySet()) {
                Map<String, RatingSummary> perDay = ratingsPerDayPerItem.get(dayOlder);
                if (perDay != null) {
                    for (String itemId : perDay.keySet()) {
                        RatingSummary summaryForThisItem = perDay.get(itemId);
                        RatingSummary existingSummaryForThisItem = perItemForAllDays.get(itemId);

                        //summaryForThisItem.mergeFrom(existingSummaryForThisItem);
                        if (existingSummaryForThisItem == null) {
                            perItemForAllDays.put(itemId, summaryForThisItem);
                        } else {
                            RatingSummary cumulativeSummary = RatingSummary.merge(summaryForThisItem, existingSummaryForThisItem);
                            perItemForAllDays.put(itemId, cumulativeSummary);
                        }

                    }
                }
            }
        }
        listener.onCallback(perItemForAllDays);
    }
}
