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

public class IngredientDao {

    public static List<String> getIngredients(long id) {
        List<String> ingredients = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "_id  = " + id;


            Cursor cursor = db.query("MENU_ITEM_INGREDIENTS", new String[]{"_id", "INGREDIENT"}, whereClause, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    String ingredient = cursor.getString(1);
                    ingredients.add(ingredient);
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
        return ingredients;
    }

    public static void deleteIngredients(long id, List<String> items) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (String item : items) {
            String whereClause = "_id=" + id + " AND INGREDIENT = '" + item + "'";
            db.delete("MENU_ITEM_INGREDIENTS", whereClause, null);
        }
    }

    public static void insertIngredients(Long id, List<String> ingredients) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (String item : ingredients) {
                ContentValues ingredient = new ContentValues();
                ingredient.put("_id", id);
                ingredient.put("INGREDIENT", item);
                db.insert("MENU_ITEM_INGREDIENTS", null, ingredient);
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
