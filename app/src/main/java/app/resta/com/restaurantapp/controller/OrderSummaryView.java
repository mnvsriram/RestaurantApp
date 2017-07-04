package app.resta.com.restaurantapp.controller;

import android.app.Activity;
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
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderSummaryView {
    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public OrderSummaryView(Activity activity) {
        this.activity = activity;
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

    protected Button getFullDetailsButton(long orderId) {
        Button b = new Button(activity);
        b.setText("Full Details");
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
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
