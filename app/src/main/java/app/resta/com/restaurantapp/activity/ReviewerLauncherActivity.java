package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;

import app.resta.com.restaurantapp.R;

public class ReviewerLauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewer_launch);
        setToolbar();
    }


    @Override
    public void onBackPressed() {

    }

    public void editOrdersPage(View view) {
        authenticationController.goToOrderSummaryPage();
    }

    public void takeOrderPage(View view) {
        authenticationController.goToReviewerMenuPage();
    }
}