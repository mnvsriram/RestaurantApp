package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderDetailsReviewerView extends OrderSummaryView {

    public OrderDetailsReviewerView(Activity activity) {
        super(activity);
    }

    public void createTable(List<OrderedItem> items, List<ReviewForDish> reviewForDishes) {
        TableLayout tl = (TableLayout) getActivity().findViewById(R.id.orderDetailsTable);
        tl.removeAllViews();
        TableRow headerRow = getHeaderRow();
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        for (OrderedItem item : items) {
            TableRow tr = getRow(item);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private TableRow getRow(OrderedItem item) {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);
        TextView itemName = getColumnTextView(item.getItemName(), true, false);
        TextView quantity = getColumnTextView(item.getQuantity() + "", false, false);
        TextView instructions = getColumnTextView(item.getInstructions(), false, true);

        tr.addView(itemName);
        tr.addView(quantity);
        tr.addView(instructions);
        return tr;
    }

    private TableRow getHeaderRow() {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_bg);
        tr.setPadding(5, 5, 5, 5);

        TextView itemName = getHeaderColumnTextView("Item", true, false);
        TextView quantity = getHeaderColumnTextView("Quantity", false, false);
        TextView instructions = getHeaderColumnTextView("Instructions", false, true);

        tr.addView(itemName);
        tr.addView(quantity);
        tr.addView(instructions);
        return tr;
    }


}
