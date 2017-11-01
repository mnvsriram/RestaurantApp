package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuTypeDao {
    private static Map<String, Long> grouMenuItemsNameByIdCache = new HashMap<>();
    private static Map<Long, MenuType> menuTypeIdByNameCache = new HashMap<>();

    private static boolean dataFetched = false;

    public void insertOrUpdateGroup(MenuType menuType) {
        if (menuType.getId() == 0) {
            insertGroup(menuType);
        } else {
            updateGroup(menuType);
        }
        refreshData();
    }

    private void insertGroup(MenuType menuType) {
        try {

            ContentValues groupMapping = new ContentValues();
            groupMapping.put("NAME", menuType.getName());
            groupMapping.put("PRICE", (menuType.getPrice() != null && menuType.getPrice().trim().length() > 0 && !menuType.getPrice().trim().equals("0")) ? menuType.getPrice() : null);
            groupMapping.put("SHOW_PRICE_FOR_CHILDREN", menuType.getShowPriceOfChildren());
            groupMapping.put("DESCRIPTION", menuType.getDescription());
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.insert("MENU_TYPE", null, groupMapping);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertGroup", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public Map<String, Long> getMenuGroupsByName() {
        loadMenuGroups();
        return grouMenuItemsNameByIdCache;
    }


    public Map<Long, MenuType> getMenuGroupsById() {
        loadMenuGroups();
        return menuTypeIdByNameCache;
    }

    public void loadMenuGroups() {
        if (!dataFetched) {
            grouMenuItemsNameByIdCache = new HashMap<>();
            menuTypeIdByNameCache = new HashMap<>();
            try {
                SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("MENU_TYPE", new String[]{"_id", "NAME", "PRICE", "SHOW_PRICE_FOR_CHILDREN", "DESCRIPTION"}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    try {
                        long groupMenuId = cursor.getLong(0);
                        String groupMenuName = cursor.getString(1);
                        String price = cursor.getString(2);
                        String showPriceOfChildren = cursor.getString(3);
                        String description = cursor.getString(4);

                        MenuType menuType = new MenuType();
                        menuType.setId(groupMenuId);
                        menuType.setName(groupMenuName);
                        if (showPriceOfChildren == null || !showPriceOfChildren.equalsIgnoreCase("N")) {
                            showPriceOfChildren = "Y";
                        }
                        menuType.setShowPriceOfChildren(showPriceOfChildren);
                        if (price != null && price.trim().length() > 0) {
                            menuType.setPrice(price);
                        } else {
                            menuType.setPrice(null);
                        }
                        menuType.setDescription(description);
                        grouMenuItemsNameByIdCache.put(groupMenuName, groupMenuId);
                        menuTypeIdByNameCache.put(groupMenuId, menuType);
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
            dataFetched = true;
        }
    }


    public void updateGroup(MenuType group) {
        try {
            String selection = "_id" + " LIKE ?";
            String[] selectionArgs = {String.valueOf(group.getId())};

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("NAME", group.getName());
            values.put("PRICE", (group.getPrice() != null && group.getPrice().trim().length() > 0 && !group.getPrice().trim().equals("0")) ? group.getPrice() : null);
            values.put("SHOW_PRICE_FOR_CHILDREN", group.getShowPriceOfChildren());
            values.put("DESCRIPTION", group.getDescription());
            db.update(
                    "MENU_TYPE",
                    values,
                    selection,
                    selectionArgs);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable13-updateGroupName", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private void refreshData() {
        menuTypeIdByNameCache = new HashMap<>();
        grouMenuItemsNameByIdCache = new HashMap<>();
        dataFetched = false;
    }

}
