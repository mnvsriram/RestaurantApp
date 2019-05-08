package app.resta.com.restaurantapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.ItemReviewDetailsView;
import app.resta.com.restaurantapp.controller.ReviewFetchService;
import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminFirestoreDao;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class ItemReviewDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ReviewFetchService reviewFetchService;
    private String fromPage = "low";
    private MenuItemUserDaoI menuItemUseDaoI = new MenuItemUserFireStoreDao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_review_details);
        setToolbar();
        reviewFetchService = new ReviewFetchService();


        String itemId = getIntent().getStringExtra("itemReviewDetail_itemId");
        int durationIndex = getIntent().getIntExtra("itemReviewDetail_reviewDurationPosition", 0);
        fromPage = getIntent().getStringExtra("itemReviewDetail_fromPage");

        setFields(durationIndex, itemId);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void setFields(int durationIndex, String itemId) {
        setSpinner(durationIndex, itemId);
        setItemNameAutoCompletion();
        setItemName(itemId);
    }

    private void setItemName(String itemId) {
        final TextView itemNameView = findViewById(R.id.itemReviewDetailsitemNameText);
        if (itemId != null) {
            menuItemUseDaoI.getItem_u(itemId, new OnResultListener<RestaurantItem>() {
                @Override
                public void onCallback(RestaurantItem itemSuggested) {
                    if (itemSuggested != null) {
                        itemNameView.setText(itemSuggested.getName());
                    } else {
                        itemNameView.setText("");
                    }
                }
            });
        } else {
            itemNameView.setText("");
        }

    }

    public void onItemClick(AdapterView<?> arg0, View arg1,
                            int arg2, long arg3) {
        TextView textView = (TextView) arg1;
        final String itemName = textView.getText().toString();
        menuItemUseDaoI.getAllItems_u(new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> items) {
                final Map<String, RestaurantItem> dishes = new HashMap<>();
                for (RestaurantItem item : items) {
                    dishes.put(item.getName(), item);
                }
                RestaurantItem itemSuggested = dishes.get(itemName);
                String selectedItemId = itemSuggested.getId();
                getDetailsForItem(selectedItemId);
            }
        });


    }

    private void getDetailsForItem(String selectedItemId) {
        Spinner durationSpinner = findViewById(R.id.itemReviewDetailsDurationSpinner);
        int selectedDuration = durationSpinner.getSelectedItemPosition();
        Map<String, Object> params = new HashMap<>();
        params.put("itemReviewDetail_itemId", selectedItemId);
        params.put("itemReviewDetail_reviewDurationPosition", selectedDuration);
        params.put("itemReviewDetail_fromPage", fromPage);
        authenticationController.goToItemReviewDetailsPage(params);
    }


    private void setSpinner(int selectedIndex, final String itemId) {
        Spinner durationSpinner = findViewById(R.id.itemReviewDetailsDurationSpinner);
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

    private RatingSummaryAdminDaoI ratingSummaryAdminDao = new RatingSummaryAdminFirestoreDao();

    private void buildTable(int noOfDaysOld, final String itemId) {
        if (itemId != null) {
            final ItemReviewDetailsView itemReviewDetailsView = new ItemReviewDetailsView(this);

            ratingSummaryAdminDao.getRatingsPerDayPerItem(noOfDaysOld, new OnResultListener<Map<Long, Map<String, RatingSummary>>>() {
                @Override
                public void onCallback(Map<Long, Map<String, RatingSummary>> ratingByItemForAllDays) {
                    itemReviewDetailsView.createTable(ratingByItemForAllDays, itemId);
                }
            });
        }
    }


    private void setItemNameAutoCompletion() {
        menuItemUseDaoI.getAllItems_u(new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> items) {
                final Map<String, RestaurantItem> dishes = new HashMap<>();
                for (RestaurantItem item : items) {
                    dishes.put(item.getName(), item);
                }
                String[] dishesArray = dishes.keySet().toArray(new String[dishes.keySet().size()]);

                ArrayAdapter<String> adapter = new ArrayAdapter<>
                        (ItemReviewDetailActivity.this, android.R.layout.select_dialog_item, dishesArray);
                AutoCompleteTextView actv = findViewById(R.id.itemReviewDetailsItemName);
                actv.setThreshold(1);//will start working from first character
                actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
                actv.setTextColor(Color.RED);
                actv.setOnItemClickListener(ItemReviewDetailActivity.this);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Spinner durationSpinner = findViewById(R.id.itemReviewDetailsDurationSpinner);
        if (fromPage == null) {
            fromPage = "low";
        }
        Map<String, Object> intentParameters;
        intentParameters = new HashMap<>();

        if (fromPage.equals("activity_reviewMainActivity")) {
            authenticationController.goToReviewsPage(intentParameters);
        } else {
            intentParameters.put("topLowActivity_reviewDurationPosition", durationSpinner.getSelectedItemPosition());
            intentParameters.put("topLowActivity_reviewContentType", fromPage);
            authenticationController.goToLowTopRatedItemsPage(intentParameters);
        }

    }

}