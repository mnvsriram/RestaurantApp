package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;

public class TopLevelActivity extends BaseActivity {

    private ImageButton adminLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_level_new);
        setToolbar();
    }

    public void showFoodMenu(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupToOpen", 0l);
        params.put("groupMenuId", 1l);
        authenticationController.goToMenuPage(params);
    }

    public void showDrinksMenu(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("groupToOpen", 0l);
        params.put("groupMenuId", 2l);
        authenticationController.goToMenuPage(params);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

}