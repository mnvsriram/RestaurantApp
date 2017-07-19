package app.resta.com.restaurantapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.ItemReviewDetailsView;
import app.resta.com.restaurantapp.controller.ReviewFetchService;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class ItemReviewDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ReviewFetchService reviewFetchService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_review_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        reviewFetchService = new ReviewFetchService();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        long itemId = getIntent().getLongExtra("itemReviewDetail_itemId", 0);
        int durationIndex = getIntent().getIntExtra("itemReviewDetail_reviewDurationPosition", 0);
        setFields(durationIndex, itemId);
    }

    private void setFields(int durationIndex, long itemId) {
        setSpinner(durationIndex, itemId);
        setItemNameAutoCompletion();
        setItemName(itemId);
    }

    private void setItemName(long itemId) {
        RestaurantItem itemSuggested = MenuItemDao.getAllItemsById().get(itemId);
        TextView itemNameView = (TextView) findViewById(R.id.itemReviewDetailsitemNameText);
        itemNameView.setText(itemSuggested.getName());
    }

    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
        TextView textView = (TextView) arg1;
        String itemName = textView.getText().toString();
        RestaurantItem itemSuggested = MenuItemDao.getDishes().get(itemName);
        long selectedItemId = itemSuggested.getId();
        getDetailsForItem(selectedItemId);
    }

    private void getDetailsForItem(long selectedItemId) {
        Spinner durationSpinner = (Spinner) findViewById(R.id.itemReviewDetailsDurationSpinner);
        int selectedDuration = durationSpinner.getSelectedItemPosition();
        Map<String, Object> params = new HashMap<>();
        params.put("itemReviewDetail_itemId", selectedItemId);
        params.put("itemReviewDetail_reviewDurationPosition", selectedDuration);
        authenticationController.goToItemReviewDetailsPage(params);
    }


    private void setSpinner(int selectedIndex, final long itemId) {
        Spinner durationSpinner = (Spinner) findViewById(R.id.itemReviewDetailsDurationSpinner);
        RestaurantUtil.setDurationSpinner(this, durationSpinner);
        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RatingDurationEnum ratingDurationEnum = RatingDurationEnum.of(position);
                buildTable(ratingDurationEnum.getValue(), itemId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                buildTable(0, itemId);
            }
        });

        durationSpinner.setSelection(selectedIndex);
    }


    private void buildTable(int noOfDaysOld, long itemId) {
        ItemReviewDetailsView itemReviewDetailsView = new ItemReviewDetailsView(this);
        Map<Integer, Map<Long, RatingSummary>> ratingByItemForAllDays = reviewFetchService.getDataGroupByDay(noOfDaysOld);
        itemReviewDetailsView.createTable(ratingByItemForAllDays, itemId);
    }

    private void setItemNameAutoCompletion() {
        Map<String, RestaurantItem> dishes = MenuItemDao.getDishes();
        String[] dishesArray = dishes.keySet().toArray(new String[dishes.keySet().size()]);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, dishesArray);
        AutoCompleteTextView actv = (AutoCompleteTextView) findViewById(R.id.itemReviewDetailsItemName);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setTextColor(Color.RED);
        actv.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToAdminLaunchPage();
    }

}