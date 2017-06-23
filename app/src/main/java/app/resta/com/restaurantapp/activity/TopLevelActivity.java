package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.LoginController;
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
        Intent intent = null;
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            intent = new Intent(this, OrderActivity.class);
        } else {
            String menuPageLayout = StyleUtil.layOutMap.get("menuPageLayout");

            if (menuPageLayout != null && menuPageLayout.equalsIgnoreCase("fragmentStyle")) {
                intent = new Intent(this, NarrowMenuActivity.class);
            } else {
                intent = new Intent(this, HorizontalMenuActivity.class);
            }

        }

        startActivity(intent);
    }

}