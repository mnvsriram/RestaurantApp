package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import app.resta.com.restaurantapp.cache.RestaurantCache;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.ItemParentMapping;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.GroupPositionComparator;
import app.resta.com.restaurantapp.util.ItemNameComparator;
import app.resta.com.restaurantapp.util.ItemPositionComparator;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantUtil;

import static app.resta.com.restaurantapp.cache.RestaurantCache.allChildItemsByName;
import static app.resta.com.restaurantapp.cache.RestaurantCache.allItemsById;
import static app.resta.com.restaurantapp.cache.RestaurantCache.allParentItemsById;
import static app.resta.com.restaurantapp.cache.RestaurantCache.imageMap;
import static app.resta.com.restaurantapp.cache.RestaurantCache.dataFetched;


public class MenuItemDao {

    //  private static Map<String, RestaurantItem> allParentItemsByName;
    private static IngredientDao ingredientDao = new IngredientDao();
    private static TagsDao tagsDao = new TagsDao();
    private MenuItemParentDao parentDao;
    private Map<Long, RestaurantItem> parentItemsForSelectedMenuType;

    /*
        public Map<String, RestaurantItem> getAllParentItemsByName() {
            return allParentItemsByName;
        }
    */
    public Map<Long, RestaurantItem> getParentItemsForSelectedMenuType() {
        return parentItemsForSelectedMenuType;
    }

    public MenuItemDao() {
        ingredientDao = new IngredientDao();
        tagsDao = new TagsDao();
        parentDao = new MenuItemParentDao();
    }

    public Map<Long, RestaurantItem> fetchMenuItems(long groupMenuId) {
        //this variable will be set to true once loaded. if any other tablet changes data. then this will be set to false. so taht next time the user launches this page, the data will be fetched from the db.
        if (!dataFetched) {
            loadAllImageMappings();
            loadMenuItems();
            allParentItemsById = parentDao.loadMenuParentItems();
            createHierarchy();
            setChildMap();
            dataFetched = true;
        }
        parentItemsForSelectedMenuType = loadItemsForGroup(groupMenuId);
        return parentItemsForSelectedMenuType;
    }

    private void setParentToChild(RestaurantItem parent) {
        List<RestaurantItem> items = parent.getChildItems();
        for (RestaurantItem child : items) {
            child.setParent(parent);
            child.setMenuTypeId(parent.getMenuTypeId());
            child.setMenuTypeName(parent.getMenuTypeName());
        }
    }


    private Map<Long, RestaurantItem> loadItemsForGroup(long groupMenuId) {
        if (groupMenuId == -1 && (LoginController.getInstance().isAdminLoggedIn() || LoginController.getInstance().isReviewAdminLoggedIn())) {
            return getAllItemsForAdmin();
        } else {
            Map<Long, RestaurantItem> groupsForSelectedMenuType = filterItemsByGroup(groupMenuId);
            return sortByValues(groupsForSelectedMenuType);
        }
    }


