package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

    public void showTagsSettingsPage(View view) {
        authenticationController.goToTagsSettingsPage();
    }

    public void showIngredientsSettingsPage(View view) {
        authenticationController.goToIngredientsSettingsPage();
    }

    public void showFoodMenuSettings(View view) {
        authenticationController.goToMenuPage();
    }
}