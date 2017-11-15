package app.resta.com.restaurantapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.PropUtil;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "restApp";
    public static final int DB_VERSION = 126;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);
    }

    public void insertSysParam(String type, String name, String value, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put("_id", type);
        values.put("TYPE", name);
        values.put("VALUE", value);
        db.insert("SYS_PARAMS", null, values);
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        updateDatabase(database, oldVersion, newVersion);
    }

    public void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {

//Change this
//        if (oldVersion <= 0) {
        if (oldVersion <= DB_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS SYS_PARAMS");
            db.execSQL("CREATE TABLE SYS_PARAMS (_id TEXT NOT NULL,\n" +
                    " TYPE TEXT NOT NULL, \n" +
                    " VALUE TEXT NOT NULL, \n" +
                    " PRIMARY KEY (_id,TYPE)" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_ITEM");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " NAME TEXT NOT NULL, \n" +
                    " PRICE TEXT NOT NULL, \n" +
                    " DESCRIPTION TEXT NOT NULL, \n" +
                    " ACTIVE TEXT NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_ITEM_PARENT");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM_PARENT (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " NAME TEXT NOT NULL, \n" +
                    " MENU_ID INTEGER NOT NULL, \n" +
                    " POSITION INTEGER , \n" +
                    " DESCRIPTION TEXT , \n" +
                    " ACTIVE TEXT NOT NULL \n" +
                    " );");

            db.execSQL("DROP TABLE IF EXISTS MENU_ITEM_PARENT_MAPPING");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM_PARENT_MAPPING (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " ITEM_ID INTEGER NOT NULL, \n" +
                    " PARENT_ID INTEGER NOT NULL, \n" +
                    " PRICE TEXT,  \n" +
                    " POSITION INTEGER  \n" +
                    " );");

            db.execSQL("DROP TABLE IF EXISTS MENU_TYPE");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_TYPE (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " NAME TEXT NOT NULL, \n" +
                    " PRICE TEXT,  \n" +
                    " DESCRIPTION TEXT,  \n" +
                    " SHOW_PRICE_FOR_CHILDREN TEXT  \n" +
                    " );");

            db.execSQL("DROP TABLE IF EXISTS MENU_ITEM_IMAGE_MAPPING");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM_IMAGE_MAPPING (_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " IMAGE TEXT NOT NULL, \n" +
                    " ITEM_ID INTEGER NOT NULL, \n" +
                    " DESCRIPTION TEXT  \n" +
                    " );");


            db.execSQL("DELETE FROM MENU_ITEM");

            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_GGW_MAPPING(_id INTEGER NOT NULL,\n" +
                    " MENU_GGW_MAPPING_ID INTEGER NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_ITEM_INGREDIENTS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM_INGREDIENTS(_id INTEGER NOT NULL,\n" +
                    " INGREDIENT_ID INTEGER NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_ITEM_TAGS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM_TAGS(_id INTEGER NOT NULL,\n" +
                    " TAG_ID INTEGER NOT NULL \n" +
                    " );");

            db.execSQL("DROP TABLE IF EXISTS ORDER_ITEMS");
            db.execSQL("CREATE TABLE IF NOT EXISTS ORDER_ITEMS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " CREATIONDATE DATE NOT NULL, \n" +
                    " ORDERCOMMENT TEXT NULL, \n" +
                    " ACTIVE TEXT NOT NULL \n" +
                    " );");
            db.execSQL("DROP TABLE IF EXISTS ORDER_ITEM_MAPPING");
            db.execSQL("CREATE TABLE IF NOT EXISTS ORDER_ITEM_MAPPING(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " ORDER_ID INTEGER NOT NULL, \n" +
                    " ITEM_ID INTEGER NOT NULL, \n" +
                    " MENU_TYPE_ID INTEGER NOT NULL, \n" +
                    " QUANTITY INTEGER NOT NULL, \n" +
                    " SETMENUGROUP INTEGER , \n" +
                    " PRICE TEXT , \n" +
                    " INSTRUCTIONS INTEGER NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS ORDER_ITEM_REVIEWS");
            db.execSQL("CREATE TABLE IF NOT EXISTS ORDER_ITEM_REVIEWS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " ORDER_ID INTEGER NOT NULL, \n" +
                    " ITEM_ID INTEGER NOT NULL, \n" +
                    " RATING INTEGER NOT NULL, \n" +
                    " REVIEW TEXT \n" +
                    " );");


            db.execSQL("CREATE TABLE IF NOT EXISTS ITEM_REVIEW_COUNTERS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " ITEM_ID INTEGER NOT NULL, \n" +
                    " RATING INTEGER NOT NULL, \n" +
                    " COUNT INTEGER NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS RATING_DAILY_SUMMARY");
            db.execSQL("CREATE TABLE IF NOT EXISTS RATING_DAILY_SUMMARY(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " DATE_OF_REVIEW DATE NOT NULL, \n" +
                    " ITEM_ID INTEGER NOT NULL, \n" +
                    " ITEM_NAME TEXT NOT NULL, \n" +
                    " RATING_TYPE INTEGER NOT NULL, \n" +
                    " COUNT INTEGER NOT NULL, \n" +
                    " COMMENTS TEXT \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_CARDS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_CARDS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " NAME TEXT NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_CARD_PROPS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_CARD_PROPS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " CARD_ID INTEGER NOT NULL, \n" +
                    " PROP_ID INTEGER NOT NULL, \n" +
                    " VALUE TEXT NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_CARD_BUTTONS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_CARD_BUTTONS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " CARD_ID INTEGER NOT NULL, \n" +
                    " NAME TEXT NOT NULL, \n" +
                    " BUTTON_ENUM_TYPE TEXT NOT NULL, \n" +
                    " ACTIVE TEXT NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_CARD_BUTTON_PROPS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_CARD_BUTTON_PROPS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " BUTTON_ID INTEGER NOT NULL, \n" +
                    " PROP_ID INTEGER NOT NULL, \n" +
                    " VALUE TEXT NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_CARD_BUTTON_ACTIONS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_CARD_BUTTON_ACTIONS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " BUTTON_ID INTEGER NOT NULL, \n" +
                    " MENU_TYPE_ID INTEGER NOT NULL, \n" +
                    " LAYOUT_ID INTEGER NOT NULL, \n" +
                    " POSITION INTEGER NOT NULL \n" +
                    " );");


            db.execSQL("CREATE TABLE IF NOT EXISTS TAGS_DATA(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " TAG_NAME TEXT NOT NULL, \n" +
                    " IMAGE TEXT \n" +
                    " );");


            db.execSQL("CREATE TABLE IF NOT EXISTS INGREDIENTS_DATA(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " INGREDIENT_NAME TEXT NOT NULL, \n" +
                    " IMAGE TEXT \n" +
                    " );");

            loadPropDataToDB(db);
        }


        if (oldVersion < 2) {
        }
    }

    private void loadPropDataToDB(SQLiteDatabase db) {
        loadColorDataToDB(db);
        loadConfigDataToDB(db);
        loadImageDataToDB(db);
        loadLayoutData(db);

        //this will be done by admin app. once admin app is built, then please comment this method call.
        loadMenuData(db);
        loadReviewData(db);
    }

    private void loadReviewData(SQLiteDatabase db) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        addReview(db, cal.getTime(), 2, "Manchurian", 3, 20, "hello");
        addReview(db, cal.getTime(), 3, "Average Item", 2, 10, "average");
        addReview(db, cal.getTime(), 4, "bad Item", 1, 5, "bad");
    }

    public void addItem(SQLiteDatabase db, long parentId, String itemName, String itemDescription, String itemPrice) {
        long itemId = add(db, itemName, itemDescription, itemPrice);
        insertParentChildMapping(db, itemId, parentId, null, 1);
    }

    private void loadMenuData(SQLiteDatabase db) {
        long groupId = addGroup(db, "Food Menu");
        long parentId = insertMenuParent(db, "Starters", groupId + "", "desc");
        addItem(db, parentId, "Bhel Puri", "A mixture of roasted pulses, gram flour straws, peanuts, fresh\n" +
                "coriander and tamarind chutneys - served with mini poppadum.", 5.00 + "");
        addItem(db, parentId, "Aloo Tikki Chaat", "A mixture of roasted pulses, gram flour straws, peanuts, fresh coriander and tamarind chutneys - served with mini poppadum.A mixture of roasted pulses, gram flour straws, peanuts, fresh coriander and tamarind chutneys - served with mini poppadum.A mixture of roasted pulses, gram flour straws, peanuts, fresh coriander and tamarind chutneys - served with mini poppadum.", 5.40 + "");

        addItem(db, parentId, "Bhatura Chana", "Light fried bread served with slightly sweet tangy chickpeas", 6.40 + "");


        addItem(db, parentId, "Paneer Tikka Shashlik", "Spiced paneer (Indian cheese) made in-house with bell peppers and\n" +
                "        onions roasted in the tandoor.", 6.20 + "");

        addItem(db, parentId, "Chicken Achari Tikka", "Sharp pickling spices with mustard oil and fresh herbs.", 7.00 + "");
        addItem(db, parentId, "Sheekh Kebab", "Tender lamb, minced with spices and fresh herbs grilled in tandoor.", 6.50 + "");
        addGroup(db, "Drinks");
    }

    void addReview(SQLiteDatabase db, Date date, int itemId, String name, int ratingType, int count, String comment) {
        ContentValues tag = new ContentValues();

        try {
            tag.put("DATE_OF_REVIEW", DateUtil.getDateString(date, "yyyy-MM-dd HH:mm:ss"));
            tag.put("ITEM_ID", itemId);
            tag.put("ITEM_NAME", name);
            tag.put("RATING_TYPE", ratingType + "");
            tag.put("COUNT", count);
            tag.put("COMMENTS", comment);
            db.insert("RATING_DAILY_SUMMARY", null, tag);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public long insertParentChildMapping(SQLiteDatabase db, long itemId, long parentId, String price, int position) {
        long newId = 0;
        try {
            ContentValues menuItemParent = new ContentValues();
            menuItemParent.put("ITEM_ID", itemId);
            menuItemParent.put("PARENT_ID", parentId);
            menuItemParent.put("PRICE", price);
            menuItemParent.put("POSITION", position);

            newId = db.insert("MENU_ITEM_PARENT_MAPPING", null, menuItemParent);
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12-insertParentChildMapping", Toast.LENGTH_LONG);
            toast.show();
        }
        return newId;
    }

    long add(SQLiteDatabase db, String name, String description, String price) {
        long id = 0;
        try {
            ContentValues menuitem = new ContentValues();
            menuitem.put("DESCRIPTION", description);
            menuitem.put("Name", name);
            menuitem.put("PRICE", price);
            menuitem.put("ACTIVE", "Y");
            id = db.insert("MENU_ITEM", null, menuitem);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }


    long addGroup(SQLiteDatabase db, String name) {
        ContentValues groupMapping = new ContentValues();
        groupMapping.put("NAME", name);
        long groupId = db.insert("MENU_TYPE", null, groupMapping);
        return groupId;
    }

    private void loadColorDataToDB(SQLiteDatabase db) {
        Map<String, String> colorProps = PropUtil.getColorProperties();
        insertToDB(db, colorProps, "color");
    }

    private void loadImageDataToDB(SQLiteDatabase db) {
        Map<String, String> imageProperties = PropUtil.getImageProperties();
        insertToDB(db, imageProperties, "image");
    }

    private void loadConfigDataToDB(SQLiteDatabase db) {
        Map<String, String> configProps = PropUtil.getConfigProperties();
        insertToDB(db, configProps, "config");
    }

    private void loadLayoutData(SQLiteDatabase db) {
        Map<String, String> layoutProps = PropUtil.getLayoutProperties();
        insertToDB(db, layoutProps, "layout");
    }


    private void insertToDB(SQLiteDatabase db, Map<String, String> props, String type) {
        for (String key : props.keySet()) {
            insertSysParam(key, type, props.get(key), db);
        }
    }


    public long insertMenuParent(SQLiteDatabase db, String name, String menuTypeId, String description) {
        long newId = 0;
        try {
            ContentValues menuItemParent = new ContentValues();
            menuItemParent.put("Name", name);
            menuItemParent.put("MENU_ID", menuTypeId);
            menuItemParent.put("ACTIVE", "Y");
            menuItemParent.put("DESCRIPTION", description);
            newId = db.insert("MENU_ITEM_PARENT", null, menuItemParent);

        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12- insertMenuParent", Toast.LENGTH_LONG);
            toast.show();
        }
        return newId;
    }

}
