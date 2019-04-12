package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuTypeListAdapter;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;

public class MenuTypeSettingsActivity extends BaseActivity {

    List<MenuType> dataModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu_type);
        initialize();
        setToolbar();
    }

    private void initialize() {
        MenuTypeAdminDaoI menuTypeAdminDao = new MenuTypeAdminFireStoreDao();
        menuTypeAdminDao.getAllMenuTypes(new OnResultListener<List<MenuType>>() {
            @Override
            public void onCallback(List<MenuType> menuTypes) {
                dataModels = menuTypes;
                setFields();
            }
        });
    }

    private void setFields() {
        setListAdapter();
        setHeader();
    }

    private void setHeader() {
        TextView menuTypeCounterHeader = findViewById(R.id.menuTypeCounterHeader);
        menuTypeCounterHeader.setText(dataModels.size() + " menus found.");
    }


    public void addMenuType(View view) {
        authenticationController.goToMenuTypeAddPage(null);
    }

    private void setListAdapter() {
        ListView listView = findViewById(R.id.menuTypeList);
        MenuTypeListAdapter listAdapter = new MenuTypeListAdapter(dataModels, this);
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }

}
