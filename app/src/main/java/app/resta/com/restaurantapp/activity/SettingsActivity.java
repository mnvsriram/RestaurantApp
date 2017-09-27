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
        setToolbar();
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

    public void allItemsSettings(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupToOpen", 0l);
        params.put("groupMenuId", -1l);
        authenticationController.goToMenuPage(params);
    }

    public void showMenuTypeSettingsButton(View view) {
        Map<String, Object> params = new HashMap<>();
        authenticationController.goToMenuTypeSettingsPage(params);
    }

}