package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuCardPropEnum;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardDao {

    private MenuButtonDao buttonDao = new MenuButtonDao();

    public void insertOrUpdateCard(MenuCard menuCard) {
        if (menuCard.getId() == 0) {
            insertMenuCard(menuCard);
            insertMenuCardProps(menuCard);
        } else {
            updateCard(menuCard);
            deleteAllPropsForCard(menuCard.getId());
            insertMenuCardProps(menuCard);
        }
    }

    private void insertMenuCard(MenuCard menuCard) {
        try {
            ContentValues groupMapping = new ContentValues();
            groupMapping.put("NAME", menuCard.getName());
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long id = db.insert("MENU_CARDS", null, groupMapping);
            menuCard.setId(id);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertGroup", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    private void insertMenuCardProps(MenuCard menuCard) {
        Map<MenuCardPropEnum, String> props = menuCard.getProps();
        for (MenuCardPropEnum propEnum : props.keySet()) {
            insertMenuCardProp(menuCard.getId(), propEnum.getValue(), props.get(propEnum));
        }
    }

    private void insertMenuCardProp(long menuCardId, int propCode, String value) {
        try {
            ContentValues groupMapping = new ContentValues();
            groupMapping.put("CARD_ID", menuCardId);
            groupMapping.put("PROP_ID", propCode);
            groupMapping.put("VALUE", value);
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert("MENU_CARD_PROPS", null, groupMapping);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertGroup", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    private void updateCard(MenuCard menuCard) {
        try {
            String selection = "_id" + " LIKE ?";
            String[] selectionArgs = {String.valueOf(menuCard.getId())};
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("NAME", menuCard.getName());
            db.update(
                    "MENU_CARDS",
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


    public static void deleteAllPropsForCard(long cardId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "CARD_ID= ?";
        String[] whereArgs = {cardId + ""};
        db.delete("MENU_CARD_PROPS", whereClause, whereArgs);
    }

    public Map<MenuCardPropEnum, String> getMenuCardProps(long cardId) {
        Map<MenuCardPropEnum, String> props = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String whereClause = "CARD_ID= ?";
            String[] whereArgs = {cardId + ""};

            Cursor cursor = db.query("MENU_CARD_PROPS", new String[]{"PROP_ID", "VALUE"}, whereClause, whereArgs, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    int propId = cursor.getInt(0);
                    String value = cursor.getString(1);
                    props.put(MenuCardPropEnum.of(propId), value);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable52", Toast.LENGTH_LONG);
            toast.show();
        }
        return props;
    }

    public MenuCard getMenuCard(long cardId) {
        MenuCard menuCard = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String whereClause = "_id = ? ";
            String[] selectionArgs = {cardId + ""};

            Cursor cursor = db.query("MENU_CARDS", new String[]{"NAME"}, whereClause, selectionArgs, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    String cardName = cursor.getString(0);
                    menuCard = new MenuCard();
                    menuCard.setId(cardId);
                    menuCard.setName(cardName);
                    menuCard.setProps(getMenuCardProps(cardId));
                    menuCard.setButtons(buttonDao.getMenuCardButtons(cardId));
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable51", Toast.LENGTH_LONG);
            toast.show();
        }
        return menuCard;
    }


}
