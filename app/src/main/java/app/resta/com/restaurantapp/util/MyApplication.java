package app.resta.com.restaurantapp.util;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.controller.RestaurantItemExtraDataController;
import app.resta.com.restaurantapp.db.dao.GGWDao;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.FilePicker;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.Paths;
import app.resta.com.restaurantapp.util.StyleUtil;
import app.resta.com.restaurantapp.validator.RestaurantItemValidator;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.controller.RestaurantItemExtraDataController;
import app.resta.com.restaurantapp.db.dao.GGWDao;
import app.resta.com.restaurantapp.db.dao.IngredientDao;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.FilePicker;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.Paths;
import app.resta.com.restaurantapp.util.StyleUtil;
import app.resta.com.restaurantapp.validator.RestaurantItemValidator;

/**
 * Created by Sriram on 13/01/2017.
 */
public class MyApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();

    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
