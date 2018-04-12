package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.wifi.DeviceDetails;

public class IngredientDao {
    private static final String TAG = "IngredientDao";
    private static boolean fetchData = true;
    private static Map<String, List<Ingredient>> ingredientsData = new HashMap<>();


    public List<Ingredient> getIngredientsDataForItem(String itemAppId) {
        if (fetchData) {
            ingredientsData = new HashMap<>();
            ingredientsData.putAll(getAllIngredientsMappings());
            fetchData = false;
        }
        return ingredientsData.get(itemAppId);
    }


    private Map<String, List<Ingredient>> getAllIngredientsMappings() {
        Map<String, List<Ingredient>> ingredientData = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
//            String sql = "SELECT ingredientMap._id, refData.INGREDIENT_NAME, refData.IMAGE FROM MENU_ITEM_INGREDIENTS ingredientMap INNER JOIN INGREDIENTS_DATA refData ON ingredientMap.INGREDIENT_ID=refData._id ";
            String sql =
                    " SELECT \n" +
                            " ingredientMap.ITEM_APP_ID, " +
                            " refData.INGREDIENT_NAME, \n" +
                            " refData.IMAGE, \n" +
                            " ingredientMap.INGREDIENT_APP_ID " +
                            " FROM MENU_ITEM_INGREDIENTS ingredientMap \n" +
                            " INNER JOIN INGREDIENTS_DATA refData ON ingredientMap.INGREDIENT_APP_ID=refData.ID || '_' ||refData.DEVICE_NAME\n";

            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    String itemId = cursor.getString(0);
                    String ingredientName = cursor.getString(1);
                    String ingredientImage = cursor.getString(2);

                    String ingredientAppId = cursor.getString(3);

                    Ingredient ingredient = new Ingredient();
                    ingredient.setImage(ingredientImage);
                    ingredient.setName(ingredientName);
ingredient.setAppId(ingredientAppId);
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
            String errorMessage = "Error while fetching ingredients for items";
            Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, errorMessage, e);
        }
        return ingredientData;
    }

    public List<Ingredient> getIngredientsRefData() {
        List<Ingredient> ingredients = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("INGREDIENTS_DATA", new String[]{"ID", "DEVICE_NAME", "INGREDIENT_NAME", "IMAGE"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    long ingredientId = cursor.getLong(0);
                    String deviceName = cursor.getString(1);
                    String ingredientName = cursor.getString(2);
                    String imageName = cursor.getString(3);
                    Ingredient ingredientFromDB = new Ingredient();
                    ingredientFromDB.setAppId(ingredientId + "_" + deviceName);
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
            String errorMessage = "Error while retrieving ingredients data";
            Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, errorMessage, e);
        }
        return ingredients;
    }
