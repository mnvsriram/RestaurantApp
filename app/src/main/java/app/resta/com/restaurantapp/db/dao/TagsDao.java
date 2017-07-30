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
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.MyApplication;

public class TagsDao {

    private static boolean fetchData = true;
    private static Map<Long, List<Tag>> tagsData = new HashMap<>();

    public Map<Long, List<Tag>> getTagsData() {
        if (fetchData) {
            tagsData = new HashMap<>();
            tagsData.putAll(getTags());
            fetchData = false;
        }
        return tagsData;
    }

    public Map<Long, List<Tag>> getTags() {
        Map<Long, List<Tag>> tagData = new HashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = "SELECT tagMap._id, refData.TAG_NAME, refData.IMAGE FROM MENU_ITEM_TAGS tagMap INNER JOIN TAGS_DATA refData ON tagMap.TAG_ID=refData._id ";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    long itemId = cursor.getLong(0);
                    String tagName = cursor.getString(1);
                    String tagImage = cursor.getString(2);

                    Tag tag = new Tag();
                    tag.setImage(tagImage);
                    tag.setName(tagName);

                    List<Tag> tagsForThisItem = tagData.get(itemId);
                    if (tagsForThisItem == null) {
                        tagsForThisItem = new ArrayList<>();
                    }
                    tagsForThisItem.add(tag);
                    tagData.put(itemId, tagsForThisItem);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable1", Toast.LENGTH_LONG);
            toast.show();
        }
        return tagData;
    }

    public static List<Tag> getTagsForItem(long id) {
        List<Tag> tags = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = "SELECT refData._id,refData.TAG_NAME, refData.IMAGE  FROM MENU_ITEM_TAGS tagMap INNER JOIN TAGS_DATA refData ON tagMap.TAG_ID=refData._id " +
                    "WHERE tagMap._id=?";
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(id)});
            //Cursor cursor = db.query("MENU_ITEM_TAGS", new String[]{"_id", "TAG"}, whereClause, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    Long tagId = cursor.getLong(0);
                    String tagName = cursor.getString(1);
                    String tagImage = cursor.getString(2);
                    Tag tag = new Tag();
                    tag.setId(tagId);
                    tag.setName(tagName);
                    tag.setImage(tagImage);
                    tags.add(tag);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable2", Toast.LENGTH_LONG);
            toast.show();
        }
        return tags;
    }

    public static void deleteTags(long id, List<Long> items) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (Long itemId : items) {
            String whereClause = "_id=" + id + " AND TAG_ID = '" + itemId + "'";
            db.delete("MENU_ITEM_TAGS", whereClause, null);
        }
        fetchData = true;
    }


    public static void deleteAllTagMappingsForTagId(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "TAG_ID= ?";
        String[] whereArgs = {id + ""};
        db.delete("MENU_ITEM_TAGS", whereClause, whereArgs);
    }

    public void deleteAllTagMappingsForItemId(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String whereClause = "_ID= ?";
        String[] whereArgs = {id + ""};
        db.delete("MENU_ITEM_TAGS", whereClause, whereArgs);
    }


    public static void insertTags(Long id, List<Long> tagIds) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            for (Long tagId : tagIds) {
                ContentValues tag = new ContentValues();
                tag.put("_id", id);
                tag.put("TAG_ID", tagId);
                db.insert("MENU_ITEM_TAGS", null, tag);
            }
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable3", Toast.LENGTH_LONG);
            toast.show();
        }
        fetchData = true;
    }


    public void insertTagsRefData(Tag tag) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues tagValues = new ContentValues();
            tagValues.put("TAG_NAME", tag.getName());
            tagValues.put("IMAGE", tag.getImage());
            db.insert("TAGS_DATA", null, tagValues);
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable4", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public List<Tag> getTagsRefData() {
        List<Tag> tags = new ArrayList<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("TAGS_DATA", new String[]{"_id", "TAG_NAME", "IMAGE"}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                try {
                    long tagId = cursor.getLong(0);
                    String tagName = cursor.getString(1);
                    String imageName = cursor.getString(2);
                    Tag tagFromDB = new Tag();
                    tagFromDB.setId(tagId);
                    tagFromDB.setName(tagName);
                    tagFromDB.setImage(imageName);
                    tags.add(tagFromDB);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable5", Toast.LENGTH_LONG);
            toast.show();
        }
        return tags;
    }


    public void deleteTagRefData(long id) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "_id=" + id;
        db.delete("TAGS_DATA", whereClause, null);
        deleteAllTagMappingsForTagId(id);
    }

    public Tag getTagRefData(String tagName, long ignoreItemId) {
        Tag tag = null;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String whereClause = "LOWER(TAG_NAME)= ? AND _id != ? ";
            String[] selectionArgs = {tagName.toLowerCase(), ignoreItemId + ""};

            Cursor cursor = db.query("TAGS_DATA", new String[]{"_id", "TAG_NAME"}, whereClause, selectionArgs, null, null, null);

            while (cursor.moveToNext()) {
                try {
                    Long id = cursor.getLong(0);
                    String name = cursor.getString(1);
                    tag = new Tag();
                    tag.setId(id);
                    tag.setName(name);
                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable6", Toast.LENGTH_LONG);
            toast.show();
        }
        return tag;
    }

}
