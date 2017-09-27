package app.resta.com.restaurantapp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.lang.reflect.Field;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.util.ImageSaver;

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