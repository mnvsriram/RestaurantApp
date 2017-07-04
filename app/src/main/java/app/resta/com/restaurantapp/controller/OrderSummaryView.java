package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.AdminLauncherActivity;
import app.resta.com.restaurantapp.activity.OrderDetailsViewActivity;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderSummaryView {
    private Activity activity;
    private AuthenticationController authenticationController;

    public Activity getActivity() {
        return activity;
    }

    public OrderSummaryView(Activity activity) {
        this.activity = activity;
        authenticationController = new AuthenticationController(activity);
    }

    protected TextView getHeaderColumnTextView(String text, boolean first, boolean last) {
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setTypeface(null, Typeface.BOLD);
        if (first) {
            textView.setPadding(0, 0, 10, 0);
            textView.setBackgroundResource(R.drawable.table_cell_bg);
        } else if (last) {
            textView.setPadding(10, 0, 0, 0);
        } else {
            textView.setBackgroundResource(R.drawable.table_cell_bg);
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 0, 10, 0);
        }
        textView.setText(text);
        return textView;
    }

    protected Button getFullDetailsButton(final List<OrderedItem> orderItems, final String orderActive, final List<ReviewForDish> reviews) {
        Button b = new Button(activity);
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        b.setText("Full Details");
        if (LoginController.getInstance().isReviewAdminLoggedIn() && orderActive != null && orderActive.equalsIgnoreCase("Y")) {
            b.setText("View/Edit Order");
        }

        View.OnClickListener clicks = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyApplication.getAppContext(), OrderDetailsViewActivity.class);
                if (orderItems != null) {
                    intent.putExtra("orderDetails_orderedItems", new ArrayList<OrderedItem>(orderItems));
                }
                intent.putExtra("orderDetails_orderActive", orderActive);
                if (reviews != null) {
                    intent.putExtra("orderDetails_reviews", new ArrayList<ReviewForDish>(reviews));
                }
                activity.startActivity(intent);
            }
        };
        b.setOnClickListener(clicks);

        return b;
    }


    protected TextView getColumnTextView(String text, boolean first, boolean last) {
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setBackgroundResource(R.drawable.table_cell_bg);
        if (first) {
            textView.setPadding(0, 0, 10, 0);
        } else if (last) {
            textView.setPadding(10, 0, 0, 0);

        } else {

            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 0, 10, 0);
        }
        textView.setText(text);
        return textView;
    }

}
