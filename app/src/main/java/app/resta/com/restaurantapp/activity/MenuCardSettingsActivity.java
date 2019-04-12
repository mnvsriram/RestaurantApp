package app.resta.com.restaurantapp.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminFireStoreDao;
import app.resta.com.restaurantapp.fragment.HomePageFragmentForAdmin;

public class MenuCardSettingsActivity extends BaseActivity {

    String menuCardId;
    MenuCardAdminDaoI menuCardAdminDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu_cards);
        initialize();
        setToolbar();
        loadIntentParams();
        setButton();
        loadScreenShotOfHomePage();
    }

    private void initialize() {
        menuCardAdminDao = new MenuCardAdminFireStoreDao();
    }


    private void setButton() {
        Button button = findViewById(R.id.createModifyMenuCard);
        if (menuCardId != null) {
            button.setText("Modify");
        } else {
            button.setText("Create Menu");
        }
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
        menuCardId = intent.getStringExtra("menuCardEdit_menuCardId");
    }

    public void loadScreenShotOfHomePage() {
        HomePageFragmentForAdmin frag = new HomePageFragmentForAdmin();
        frag.setMenuCardId(menuCardId);
        frag.setEnableAll(false);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.menuCardFragment_container, frag);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }


    public void createModifyMenuCard(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("activity_menucardEdit_cardId", menuCardId);
        authenticationController.goToMenuEditPage(params);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }
}
