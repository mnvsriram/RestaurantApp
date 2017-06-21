package app.resta.com.restaurantapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Map;

import app.resta.com.restaurantapp.util.PropUtil;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "restApp";
    public static final int DB_VERSION = 43;

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
                    " PARENTMENUITEMID INTEGER NOT NULL, \n" +
                    " IMAGE TEXT NOT NULL, \n" +
                    " PRICE TEXT NOT NULL, \n" +
                    " DESCRIPTION TEXT NOT NULL, \n" +
                    " ACTIVE TEXT NOT NULL \n" +
                    // " PRIMARY KEY (NAME,PARENTMENUITEMID,ACTIVE)" +
                    " );");

            db.execSQL("DELETE FROM MENU_ITEM");

            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_GGW_MAPPING(_id INTEGER NOT NULL,\n" +
                    " MENU_GGW_MAPPING_ID INTEGER NOT NULL \n" +
                    " );");


            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM_INGREDIENTS(_id INTEGER NOT NULL,\n" +
                    " INGREDIENT TEXT NOT NULL \n" +
                    " );");


            db.execSQL("CREATE TABLE IF NOT EXISTS MENU_ITEM_TAGS(_id INTEGER NOT NULL,\n" +
                    " TAG TEXT NOT NULL \n" +
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
    }

    private void loadMenuData(SQLiteDatabase db) {

        add(db, "1", "-1", "Starters");
        add(db, "2", "1", "Manchurian");
        add(db, "3", "1", "Pakoda");
        add(db, "31", "1", "Chicken 65");
        add(db, "32", "1", "Chilli Chicken");
        add(db, "33", "1", "Chicken Pepper Fry");
        add(db, "34", "1", "Lamb Chukka Varual");
        add(db, "35", "1", "Chilli & Garlic Prawn");
        add(db, "36", "1", "Crispy Chilli Squid");
        add(db, "37", "1", "Chicken Tikka");
        add(db, "38", "1", "Tandoori Chicken ");
        add(db, "39", "1", "Lamb Tikka");
        add(db, "311", "1", "Shish Kebab");
        add(db, "312", "1", "Lamb Chops");
        add(db, "313", "1", "King Prawn Tikka");
        add(db, "314", "1", "Methu vadai");
        add(db, "315", "1", "Saambar or Thair Vada");
        add(db, "316", "1", "Masala Vada");
        add(db, "317", "1", "Mysor Bonda");
        add(db, "318", "1", "Samosa");


        loadDosaData(db);
    }


    void add(SQLiteDatabase db, String id, String parentId, String name) {
        add(db, id, parentId, name, "filter", "Dummy description");
    }

    void add(SQLiteDatabase db, String id, String parentId, String name, String imageName, String description) {
        ContentValues pakodaChild = new ContentValues();
        pakodaChild.put("_id", id);
        pakodaChild.put("PARENTMENUITEMID", parentId);
        pakodaChild.put("DESCRIPTION", description);
        pakodaChild.put("Name", name);
        pakodaChild.put("IMAGE", imageName);
        pakodaChild.put("PRICE", "PRICE");
        pakodaChild.put("ACTIVE", "Y");
        db.insert("MENU_ITEM", null, pakodaChild);
    }

    private void loadDosaData(SQLiteDatabase db) {
        add(db, "4", "-1", "Dosa");
        add(db, "41", "4", "Plain Dosa", "latte", "Dosa is a type of pancake made from a fermented batter. It is somewhat similar to a crepe but its main ingredients are rice and black gram. Dosa is a typical part of the South Indian diet and popular all over the Indian subcontinent. Traditionally, Dosa is served hot along with sambar, stuffing of potatoes or paneer and chutney. It can be consumed with idli podi as well.");
        add(db, "42", "4", "Masala Dosa", "masala", "Masala dosa or masale dose ( Tulu |ಮಸಾಲೆ ದೋಸೆ) is a variant of the popular South Indian food dosa,which has its origins in Tulu Mangalorean cuisine made popular by the Udupi hotels all over India.[1] It is made from rice, lentils, potato, methi, and curry leaves, and served with chutneys and sambar. Though it was only popular in South India,[2] it can be found in all other parts of the country[3] and overseas.[4][5] In South India, preparation of masala dosa varies from city to city");
        add(db, "43", "4", "Chicken Dosa", "chickendosa", "Dosa filled with chicken.");
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

}