package app.resta.com.restaurantapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import app.resta.com.restaurantapp.R;

public class ReviewMainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_main);

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


        BarChart barChart = (BarChart) findViewById(R.id.barChart);

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(2f, 2));

        ArrayList<BarEntry> entries2 = new ArrayList<>();
        entries2.add(new BarEntry(4f, 8));

        ArrayList<BarEntry> entries3 = new ArrayList<>();
        entries3.add(new BarEntry(6f, 4));


        BarDataSet dataset = new BarDataSet(entries, "Bad");
        BarDataSet dataset2 = new BarDataSet(entries2, "Average");
        BarDataSet dataset3 = new BarDataSet(entries3, "Good");


        dataset.setColor(getResources().getColor(R.color.red));
        dataset2.setColor(Color.YELLOW);
        dataset3.setColor(getResources().getColor(R.color.green));

        BarData data = new BarData(dataset, dataset2, dataset3);
        barChart.setData(data);
        barChart.animateY(5000);

    }

    @Override
    public void onBackPressed() {
        authenticationController.goToAdminLaunchPage();
    }

    public void showOrdersPage(View view) {
        authenticationController.goToOrderSummaryPage();
    }

}