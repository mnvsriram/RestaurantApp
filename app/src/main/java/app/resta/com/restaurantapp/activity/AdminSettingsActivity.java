package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.util.FireStoreUtil;
import app.resta.com.restaurantapp.util.MyApplication;

public class AdminSettingsActivity extends BaseActivity {
    MenuCardAdminDaoI menuCardAdminDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_settings);
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
        if (!FireStoreUtil.isInternetAvailable()) {
            Toast.makeText(MyApplication.getAppContext(), "Please connect to internet to modify Tags", Toast.LENGTH_LONG).show();
        } else {
            authenticationController.goToTagsSettingsPage();
        }
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
        if (!FireStoreUtil.isInternetAvailable()) {
            Toast.makeText(MyApplication.getAppContext(), "Please connect to internet to modify Ingredients", Toast.LENGTH_LONG).show();
        } else {
            authenticationController.goToIngredientsSettingsPage();
        }
    }

    public void allItemsSettings(View view) {
        if (!FireStoreUtil.isInternetAvailable()) {
            Toast.makeText(MyApplication.getAppContext(), "Please connect to internet to modify All Items", Toast.LENGTH_LONG).show();
        } else {
            Map<String, Object> params = new HashMap<>();
            authenticationController.goToMenuPage(params);
        }
    }

    public void showMenuTypeSettingsButton(View view) {
        if (!FireStoreUtil.isInternetAvailable()) {
            Toast.makeText(MyApplication.getAppContext(), "Please connect to internet to modify Menu Types", Toast.LENGTH_LONG).show();
        } else {
            Map<String, Object> params = new HashMap<>();
            authenticationController.goToMenuTypeSettingsPage(params);
        }
    }
}