package app.resta.com.restaurantapp.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
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
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;

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
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            hideSpinner();
            //buildTable(getTodaysDate(), null);
            buildTable(getBefore7DaysDate(), new Date());
        } else {
            setSpinnerListener();
        }

    }

    private void hideSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.ordersViewDurationSpinner);
        spinner.setVisibility(View.GONE);
    }

    private void setSpinnerListener() {
        Spinner spinner = (Spinner) findViewById(R.id.ordersViewDurationSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position == 0) {
                    buildTable(getTodaysDate(), null);
                } else if (position == 1) {
                    buildTable(getBefore7DaysDate(), Calendar.getInstance().getTime());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            authenticationController.goToReviewMenuPage();
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

    private Date getBefore7DaysDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 7);
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