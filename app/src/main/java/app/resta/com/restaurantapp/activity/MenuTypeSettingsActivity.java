package app.resta.com.restaurantapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuTypeListAdapter;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.MenuType;

public class MenuTypeSettingsActivity extends BaseActivity {

    private ListView listView;
    private MenuTypeListAdapter listAdapter;
    List<MenuType> dataModels;
    private MenuTypeDao menuTypeDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_menu_type);
        initialize();
        setFields();
        setToolbar();
    }

    private void initialize() {
        menuTypeDao = new MenuTypeDao();
        dataModels = new ArrayList<>(menuTypeDao.getMenuGroupsById().values());
    }

    private void setFields() {
        setListAdapter();
        setHeader();
    }

    private void setHeader() {
        TextView menuTypeCounterHeader = (TextView) findViewById(R.id.menuTypeCounterHeader);
        menuTypeCounterHeader.setText(dataModels.size() + " menus found.");
    }


    public void addMenuType(View view) {
        authenticationController.goToMenuTypeAddPage(null);
    }

    private void setListAdapter() {
        listView = (ListView) findViewById(R.id.menuTypeList);
        listAdapter = new MenuTypeListAdapter(dataModels, this);
        listView.setAdapter(listAdapter);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }

}
