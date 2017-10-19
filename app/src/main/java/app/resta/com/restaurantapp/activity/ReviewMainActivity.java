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
import app.resta.com.restaurantapp.db.dao.ReviewDao;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.ReviewCount;
import app.resta.com.restaurantapp.util.PerformanceUtils;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class ReviewMainActivity extends BaseActivity {
    private ReviewFetchService reviewFetchService;
    private ReviewDao reviewDao;


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
        reviewDao = new ReviewDao();
    }

    private void setBarChart(float goodCount, float averageCount, float badCount) {
        BarChart barChart = (BarChart) findViewById(R.id.barChart);

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

    private void getData(int noOfDaysData) {
        Map<Long, RatingSummary> ratingByItem = reviewFetchService.getDataGroupByItem(noOfDaysData);
        setReviewCounts(ratingByItem);
        setGoodItems(ratingByItem);
        setBadItems(ratingByItem);
        setTitles(noOfDaysData);
        setRank(noOfDaysData);
        setComments();
    }

    private void setComments() {
        String comments = reviewDao.getLatestFiveComments();
        TextView recentComments = (TextView) findViewById(R.id.recentComments);
        recentComments.setTypeface(Typeface.MONOSPACE);
        recentComments.setText(comments);
    }

    private void setTitles(int noOfDays) {
        String duration = "";
        if (noOfDays == 0) {
            duration = "today";
        } else {
            duration = "in last " + noOfDays + " days";
        }

        TextView topRatedItemsTitle = (TextView) findViewById(R.id.topRatedItemsLabel);
        TextView lowRatedItemsTitle = (TextView) findViewById(R.id.lowRatedItemsLabel);

        topRatedItemsTitle.setText("Top rated items " + duration);
        lowRatedItemsTitle.setText("Lowest rated items " + duration);
    }

    private void setReviewCounts(Map<Long, RatingSummary> ratingByItem) {
        float totalGoodCount = 0f;
        float totalAverageCount = 0f;
        float totalBadCount = 0f;

        for (Long itemId : ratingByItem.keySet()) {
            RatingSummary summary = ratingByItem.get(itemId);
            totalGoodCount += summary.getGoodRatingCount();
            totalAverageCount += summary.getAverageRatingCount();
            totalBadCount += summary.getBadRatingCount();
        }
        setBarChart(totalGoodCount, totalAverageCount, totalBadCount);
    }


    private void setBadItems(Map<Long, RatingSummary> ratingByItem) {
        Map<Double, List<RatingSummary>> badScoreMap = reviewFetchService.generateScoreMap(ratingByItem, false);
        String badItems = "";
        int noOfBadElements = 0;
        for (Double score : badScoreMap.keySet()) {
            List<RatingSummary> itemSummaries = badScoreMap.get(score);
            for (RatingSummary summary : itemSummaries) {
                int badCount = summary.getBadRatingCount();
                int averageCount = summary.getAverageRatingCount();
                if (badCount <= 0 && averageCount <= 0) {
                    break;
                }
                badItems += RestaurantUtil.getFormattedString(summary.getItemName(), 15) + "  (Bad:" + RestaurantUtil.getFormattedString(badCount + "", 2) + ", Average:" + RestaurantUtil.getFormattedString(averageCount + "", 2) + ")" + "\n";
                if (++noOfBadElements == 5) {
                    break;
                }
            }
        }
        setLowRatedItems(badItems);
    }

    private void setGoodItems(Map<Long, RatingSummary> ratingByItem) {
        Map<Double, List<RatingSummary>> goodScoreMap = reviewFetchService.generateScoreMap(ratingByItem, true);
        String goodItems = "";
        int noOfGoodElements = 0;
        for (Double score : goodScoreMap.keySet()) {
            List<RatingSummary> itemSummaries = goodScoreMap.get(score);
            for (RatingSummary summary : itemSummaries) {
                int goodCount = summary.getGoodRatingCount();
                int averageCount = summary.getAverageRatingCount();
                if (goodCount <= 0 && averageCount <= 0) {
                    break;
                }
                goodItems += RestaurantUtil.getFormattedString(summary.getItemName(), 15) + "(Good:" + RestaurantUtil.getFormattedString(goodCount + "", 2) + ", Average:" + RestaurantUtil.getFormattedString(averageCount + "", 2) + ")" + "\n";
                if (noOfGoodElements++ == 5) {
                    break;
                }
            }
        }
        setTopRatedItems(goodItems);
    }

    private void setTopRatedItems(String goodItems) {
        TextView goodItemsTextView = (TextView) findViewById(R.id.topRatedItems);
        goodItemsTextView.setTypeface(Typeface.MONOSPACE);
        goodItemsTextView.setText(goodItems);
    }

    private void setLowRatedItems(String badItems) {
        TextView badItemsTextView = (TextView) findViewById(R.id.lowRatedItems);
        badItemsTextView.setTypeface(Typeface.MONOSPACE);
        badItemsTextView.setText(badItems);
    }

    private void setSpinner(int selectedIndex) {
        final Spinner durationSpinner = (Spinner) findViewById(R.id.reviewsViewDurationSpinner);

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
        final Spinner durationSpinner = (Spinner) findViewById(R.id.reviewsViewDurationSpinner);
        LinearLayout lowRatedItemsLayout = (LinearLayout) findViewById(R.id.lowRatedItemsLayout);
        lowRatedItemsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> intentParameters = new HashMap<String, Object>();
                intentParameters.put("topLowActivity_reviewDurationPosition", durationSpinner.getSelectedItemPosition());
                intentParameters.put("topLowActivity_reviewContentType", "low");
                authenticationController.goToLowTopRatedItemsPage(intentParameters);
            }
        });


        LinearLayout topRatedItemsLayout = (LinearLayout) findViewById(R.id.topRatedItemsLayout);
        topRatedItemsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> intentParameters = new HashMap<String, Object>();
                intentParameters.put("topLowActivity_reviewDurationPosition", durationSpinner.getSelectedItemPosition());
                intentParameters.put("topLowActivity_reviewContentType", "top");
                authenticationController.goToLowTopRatedItemsPage(intentParameters);
            }
        });


        LinearLayout recentCommentsLayout = (LinearLayout) findViewById(R.id.recentCommentsLayout);
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
        final Spinner durationSpinner = (Spinner) findViewById(R.id.reviewsViewDurationSpinner);
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

    Map<Integer, ReviewCount> ratingCountByDay = null;
    int noOfDaysDataSelected = -1;

    private void setRank(int noOfDaysData) {
        if (noOfDaysData == 0) {
            noOfDaysData = 10;
        }
        if (ratingCountByDay == null || noOfDaysDataSelected < noOfDaysData) {
            Map<Integer, Map<Long, RatingSummary>> ratingByDayAndByItem = reviewFetchService.getDataGroupByDay(noOfDaysData);
            ratingCountByDay = PerformanceUtils.getRatingCountByDay(ratingByDayAndByItem);
            noOfDaysDataSelected = noOfDaysData;
        }


        Map<Integer, Double> scoreMap = PerformanceUtils.getPerformanceScoreMap(ratingCountByDay, noOfDaysData);
        Double todaysScore = scoreMap.get(0);

        Map<Double, Integer> rankMap = new TreeMap<>(Collections.reverseOrder());
        for (Map.Entry<Integer, Double> entry : scoreMap.entrySet()) {
            rankMap.put(entry.getValue(), entry.getKey());
        }

        int rank = 0;
        for (Double score : rankMap.keySet()) {
            if (score == todaysScore) {
                break;
            }
            rank++;
        }
        TextView rankText = (TextView) findViewById(R.id.rankText);
        rankText.setText("Today's Rank is #" + (rank + 1) + " sout of last " + noOfDaysData + " days");
    }


}