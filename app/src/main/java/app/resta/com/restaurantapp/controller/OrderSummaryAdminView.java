package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.content.Context;
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
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderSummaryAdminView extends OrderSummaryView {

    public OrderSummaryAdminView(Activity activity) {
        super(activity);
    }

    private TableRow getHeaderRowForAdmin() {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_bg);
        tr.setPadding(5, 5, 5, 5);

        TextView dateCol = getHeaderColumnTextView("Date", true, false);
        TextView fullDetails = getHeaderColumnTextView("Full Details", false, false);
        TextView ratings = getHeaderColumnTextView("Ratings", false, false);
        TextView reviews = getHeaderColumnTextView("Reviews", false, true);

        tr.addView(dateCol);
        tr.addView(fullDetails);
        tr.addView(ratings);
        tr.addView(reviews);

        return tr;
    }

    public void createTable(Map<Long, List<OrderedItem>> orders, Map<Long, List<ReviewForDish>> reviewsPerOrder) {
        TableLayout tl = (TableLayout) getActivity().findViewById(R.id.ordersTable);
        tl.removeAllViews();
        TableRow headerRow = getHeaderRowForAdmin();
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        for (Long orderId : orders.keySet()) {
            List<OrderedItem> order = orders.get(orderId);
            List<View> ratingViews = new ArrayList<>();
            String date = "";
            List<ReviewForDish> reviews = reviewsPerOrder.get(orderId);
            String comments = "";
            String orderStatus = "";
            if (reviews != null) {
                if (reviews.size() > 20) {
                    reviews = reviews.subList(0, 20);
                }
                for (ReviewForDish reviewForDish : reviews) {
                    View ratingView = ratingView(reviewForDish);
                    if (ratingView != null) {
                        ratingViews.add(ratingView);
                        if (reviewForDish.getReviewText() != null && reviewForDish.getReviewText().length() > 0) {
                            comments += reviewForDish.getItem().getName() + "- " + reviewForDish.getReviewText() + "\n";
                        }
                    }
                }
            }

            if (order.size() > 1) {
                date = order.get(0).getOrderDate();
                orderStatus = order.get(0).getOrderStatus();
            }

            TableRow tr = getRow(orderId, date, ratingViews, comments, orderStatus, order, reviews);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    public View ratingView(ReviewForDish reviewForDish) {
        View v; // Creating an instance for View Object
        LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.ordersummaryratingview, null);
        ImageButton imageButton = getRatingButton(v, reviewForDish);
        final TextView toolTip = (TextView) v.findViewById(R.id.ratingItemTextView);
        toolTip.setText(reviewForDish.getItem().getName());
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //hideAllOtherTextViews(getApplicationContext(), (View) toolTip.getParent().getParent());
                    toolTip.setVisibility(View.VISIBLE);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    toolTip.setVisibility(View.GONE);
                }
                return true;
            }
        });
        return v;
    }

    public View reviewCommentView(String comment) {
        View v; // Creating an instance for View Object
        LayoutInflater inflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.ordersummaryreviewcommentsview, null);
        //ImageButton imageButton = getReviewButton(null);
        ImageButton imageButton = (ImageButton) v.findViewById(R.id.reviewCommentImage);
        final TextView toolTip = (TextView) v.findViewById(R.id.reviewCommentName);
        toolTip.setText(comment);
        imageButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //hideAllOtherTextViews(getApplicationContext(), (View) toolTip.getParent().getParent());
                    toolTip.setVisibility(View.VISIBLE);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    toolTip.setVisibility(View.GONE);
                }
                return true;
            }
        });
        return v;
    }

    private TableRow getRow(Long orderId, String date, List<View> ratingButtons, String reviews, String orderActive, List<OrderedItem> orderedItems, List<ReviewForDish> reviewForDishes) {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);
        TextView dateCol = getColumnTextView(date, true, false);
        Button b = getFullDetailsButton(orderedItems, orderActive, reviewForDishes);
        View reviewCommentsView = reviewCommentView(reviews);

        LinearLayout LL = getRatingLayout(ratingButtons);
        tr.addView(dateCol);
        tr.addView(b);
        tr.addView(LL);
        tr.addView(reviewCommentsView);
        return tr;
    }

    private LinearLayout createReviewLayout() {
        LinearLayout LL = new LinearLayout(getActivity());
        LL.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        return LL;
    }

    private LinearLayout getRatingLayout(List<View> reviewButtons) {
        LinearLayout ll = createReviewLayout();
        for (View button : reviewButtons) {
            ll.addView(button);
        }
        return ll;
    }

    private ImageButton getRatingButton(View v, ReviewForDish reviewForDish) {
        ImageButton imageButton = (ImageButton) v.findViewById(R.id.ratingImage);
        if (reviewForDish.getReview() == ReviewEnum.AVERAGE) {
            imageButton.setBackgroundResource(R.drawable.reviewaveragecolor);
        } else if (reviewForDish.getReview() == ReviewEnum.BAD) {
            imageButton.setBackgroundResource(R.drawable.reviewbadcolor);
        } else if (reviewForDish.getReview() == ReviewEnum.GOOD) {
            imageButton.setBackgroundResource(R.drawable.reviewgoodcolor);
        }
        return imageButton;
    }


}
