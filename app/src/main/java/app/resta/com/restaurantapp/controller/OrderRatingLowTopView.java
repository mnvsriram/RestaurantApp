package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderRatingLowTopView {
    private Activity activity;
    private int backgroundColor = 0;
    private boolean displayTopItems = false;

    public Activity getActivity() {
        return activity;
    }

    public OrderRatingLowTopView(Activity activity, boolean good) {
        this.activity = activity;
        if (good) {
            backgroundColor = MyApplication.getAppContext().getResources().getColor(R.color.topReviewBackground);
            displayTopItems = true;
        } else {
            backgroundColor = MyApplication.getAppContext().getResources().getColor(R.color.lowReviewBackground);
            displayTopItems = false;
        }

    }

    public void createTable(Map<Double, List<RatingSummary>> data) {
        TableLayout tl = (TableLayout) getActivity().findViewById(R.id.ratingTableTopLow);
        tl.removeAllViews();
        TableRow headerRow = getHeaderRow();
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        if (data != null) {
            for (Double score : data.keySet()) {
                List<RatingSummary> ratingSummaries = data.get(score);
                if (ratingSummaries != null) {
                    for (RatingSummary summary : ratingSummaries) {

                        int goodCount = summary.getGoodRatingCount();
                        int badCount = summary.getBadRatingCount();
                        int averageCount = summary.getAverageRatingCount();

                        if (displayTopItems) {
                            if (goodCount <= 0 && averageCount <= 0) {
                                break;
                            }
                        } else {
                            if (badCount <= 0 && averageCount <= 0) {
                                break;
                            }
                        }
                        TableRow tr = getRow(summary);
                        tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    }
                }
            }
        }
    }

    private TableRow getHeaderRow() {
        TableRow tr = new TableRow(activity);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_bg);
        tr.setPadding(5, 5, 5, 5);

        TextView dateCol = getHeaderColumnTextView("Item", true, false);
        TextView fullDetails = getHeaderColumnTextView("Ratings", false, false);
        TextView ratings = getHeaderColumnTextView("Reviews", false, true);

        tr.addView(dateCol);
        tr.addView(fullDetails);
        tr.addView(ratings);
        return tr;
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
        textView.setTextSize(20);
        textView.setText(text);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundColor(backgroundColor);
        return textView;
    }


    protected TextView getColumnTextView(String text, boolean first, boolean last) {
        TextView textView = new TextView(activity);
        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        textView.setBackgroundResource(R.drawable.table_cell_bg);
        textView.setTextSize(20);
        if (first) {
            textView.setPadding(0, 0, 10, 0);
        } else if (last) {
            textView.setPadding(10, 0, 0, 0);

        } else {

            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10, 0, 10, 0);
        }
        textView.setText(text);
        textView.setBackgroundColor(backgroundColor);
        textView.setTextColor(Color.BLACK);
        return textView;
    }


    private TableRow getRow(RatingSummary summary) {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);


        TextView itemName = getColumnTextView(summary.getItemName(), true, false);
        //View ratingView = ratingView(summary);
        TextView ratingsColumn = null;
        int badCount = summary.getBadRatingCount();
        int averageCount = summary.getAverageRatingCount();
        int goodCount = summary.getGoodRatingCount();


        if (displayTopItems) {
            ratingsColumn = getColumnTextView("Good: " + goodCount + " Avg: " + averageCount, false, false);
        } else {
            ratingsColumn = getColumnTextView("Bad: " + badCount + " Avg: " + averageCount, false, false);
        }

        String badComments = summary.getBadRatingComments();
        String AvgComments = summary.getAverageRatingComments();
        TextView comments = getColumnTextView(badComments + AvgComments, false, true);

        tr.addView(itemName);
        tr.addView(ratingsColumn);
        tr.addView(comments);

        return tr;
    }

/*
    public View ratingView(RatingSummary summary) {
        String badCount = summary.getBadRatingCount() + "";
        String averageCount = summary.getAverageRatingCount() + "";
        String goodCount = summary.getGoodRatingCount() + "";

        View v;
        LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.order_details_review_item, null);
        v.setBackgroundColor(backgroundColor);
        ImageButton average = (ImageButton) v.findViewById(R.id.OrderDetailsReviewAverage);
        ImageButton good = (ImageButton) v.findViewById(R.id.OrderDetailsReviewGood);
        ImageButton bad = (ImageButton) v.findViewById(R.id.OrderDetailsReviewBad);

        TextView goodCountView = (TextView) v.findViewById(R.id.orderDetailsReviewGoodCount);
        TextView averageCountView = (TextView) v.findViewById(R.id.orderDetailsReviewAverageCount);
        TextView badCountView = (TextView) v.findViewById(R.id.orderDetailsReviewBadCount);

        if (displayTopItems) {
            bad.setVisibility(View.GONE);

            averageCountView.setText(averageCount);
            averageCountView.setVisibility(View.VISIBLE);
            RestaurantUtil.setImage(average, R.drawable.reviewaveragecolor, 20, 20);

            goodCountView.setText(goodCount);
            goodCountView.setVisibility(View.VISIBLE);
            RestaurantUtil.setImage(good, R.drawable.reviewgoodcolor, 20, 20);
        } else {
            good.setVisibility(View.GONE);

            averageCountView.setText(averageCount);
            averageCountView.setVisibility(View.VISIBLE);
            RestaurantUtil.setImage(average, R.drawable.reviewaveragecolor, 20, 20);

            badCountView.setText(badCount);
            badCountView.setVisibility(View.VISIBLE);
            RestaurantUtil.setImage(bad, R.drawable.reviewgoodcolor, 20, 20);
        }
        return v;
    }
*/
}
