package app.resta.com.restaurantapp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ReviewAdapter;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.ReviewDao;
import app.resta.com.restaurantapp.fragment.OrderListFragment;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.model.ReviewForOrder;

public class SubmitReviewActivity extends BaseActivity {

    OrderListFragment frag = new OrderListFragment();
    List<ReviewForDish> reviews;
    ReviewDao reviewDao;
    AuthenticationController authenticationController;

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


        if (reviewForOrder != null) {
            reviews = new ArrayList<>(reviewForOrder.getReviews());
            ReviewAdapter adapter = new ReviewAdapter(this, reviews, getApplicationContext());
            listView.setAdapter(adapter);
        }
        reviewDao = new ReviewDao();
        authenticationController = new AuthenticationController(this);
    }

    public void submitReview(View view) {
        reviewDao.saveReviews(reviews);
        Toast.makeText(this, "Thanks for submitting the review", Toast.LENGTH_LONG);
        LoginController.getInstance().logout();
        authenticationController.goToHomePage();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }


}
