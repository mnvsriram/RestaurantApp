package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.MenuGroup;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuItemGroupDao {
    private static Map<String, Long> grouMenuItemsNameByIdCache = new HashMap<>();
    private static Map<Long, String> grouMenuItemsIdByNameCache = new HashMap<>();

    private static boolean dataFetched = false;

    public void insertOrUpdateGroup(MenuGroup item) {
        if (item.getId() == 0) {
            insertGroup(item.getName());
        } else {
            updateGroupName(item);
        }
        refreshData();
    }

    private void insertGroup(String groupName) {
        try {

            ContentValues groupMapping = new ContentValues();
            groupMapping.put("NAME", groupName);
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.insert("MENU_TYPE", null, groupMapping);

            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public Map<String, Long> getMenuGroupsByName() {
        loadMenuGroups();
        return grouMenuItemsNameByIdCache;
    }


    public Map<Long, String> getMenuGroupsById() {
        loadMenuGroups();
        return grouMenuItemsIdByNameCache;
    }

    public void loadMenuGroups() {
        if (!dataFetched) {
            grouMenuItemsNameByIdCache = new HashMap<>();
            grouMenuItemsIdByNameCache = new HashMap<>();
            try {
                SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("MENU_TYPE", new String[]{"_id", "NAME"}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    try {
                        long groupMenuId = cursor.getLong(0);
                        String groupMenuName = cursor.getString(1);
                        grouMenuItemsNameByIdCache.put(groupMenuName, groupMenuId);
                        grouMenuItemsIdByNameCache.put(groupMenuId, groupMenuName);
                    } catch (Exception e) {
                        continue;
                    }
                }
                cursor.close();
                db.close();
            } catch (Exception e) {
                Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable5", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


    public void updateGroupName(MenuGroup group) {
        try {
            String selection = "_id" + " LIKE ?";
            String[] selectionArgs = {String.valueOf(group.getId())};

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("NAME", group.getName());
            db.update(
                    "MENU_TYPE",
                    values,
                    selection,
                    selectionArgs);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable13", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void refreshData() {
        grouMenuItemsIdByNameCache = new HashMap<>();
        grouMenuItemsNameByIdCache = new HashMap<>();
        dataFetched = false;
    }

}
