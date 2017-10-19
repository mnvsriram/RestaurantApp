package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ReviewAdapter;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.db.dao.ReviewDao;
import app.resta.com.restaurantapp.fragment.OrderListFragment;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.model.ReviewForOrder;
import app.resta.com.restaurantapp.util.MyApplication;

public class SubmitReviewActivity extends BaseActivity {

    OrderListFragment frag = new OrderListFragment();
    List<ReviewForDish> reviews;
    ReviewDao reviewDao;
    AuthenticationController authenticationController;
    long orderId = 0;
    OrderItemDao orderItemDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_submit_menu);

        ListView listView = (ListView) findViewById(R.id.reviewlist);

        ReviewForOrder reviewForOrder = null;
        Intent intent = getIntent();
        if (intent.hasExtra("ordered_items")) {
            reviewForOrder = (ReviewForOrder) intent.getSerializableExtra("ordered_items");
        }
        if (intent.hasExtra("orderId")) {
            orderId = (Long) intent.getLongExtra("orderId", 0);
        }

        if (reviewForOrder != null) {
            reviews = new ArrayList<>(reviewForOrder.getReviews());
            ReviewAdapter adapter = new ReviewAdapter(this, reviews, getApplicationContext());
            listView.setAdapter(adapter);
        }
        reviewDao = new ReviewDao();
        authenticationController = new AuthenticationController(this);
        orderItemDao = new OrderItemDao();
    }

    private boolean atLeastOneReviewPresent() {
        if (reviews != null) {
            for (ReviewForDish reviewForDish : reviews) {
                if (reviewForDish.getReview() != null || (reviewForDish.getReviewText() != null && reviewForDish.getReviewText().trim().length() > 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void submitReview(View view) {
        if (atLeastOneReviewPresent()) {
            reviewDao.saveReviews(reviews);
            orderItemDao.markOrderAsComplete(orderId);
            Toast.makeText(this, "Thanks for submitting the review", Toast.LENGTH_LONG);
            LoginController.getInstance().logout();
            authenticationController.goToHomePage();
        } else {
            confirmCancel();
        }
    }

    private void confirmCancel() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.edit);
        builderSingle.setTitle("Are you sure you do not want to rate any item?");

        builderSingle.setPositiveButton("I do not want to rate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                orderItemDao.markOrderAsComplete(orderId);
                Toast.makeText(MyApplication.getAppContext(), "No problem. Thank you!!", Toast.LENGTH_LONG).show();
                LoginController.getInstance().logout();
                authenticationController.goToHomePage();
            }
        });
        builderSingle.setNegativeButton("I want to..", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }


    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }


}
