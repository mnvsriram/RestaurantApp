package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardButtonEnum;
import app.resta.com.restaurantapp.model.MenuCardButtonPropEnum;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuButtonDao {

    public void insertOrUpdateButton(MenuCardButton menuCardButton) {
        if (menuCardButton.getId() == 0) {
            insertMenuButton(menuCardButton);
            insertMenuButtonProps(menuCardButton);
        } else {
            updateButton(menuCardButton);
            deleteAllPropsForButton(menuCardButton.getId());
            insertMenuButtonProps(menuCardButton);
        }
    }

    private void insertMenuButton(MenuCardButton menuCardButton) {
        try {
            ContentValues groupMapping = new ContentValues();
            groupMapping.put("NAME", menuCardButton.getName());
            groupMapping.put("CARD_ID", menuCardButton.getCardId());
            groupMapping.put("BUTTON_ENUM_TYPE", menuCardButton.getLocation().getValue());

            groupMapping.put("ACTIVE", menuCardButton.isEnabled() ? "Y" : "N");

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long id = db.insert("MENU_CARD_BUTTONS", null, groupMapping);
            menuCardButton.setId(id);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertGroup", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    private void insertMenuButtonProps(MenuCardButton menuCardButton) {
        Map<MenuCardButtonPropEnum, String> props = menuCardButton.getProps();
        for (MenuCardButtonPropEnum propEnum : props.keySet()) {
            if (propEnum != null && props.get(propEnum) != null) {
                insertMenuButtonProp(menuCardButton.getId(), propEnum.getValue(), props.get(propEnum));
            }
        }
    }

    private void insertMenuButtonProp(long buttonId, int propCode, String value) {
        try {
            ContentValues groupMapping = new ContentValues();
            groupMapping.put("BUTTON_ID", buttonId);
            groupMapping.put("PROP_ID", propCode);
            groupMapping.put("VALUE", value);
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.insert("MENU_CARD_BUTTON_PROPS", null, groupMapping);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertGroup", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    private void updateButton(MenuCardButton menuCardButton) {
        try {
            String selection = "_id = ?";
            String[] selectionArgs = {String.valueOf(menuCardButton.getId())};
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("NAME", menuCardButton.getName());
            values.put("ACTIVE", menuCardButton.isEnabled() ? "Y" : "N");
            values.put("BUTTON_ENUM_TYPE", menuCardButton.getLocation().getValue());

            db.update(
                    "MENU_CARD_BUTTONS",
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


    public static void deleteAllPropsForButton(long buttonId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "BUTTON_ID= ?";
        String[] whereArgs = {buttonId + ""};
        db.delete("MENU_CARD_BUTTON_PROPS", whereClause, whereArgs);
    }

    public Map<MenuCardButtonPropEnum, String> getMenuButtonProps(long buttonId) {
        Map<MenuCardButtonPropEnum, String> props = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String whereClause = "BUTTON_ID= ?";
            String[] whereArgs = {buttonId + ""};

            Cursor cursor = db.query("MENU_CARD_BUTTON_PROPS", new String[]{"PROP_ID", "VALUE"}, whereClause, whereArgs, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    int propId = cursor.getInt(0);
                    String value = cursor.getString(1);
                    props.put(MenuCardButtonPropEnum.of(propId), value);
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

    public Map<MenuCardButtonEnum, MenuCardButton> getMenuCardButtons(long cardId) {
        Map<MenuCardButtonEnum, MenuCardButton> buttons = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String whereClause = "CARD_ID = ? ";
            String[] selectionArgs = {cardId + ""};

            Cursor cursor = db.query("MENU_CARD_BUTTONS", new String[]{"_id", "NAME", "BUTTON_ENUM_TYPE", "ACTIVE"}, whereClause, selectionArgs, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    MenuCardButton menuCardButton = new MenuCardButton();
                    long id = cursor.getLong(0);
                    String buttonName = cursor.getString(1);
                    String status = cursor.getString(3);


                    menuCardButton.setId(id);
                    menuCardButton.setCardId(cardId);
                    menuCardButton.setName(buttonName);
                    menuCardButton.setEnabled(status.equalsIgnoreCase("y") ? true : false);
                    menuCardButton.setProps(getMenuButtonProps(id));
                    menuCardButton.setLocation(MenuCardButtonEnum.of(cursor.getInt(2)));
                    buttons.put(MenuCardButtonEnum.of(cursor.getInt(2)), menuCardButton);

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
        return buttons;
    }


    public MenuCardButton getButtonInCard(long cardId, MenuCardButtonEnum menuCardButtonEnum) {
        MenuCardButton menuCardButton = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String whereClause = "CARD_ID = ? AND BUTTON_ENUM_TYPE= ?";
            String[] selectionArgs = {cardId + "", menuCardButtonEnum.getValue() + ""};

            Cursor cursor = db.query("MENU_CARD_BUTTONS", new String[]{"_id", "NAME", "ACTIVE"}, whereClause, selectionArgs, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    menuCardButton = new MenuCardButton();
                    long id = cursor.getLong(0);
                    String buttonName = cursor.getString(1);
                    String status = cursor.getString(2);
                    menuCardButton.setId(id);
                    menuCardButton.setCardId(cardId);
                    menuCardButton.setName(buttonName);
                    menuCardButton.setEnabled(status.equalsIgnoreCase("y") ? true : false);
                    menuCardButton.setProps(getMenuButtonProps(id));
                    menuCardButton.setLocation(menuCardButtonEnum);
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
        return menuCardButton;
    }
}
