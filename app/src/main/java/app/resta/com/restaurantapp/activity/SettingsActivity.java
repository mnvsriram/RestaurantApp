package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;

public class SettingsActivity extends BaseActivity {

    MenuCardAdminDaoI menuCardAdminDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initialize();
        setToolbar();
    }

    private void initialize() {
        menuCardAdminDao = new MenuCardAdminFireStoreDao();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

    public void showTagsSettingsPage(View view) {
        authenticationController.goToTagsSettingsPage();
    }

    public void menuCardSettingsPage(View view) {
        final Map<String, Object> params = new HashMap<>();
        //TODO.. This goes to default menu card. If there should be more than one menu card, then it should go to the page where there is a list of menu cards and then user will choose which one to edit or to create.
        menuCardAdminDao.getDefaultCard(new OnResultListener<MenuCard>() {
            @Override
            public void onCallback(MenuCard menuCardFromDB) {
                if (menuCardFromDB != null) {
                    String menuCardId = menuCardFromDB.getId();
                    params.put("menuCardEdit_menuCardId", menuCardId);
                }
                authenticationController.goToMenuCardSettingsPage(params);
            }
        });
    }

    public void showUpdateSettingsPage(View view) {
        authenticationController.goToMenuClusterNetworkSettingsPage(null);
    }

    public void showIngredientsSettingsPage(View view) {
        authenticationController.goToIngredientsSettingsPage();
    }

    public void allItemsSettings(View view) {
        Map<String, Object> params = new HashMap<>();
//        params.put("groupToOpen", "ALL_ITEMS");
//        params.put("groupMenuId", "ALL_ITEMS");
        authenticationController.goToMenuPage(params);
    }

    public void showMenuTypeSettingsButton(View view) {
        Map<String, Object> params = new HashMap<>();
        authenticationController.goToMenuTypeSettingsPage(params);
    }

}