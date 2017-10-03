package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import app.resta.com.restaurantapp.R;

public class MenuButtonEditActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_card_button_edit);
        setToolbar();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setFields();
    }

    private void setFields() {
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToMenuEditPage(null);
    }

}
