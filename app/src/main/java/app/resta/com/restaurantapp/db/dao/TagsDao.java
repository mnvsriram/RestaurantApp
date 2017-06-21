package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.util.MyApplication;

public class TagsDao {

    public static List<String> getTags(long id) {
        List<String> tags = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "_id  = " + id;
            Cursor cursor = db.query("MENU_ITEM_TAGS", new String[]{"_id", "TAG"}, whereClause, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    String tag = cursor.getString(1);
                    tags.add(tag);
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
        return tags;
    }

    public static void deleteTags(long id, List<String> items) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (String item : items) {
            String whereClause = "_id=" + id + " AND TAG = '" + item + "'";
            db.delete("MENU_ITEM_TAGS", whereClause, null);
        }
    }

    public static void insertTags(Long id, List<String> tags) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (String item : tags) {
                ContentValues tag = new ContentValues();
                tag.put("_id", id);
                tag.put("TAG", item);
                db.insert("MENU_ITEM_TAGS", null, tag);
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
