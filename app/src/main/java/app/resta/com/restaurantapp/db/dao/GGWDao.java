package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class GGWDao {

    public static List<RestaurantItem> getGGWMappings(long id) {
        List<RestaurantItem> ggwItems = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "_id  = " + id;


            Cursor cursor = db.query("MENU_GGW_MAPPING", new String[]{"_id", "MENU_GGW_MAPPING_ID"}, whereClause, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    Long mappingRefId = cursor.getLong(1);
                    RestaurantItem refObj = MenuItemDao.getAllItemsById().get(mappingRefId);
                    if (refObj != null) {
                        ggwItems.add(refObj);
                    }
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

        return ggwItems;
    }

    public static void deleteGGWItems(long itemId, List<Long> items) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Long item : items) {
            String whereClause = "_id=" + itemId + " AND MENU_GGW_MAPPING_ID = " + item;
            db.delete("MENU_GGW_MAPPING", whereClause, null);
        }
    }

    public static void deleteAllGGWItemsForId(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "_id= ? OR MENU_GGW_MAPPING_ID = ?";
        String[] whereArgs = {id + "", id + ""};
        db.delete("MENU_GGW_MAPPING", whereClause, whereArgs);

    }

    public static void insertGGWItems(Long id, List<Long> ggwItems) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (Long item : ggwItems) {
                ContentValues ggwItem = new ContentValues();
                ggwItem.put("_id", id);
                ggwItem.put("MENU_GGW_MAPPING_ID", item);
                db.insert("MENU_GGW_MAPPING", null, ggwItem);
            }
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }
}



