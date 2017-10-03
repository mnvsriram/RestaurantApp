package app.resta.com.restaurantapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.PropUtil;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "restApp";
    public static final int DB_VERSION = 114;

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
                    " TYPE TEXT NOT NULL, \n" +
                    " NAME TEXT NOT NULL, \n" +
                    " LOCATION TEXT NOT NULL, \n" +
                    " SHAPE TEXT NOT NULL, \n" +
                    " ACTIVE TEXT NOT NULL \n" +
                    " );");


            db.execSQL("DROP TABLE IF EXISTS MENU_CARD_BUTTON_PROPS");
            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_CARD_BUTTON_PROPS(_id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    " BUTTON_ID INTEGER NOT NULL, \n" +
                    " MENU_LAYOUT INTEGER NOT NULL, \n" +
                    " MENU_GROUP INTEGER NOT NULL \n" +
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

    private void loadMenuData(SQLiteDatabase db) {
        long groupId = addGroup(db, "Food");
        addGroup(db, "Drinks");
        //      long parentId = addParent(db, "Starters", groupId);
//        long itemId = add(db, "Manchurian");
        //    addMapping(db, itemId, parentId);
        String s = "";
        /*add(db, "3", "1", "Pakoda", groupId);
        add(db, "31", "1", "Chicken 65", groupId);
        add(db, "32", "1", "Chilli Chicken", groupId);
        add(db, "33", "1", "Chicken Pepper Fry", groupId);
        add(db, "34", "1", "Lamb Chukka Varual", groupId);
        add(db, "35", "1", "Chilli & Garlic Prawn", groupId);
        add(db, "36", "1", "Crispy Chilli Squid", groupId);
        add(db, "37", "1", "Chicken Tikka", groupId);
        add(db, "38", "1", "Tandoori Chicken ", groupId);
        add(db, "39", "1", "Lamb Tikka", groupId);
        add(db, "311", "1", "Shish Kebab", groupId);
        add(db, "312", "1", "Lamb Chops", groupId);
        add(db, "313", "1", "King Prawn Tikka", groupId);
        add(db, "314", "1", "Methu vadai", groupId);
        add(db, "315", "1", "Saambar or Thair Vada", groupId);
        add(db, "316", "1", "Masala Vada", groupId);
        add(db, "317", "1", "Mysor Bonda", groupId);
        add(db, "318", "1", "Samosa", groupId);


        loadDosaData(db, groupId);
*/
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


    void addMapping(SQLiteDatabase db, long itemId, long parentId) {
        try {
            ContentValues pakodaChild = new ContentValues();
            pakodaChild.put("ITEM_ID", itemId);
            pakodaChild.put("PARENT_ID", parentId);
            db.insert("MENU_ITEM_PARENT_MAPPING", null, pakodaChild);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    long add(SQLiteDatabase db, String name) {
        long id = 0;
        try {
            ContentValues pakodaChild = new ContentValues();
            pakodaChild.put("DESCRIPTION", "Hello");
            pakodaChild.put("Name", name);
            pakodaChild.put("PRICE", 2);
            pakodaChild.put("ACTIVE", "Y");
            id = db.insert("MENU_ITEM", null, pakodaChild);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }


    long addParent(SQLiteDatabase db, String name, long groupId) {
        long id = 0;
        try {
            ContentValues pakodaChild = new ContentValues();
            pakodaChild.put("Name", name);
            pakodaChild.put("ACTIVE", "Y");
            pakodaChild.put("MENU_ID", groupId);

            id = db.insert("MENU_ITEM_PARENT", null, pakodaChild);
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

/*
    private void loadDosaData(SQLiteDatabase db, long groupId) {
        add(db, "4", "-1", "Dosa", groupId);
        add(db, "41", "4", "Plain Dosa", "latte", "Dosa is a type of .", groupId);
        add(db, "42", "4", "Masala Dosa", "masala", "Masala dosa or masale dose ( preparation of masala dosa varies from city to city", groupId);
        add(db, "43", "4", "Chicken Dosa", "chickendosa", "Dosa filled with chicken.", groupId);
    }
*/

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

}
