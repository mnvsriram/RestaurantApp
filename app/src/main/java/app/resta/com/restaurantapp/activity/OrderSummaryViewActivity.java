package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.controller.OrderSummaryAdminView;
import app.resta.com.restaurantapp.controller.OrderSummaryReviewerView;
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.db.dao.ReviewDao;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class OrderSummaryViewActivity extends BaseActivity {
    private GestureDetector gestureDetector;

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

        setToolbar();
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            hideSpinner();
            buildTable(getBeforeDaysDate(7), Calendar.getInstance().getTime());
        } else {
            setSpinnerListener(getIntent().getIntExtra("orderSummary_selectedIndex", 0));
        }

    }

    private void hideSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.ordersViewDurationSpinner);
        spinner.setVisibility(View.GONE);
    }

    private void setSpinnerListener(int selectedIndex) {
        Spinner spinner = (Spinner) findViewById(R.id.ordersViewDurationSpinner);
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
        OrderItemDao orderItemDao = new OrderItemDao();
        Map<Long, List<OrderedItem>> orders = orderItemDao.getOrders(fromDate, toDate);
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            OrderSummaryReviewerView reviewerView = new OrderSummaryReviewerView(this);
            reviewerView.createTable(orders);
        } else {
            ReviewDao reviewDao = new ReviewDao();
            Map<Long, List<ReviewForDish>> reviewsPerOrder = reviewDao.getReviews(orders.keySet());
            OrderSummaryAdminView adminView = new OrderSummaryAdminView(this);
            adminView.createTable(orders, reviewsPerOrder);
        }
    }

}