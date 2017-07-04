package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.SubmitReviewActivity;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.model.ReviewForOrder;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderSummaryReviewerView extends OrderSummaryView {

    public OrderSummaryReviewerView(Activity activity) {
        super(activity);
    }

    public void createTable(Map<Long, List<OrderedItem>> orders) {
        TableLayout tl = (TableLayout) getActivity().findViewById(R.id.ordersTable);
        tl.removeAllViews();
        TableRow headerRow = getHeaderRowForAdmin();
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        List<OrderedItem> orderedItems = null;
        for (Long orderId : orders.keySet()) {
            orderedItems = orders.get(orderId);
            String date = "";
            String orderTable = "T4";//this column is yet to be inserted in the db.. this is the comment field while creating the order to give the table name
            String orderStatus = "";
            List<OrderedItem> items = orders.get(orderId);
            String itemNames = "";
            int oldValue = 0;
            if (items != null) {
                if (items.size() > 20) {
                    items = items.subList(0, 20);
                }
                for (OrderedItem orderedItem : items) {
                    date = orderedItem.getOrderDate();
                    orderStatus = orderedItem.getOrderStatus();
                    itemNames += orderedItem.getItemName() + ",";
                    int newValue = itemNames.length() % 60;
                    if (oldValue > newValue) {
                        itemNames += "\n";
                    }
                    oldValue = itemNames.length() % 60;
                }
            }
            TableRow tr = getRow(date, orderId, orderedItems, orderTable, itemNames, orderStatus);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private TableRow getRow(String date, Long orderId, List<OrderedItem> orderItems, String orderTable, String itemNames, String orderStatus) {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);
        TextView dateCol = getColumnTextView(date, true, false);
        TextView comment = getColumnTextView(orderTable, false, false);
        TextView itemNamesTextView = getColumnTextView(itemNames, false, false);
        View startReviewButton = getReviewView(orderId, orderStatus, orderItems);
        Button fullDetailsButton = getFullDetailsButton(orderItems, orderStatus, null);

        tr.addView(dateCol);
        tr.addView(comment);
        tr.addView(itemNamesTextView);
        tr.addView(startReviewButton);
        tr.addView(fullDetailsButton);
        return tr;
    }

    private TableRow getHeaderRowForAdmin() {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_bg);
        tr.setPadding(5, 5, 5, 5);

        TextView dateCol = getHeaderColumnTextView("Date", true, false);
        TextView comment = getHeaderColumnTextView("Comment", false, false);
        TextView itemsOrdered = getHeaderColumnTextView("Items Ordered", false, false);
        TextView reviewStatus = getHeaderColumnTextView("Review Status", false, false);
        TextView fullDetails = getHeaderColumnTextView("Full Details", false, true);

        tr.addView(dateCol);
        tr.addView(comment);
        tr.addView(itemsOrdered);
        tr.addView(reviewStatus);
        tr.addView(fullDetails);
        return tr;
    }

    private View getReviewView(long orderId, String orderActive, List<OrderedItem> items) {
        if (orderActive.equals("Y")) {
            return getStartReviewButton(orderId, items);
        } else {
            return getColumnTextView("Review Complete", false, true);
        }
    }

    private Button getStartReviewButton(final long orderId, final List<OrderedItem> items) {
        Button b = new Button(getActivity());
        b.setText("Start Review");
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReviewForOrder reviewForOrder = new ReviewForOrder(items, orderId);
                Intent intent = new Intent(getActivity(), SubmitReviewActivity.class);
                intent.putExtra("ordered_items", reviewForOrder);
                intent.putExtra("orderId", orderId);
                getActivity().startActivity(intent);

            }
        });
        return b;
    }

}
