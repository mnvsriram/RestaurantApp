package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.controller.OrderDetailsAdminView;
import app.resta.com.restaurantapp.controller.OrderDetailsReviewerView;
import app.resta.com.restaurantapp.controller.OrderSummaryAdminView;
import app.resta.com.restaurantapp.controller.OrderSummaryReviewerView;
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.db.dao.ReviewDao;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.model.ReviewForOrder;

public class OrderDetailsViewActivity extends BaseActivity {
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
        setContentView(R.layout.activity_order_details);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        Intent intent = getIntent();
        ArrayList<OrderedItem> items = null;
        if (intent.hasExtra("orderDetails_orderedItems")) {
            items = (ArrayList<OrderedItem>) intent.getSerializableExtra("orderDetails_orderedItems");
        }
        String orderActive = "";
        if (intent.hasExtra("orderDetails_orderActive")) {
            orderActive = intent.getStringExtra("orderDetails_orderActive");
        }

        ArrayList<ReviewForDish> reviewForDishes = null;
        if (intent.hasExtra("orderDetails_reviews")) {
            reviewForDishes = (ArrayList<ReviewForDish>) intent.getSerializableExtra("orderDetails_reviews");
        }

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
        buildTable(items, reviewForDishes);
        if (items != null && items.size() > 0) {
            setOrderDetails(items, orderActive);
        }

    }

    private void setOrderDetails(final List<OrderedItem> items, String orderActive) {
        OrderedItem item = items.get(0);
        TextView date = (TextView) findViewById(R.id.orderDetailsOrderDate);
        date.setText(item.getOrderDate());

        TextView comment = (TextView) findViewById(R.id.orderDetailsOrderComment);
        comment.setText("to do");

        ImageButton button = (ImageButton) findViewById(R.id.orderDetailsEditButton);

        if (LoginController.getInstance().isReviewAdminLoggedIn() && orderActive != null && orderActive.equalsIgnoreCase("Y")) {
            comment.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);

            View.OnClickListener editListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    authenticationController.goToReviewMenuPage(items);
                }
            };
            button.setOnClickListener(editListener);

        } else {
            TextView commentLabel = (TextView) findViewById(R.id.orderDetailsOrderCommentLabel);
            comment.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
            commentLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            authenticationController.goToReviewMenuPage();
        } else {
            authenticationController.goToAdminLaunchPage();
        }
    }

    private void buildTable(List<OrderedItem> items, List<ReviewForDish> reviewForDishes) {
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            OrderDetailsReviewerView reviewerView = new OrderDetailsReviewerView(this);
            reviewerView.createTable(items, reviewForDishes);
        } else {
            OrderDetailsAdminView adminView = new OrderDetailsAdminView(this);
            adminView.createTable(items, reviewForDishes);
        }

    }

}