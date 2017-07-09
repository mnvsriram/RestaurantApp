package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuItemDao {
    public static boolean dataFetched = false;
    private static Map<Long, RestaurantItem> parentItems = new HashMap<>();
    private static Map<String, RestaurantItem> dishes;
    private static Map<Long, RestaurantItem> allItemsById;
    private static Map<Long, RestaurantImage[]> imageMap;

    private static IngredientDao ingredientDao = new IngredientDao();
    private static TagsDao tagsDao = new TagsDao();

    public MenuItemDao() {
        ingredientDao = new IngredientDao();
        tagsDao = new TagsDao();
    }

    public static Map<Long, RestaurantItem> fetchMenuItems(boolean onlyActiveItems) {
        //this variable will be set to true once loaded. if any other tablet changes data. then this will be set to false. so taht next time the user launches this page, the data will be fetched from the db.
        if (!dataFetched) {
            loadAllImageMappings();
            loadMenuItems(onlyActiveItems);

            createHierarchy(allItemsById);
            parentItems = filterParentItems(allItemsById);
            setAllDishes(parentItems);
            dataFetched = true;
        }
        return parentItems;
    }

    public static Map<Long, RestaurantItem> getAllItemsById() {
        return allItemsById;
    }

    public static Map<String, RestaurantItem> getDishes() {
        return dishes;
    }

    private static void setAllDishes(Map<Long, RestaurantItem> parentItems) {
        dishes = new HashMap<>();
        if (parentItems != null && parentItems.size() > 0) {
            for (Long id : parentItems.keySet()) {
                RestaurantItem parent = parentItems.get(id);
                List<RestaurantItem> items = parent.getChildItems();
                if (items != null) {
                    for (RestaurantItem item : items) {
                        dishes.put(item.getName(), item);
                    }
                }
            }
        }
    }

    private static Map<Long, RestaurantItem> filterParentItems(Map<Long, RestaurantItem> items) {
        Map<Long, RestaurantItem> parentItems = new HashMap<>();
        for (RestaurantItem item : items.values()) {
            if (item.getParentId() == -1) {
                parentItems.put(item.getId(), item);
            }
        }
        return parentItems;
    }

    private static void createHierarchy(Map<Long, RestaurantItem> items) {
        for (RestaurantItem item : items.values()) {
            item.setImages(imageMap.get(item.getId()));
            Long parentId = item.getParentId();
            if (parentId != -1) {
                RestaurantItem parent = items.get(parentId);
                if (parent != null) {
                    item.setParentItem(parent);
                    parent.addChildItem(item);
                }
            }
        }
    }

    private static Map<Long, RestaurantItem> loadMenuItems(boolean onlyActiveItems) {
        allItemsById = new LinkedHashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = null;
            String orderBy = "ACTIVE DESC";
            if (onlyActiveItems) {
                whereClause = "ACTIVE = 'Y'";
            }

            Cursor cursor = db.query("MENU_ITEM", new String[]{"_id", "NAME", "PARENTMENUITEMID", "PRICE", "ACTIVE", "DESCRIPTION"}, whereClause, null, null, null, orderBy);
            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    Long parentId = cursor.getLong(2);
                    String price = cursor.getString(3);
                    String active = cursor.getString(4);
                    String description = cursor.getString(5);
                    RestaurantItem item = new RestaurantItem();
                    item.setId(id);
                    item.setName(name);
                    item.setParentId(parentId);
                    item.setDescription(description);
                    item.setPrice(price);
                    item.setActive(active);

                    RestaurantImage[] images = imageMap.get(id);
                    if (images == null) {
                        images = new RestaurantImage[3];
                    }
                    item.setImages(images);
                    allItemsById.put(id, item);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable14", Toast.LENGTH_LONG);
            toast.show();
        }
        return allItemsById;
    }


    public static RestaurantItem getItem(String itemName, long parentId) {
        return getItem(itemName, parentId, -1);
    }

    public static RestaurantItem getItem(String itemName, long parentId, long ignoreItemId) {
        return getItem(itemName, parentId, ignoreItemId, false);
    }

    public static RestaurantItem getItem(String itemName, long parentId, long ignoreItemId, boolean fetchOnlyActive) {
        RestaurantItem item = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "";
            if (fetchOnlyActive) {
                whereClause = "ACTIVE = 'Y' AND LOWER(NAME)= ? AND _id != ? ";
            } else {
                whereClause = "LOWER(NAME)= ? AND _id != ? ";
            }


            if (parentId > 0) {
                whereClause += " AND PARENTMENUITEMID= ?";
            }

            String[] selectionArgs = null;


            if (parentId > 0) {
                selectionArgs = new String[3];
                selectionArgs[0] = itemName.toLowerCase();
                selectionArgs[1] = ignoreItemId + "";
                selectionArgs[2] = parentId + "";
            } else {
                selectionArgs = new String[2];
                selectionArgs[0] = itemName.toLowerCase();
                selectionArgs[1] = ignoreItemId + "";
            }

            Cursor cursor = db.query("MENU_ITEM", new String[]{"_id", "NAME"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    item = new RestaurantItem();
                    item.setId(id);
                    item.setName(name);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable11", Toast.LENGTH_LONG);
            toast.show();
        }
        return item;
    }

    public static long insertOrUpdateMenuItem(RestaurantItem item) {
        long status = 0;
        if (item.getId() == 0) {
            status = insertMenuItem(item);
            insertAllImages(item.getImages(), status);
        } else {
            status = updateMenuItem(item);
            deleteAllImageMappings(item.getId());
            insertAllImages(item.getImages(), item.getId());
        }
        refreshData();
        return status;
    }


    public static void deleteAllImageMappings(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "ITEM_ID= ?";
        String[] whereArgs = {id + ""};
        db.delete("MENU_ITEM_IMAGE_MAPPING", whereClause, whereArgs);
    }

    private static void insertAllImages(RestaurantImage[] images, long itemId) {
        if (images != null) {
            for (RestaurantImage image : images) {
                if (image != null) {
                    image.setItemId(itemId);
                    insertImageMapping(image);
                }
            }
        }
    }

    public static long insertImageMapping(RestaurantImage image) {
        long count = 0;
        try {

            ContentValues menuitem = new ContentValues();
            menuitem.put("ITEM_ID", image.getItemId());
            menuitem.put("IMAGE", image.getName());
            menuitem.put("DESCRIPTION", image.getDescription());


            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            count = db.insert("MENU_ITEM_IMAGE_MAPPING", null, menuitem);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12", Toast.LENGTH_LONG);
            toast.show();
        }
        return count;
    }

    public static void loadAllImageMappings() {
        try {
            imageMap = new HashMap<>();
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query("MENU_ITEM_IMAGE_MAPPING", new String[]{"ITEM_ID", "IMAGE", "DESCRIPTION"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    Long itemId = cursor.getLong(0);

                    RestaurantImage[] imagesForItem = imageMap.get(itemId);
                    if (imagesForItem == null) {
                        imagesForItem = new RestaurantImage[3];
                    }

                    String imageName = cursor.getString(1);
                    String description = cursor.getString(2);
                    RestaurantImage restaurantImage = new RestaurantImage();
                    restaurantImage.setItemId(itemId);
                    restaurantImage.setName(imageName);
                    restaurantImage.setDescription(description);

                    int index = 0;
                    while (imagesForItem[index] != null) {
                        index++;
                    }
                    if (index <= 2) {
                        imagesForItem[index] = restaurantImage;
                    }

                    imageMap.put(itemId, imagesForItem);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Error while fetching Image Mappings", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public static void refreshData() {
        MenuItemDao.dataFetched = false;
        MenuItemDao.fetchMenuItems(!LoginController.getInstance().isAdminLoggedIn());
    }


    public static void deleteMenuItem(RestaurantItem item) {
        deleteItemFromMenu(item);
        GGWDao.deleteAllGGWItemsForId(item.getId());
        ingredientDao.deleteAllIngredientMappingsForItemId(item.getId());
        tagsDao.deleteAllTagMappingsForItemId(item.getId());
    }

    private static boolean deleteItemFromMenu(RestaurantItem item) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean result = db.delete("MENU_ITEM", "_id=" + item.getId(), null) > 0;
        refreshData();
        return result;
    }

    public static long insertMenuItem(RestaurantItem item) {
        long count = 0;
        try {

            ContentValues menuitem = new ContentValues();
            menuitem.put("PARENTMENUITEMID", item.getParentId());
            menuitem.put("DESCRIPTION", item.getDescription());
            menuitem.put("Name", item.getName());
            menuitem.put("PRICE", item.getPrice());
            menuitem.put("ACTIVE", item.getActive());


            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            count = db.insert("MENU_ITEM", null, menuitem);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable12", Toast.LENGTH_LONG);
            toast.show();
        }
        return count;
    }


    public static long updateMenuItem(RestaurantItem item) {
        long count = 0;
        try {


            String selection = "_id" + " LIKE ?";
            String[] selectionArgs = {String.valueOf(item.getId())};


            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("NAME", item.getName());
            values.put("DESCRIPTION", item.getDescription());
            values.put("PRICE", item.getPrice());
            values.put("ACTIVE", item.getActive());
            values.put("PARENTMENUITEMID", item.getParentId());

            count = db.update(
                    "MENU_ITEM",
                    values,
                    selection,
                    selectionArgs);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable13", Toast.LENGTH_LONG);
            toast.show();
        }
        return count;
    }

}
