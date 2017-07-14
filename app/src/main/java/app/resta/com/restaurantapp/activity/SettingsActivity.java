package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;

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
        Map<String, Object> params = new HashMap<>();
        params.put("groupToOpen", 0l);
        authenticationController.goToMenuPage(params);
    }
}