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

import app.resta.com.restaurantapp.cache.RestaurantCache;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuItemParentDao {
    public Map<Long, RestaurantItem> loadMenuParentItems() {
        Map<Long, RestaurantItem> allParentItemsById = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String sql = "select itemParent._id, itemParent.NAME, itemParent.MENU_ID, menuType.NAME,itemParent.ACTIVE from MENU_ITEM_PARENT itemParent , MENU_TYPE menuType where itemParent.MENU_ID = menuType._id";
            if (!LoginController.getInstance().isAdminLoggedIn()) {
                sql += " and itemParent.ACTIVE = 'Y'";
            }
            sql += " order by itemParent.ACTIVE DESC";

            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    Long menuId = cursor.getLong(2);
                    String menuName = cursor.getString(3);
                    String active = cursor.getString(4);

                    RestaurantItem item = new RestaurantItem();
                    item.setId(id);
                    item.setName(name);
                    item.setActive(active);
                    item.setMenuTypeId(menuId);
                    item.setMenuTypeName(menuName);

                    allParentItemsById.put(id, item);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable14 loadMenuParentItems", Toast.LENGTH_LONG);
            toast.show();
        }
        return allParentItemsById;
    }


    public List<RestaurantItem> loadChildrenForParentId(long parentId) {
        List<RestaurantItem> childItems = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String sql = "select ITEM_ID from MENU_ITEM_PARENT_MAPPING where PARENT_ID = ?";
            String[] selectionArgs = {String.valueOf(parentId)};

            Cursor cursor = db.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                try {
                    Long itemId = cursor.getLong(0);

                    RestaurantItem item = RestaurantCache.allParentItemsById.get(itemId);
                    if (item != null) {
                        childItems.add(item);
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable14 loadMenuParentItems", Toast.LENGTH_LONG);
            toast.show();
        }
        return childItems;
    }

    public boolean isChildExistForParent(long itemId, long parentId) {
        boolean exists = false;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String sql = "select ITEM_ID from MENU_ITEM_PARENT_MAPPING where ITEM_ID= ? AND PARENT_ID = ?";
            String[] selectionArgs = {String.valueOf(itemId), String.valueOf(parentId)};

            Cursor cursor = db.rawQuery(sql, selectionArgs);
            while (cursor.moveToNext()) {
                try {
                    exists = true;
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable14 loadMenuParentItems", Toast.LENGTH_LONG);
            toast.show();
        }
        return exists;
    }

    public long insertMenuParent(RestaurantItem itemParent) {
        long newId = 0;
        try {
            ContentValues menuItemParent = new ContentValues();
            menuItemParent.put("Name", itemParent.getName());
            menuItemParent.put("MENU_ID", itemParent.getMenuTypeId());
            menuItemParent.put("ACTIVE", itemParent.getActive());

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            newId = db.insert("MENU_ITEM_PARENT", null, menuItemParent);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertMenuParent", Toast.LENGTH_LONG);
            toast.show();
        }
        return newId;
    }


    public void updateMenuItemParent(RestaurantItem itemParent) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String selection = "_id = ?";
            String[] selectionArgs = {String.valueOf(itemParent.getId())};

            ContentValues values = new ContentValues();
            values.put("NAME", itemParent.getName());
            values.put("ACTIVE", itemParent.getActive());
            values.put("MENU_ID", itemParent.getMenuTypeId());
            db.update("MENU_ITEM_PARENT", values, selection, selectionArgs);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable13-updateMenuItemParent", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public void deleteAllMappingsForItem(long itemId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "ITEM_ID= ?";
        String[] whereArgs = {itemId + ""};
        db.delete("MENU_ITEM_PARENT_MAPPING", whereClause, whereArgs);
        RestaurantCache.refreshCache();
    }


    public void deleteMappingsForItem(long id, long parentId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "ITEM_ID= ? and PARENT_ID=?";
        String[] whereArgs = {id + "", parentId + ""};
        db.delete("MENU_ITEM_PARENT_MAPPING", whereClause, whereArgs);
        RestaurantCache.dataFetched = false;
    }

    public long insertParentChildMapping(long itemId, long parentId) {
        long newId = 0;
        try {
            ContentValues menuItemParent = new ContentValues();
            menuItemParent.put("ITEM_ID", itemId);
            menuItemParent.put("PARENT_ID", parentId);

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            newId = db.insert("MENU_ITEM_PARENT_MAPPING", null, menuItemParent);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12-insertParentChildMapping", Toast.LENGTH_LONG);
            toast.show();
        }
        RestaurantCache.dataFetched = false;
        return newId;
    }


    public void insertOrUpdateMenuItemParent(RestaurantItem itemParent) {
        if (itemParent.getId() == 0) {
            insertMenuParent(itemParent);
        } else {
            updateMenuItemParent(itemParent);
        }
        RestaurantCache.refreshCache();
    }


    public Map<Long, List<Long>> fetchParentsForItems() {
        Map<Long, List<Long>> parentMapping = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();


            String sql = "select ITEM_ID,PARENT_ID from MENU_ITEM_PARENT_MAPPING";

            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    Long itemId = cursor.getLong(0);
                    Long parentId = cursor.getLong(1);
                    List<Long> parents = parentMapping.get(itemId);
                    if (parents == null) {
                        parents = new ArrayList<>();
                    }
                    parents.add(parentId);
                    parentMapping.put(itemId, parents);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable14 loadMenuItemParentMapping", Toast.LENGTH_LONG);
            toast.show();
        }
        return parentMapping;
    }

}
