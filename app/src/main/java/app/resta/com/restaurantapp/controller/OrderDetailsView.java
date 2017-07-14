package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableRow;
import android.widget.TextView;

import app.resta.com.restaurantapp.R;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderDetailsView {
    private Activity activity;
    private AuthenticationController authenticationController;

    public Activity getActivity() {
        return activity;
    }

    public OrderDetailsView(Activity activity) {
        this.activity = activity;
        authenticationController = new AuthenticationController(activity);
    }


    protected TextView getHeaderColumnTextView(String text, boolean first, boolean last) {
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
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
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        return textView;
    }

    protected TextView getColumnTextView(String text, boolean last) {
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new TableRow.LayoutParams(90, 90));
        textView.setBackgroundResource(R.drawable.table_cell_bg);
        if (last) {
            textView.setPadding(10, 0, 0, 0);
        } else {
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 0, 10, 0);
        }
        textView.setTextSize(20);
        textView.setText(text);
        return textView;
    }
}
