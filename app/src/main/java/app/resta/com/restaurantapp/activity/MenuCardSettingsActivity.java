package app.resta.com.restaurantapp.activity;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.fragment.HomePageFragment;

public class MenuCardSettingsActivity extends BaseActivity {

    long menuCardId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu_cards);
        setToolbar();
        loadIntentParams();
        setButton();
        loadScreenShotOfHomePage();
    }

    private void setButton() {
        Button button = (Button) findViewById(R.id.createModifyMenuCard);
        if (menuCardId > 0) {
            button.setText("Modify");
        } else {
            button.setText("Create Menu");
        }
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
        menuCardId = intent.getLongExtra("menuCardEdit_menuCardId", 1l);
    }

    public void loadScreenShotOfHomePage() {
        HomePageFragment frag = new HomePageFragment();
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
