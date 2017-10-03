package app.resta.com.restaurantapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuCardDao;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuCardEditActivity extends BaseActivity {

    private int menuCardId;
    private MenuCardDao menuCardDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_card_edit);
        setToolbar();
        initialize();
        loadIntentParams();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setFields();
    }

    private void initialize() {
        menuCardDao = new MenuCardDao();
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
        menuCardId = intent.getIntExtra("activity_menucardEdit_cardId", 0);
    }

    private void setFields() {

    }

    public void goToButtonEditPage(View view) {
        authenticationController.goToMenuCardButtonEditPage(null);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToMenuCardSettingsPage();
    }

    public void save(View view) {
        menuCardDao.insertOrUpdateCard();
        authenticationController.goToMenuCardSettingsPage();
    }
}