//
//    public static List<Ingredient> getIngredientsForItem(String id) {
//        List<Ingredient> ingredients = new ArrayList<>();
//        try {
//            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
//            SQLiteDatabase db = dbHelper.getReadableDatabase();
////            String whereClause = "_id  = " + id;
//            //String sql = "SELECT refData._id,refData.INGREDIENT_NAME, refData.IMAGE FROM MENU_ITEM_INGREDIENTS ingredientMap INNER JOIN INGREDIENTS_DATA refData ON ingredientMap.INGREDIENT_ID=refData._id " +
//            //      " WHERE ingredientMap._id=?";
//
//            String sql =
//                    " SELECT \n" +
//                            " ingredientMap.INGREDIENT_APP_ID, " +
//                            " refData.INGREDIENT_NAME, \n" +
//                            " refData.IMAGE \n" +
//                            " FROM MENU_ITEM_INGREDIENTS ingredientMap \n" +
//                            " INNER JOIN INGREDIENTS_DATA refData ON ingredientMap.INGREDIENT_APP_ID=refData.ID || '_' ||refData.DEVICE_NAME\n" +
//                            " WHERE ingredientMap.ITEM_APP_ID=?";
//            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
//            //Cursor cursor = db.query("MENU_ITEM_INGREDIENTS", new String[]{"_id", "INGREDIENT"}, whereClause, null, null, null, null);
//            while (cursor.moveToNext()) {
//                try {
//                    String ingredientAppId = cursor.getString(0);
//                    String ingredientName = cursor.getString(1);
//                    String ingredientImage = cursor.getString(2);
//                    Ingredient ingredient = new Ingredient();
//                    ingredient.setAppId(ingredientAppId);
//                    ingredient.setName(ingredientName);
//                    ingredient.setImage(ingredientImage);
//                    ingredients.add(ingredient);
//                } catch (Exception e) {
//                    continue;
//                }
//            }
//            cursor.close();
//            db.close();
//        } catch (Exception e) {
//            String errorMessage = "Error while retrieving ingredients for chosen item.";
//            Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
//            Log.e(TAG, errorMessage, e);
//        }
//        return ingredients;
//    }

    public static void deleteIngredients(String itemAppId, List<String> ingredients) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (String ingredientAppId : ingredients) {
            String whereClause = "ITEM_APP_ID= ?  AND INGREDIENT_APP_ID = ?";
            String[] whereArgs = {itemAppId, ingredientAppId};
            db.delete("MENU_ITEM_INGREDIENTS", whereClause, whereArgs);
        }
        fetchData = true;
    }

    public static void deleteAllIngredientMappingsForIngredientId(String ingredientAppId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "INGREDIENT_APP_ID= ?";
        String[] whereArgs = {ingredientAppId};
        db.delete("MENU_ITEM_INGREDIENTS", whereClause, whereArgs);
        fetchData = true;
    }

    public void deleteAllIngredientMappingsForItemId(String itemAppId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "ITEM_APP_ID= ?";
        String[] whereArgs = {itemAppId};
        db.delete("MENU_ITEM_INGREDIENTS", whereClause, whereArgs);
        fetchData = true;
    }

    public static void insertIngredients(String itemAppId, List<String> ingredientAppIds) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (String ingredientAppId : ingredientAppIds) {
                ContentValues ingredient = new ContentValues();
                ingredient.put("ITEM_APP_ID", itemAppId);
                ingredient.put("INGREDIENT_APP_ID", ingredientAppId);
                db.insert("MENU_ITEM_INGREDIENTS", null, ingredient);
            }
            db.close();
        } catch (Exception e) {
            String errorMessage = "Error while saving ingredients";
            Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, errorMessage, e);
        }
        fetchData = true;
    }


    public long getNextValForIngredientId() {
        long maxIngredientId = 1;
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "select MAX(ID) from INGREDIENTS_DATA";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.moveToFirst()) {
            maxIngredientId = cursor.getLong(0);
        }
        cursor.close();
        db.close();
        return maxIngredientId + 1;
    }

    public void insertIngredientRefData(Ingredient ingredient) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues ingredientValues = new ContentValues();

            ingredientValues.put("ID", getNextValForIngredientId());
            ingredientValues.put("DEVICE_NAME", DeviceDetails.THIS_DEVICE_NAME);
            ingredientValues.put("INGREDIENT_NAME", ingredient.getName());
            ingredientValues.put("IMAGE", ingredient.getImage());
            db.insert("INGREDIENTS_DATA", null, ingredientValues);
            db.close();
        } catch (Exception e) {
            String errorMessage = "Error while inserting ingredient data";
            Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, errorMessage, e);
        }
        fetchData = true;
    }


    public void deleteIngredientRefData(String ingredientAppId) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String whereClause = "ID || '_' || DEVICE_NAME ='" + ingredientAppId + "'";
            db.delete("INGREDIENTS_DATA", whereClause, null);
            deleteAllIngredientMappingsForIngredientId(ingredientAppId);
        } catch (Exception e) {
            String errorMessage = "Error while deleting ingredient.";
            Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e("TAG", errorMessage, e);
        }
        fetchData = true;
    }

    public Ingredient getIngredientRefData(String ingredientName, String ignoreIngredientAppId) {
        Ingredient ingredient = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "LOWER(INGREDIENT_NAME)= ? AND ID || '_' || DEVICE_NAME  != '" + ignoreIngredientAppId + "' ";
            String[] selectionArgs = {ingredientName.toLowerCase()};

            Cursor cursor = db.query("INGREDIENTS_DATA", new String[]{"ID", "DEVICE_NAME", "INGREDIENT_NAME"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                try {

                    long ingredientId = cursor.getLong(0);
                    String deviceName = cursor.getString(1);
                    String name = cursor.getString(2);
                    ingredient = new Ingredient();
                    ingredient.setAppId(ingredientId + "_" + deviceName);
                    ingredient.setName(name);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            String errorMessage = "Error while getting ingredient ref data.";
            Toast.makeText(MyApplication.getAppContext(), errorMessage, Toast.LENGTH_SHORT).show();
            Log.e(TAG, errorMessage, e);
        }
        return ingredient;
    }
}
