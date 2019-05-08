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
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.order.OrderAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.order.OrderAdminFirestoreDao;
import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.ratingSummary.RatingSummaryAdminFirestoreDao;
import app.resta.com.restaurantapp.db.dao.admin.review.ReviewAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.review.ReviewAdminFirestoreDao;
import app.resta.com.restaurantapp.db.dao.admin.score.ScoreAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.score.ScoreAdminFirestoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.model.ReviewForOrder;
import app.resta.com.restaurantapp.util.MyApplication;

public class SubmitReviewActivity extends BaseActivity {

    List<ReviewForDish> reviews;
    AuthenticationController authenticationController;
    String orderId = null;
    OrderAdminDaoI orderAdminDao;
    ReviewAdminDaoI reviewDao;
    MenuItemAdminDaoI menuItemAdminDao;
    ScoreAdminDaoI scoreAdminDao;
    RatingSummaryAdminDaoI ratingSummaryAdminDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_submit_menu);

        ListView listView = findViewById(R.id.reviewlist);

        ReviewForOrder reviewForOrder = null;
        Intent intent = getIntent();
        if (intent.hasExtra("ordered_items")) {
            reviewForOrder = (ReviewForOrder) intent.getSerializableExtra("ordered_items");
        }
        if (intent.hasExtra("orderId")) {
            orderId = intent.getStringExtra("orderId");
        }

        if (reviewForOrder != null) {
            reviews = new ArrayList<>(reviewForOrder.getReviews());
            ReviewAdapter adapter = new ReviewAdapter(this, reviews, getApplicationContext());
            listView.setAdapter(adapter);
        }
//        reviewDao = new ReviewDao();
        authenticationController = new AuthenticationController(this);
        orderAdminDao = new OrderAdminFirestoreDao();
        reviewDao = new ReviewAdminFirestoreDao();
        menuItemAdminDao = new MenuItemAdminFireStoreDao();
        scoreAdminDao = new ScoreAdminFirestoreDao();
        ratingSummaryAdminDao = new RatingSummaryAdminFirestoreDao();
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
            reviewDao.addReviews(reviews);
            orderAdminDao.addReviewsAndRatingsToOrder(reviews);
            menuItemAdminDao.updateItemsWithRating(reviews);
            scoreAdminDao.modifyScores(reviews);
            ratingSummaryAdminDao.modifyRatingSummary(reviews);

            orderAdminDao.markOrderAsComplete(orderId, new OnResultListener<String>() {
                @Override
                public void onCallback(String status) {
                    Toast.makeText(SubmitReviewActivity.this, "Thanks for submitting the review", Toast.LENGTH_LONG);
                    LoginController.getInstance().logout();
                    authenticationController.goToHomePage();
                }
            });
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
                orderAdminDao.markOrderAsComplete(orderId, new OnResultListener<String>() {
                    @Override
                    public void onCallback(String status) {
                        Toast.makeText(MyApplication.getAppContext(), "No problem. Thank you!!", Toast.LENGTH_LONG).show();
                        LoginController.getInstance().logout();
                        authenticationController.goToHomePage();
                    }
                });
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
