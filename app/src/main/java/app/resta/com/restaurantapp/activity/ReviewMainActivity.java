package app.resta.com.restaurantapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.ReviewFetchService;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class ReviewMainActivity extends BaseActivity {
    private ReviewFetchService reviewFetchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_main);
        reviewFetchService = new ReviewFetchService();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        int durationIndex = getIntent().getIntExtra("reviewMainActivity_reviewDurationPosition", 0);
        final Spinner durationSpinner = (Spinner) findViewById(R.id.reviewsViewDurationSpinner);
        setSpinner(durationSpinner, durationIndex);
        setListeners();
    }

    private void setBarChart(float goodCount, float averageCount, float badCount) {
        BarChart barChart = (BarChart) findViewById(R.id.barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(2f, badCount));

        ArrayList<BarEntry> entries2 = new ArrayList<>();
        entries2.add(new BarEntry(4f, averageCount));

        ArrayList<BarEntry> entries3 = new ArrayList<>();
        entries3.add(new BarEntry(6f, goodCount));


        BarDataSet dataset = new BarDataSet(entries, "Bad");
        BarDataSet dataset2 = new BarDataSet(entries2, "Average");
        BarDataSet dataset3 = new BarDataSet(entries3, "Good");


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
    }

    private void setTitles(int noOfDays) {
        String duration = "";
        if (noOfDays == 0) {
            duration = "today";
        } else if (noOfDays == 7) {
            duration = "in last 7 days";
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
                badItems += summary.getItemName() + "(Bad:" + badCount + ", Average:" + averageCount + ")" + "\n";
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
                goodItems += summary.getItemName() + "(Good:" + goodCount + ", Average:" + averageCount + ")" + "\n";
                if (noOfGoodElements++ == 5) {
                    break;
                }
            }
        }
        setTopRatedItems(goodItems);
    }

    private void setTopRatedItems(String goodItems) {
        TextView goodItemsTextView = (TextView) findViewById(R.id.topRatedItems);
        goodItemsTextView.setText(goodItems);
    }

    private void setLowRatedItems(String badItems) {
        TextView badItemsTextView = (TextView) findViewById(R.id.lowRatedItems);
        badItemsTextView.setText(badItems);
    }

    private void setSpinner(Spinner durationSpinner, int selectedIndex) {
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
                Toast.makeText(MyApplication.getAppContext(), "Comment", Toast.LENGTH_LONG).show();
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

}