    private Map<Long, RestaurantItem> sortByValues(Map<Long, RestaurantItem> map) {
        List<Map.Entry<Long, RestaurantItem>> list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {

                Object val1 = ((Map.Entry) (o1)).getValue();
                Object val2 = ((Map.Entry) (o2)).getValue();


                Integer position1 = -1;
                if (val1 != null) {
                    position1 = ((RestaurantItem) val1).getPosition();
                }

                Integer position2 = -1;
                if (val2 != null) {
                    position2 = ((RestaurantItem) val2).getPosition();
                }

                return position1.compareTo(position2);
            }
        });

        Map<Long, RestaurantItem> sortedHashMap = new LinkedHashMap<Long, RestaurantItem>();
        for (Map.Entry<Long, RestaurantItem> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }
        return sortedHashMap;
    }

    private Map<Long, RestaurantItem> getAllItemsForAdmin() {
        RestaurantItem dummyParent = new RestaurantItem();
        dummyParent.setId(-1);
        dummyParent.setName("All Items");
        List<RestaurantItem> itemValueList = new ArrayList<RestaurantItem>(allChildItemsByName.values());
        dummyParent.setChildItems(new ArrayList<RestaurantItem>(itemValueList));

        for (RestaurantItem child : itemValueList) {
            child.setParent(dummyParent);
        }

        Map<Long, RestaurantItem> itemMap = new HashMap<>();
        itemMap.put(-1l, dummyParent);
        return itemMap;
    }

    private void sortChildrenBasedOnPosition(RestaurantItem parentItem) {
        Collections.sort(parentItem.getChildItems(), new ItemPositionComparator());
    }

    private Map<Long, RestaurantItem> filterItemsByGroup(long groupMenuId) {
        Map<Long, RestaurantItem> filteredItems = new HashMap<>();
        for (RestaurantItem item : allParentItemsById.values()) {
            if (item.getMenuTypeId() == groupMenuId) {
                filteredItems.put(item.getId(), item);
                setParentToChild(item);
                sortChildrenBasedOnPosition(item);
            }
        }
        return filteredItems;
    }


    public static Map<Long, RestaurantItem> getAllItemsById() {
        return allItemsById;
    }


    public Map<String, RestaurantItem> getAllChildItemsByName() {
        return allChildItemsByName;
    }

    /*
    private static void setAllDishes(Map<Long, RestaurantItem> parentItems) {
        allChildItemsByName = new HashMap<>();
        allParentItemsByName = new HashMap<>();
        if (parentItems != null && parentItems.size() > 0) {
            for (Long id : parentItems.keySet()) {
                RestaurantItem parent = parentItems.get(id);
                allParentItemsByName.put(parent.getName(), parent);
                List<RestaurantItem> items = parent.getChildItems();
                if (items != null) {
                    for (RestaurantItem item : items) {
                        allChildItemsByName.put(item.getName(), item);
                    }
                }
            }
        }
    }
*/
  /*
    private static Map<Long, RestaurantItem> filterParentItems(Map<Long, RestaurantItem> items) {
        Map<Long, RestaurantItem> parentItems = new HashMap<>();
        for (RestaurantItem item : items.values()) {
            if (item.getParentId() == -1) {
                parentItems.put(item.getId(), item);
            }
        }
        return parentItems;
    }
*/
    public List<RestaurantItem> getParents(RestaurantItem item) {
        List<RestaurantItem> parents = new ArrayList<>();
        for (RestaurantItem parent : allParentItemsById.values()) {
            List<RestaurantItem> children = parent.getChildItems();
            if (children != null) {
                for (RestaurantItem child : children) {
                    if (child.equals(item)) {
                        parents.add(parent);
                    }
                }
            }
        }
        return parents;
    }

    private Map<Long, ItemParentMapping> createIdMapping(List<ItemParentMapping> itemParentMappings) {
        Map<Long, ItemParentMapping> parentToObjectMapping = new HashMap<>();
        if (itemParentMappings != null) {
            for (ItemParentMapping mapping : itemParentMappings) {
                parentToObjectMapping.put(mapping.getParentId(), mapping);
            }
        }
        return parentToObjectMapping;
    }

    private void createHierarchy() {
        Map<Long, List<ItemParentMapping>> parentMapping = parentDao.fetchParentsForItems();
        for (RestaurantItem item : allItemsById.values()) {
            item.setImages(imageMap.get(item.getId()));
            List<ItemParentMapping> parents = parentMapping.get(item.getId());
            item.setItemToParentMappings(createIdMapping(parents));

            if (parents != null) {
                for (ItemParentMapping mapping : parents) {
                    RestaurantItem parent = allParentItemsById.get(mapping.getParentId());
                    parent.addChildItem(item);
                }
            }
        }
    }

    private static Map<Long, RestaurantItem> loadMenuItems() {
        allItemsById = new LinkedHashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = "select menuItem._id, menuItem.NAME, menuItem.PRICE, menuItem.ACTIVE, menuItem.DESCRIPTION \n" +
                    "from MENU_ITEM menuItem \n" +
                    "where 1=1 \n";
            if (!LoginController.getInstance().isAdminLoggedIn()) {
                sql += " and menuItem.ACTIVE = 'Y'";
                //sql += " and menuItem.GROUP_ID = " + menuGroupId;
            }

            sql += " order by menuItem.ACTIVE DESC";

            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    String price = cursor.getString(2);
                    String active = cursor.getString(3);
                    String description = cursor.getString(4);

                    RestaurantItem item = new RestaurantItem();
                    item.setId(id);
                    item.setName(name);
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

    public static RestaurantItem getItem(long itemId) {
        return getItem(itemId, -1);
    }

    public static RestaurantItem getItem(long itemId, long ignoreItemId) {
        return getItem(itemId, ignoreItemId, false);
    }

    public static RestaurantItem getItem(long itemId, long ignoreItemId, boolean fetchOnlyActive) {
        RestaurantItem item = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "";
            if (fetchOnlyActive) {
                whereClause = "ACTIVE = 'Y' AND _id=? AND _id != ? ";
            } else {
                whereClause = "_id= ? AND _id != ? ";
            }

            String[] selectionArgs = null;

            selectionArgs = new String[2];
            selectionArgs[0] = itemId + "";
            selectionArgs[1] = ignoreItemId + "";

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


    public RestaurantItem getItemWithName(String itemName, long ignoreItemId, boolean fetchOnlyActive) {
        RestaurantItem item = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "LOWER(NAME)=? AND _id != ?";
            if (fetchOnlyActive) {
                whereClause += " AND ACTIVE = 'Y'";
            }

            String[] selectionArgs = null;

            selectionArgs = new String[2];
            selectionArgs[0] = itemName.toLowerCase().trim();
            selectionArgs[1] = ignoreItemId + "";

            Cursor cursor = db.query("MENU_ITEM", new String[]{"_id"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    item = new RestaurantItem();
                    item.setId(id);
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

    public long insertOrUpdateMenuItem(RestaurantItem item) {
        long status = 0;
        if (item.getId() == 0) {
            status = insertMenuItem(item);
            insertAllImages(item.getImages(), status);
        } else {
            status = updateMenuItem(item);
            //updateChildren(item.getId(), item.getMenuTypeId());
            deleteAllImageMappings(item.getId());
            insertAllImages(item.getImages(), item.getId());
        }
        RestaurantCache.dataFetched = false;
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
/*
    public void refreshData() {
        dataFetched = false;
        fetchMenuItems(-1l);
    }
*/

    public void deleteMenuItem(RestaurantItem item) {
        deleteItemFromMenu(item);
        GGWDao.deleteAllGGWItemsForId(item.getId());
        ingredientDao.deleteAllIngredientMappingsForItemId(item.getId());
        tagsDao.deleteAllTagMappingsForItemId(item.getId());
    }

    private boolean deleteItemFromMenu(RestaurantItem item) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        boolean result = db.delete("MENU_ITEM", "_id=" + item.getId(), null) > 0;
        RestaurantCache.dataFetched = false;
        return result;
    }

    public static long insertMenuItem(RestaurantItem item) {
        long count = 0;
        try {

            ContentValues menuitem = new ContentValues();
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

/*
    public static long updateChildren(long parentId, long groupMenuId) {
        long count = 0;
        try {
            String selection = "PARENTMENUITEMID" + " LIKE ?";
            String[] selectionArgs = {String.valueOf(parentId)};

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            ContentValues values = new ContentValues();
            values.put("GROUP_ID", groupMenuId);

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
*/

    public List<String> getParentNamesForSelectedMenuType() {
        List<String> parentNames = new ArrayList<>();
        if (parentItemsForSelectedMenuType != null) {
            for (RestaurantItem parent : parentItemsForSelectedMenuType.values()) {
                parentNames.add(parent.getName());
            }
        }
        return parentNames;
    }

    private void setChildMap() {
        allChildItemsByName = new HashMap<>();
        List<String> childNames = new ArrayList<>();
        for (RestaurantItem item : allItemsById.values()) {
            allChildItemsByName.put(item.getName(), item);
        }
    }
}
