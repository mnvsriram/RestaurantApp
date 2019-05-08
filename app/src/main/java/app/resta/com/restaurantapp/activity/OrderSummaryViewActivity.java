package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.controller.OrderSummaryAdminView;
import app.resta.com.restaurantapp.controller.OrderSummaryReviewerView;
import app.resta.com.restaurantapp.db.dao.admin.order.OrderAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.order.OrderAdminFirestoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Order;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class OrderSummaryViewActivity extends BaseActivity {
    private GestureDetector gestureDetector;
    private OrderAdminDaoI orderAdminDao;

    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_summary);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());
        orderAdminDao = new OrderAdminFirestoreDao();
        setToolbar();
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            hideSpinner();
            buildTable(getBeforeDaysDate(7), Calendar.getInstance().getTime());
        } else {
            setSpinnerListener(getIntent().getIntExtra("orderSummary_selectedIndex", 0));
        }

    }

    private void hideSpinner() {
        Spinner spinner = findViewById(R.id.ordersViewDurationSpinner);
        spinner.setVisibility(View.GONE);
    }

    private void setSpinnerListener(int selectedIndex) {
        Spinner spinner = findViewById(R.id.ordersViewDurationSpinner);
        RestaurantUtil.setDurationSpinner(this, spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                RatingDurationEnum ratingDurationEnum = RatingDurationEnum.of(position);
                buildTable(getBeforeDaysDate(ratingDurationEnum.getValue()), Calendar.getInstance().getTime());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        spinner.setSelection(selectedIndex);
    }

    @Override
    public void onBackPressed() {
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            authenticationController.goToReviewerLaunchPage();
        } else {
            authenticationController.goToAdminLaunchPage();
        }
    }

    private Date getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getBeforeDaysDate(int numberOfDays) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - numberOfDays);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private void buildTable(Date fromDate, Date toDate) {
        final OrderAdminDaoI orderAdminDao = new OrderAdminFirestoreDao();
        final Map<String, Order> ordersMap = new HashMap<>();
        orderAdminDao.getOrdersWithItems(fromDate, toDate, new OnResultListener<List<Order>>() {
            @Override
            public void onCallback(List<Order> orders) {
                for (Order order : orders) {
                    ordersMap.put(order.getOrderId(), order);
                }
                if (LoginController.getInstance().isReviewAdminLoggedIn()) {
                    OrderSummaryReviewerView reviewerView = new OrderSummaryReviewerView(OrderSummaryViewActivity.this);
                    reviewerView.fetchMenuTypesAndCreateTable(ordersMap);
                } else {
                    orderAdminDao.getReviewsForOrders(ordersMap.keySet(), new OnResultListener<Map<String, List<ReviewForDish>>>() {
                        @Override
                        public void onCallback(Map<String, List<ReviewForDish>> ratingsForOrders) {
                            OrderSummaryAdminView adminView = new OrderSummaryAdminView(OrderSummaryViewActivity.this);
                            adminView.createTable(ordersMap, ratingsForOrders);
                        }
                    });

                }
            }
        });

    }

}