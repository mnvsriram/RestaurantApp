package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ItemListInGroupAdapter;
import app.resta.com.restaurantapp.adapter.MenuTypeListAdapter;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.validator.TagsValidator;

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
