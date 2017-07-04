package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

public class TopLevelActivity extends BaseActivity {

    private ImageButton adminLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String layout = StyleUtil.layOutMap.get("mainPageLayout");
        if (layout != null && layout.equalsIgnoreCase("second")) {
            setContentView(R.layout.activity_second);
        } else {
            setContentView(R.layout.activity_top_level);
        }
        setStyle();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //AdminLoginDialog.show(TopLevelActivity.this);

    }

    private void setStyle() {
        int layoutID = getResources().getIdentifier("mainlayout", "id", getPackageName());
        RelativeLayout mainLayout = (RelativeLayout) findViewById(layoutID);
        mainLayout.setBackgroundColor(StyleUtil.colorMap.get("mainPageBackground"));

    }

    public void showFoodMenu(View view) {
        authenticationController.goToMenuPage();
    }


    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

}