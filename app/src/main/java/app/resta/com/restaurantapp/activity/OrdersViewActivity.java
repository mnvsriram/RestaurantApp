package app.resta.com.restaurantapp.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.db.dao.ReviewDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.MyApplication;

public class OrdersViewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_view);

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
        buildTable();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToAdminLaunchPage();
    }

    private void buildTable() {
        OrderItemDao orderItemDao = new OrderItemDao();
        Map<Long, List<OrderedItem>> orders = orderItemDao.getOrders(new Date(), new Date());
        ReviewDao reviewDao = new ReviewDao();
        Map<Long, List<ReviewForDish>> reviewsPerOrder = reviewDao.getReviews(orders.keySet());

        createTable(orders, reviewsPerOrder);
    }

    private void createTable(Map<Long, List<OrderedItem>> orders, Map<Long, List<ReviewForDish>> reviewsPerOrder) {
        TableLayout tl = (TableLayout) findViewById(R.id.ordersTable);


        for (Long orderId : orders.keySet()) {
            List<OrderedItem> order = orders.get(orderId);
            List<ImageButton> reviewButtons = new ArrayList<>();
            String date = "";
            String orderTable = "T4";//this column is yet to be inserted in the db.. this is the comment field while creating the order to give the table name
            List<ReviewForDish> reviews = reviewsPerOrder.get(orderId);

            if (reviews != null) {
                for (ReviewForDish reviewForDish : reviews) {
                    ImageButton reviewButton = getReviewButton(reviewForDish.getReview().getValue());
                    if (reviewButton != null) {
                        reviewButtons.add(reviewButton);
                    }
                }
            }

            if (order.size() > 1) {
                date = order.get(0).getOrderDate();
            }
            TableRow tr = getRow(date, orderTable, reviewButtons);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }


    }

    private TableRow getRow(String date, String orderTable, List<ImageButton> reviewButtons) {
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);
        TextView dateCol = getColumnTextView(date, true, false);
        TextView comment = getColumnTextView(orderTable, false, false);
        Button b = getFullDetailsButton(1);
        LinearLayout LL = getReviewLayout(reviewButtons);

        tr.addView(dateCol);
        tr.addView(comment);
        tr.addView(b);
        tr.addView(LL);
        return tr;
    }

    private TextView getColumnTextView(String text, boolean first, boolean last) {
        TextView textView = new TextView(this);
        textView.setText("hello");
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setBackgroundResource(R.drawable.table_cell_bg);
        if (first) {
            textView.setPadding(0, 0, 10, 0);
        } else if (last) {
            textView.setPadding(10, 0, 0, 0);

        } else {

            //textView.setGravity(Gravity.RIGHT);
            textView.setPadding(10, 0, 10, 0);
        }
        textView.setText(text);
        return textView;
    }


    private Button getFullDetailsButton(long orderId) {
        Button b = new Button(this);
        b.setText("Full Details");
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        //textView.setBackgroundResource(R.drawable.table_cell_bg);
        return b;
    }

    private LinearLayout createReviewLayout() {
        LinearLayout LL = new LinearLayout(this);
        LL.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        return LL;
    }

    private LinearLayout getReviewLayout(List<ImageButton> reviewButtons) {
        LinearLayout ll = createReviewLayout();
        for (ImageButton button : reviewButtons) {
            ll.addView(button);
        }
        return ll;
    }

    private ImageButton getReviewButton(long review) {
        ImageButton reviewButton = new ImageButton(MyApplication.getAppContext());
        if (review == ReviewEnum.AVERAGE.getValue()) {
            reviewButton.setBackgroundResource(R.drawable.reviewaveragecolor);
        } else if (review == ReviewEnum.GOOD.getValue()) {
            reviewButton.setBackgroundResource(R.drawable.reviewgoodcolor);
        } else if (review == ReviewEnum.BAD.getValue()) {
            reviewButton.setBackgroundResource(R.drawable.reviewbadcolor);
        } else {
            return null;
        }
        reviewButton.setLayoutParams(new TableRow.LayoutParams(50, 50));
        return reviewButton;
    }
}