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

import app.resta.com.restaurantapp.activity.NarrowMenuActivity;
import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.util.MyApplication;

public class IngredientDao {

    public Map<Long, List<Ingredient>> getAllIngredientsMappings() {
        Map<Long, List<Ingredient>> ingredientData = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = "SELECT ingredientMap._id, refData.INGREDIENT_NAME, refData.IMAGE FROM MENU_ITEM_INGREDIENTS ingredientMap INNER JOIN INGREDIENTS_DATA refData ON ingredientMap.INGREDIENT_ID=refData._id ";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    long itemId = cursor.getLong(0);
                    String ingredientName = cursor.getString(1);
                    String ingredientImage = cursor.getString(2);

                    Ingredient ingredient = new Ingredient();
                    ingredient.setImage(ingredientImage);
                    ingredient.setName(ingredientName);

                    List<Ingredient> ingredientsForThisItem = ingredientData.get(itemId);
                    if (ingredientsForThisItem == null) {
                        ingredientsForThisItem = new ArrayList<>();
                    }
                    ingredientsForThisItem.add(ingredient);
                    ingredientData.put(itemId, ingredientsForThisItem);
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
        return ingredientData;
    }


    public static List<Ingredient> getIngredientsForItem(long id) {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
//            String whereClause = "_id  = " + id;
            String sql = "SELECT refData._id,refData.INGREDIENT_NAME, refData.IMAGE FROM MENU_ITEM_INGREDIENTS ingredientMap INNER JOIN INGREDIENTS_DATA refData ON ingredientMap.INGREDIENT_ID=refData._id " +
                    " WHERE ingredientMap._id=?";
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
            //Cursor cursor = db.query("MENU_ITEM_INGREDIENTS", new String[]{"_id", "INGREDIENT"}, whereClause, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    Long ingredientId = cursor.getLong(0);
                    String ingredientName = cursor.getString(1);
                    String ingredientImage = cursor.getString(2);
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(ingredientId);
                    ingredient.setName(ingredientName);
                    ingredient.setImage(ingredientImage);
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

    public static void deleteIngredients(long id, List<Long> items) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Long item : items) {
            String whereClause = "_id=" + id + " AND INGREDIENT_ID = '" + item + "'";
            db.delete("MENU_ITEM_INGREDIENTS", whereClause, null);
        }
        NarrowMenuActivity.fetchData = true;
    }

    public static void deleteAllIngredientMappingsForIngredientId(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "INGREDIENT_ID= ?";
        String[] whereArgs = {id + ""};
        db.delete("MENU_ITEM_INGREDIENTS", whereClause, whereArgs);
    }

    public void deleteAllIngredientMappingsForItemId(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "_ID= ?";
        String[] whereArgs = {id + ""};
        db.delete("MENU_ITEM_INGREDIENTS", whereClause, whereArgs);
    }

    public static void insertIngredients(Long id, List<Long> ingredientIds) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (Long itemId : ingredientIds) {
                ContentValues ingredient = new ContentValues();
                ingredient.put("_id", id);
                ingredient.put("INGREDIENT_ID", itemId);
                db.insert("MENU_ITEM_INGREDIENTS", null, ingredient);
            }
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        NarrowMenuActivity.fetchData = true;
    }


    public void insertIngredientRefData(Ingredient ingredient) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("INGREDIENT_NAME", ingredient.getName());
            ingredientValues.put("IMAGE", ingredient.getImage());
            db.insert("INGREDIENTS_DATA", null, ingredientValues);
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public List<Ingredient> getIngredientsRefData() {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("INGREDIENTS_DATA", new String[]{"_id", "INGREDIENT_NAME", "IMAGE"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    long ingredientId = cursor.getLong(0);
                    String ingredientName = cursor.getString(1);
                    String imageName = cursor.getString(2);
                    Ingredient ingredientFromDB = new Ingredient();
                    ingredientFromDB.setId(ingredientId);
                    ingredientFromDB.setName(ingredientName);
                    ingredientFromDB.setImage(imageName);
                    ingredients.add(ingredientFromDB);
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


    public void deleteIngredientRefData(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "_id=" + id;
        db.delete("INGREDIENTS_DATA", whereClause, null);
        deleteAllIngredientMappingsForIngredientId(id);
    }

    public Ingredient getIngredientRefData(String ingredientName, long ignoreItemId) {
        Ingredient ingredient = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "LOWER(INGREDIENT_NAME)= ? AND _id != ? ";
            String[] selectionArgs = {ingredientName.toLowerCase(), ignoreItemId + ""};

            Cursor cursor = db.query("INGREDIENTS_DATA", new String[]{"_id", "INGREDIENT_NAME"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    ingredient = new Ingredient();
                    ingredient.setId(id);
                    ingredient.setName(name);
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
        return ingredient;
    }

}
