package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.RestaurantUtil;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderDetailsAdminView extends OrderSummaryView {

    public OrderDetailsAdminView(Activity activity) {
        super(activity);
    }

    private Map<Long, ReviewForDish> convertToMap(List<ReviewForDish> reviewForDishes) {
        Map<Long, ReviewForDish> reviewForDishMap = new HashMap<>();
        if (reviewForDishes != null) {
            for (ReviewForDish reviewForDish : reviewForDishes) {
                reviewForDishMap.put(reviewForDish.getItem().getId(), reviewForDish);
            }
        }
        return reviewForDishMap;
    }

    public void createTable(List<OrderedItem> items, List<ReviewForDish> reviewForDishes) {
        Map<Long, ReviewForDish> reviewsPerItemMap = convertToMap(reviewForDishes);
        TableLayout tl = (TableLayout) getActivity().findViewById(R.id.orderDetailsTable);
        tl.removeAllViews();
        TableRow headerRow = getHeaderRow();
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        for (OrderedItem item : items) {
            TableRow tr = getRow(item, reviewsPerItemMap.get(item.getItemId()));
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private TableRow getRow(OrderedItem item, ReviewForDish reviewForDish) {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);
        TextView itemName = getColumnTextView(item.getItemName(), true, false);
        TextView quantity = getColumnTextView(item.getQuantity() + "", false, false);
        View rating = ratingView(reviewForDish);//getColumnTextView(reviewForDish.getReview().toString(), false, false);
        TextView review = getColumnTextView("", false, true);
        if (reviewForDish != null) {
            review = getColumnTextView(reviewForDish.getReviewText(), false, true);
        }

        tr.addView(itemName);
        tr.addView(quantity);
        tr.addView(rating);
        tr.addView(review);
        return tr;
    }

    private TableRow getHeaderRow() {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_bg);
        tr.setPadding(5, 5, 5, 5);

        TextView itemName = getHeaderColumnTextView("Item", true, false);
        TextView quantity = getHeaderColumnTextView("Quantity", false, false);
        TextView rating = getHeaderColumnTextView("Rating", false, false);
        TextView reviews = getHeaderColumnTextView("Review", false, true);

        tr.addView(itemName);
        tr.addView(quantity);
        tr.addView(rating);
        tr.addView(reviews);
        return tr;
    }

    public View ratingView(ReviewForDish reviewForDish) {
        if (reviewForDish == null || reviewForDish.getReview() == null) {
            return getColumnTextView("No Rating", false, false);
        }
        View v;
        LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.order_details_review_item, null);

        if (reviewForDish.getReview().equals(ReviewEnum.AVERAGE)) {
            ImageButton average = (ImageButton) v.findViewById(R.id.OrderDetailsReviewAverage);
            RestaurantUtil.setImage(average, R.drawable.reviewaveragecolor, 35, 35);
        } else if (reviewForDish.getReview().equals(ReviewEnum.GOOD)) {
            ImageButton good = (ImageButton) v.findViewById(R.id.OrderDetailsReviewGood);
            RestaurantUtil.setImage(good, R.drawable.reviewgoodcolor, 35, 35);
        } else if (reviewForDish.getReview().equals(ReviewEnum.BAD)) {
            ImageButton bad = (ImageButton) v.findViewById(R.id.OrderDetailsReviewBad);
            RestaurantUtil.setImage(bad, R.drawable.reviewbadcolor, 35, 35);
        }
        return v;
    }

}
