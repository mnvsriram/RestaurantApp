package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.controller.OrderDetailsAdminView;
import app.resta.com.restaurantapp.controller.OrderDetailsReviewerView;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewForDish;

public class OrderDetailsViewActivity extends BaseActivity {
    private GestureDetector gestureDetector;
    int selectedIndex = 0;

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
        if (intent.hasExtra("orderDetails_selectedIndex")) {
            selectedIndex = intent.getIntExtra("orderDetails_selectedIndex", 0);
        }

        setToolbar();
        buildTable(items, reviewForDishes);
        if (items != null && items.size() > 0) {
            setOrderDetails(items, orderActive);
        }
    }

    private void setOrderDetails(final List<OrderedItem> items, String orderActive) {
        final OrderedItem item = items.get(0);
        final long orderId = item.getOrderId();
        TextView date = (TextView) findViewById(R.id.orderDetailsOrderDate);
        date.setText(item.getOrderDate());

        TextView comment = (TextView) findViewById(R.id.orderDetailsOrderComment);
        comment.setText(item.getOrderComment());

        ImageButton button = (ImageButton) findViewById(R.id.orderDetailsEditButton);

        TextView commentLabel = (TextView) findViewById(R.id.orderDetailsOrderCommentLabel);
        comment.setVisibility(View.GONE);
        button.setVisibility(View.GONE);
        commentLabel.setVisibility(View.GONE);

        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            comment.setVisibility(View.VISIBLE);
            commentLabel.setVisibility(View.VISIBLE);
            if (orderActive != null && orderActive.equalsIgnoreCase("Y")) {
                button.setVisibility(View.VISIBLE);
                View.OnClickListener editListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Map<String, Object> params = new HashMap<>();
                        params.put("orderActivity_orderItems", new ArrayList<OrderedItem>(items));
                        params.put("orderActivity_orderId", orderId);
                        params.put("orderActivity_orderComment", item.getOrderComment());
                        authenticationController.goToReviewerMenuPage(params);
                    }
                };
                button.setOnClickListener(editListener);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Map<String, Object> params = new HashMap<>();
        params.put("orderSummary_selectedIndex", selectedIndex);
        authenticationController.goToOrderSummaryPage(params);
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