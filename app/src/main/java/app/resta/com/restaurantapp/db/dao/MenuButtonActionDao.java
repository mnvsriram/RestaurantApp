package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuCardButton;
import app.resta.com.restaurantapp.model.MenuCardLayoutEnum;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuButtonActionDao {

    private MenuTypeDao menuTypeDao = new MenuTypeDao();

    public void insertOrUpdateActions(MenuCardButton menuCardButton) {
        if (menuCardButton.getId() > 0) {
            deleteAllActionsForButton(menuCardButton.getId());
            insertActions(menuCardButton);
        }
    }

    private void insertAction(MenuCardAction menuCardAction) {
        try {
            ContentValues groupMapping = new ContentValues();
            groupMapping.put("BUTTON_ID", menuCardAction.getButtonId());
            groupMapping.put("MENU_TYPE_ID", menuCardAction.getMenuTypeId());
            groupMapping.put("LAYOUT_ID", menuCardAction.getLayoutId());
            groupMapping.put("POSITION", menuCardAction.getPosition());

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long id = db.insert("MENU_CARD_BUTTON_ACTIONS", null, groupMapping);
            menuCardAction.setId(id);
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertGroup", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    private void insertActions(MenuCardButton menuCardButton) {
        if (menuCardButton != null && menuCardButton.getActions() != null) {
            int position = 1;
            for (MenuCardAction action : menuCardButton.getActions()) {
                if (action != null) {
                    action.setButtonId(menuCardButton.getId());
                    action.setPosition(position++);
                    insertAction(action);
                }
            }
        }
    }

    public static void deleteAllActionsForButton(long buttonId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "BUTTON_ID= ?";
        String[] whereArgs = {buttonId + ""};
        db.delete("MENU_CARD_BUTTON_ACTIONS", whereClause, whereArgs);
    }

    public List<MenuCardAction> getActions(long buttonId) {
        List<MenuCardAction> actions = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String whereClause = "BUTTON_ID= ?";
            String[] whereArgs = {buttonId + ""};

            Cursor cursor = db.query("MENU_CARD_BUTTON_ACTIONS", new String[]{"_id", "MENU_TYPE_ID", "LAYOUT_ID", "POSITION"}, whereClause, whereArgs, "POSITION", null, null);
            while (cursor.moveToNext()) {
                try {
                    long actionId = cursor.getLong(0);
                    long menuTypeId = cursor.getLong(1);
                    int layoutId = cursor.getInt(2);
                    int position = cursor.getInt(3);

                    MenuCardAction menuCardAction = new MenuCardAction();
                    menuCardAction.setButtonId(buttonId);
                    menuCardAction.setId(actionId);
                    menuCardAction.setMenuTypeId(menuTypeId);
                    menuCardAction.setLayoutId(layoutId);
                    menuCardAction.setPosition(position);

                    MenuCardLayoutEnum layout = MenuCardLayoutEnum.of(layoutId);
                    if (layout != null) {
                        menuCardAction.setLayoutName(layout.name());
                    }
                    MenuType menuType = menuTypeDao.getMenuGroupsById().get(menuTypeId);
                    if (menuType != null) {
                        menuCardAction.setMenuTypeName(menuType.getName());
                    }

                    actions.add(menuCardAction);
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
        return actions;
    }
}
