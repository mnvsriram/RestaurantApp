package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuItemDao {
    public static boolean dataFetched = false;
    private static Map<Long, RestaurantItem> parentItems = new HashMap<>();
    private static Map<String, RestaurantItem> dishes;
    private static Map<Long, RestaurantItem> allItemsById;

    public static Map<Long, RestaurantItem> fetchMenuItems(boolean onlyActiveItems) {
        //this variable will be set to true once loaded. if any other tablet changes data. then this will be set to false. so taht next time the user launches this page, the data will be fetched from the db.
        if (!dataFetched) {
            loadMenuItems(onlyActiveItems);
            createHierarchy(allItemsById);
            parentItems = filterParentItems(allItemsById);
            setAllDishes(parentItems);
            dataFetched = true;
        }
        return parentItems;
    }

    public static Map<Long, RestaurantItem> getAllItemsById() {
        return allItemsById;
    }

    public static Map<String, RestaurantItem> getDishes() {
        return dishes;
    }

    private static void setAllDishes(Map<Long, RestaurantItem> parentItems) {
        dishes = new HashMap<>();
        if (parentItems != null && parentItems.size() > 0) {
            for (Long id : parentItems.keySet()) {
                RestaurantItem parent = parentItems.get(id);
                List<RestaurantItem> items = parent.getChildItems();
                if (items != null) {
                    for (RestaurantItem item : items) {
                        dishes.put(item.getName(), item);
                    }
                }
            }
        }
    }

    private static Map<Long, RestaurantItem> filterParentItems(Map<Long, RestaurantItem> items) {
        Map<Long, RestaurantItem> parentItems = new HashMap<>();
        for (RestaurantItem item : items.values()) {
            if (item.getParentId() == -1) {
                parentItems.put(item.getId(), item);
            }
        }
        return parentItems;
    }

    private static void createHierarchy(Map<Long, RestaurantItem> items) {
        for (RestaurantItem item : items.values()) {
            Long parentId = item.getParentId();
            if (parentId != -1) {
                RestaurantItem parent = items.get(parentId);
                item.setParentItem(parent);
                parent.addChildItem(item);
            }
        }
    }

    private static Map<Long, RestaurantItem> loadMenuItems(boolean onlyActiveItems) {
        allItemsById = new LinkedHashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = null;
            String orderBy = "ACTIVE DESC";
            if (onlyActiveItems) {
                whereClause = "ACTIVE = 'Y'";
            }

            Cursor cursor = db.query("MENU_ITEM", new String[]{"_id", "NAME", "PARENTMENUITEMID", "IMAGE", "PRICE", "ACTIVE", "DESCRIPTION"}, whereClause, null, null, null, orderBy);
            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    Long parentId = cursor.getLong(2);
                    String image = cursor.getString(3);
                    String price = cursor.getString(4);
                    String active = cursor.getString(5);
                    String description = cursor.getString(6);
                    RestaurantItem item = new RestaurantItem();
                    item.setId(id);
                    item.setName(name);
                    item.setParentId(parentId);
                    item.setDescription(description);
                    item.setImage(image);
                    item.setPrice(price);
                    item.setActive(active);
                    allItemsById.put(id, item);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        return allItemsById;
    }

    public static RestaurantItem getItem(String itemName, long parentId) {
        RestaurantItem item = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String whereClause = "ACTIVE = 'Y' AND PARENTMENUITEMID= ?  AND LOWER(NAME)= ?";
            String[] selectionArgs = {parentId + "", itemName.toLowerCase()};
            Cursor cursor = db.query("MENU_ITEM", new String[]{"_id", "NAME"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    item = new RestaurantItem();
                    item.setId(id);
                    item.setName(name);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        return item;
    }

    public static long insertOrUpdateMenuItem(RestaurantItem item) {
        long status = 0;
        if (item.getId() == 0) {
            status = insertMenuItem(item);
        } else {
            status = updateMenuItem(item);
        }
        refreshData();
        return status;
    }

    public static void refreshData() {
        MenuItemDao.dataFetched = false;
        MenuItemDao.fetchMenuItems(!LoginController.getInstance().isAdminLoggedIn());
    }

    public static boolean deleteMenuItem(RestaurantItem item) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean result = db.delete("MENU_ITEM", "_id=" + item.getId(), null) > 0;
        refreshData();
        return result;
    }

    public static long insertMenuItem(RestaurantItem item) {
        long count = 0;
        try {

            ContentValues menuitem = new ContentValues();
            menuitem.put("PARENTMENUITEMID", item.getParentId());
            menuitem.put("DESCRIPTION", item.getDescription());
            menuitem.put("Name", item.getName());
            menuitem.put("IMAGE", item.getImage());
            menuitem.put("PRICE", item.getPrice());
            menuitem.put("ACTIVE", item.getActive());


            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            count = db.insert("MENU_ITEM", null, menuitem);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        return count;
    }

    public static long updateMenuItem(RestaurantItem item) {
        long count = 0;
        try {


            String selection = "_id" + " LIKE ?";
            String[] selectionArgs = {String.valueOf(item.getId())};


            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("NAME", item.getName());

            values.put("DESCRIPTION", item.getDescription());
            values.put("PRICE", item.getPrice());
            values.put("ACTIVE", item.getActive());
            values.put("PARENTMENUITEMID", item.getParentId());
            values.put("IMAGE", item.getImage());

            count = db.update(
                    "MENU_ITEM",
                    values,
                    selection,
                    selectionArgs);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        return count;
    }

}



