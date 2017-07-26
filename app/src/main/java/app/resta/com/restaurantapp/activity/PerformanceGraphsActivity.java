package app.resta.com.restaurantapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.ReviewFetchService;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.ReviewCount;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class PerformanceGraphsActivity extends BaseActivity {
    private ReviewFetchService reviewFetchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perforamnce_graphs);
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

        int durationIndex = getIntent().getIntExtra("activity_performanceGraphsDurationSpinnerIndex", 0);
        final Spinner durationSpinner = (Spinner) findViewById(R.id.performanceGraphsDurationSpinner);
        setSpinner(durationSpinner, durationIndex);
        setDetailsButton();
    }

    private void setDetailsButton() {
        Button button = (Button) findViewById(R.id.detailsPerformanceButton);
        button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    createGraphByReviewType(ratingCountByDay);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    Spinner spinner = (Spinner) findViewById(R.id.performanceGraphsDurationSpinner);
                    int position = spinner.getSelectedItemPosition();
                    if (position == 0) position = 1;
                    RatingDurationEnum ratingDurationEnum = RatingDurationEnum.of(position);
                    getData(ratingDurationEnum.getValue());
                }
                return true;
            }
        });
    }

    Map<Integer, ReviewCount> ratingCountByDay = null;
    int noOfDaysDataSelected = -1;

    private void getData(int noOfDaysData) {
        if (ratingCountByDay == null || noOfDaysDataSelected != noOfDaysData) {
            Map<Integer, Map<Long, RatingSummary>> ratingByDayAndByItem = reviewFetchService.getDataGroupByDay(noOfDaysData);
            //this wil be used for one graphs
            ratingCountByDay = getRatingCountByDay(ratingByDayAndByItem);
            //this will be used by another graph
            noOfDaysDataSelected = noOfDaysData;
        }


/*
        ratingCountByDay.put(72, new ReviewCount(20, 12, 6));
        ratingCountByDay.put(65, new ReviewCount(9, 6, 32));
        ratingCountByDay.put(58, new ReviewCount(16, 6, 21));
        ratingCountByDay.put(56, new ReviewCount(16, 18, 11));
        ratingCountByDay.put(50, new ReviewCount(11, 13, 12));
        ratingCountByDay.put(48, new ReviewCount(11, 4, 11));
        ratingCountByDay.put(46, new ReviewCount(17, 4, 11));
        ratingCountByDay.put(44, new ReviewCount(13, 4, 2));
        ratingCountByDay.put(42, new ReviewCount(3, 14, 11));


        ratingCountByDay.put(36, new ReviewCount(13, 4, 2));
        ratingCountByDay.put(34, new ReviewCount(20, 12, 6));
        ratingCountByDay.put(32, new ReviewCount(9, 6, 32));
        ratingCountByDay.put(28, new ReviewCount(16, 6, 21));
        ratingCountByDay.put(26, new ReviewCount(16, 18, 11));
        ratingCountByDay.put(21, new ReviewCount(11, 13, 12));
        ratingCountByDay.put(17, new ReviewCount(11, 4, 11));
        ratingCountByDay.put(15, new ReviewCount(17, 4, 11));
        ratingCountByDay.put(13, new ReviewCount(13, 4, 2));
        ratingCountByDay.put(11, new ReviewCount(3, 14, 11));


        ratingCountByDay.put(10, new ReviewCount(13, 4, 2));
        ratingCountByDay.put(9, new ReviewCount(20, 12, 6));
        ratingCountByDay.put(8, new ReviewCount(9, 6, 32));
        ratingCountByDay.put(7, new ReviewCount(16, 6, 21));
        ratingCountByDay.put(6, new ReviewCount(16, 18, 11));
        ratingCountByDay.put(5, new ReviewCount(11, 13, 12));
        ratingCountByDay.put(4, new ReviewCount(11, 4, 11));
        ratingCountByDay.put(3, new ReviewCount(17, 4, 11));
        ratingCountByDay.put(2, new ReviewCount(13, 4, 2));
        ratingCountByDay.put(1, new ReviewCount(9, 14, 11));
        ratingCountByDay.put(0, new ReviewCount(9, 14, 11));

*/
        Map<Integer, Double> scoreMap = getPerformanceScoreMap(ratingCountByDay);

        //createGraphByReviewType(ratingCountByDay, noOfDaysData);
        createOverAllPerformanceGraph(scoreMap, noOfDaysData);
    }

    int noOfYPartitions = 10;

    private ArrayList<Entry> getEntriesPerReviewType(Map<Integer, ReviewCount> entries, ReviewEnum reviewEnum) {
        ArrayList<Entry> entriesByType = new ArrayList<>();
        for (Integer daysOld : entries.keySet()) {

            int count = 0;
            if (reviewEnum.equals(ReviewEnum.BAD)) {
                count = entries.get(daysOld).getBadReviewCount();
            } else if (reviewEnum.equals(ReviewEnum.GOOD)) {
                count = entries.get(daysOld).getGoodReviewCount();
            } else if (reviewEnum.equals(ReviewEnum.AVERAGE)) {
                count = entries.get(daysOld).getAverageReviewCount();
            }
            entriesByType.add(new Entry(new Float(noOfDaysDataSelected - daysOld), count));
        }
        return entriesByType;
    }

    private void createGraphByReviewType(Map<Integer, ReviewCount> ratingCountByDay) {
        TextView heading = (TextView) findViewById(R.id.overallPerformanceGraphTitle);
        heading.setText("Detailed Performance");
        LineChart lineChart = (LineChart) findViewById(R.id.performanceGraphOverAll);
        ArrayList<Entry> goodEntries = getEntriesPerReviewType(ratingCountByDay, ReviewEnum.GOOD);
        ArrayList<Entry> averageEntries = getEntriesPerReviewType(ratingCountByDay, ReviewEnum.AVERAGE);
        ArrayList<Entry> badEntries = getEntriesPerReviewType(ratingCountByDay, ReviewEnum.BAD);


        Collections.sort(badEntries, new EntryXComparator());
        Collections.sort(goodEntries, new EntryXComparator());
        Collections.sort(averageEntries, new EntryXComparator());


        LineDataSet goodDataset = new LineDataSet(goodEntries, "Good Reviews");
        LineDataSet averageDataset = new LineDataSet(averageEntries, "Average Reviews");
        LineDataSet badDataset = new LineDataSet(badEntries, "Bad Reviews");


        badDataset.setColor(Color.RED);
        averageDataset.setColor(Color.YELLOW);
        goodDataset.setColor(getResources().getColor(R.color.green));


        lineChart.getAxisLeft().setValueFormatter(null);
        lineChart.getAxisRight().setValueFormatter(null);


        badDataset.setFillColor(Color.RED);
        averageDataset.setFillColor(Color.YELLOW);
        goodDataset.setFillColor(getResources().getColor(R.color.green));

        goodDataset.setMode(LineDataSet.Mode.LINEAR);
        averageDataset.setMode(LineDataSet.Mode.LINEAR);
        badDataset.setMode(LineDataSet.Mode.LINEAR);


        badDataset.setDrawCircles(false);
        badDataset.setLineWidth(5f);
        badDataset.setDrawValues(false);

        averageDataset.setDrawCircles(false);
        averageDataset.setLineWidth(5f);
        averageDataset.setDrawValues(false);

        goodDataset.setDrawCircles(false);
        goodDataset.setLineWidth(5f);
        goodDataset.setDrawValues(false);

        LineData data = new LineData(goodDataset, averageDataset, badDataset);
        lineChart.setData(data);
        lineChart.animateX(1000);
        lineChart.animateY(1000);
    }

    private ArrayList<Entry> createEntriesList(Map<Integer, Double> scoreMap, int noOfDaysQueried) {
        ArrayList<Entry> entries = new ArrayList<>();
        if (scoreMap != null) {
            for (Integer daysOld : scoreMap.keySet()) {
                entries.add(new Entry(new Float(noOfDaysQueried - daysOld), new Float(scoreMap.get(daysOld))));
            }
        }
        return entries;
    }

    public class YAxisValueFormatter implements IAxisValueFormatter {
        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            String performance = "";
            if (value > 0) {
                if (value > 30) {
                    performance = "Good";
                } else if (value > 60) {
                    performance = "Very Good";
                } else if (value > 100) {
                    performance = "The Best";
                } else if (value > 200) {
                    performance = "No 1";
                } else {
                    performance = "Mediocre";
                }
            } else if (value < 0) {
                if (value < -20) {
                    performance = "Very Bad";
                } else if (value <= -150) {
                    performance = "Worse";
                } else {
                    performance = "Bad";
                }
            } else if (value == 0f) {
                performance = "Neutral";
            }
            return performance;
        }
    }

    private void createOverAllPerformanceGraph(Map<Integer, Double> scoreMap, final int noOfDaysQueried) {

        TextView heading = (TextView) findViewById(R.id.overallPerformanceGraphTitle);
        heading.setText("Overall Performance");

        LineChart lineChart = (LineChart) findViewById(R.id.performanceGraphOverAll);


        ArrayList<Entry> entries = createEntriesList(scoreMap, noOfDaysQueried);

        Collections.sort(entries, new EntryXComparator());

        LineDataSet dataset = new LineDataSet(entries, "Performance");


        dataset.setColor(Color.BLUE);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int result = Math.round(noOfDaysQueried - value);
                String label = "";
                if (result == 0) {
                    label = "Today";
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.DATE, -result);

                    return DateUtil.getDateString(calendar.getTime(), "dd MMM");
                    //label = result + " days back";
                }
                return label;
            }
        });

        xAxis.setAxisLineColor(Color.RED);


        YAxis yAxisLeft = lineChart.getAxisLeft();
        YAxis yAxisRight = lineChart.getAxisRight();

        YAxisValueFormatter yAxisValueFormatter = new YAxisValueFormatter();
        yAxisLeft.setValueFormatter(yAxisValueFormatter);
        yAxisRight.setValueFormatter(yAxisValueFormatter);
        yAxisLeft.setTextSize(18f);
        yAxisRight.setTextSize(18f);

        xAxis.setTextSize(12f);
        dataset.setMode(LineDataSet.Mode.LINEAR);
        dataset.setDrawCircles(false);
        dataset.setLineWidth(5f);
        dataset.setDrawValues(false);


        lineChart.animateX(1000);
        lineChart.animateY(1000);

        LineData data = new LineData(dataset);
        lineChart.setData(data);
    }

    private Map<Integer, Double> getPerformanceScoreMap(Map<Integer, ReviewCount> ratingCountByDay) {
        Map<Integer, Double> scoreMapByDay = new HashMap<>();
        if (ratingCountByDay != null) {
            for (Integer daysOld : ratingCountByDay.keySet()) {
                scoreMapByDay.put(daysOld, getDayPerformanceScore(ratingCountByDay.get(daysOld)));
            }
        }
        return scoreMapByDay;
    }

    private Double getDayPerformanceScore(ReviewCount count) {
        Double score = 0d;
        if (count != null) {
            score = count.getGoodReviewCount() * 1 + (count.getAverageReviewCount() * 0.5) - count.getBadReviewCount();
        }
        return score;
    }

    private Map<Integer, ReviewCount> getRatingCountByDay(Map<Integer, Map<Long, RatingSummary>> ratingByDayAndByItem) {
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

    private void setSpinner(Spinner durationSpinner, int selectedIndex) {
        RestaurantUtil.setDurationSpinner(this, durationSpinner);

        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    position = 1;
                }
                RatingDurationEnum ratingDurationEnum = RatingDurationEnum.of(position);
                getData(ratingDurationEnum.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                RatingDurationEnum ratingDurationEnum = RatingDurationEnum.of(1);
                getData(ratingDurationEnum.getValue());
            }

        });

        durationSpinner.setSelection(selectedIndex);
    }


    @Override
    public void onBackPressed() {
        authenticationController.goToAdminLaunchPage();
    }
}