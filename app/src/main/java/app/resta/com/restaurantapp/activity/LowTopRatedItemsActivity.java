package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.OrderRatingLowTopView;
import app.resta.com.restaurantapp.controller.ReviewFetchService;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class LowTopRatedItemsActivity extends BaseActivity {

    ReviewFetchService reviewFetchService;
    private boolean displayTopItems = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_top_low);

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
        reviewFetchService = new ReviewFetchService();
        int durationIndex = getIntent().getIntExtra("topLowActivity_reviewDurationPosition", 0);
        String contentType = getIntent().getStringExtra("topLowActivity_reviewContentType");
        if (contentType != null && contentType.equalsIgnoreCase("top")) {
            displayTopItems = true;
        }
        setSpinner(durationIndex);
    }

    private void setSpinner(int selectedIndex) {
        Spinner durationSpinner = (Spinner) findViewById(R.id.ratingTopLowDurationSpinner);
        RestaurantUtil.setDurationSpinner(this, durationSpinner);
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RatingDurationEnum ratingDurationEnum = RatingDurationEnum.of(position);
                buildTable(ratingDurationEnum.getValue());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                buildTable(0);
            }

        });

        durationSpinner.setSelection(selectedIndex);
    }

    public View.OnClickListener rowOnclickListener = new View.OnClickListener() {
        public void onClick(View v) {
            Long itemId = (Long) v.getTag();
            Spinner spinner = (Spinner) findViewById(R.id.ratingTopLowDurationSpinner);
            int position = spinner.getSelectedItemPosition();

            Map<String, Object> params = new HashMap<>();
            params.put("itemReviewDetail_itemId", itemId);
            params.put("itemReviewDetail_reviewDurationPosition", position);
            authenticationController.goToItemReviewDetailsPage(params);
        }
    };

    @Override
    public void onBackPressed() {
        Spinner durationSpinner = (Spinner) findViewById(R.id.ratingTopLowDurationSpinner);
        Map<String, Object> params = new HashMap<>();
        params.put("reviewMainActivity_reviewDurationPosition", durationSpinner.getSelectedItemPosition());
        authenticationController.goToReviewsPage(params);
    }


    private void buildTable(int noOfDaysOld) {
        OrderRatingLowTopView orderRatingLowTopView = new OrderRatingLowTopView(this, displayTopItems);
        Map<Long, RatingSummary> ratingByItem = reviewFetchService.getDataGroupByItem(noOfDaysOld);
        Map<Double, List<RatingSummary>> scoreMap = null;
        if (displayTopItems) {
            scoreMap = reviewFetchService.generateScoreMap(ratingByItem, true);
        } else {
            scoreMap = reviewFetchService.generateScoreMap(ratingByItem, false);
        }
        orderRatingLowTopView.createTable(scoreMap);
    }


}