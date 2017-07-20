package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.ItemReviewDetailActivity;
import app.resta.com.restaurantapp.activity.LowTopRatedItemsActivity;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.ReviewCount;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantUtil;

/**
 * Created by Sriram on 04/07/2017.
 */
public class ItemReviewDetailsView {
    private ItemReviewDetailActivity activity;
    private int badReviewbackgroundColor;
    private int goodReviewbackgroundColor;
    private int averageReviewbackgroundColor;
    private int noReviewBackgroundColor;
    private AuthenticationController authenticationController;

    public Activity getActivity() {
        return activity;
    }

    public ItemReviewDetailsView(ItemReviewDetailActivity activity) {
        this.activity = activity;
        authenticationController = new AuthenticationController(activity);
        badReviewbackgroundColor = MyApplication.getAppContext().getResources().getColor(R.color.lowReviewBackground);
        goodReviewbackgroundColor = MyApplication.getAppContext().getResources().getColor(R.color.topReviewBackground);
        averageReviewbackgroundColor = MyApplication.getAppContext().getResources().getColor(R.color.averageReviewBackground);
        noReviewBackgroundColor = MyApplication.getAppContext().getResources().getColor(R.color.recentCommentsBackground);
    }

    public void createTable(Map<Integer, Map<Long, RatingSummary>> ratingByItemForAllDays, long itemId) {
        TableLayout tl = (TableLayout) getActivity().findViewById(R.id.itemReviewDetailsTable);
        tl.removeAllViews();
        TableRow headerRow = getHeaderRow();
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        if (ratingByItemForAllDays != null) {
            int badCount = 0;
            int goodCount = 0;
            int averageCount = 0;
            for (Integer daysOld : ratingByItemForAllDays.keySet()) {
                Map<Long, RatingSummary> daysOldData = ratingByItemForAllDays.get(daysOld);
                RatingSummary daysOldDataForThisItem = daysOldData.get(itemId);
                if (daysOldDataForThisItem != null) {
                    badCount += daysOldDataForThisItem.getBadRatingCount();
                    averageCount += daysOldDataForThisItem.getAverageRatingCount();
                    goodCount += daysOldDataForThisItem.getGoodRatingCount();
                    List<TableRow> trs = getRows(daysOldDataForThisItem, daysOld);
                    if (trs != null) {
                        for (TableRow tr : trs) {
                            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        }
                    }

                }
            }


            TextView goodCountView = (TextView) activity.findViewById(R.id.itemReviewDetailsGoodCount);
            TextView averageCountView = (TextView) activity.findViewById(R.id.itemReviewDetailsAverageCount);
            TextView badCountView = (TextView) activity.findViewById(R.id.itemReviewDetailsBadCount);
            goodCountView.setText(goodCount + "");
            averageCountView.setText(averageCount + "");
            badCountView.setText(badCount + "");

        }
    }

    private TableRow getHeaderRow() {
        TableRow tr = new TableRow(activity);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_bg);
        tr.setPadding(5, 5, 5, 5);

        TextView dateCol = getHeaderColumnTextView("Date", true, false);
        TextView ratings = getHeaderColumnTextView("Ratings", false, false);
        TextView reviews = getHeaderColumnTextView("Reviews", false, true);

        tr.addView(dateCol);
        tr.addView(ratings);
        tr.addView(reviews);
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

        //textView.setBackgroundColor(backgroundColor);
        return textView;
    }


    protected TextView getColumnTextView(String text, boolean first, boolean last, int backgroundColor) {
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

    private String getDate(int noOfDaysOld) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -noOfDaysOld);
        return DateUtil.getDateString(calendar.getTime(), "dd MMM yyyy");
    }

    private List<TableRow> getRows(RatingSummary summary, int daysOld) {

        String badComments = summary.getBadRatingComments();
        String avgComments = summary.getAverageRatingComments();
        String goodComments = summary.getGoodRatingComments();
        String noReviewComments = summary.getNoReviewComments();

        String[] badCommentsArr = badComments.split(",");
        String[] averageCommentsArr = avgComments.split(",");
        String[] goodCommentsArr = goodComments.split(",");
        String[] nRCommentsArr = noReviewComments.split(",");
        List<TableRow> rows = new ArrayList<>();

        rows.addAll(getOneRowForEachComment(badCommentsArr, ReviewEnum.BAD, daysOld, summary));
        rows.addAll(getOneRowForEachComment(nRCommentsArr, ReviewEnum.NOREVIEW, daysOld, summary));
        rows.addAll(getOneRowForEachComment(averageCommentsArr, ReviewEnum.AVERAGE, daysOld, summary));
        rows.addAll(getOneRowForEachComment(goodCommentsArr, ReviewEnum.GOOD, daysOld, summary));
        return rows;
    }

    private List<TableRow> getOneRowForEachComment(String[] commentsArr, ReviewEnum reviewEnum, int daysOld, RatingSummary summary) {
        List<TableRow> rows = new ArrayList<>();
        if (commentsArr != null && commentsArr.length > 0) {
            int index = 0;

            int badCount = summary.getBadRatingCount();
            int averageCount = summary.getAverageRatingCount();
            int goodCount = summary.getGoodRatingCount();

            ReviewCount reviewCount = new ReviewCount(goodCount, averageCount, badCount);
            for (String comment : commentsArr) {
                if (comment != null && comment.trim().length() > 0) {
                    TableRow row = null;
                    if (index++ == 0) {
                        row = getRow(daysOld, comment, reviewEnum, reviewCount);
                    } else {
                        row = getRow(daysOld, comment, reviewEnum, null);
                    }
                    rows.add(row);
                }
            }
        }
        return rows;
    }

    private TableRow getRow(int daysOld, String comment, ReviewEnum reviewEnum, ReviewCount reviewCount) {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);


        int backgroundColor = noReviewBackgroundColor;
        if (reviewEnum != null) {

            if (reviewEnum.equals(ReviewEnum.BAD)) {
                backgroundColor = badReviewbackgroundColor;
            } else if (reviewEnum.equals(ReviewEnum.GOOD)) {
                backgroundColor = goodReviewbackgroundColor;
            } else if (reviewEnum.equals(ReviewEnum.AVERAGE)) {
                backgroundColor = averageReviewbackgroundColor;
            }
        }

        tr.setBackgroundColor(backgroundColor);

        TextView dateColumn = null;
        TextView ratingsColumn = null;
        if (reviewCount == null) {
            dateColumn = getColumnTextView("", true, false, backgroundColor);
            ratingsColumn = getColumnTextView("", false, false, backgroundColor);
        } else {
            dateColumn = getColumnTextView(getDate(daysOld), true, false, backgroundColor);
            ratingsColumn = getColumnTextView("Good: " + RestaurantUtil.getFormattedString(reviewCount.getGoodReviewCount() + "", 2) + " Avg: " + RestaurantUtil.getFormattedString(reviewCount.getAverageReviewCount() + "", 2) + "Bad: " + RestaurantUtil.getFormattedString(reviewCount.getBadReviewCount() + "", 2), false, false, backgroundColor);
        }

        TextView comments = getColumnTextView(comment, false, true, backgroundColor);
        tr.addView(dateColumn);
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
