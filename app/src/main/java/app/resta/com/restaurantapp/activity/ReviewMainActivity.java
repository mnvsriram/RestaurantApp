package app.resta.com.restaurantapp.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.ReviewFetchService;
import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminFirestoreDao;
import app.resta.com.restaurantapp.db.dao.admin.review.ReviewAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.review.ReviewAdminFirestoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.ReviewCount;
import app.resta.com.restaurantapp.util.PerformanceUtils;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class ReviewMainActivity extends BaseActivity {
    private ReviewFetchService reviewFetchService;
    private ReviewAdminDaoI reviewAdminDao;
    private RatingSummaryAdminDaoI ratingSummaryAdminDao = new RatingSummaryAdminFirestoreDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_main);
        setToolbar();
        int durationIndex = getIntent().getIntExtra("reviewMainActivity_reviewDurationPosition", 0);
        initialize();
        setSpinner(durationIndex);
        setListeners();
    }

    private void initialize() {
        reviewFetchService = new ReviewFetchService();
        reviewAdminDao = new ReviewAdminFirestoreDao();
    }

    private void setBarChart(float goodCount, float averageCount, float badCount) {
        BarChart barChart = findViewById(R.id.barChart);

        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getAxisRight().setAxisMinimum(0f);

        barChart.getAxisLeft().setTextSize(15f);
        barChart.getAxisRight().setTextSize(15f);

        barChart.getXAxis().setDrawLabels(false);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(2f, badCount));

        ArrayList<BarEntry> entries2 = new ArrayList<>();
        entries2.add(new BarEntry(4f, averageCount));

        ArrayList<BarEntry> entries3 = new ArrayList<>();
        entries3.add(new BarEntry(6f, goodCount));


        BarDataSet dataset = new BarDataSet(entries, "Bad");
        BarDataSet dataset2 = new BarDataSet(entries2, "Average");
        BarDataSet dataset3 = new BarDataSet(entries3, "Good");

        dataset.setValueTextSize(15f);
        dataset2.setValueTextSize(15f);
        dataset3.setValueTextSize(15f);

        barChart.getLegend().setTextSize(15f);

        dataset.setColor(Color.RED);
        dataset2.setColor(Color.YELLOW);
        dataset3.setColor(getResources().getColor(R.color.green));

        BarData data = new BarData(dataset, dataset2, dataset3);
        barChart.setData(data);
        barChart.animateY(1000);

    }

    private void getData(final int noOfDaysData) {
        reviewFetchService.getDataGroupByItem(noOfDaysData, new OnResultListener<Map<String, RatingSummary>>() {
            @Override
            public void onCallback(final Map<String, RatingSummary> ratingsByItem) {
                setReviewCounts(ratingsByItem);
                setGoodItems(ratingsByItem);
                setBadItems(ratingsByItem);
                setTitles(noOfDaysData);
                setRank(noOfDaysData);
                setComments();
            }
        });

    }

    private void setComments() {
        reviewAdminDao.getLatestFiveComments(new OnResultListener<String>() {
            @Override
            public void onCallback(String comments) {
                TextView recentComments = findViewById(R.id.recentComments);
                recentComments.setTypeface(Typeface.MONOSPACE);
                recentComments.setText(comments);
            }
        });
    }

    private void setTitles(int noOfDays) {
        String duration;
        if (noOfDays == 0) {
            duration = "today";
        } else {
            duration = "in last " + noOfDays + " days";
        }

        TextView topRatedItemsTitle = findViewById(R.id.topRatedItemsLabel);
        TextView lowRatedItemsTitle = findViewById(R.id.lowRatedItemsLabel);

        topRatedItemsTitle.setText("Top rated items " + duration);
        lowRatedItemsTitle.setText("Lowest rated items " + duration);
    }

    private void setReviewCounts(Map<String, RatingSummary> ratingByItem) {
        float totalGoodCount = 0f;
        float totalAverageCount = 0f;
        float totalBadCount = 0f;

        for (String itemId : ratingByItem.keySet()) {
            RatingSummary summary = ratingByItem.get(itemId);
            totalGoodCount += summary.getGoodRatingCount();
            totalAverageCount += summary.getAverageRatingCount();
            totalBadCount += summary.getBadRatingCount();
        }
        setBarChart(totalGoodCount, totalAverageCount, totalBadCount);
    }


    private void setBadItems(Map<String, RatingSummary> ratingByItem) {
        Map<Double, List<RatingSummary>> badScoreMap = reviewFetchService.generateScoreMap(ratingByItem, false);
        StringBuilder badItems = new StringBuilder();
        int noOfBadElements = 0;
        for (Double score : badScoreMap.keySet()) {
            List<RatingSummary> itemSummaries = badScoreMap.get(score);
            for (RatingSummary summary : itemSummaries) {
                int badCount = summary.getBadRatingCount();
                int averageCount = summary.getAverageRatingCount();
                if (badCount <= 0 && averageCount <= 0) {
                    break;
                }
                badItems.append(RestaurantUtil.getFormattedString(summary.getItemName(), 15)).append("  (Bad:").append(RestaurantUtil.getFormattedString(badCount + "", 2)).append(", Average:").append(RestaurantUtil.getFormattedString(averageCount + "", 2)).append(")").append("\n");
                if (++noOfBadElements == 5) {
                    break;
                }
            }
        }
        setLowRatedItems(badItems.toString());
    }

    private void setGoodItems(Map<String, RatingSummary> ratingByItem) {
        Map<Double, List<RatingSummary>> goodScoreMap = reviewFetchService.generateScoreMap(ratingByItem, true);
        StringBuilder goodItems = new StringBuilder();
        int noOfGoodElements = 0;
        for (Double score : goodScoreMap.keySet()) {
            List<RatingSummary> itemSummaries = goodScoreMap.get(score);
            for (RatingSummary summary : itemSummaries) {
                int goodCount = summary.getGoodRatingCount();
                int averageCount = summary.getAverageRatingCount();
                if (goodCount <= 0 && averageCount <= 0) {
                    break;
                }
                goodItems.append(RestaurantUtil.getFormattedString(summary.getItemName(), 15)).append("(Good:").append(RestaurantUtil.getFormattedString(goodCount + "", 2)).append(", Average:").append(RestaurantUtil.getFormattedString(averageCount + "", 2)).append(")").append("\n");
                if (noOfGoodElements++ == 5) {
                    break;
                }
            }
        }
        setTopRatedItems(goodItems.toString());
    }

    private void setTopRatedItems(String goodItems) {
        TextView goodItemsTextView = findViewById(R.id.topRatedItems);
        goodItemsTextView.setTypeface(Typeface.MONOSPACE);
        goodItemsTextView.setText(goodItems);
    }

    private void setLowRatedItems(String badItems) {
        TextView badItemsTextView = findViewById(R.id.lowRatedItems);
        badItemsTextView.setTypeface(Typeface.MONOSPACE);
        badItemsTextView.setText(badItems);
    }

    private void setSpinner(int selectedIndex) {
        final Spinner durationSpinner = findViewById(R.id.reviewsViewDurationSpinner);

        RestaurantUtil.setDurationSpinner(this, durationSpinner);

        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RatingDurationEnum ratingDurationEnum = RatingDurationEnum.of(position);
                getData(ratingDurationEnum.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                getData(0);
            }

        });

        durationSpinner.setSelection(selectedIndex);
    }

    private void setListeners() {
        final Spinner durationSpinner = findViewById(R.id.reviewsViewDurationSpinner);
        LinearLayout lowRatedItemsLayout = findViewById(R.id.lowRatedItemsLayout);
        lowRatedItemsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> intentParameters;
                intentParameters = new HashMap<>();
                intentParameters.put("topLowActivity_reviewDurationPosition", durationSpinner.getSelectedItemPosition());
                intentParameters.put("topLowActivity_reviewContentType", "low");
                authenticationController.goToLowTopRatedItemsPage(intentParameters);
            }
        });


        LinearLayout topRatedItemsLayout;
        topRatedItemsLayout = findViewById(R.id.topRatedItemsLayout);
        topRatedItemsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> intentParameters = new HashMap<>();
                intentParameters.put("topLowActivity_reviewDurationPosition", durationSpinner.getSelectedItemPosition());
                intentParameters.put("topLowActivity_reviewContentType", "top");
                authenticationController.goToLowTopRatedItemsPage(intentParameters);
            }
        });


        LinearLayout recentCommentsLayout = findViewById(R.id.recentCommentsLayout);
        recentCommentsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToReviewByItemPage(null);
            }
        });

    }

    @Override
    public void onBackPressed() {
        authenticationController.goToAdminLaunchPage();
    }

    public void showOrdersPage(View view) {
        authenticationController.goToOrderSummaryPage();
    }

    public void showGraphsPage(View view) {
        final Spinner durationSpinner = findViewById(R.id.reviewsViewDurationSpinner);
        int selectedIndex = durationSpinner.getSelectedItemPosition();

        Map<String, Object> params = new HashMap<>();
        params.put("activity_performanceGraphsDurationSpinnerIndex", selectedIndex);
        authenticationController.goToPerformanceGraphsPage(params);
    }

    public void goToReviewByItemPage(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("itemReviewDetail_fromPage", "activity_reviewMainActivity");
        authenticationController.goToItemReviewDetailsPage(params);
    }

    Map<Long, ReviewCount> ratingCountByDay = null;

    private void setRank(int noOfDaysData) {
        if (noOfDaysData == 0) {
            noOfDaysData = 10;
        }
        if (ratingCountByDay == null) {
            ratingSummaryAdminDao.getRatingsPerDayPerItem(noOfDaysData, new OnResultListener<Map<Long, Map<String, RatingSummary>>>() {
                @Override
                public void onCallback(Map<Long, Map<String, RatingSummary>> ratingByItemForAllDays) {
                    ratingCountByDay = PerformanceUtils.getRatingCountByDay(ratingByItemForAllDays);
                }
            });
        }


        Map<Long, Double> scoreMap = PerformanceUtils.getPerformanceScoreMap(ratingCountByDay, noOfDaysData);
        Double todaysScore = scoreMap.get(0);

        Map<Double, Long> rankMap = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Long, Double> entry : scoreMap.entrySet()) {
            rankMap.put(entry.getValue(), entry.getKey());
        }

        int rank = 0;
        for (Double score : rankMap.keySet()) {
            if (score == todaysScore) {
                break;
            }
            rank++;
        }
        TextView rankText = findViewById(R.id.rankText);
        rankText.setText("Today's Rank is #" + (rank + 1) + " sout of last " + noOfDaysData + " days");
    }


}