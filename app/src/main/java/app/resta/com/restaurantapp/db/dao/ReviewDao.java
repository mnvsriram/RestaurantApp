package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.MyApplication;

public class ReviewDao {

    public void saveReviews(List<ReviewForDish> items) {
        mapReviewsToOrder(items);
        updateCounters(items);
    }

    private void mapReviewsToOrder(List<ReviewForDish> items) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (ReviewForDish item : items) {
                ContentValues tag = new ContentValues();
                tag.put("ORDER_ID", item.getOrderId());
                tag.put("ITEM_ID", item.getItem().getId());
                tag.put("RATING", item.getReview().getValue());
                tag.put("REVIEW", item.getReviewText());
                db.insert("ORDER_ITEM_REVIEWS", null, tag);
            }
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Reviews added to Order successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }

    }

    private void updateCounters(List<ReviewForDish> items) {
        for (ReviewForDish item : items) {
            int score = getScore(item.getItem().getId(), item.getReview().getValue());
            score++;
            if (score == 1) {
                insertInitialCounter(item.getItem().getId(), item.getReview().getValue());
            } else {
                updateCounterForItem(item.getItem().getId(), item.getReview().getValue(), score);
            }
        }
    }


    public Map<ReviewEnum, Integer> getScores(long itemId) {
        int count = 0;
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Map<ReviewEnum, Integer> scoreMap = new HashMap<>();

        String[] selectionColumns = {"RATING", "COUNT"};
        String whereClause = "ITEM_ID=  ? ";
        String[] selectionArgs = {itemId + ""};

        Cursor cursor = db.query("ITEM_REVIEW_COUNTERS", selectionColumns, whereClause, selectionArgs, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int rating = cursor.getInt(0);
                int score = cursor.getInt(1);
                scoreMap.put(ReviewEnum.of(rating), score);
            }

        }

        fillDefaults(scoreMap);
        return scoreMap;
    }

    private void fillDefaults(Map<ReviewEnum, Integer> scores) {
        if (scores.get(ReviewEnum.BAD) == null) {
            scores.put(ReviewEnum.BAD, 0);
        }

        if (scores.get(ReviewEnum.GOOD) == null) {
            scores.put(ReviewEnum.GOOD, 0);
        }

        if (scores.get(ReviewEnum.AVERAGE) == null) {
            scores.put(ReviewEnum.AVERAGE, 0);
        }
    }

    private int getScore(long itemId, int review) {
        int count = 0;
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] selectionColumns = {"COUNT"};
        String whereClause = "ITEM_ID=  ?  AND RATING=?";
        String[] selectionArgs = {itemId + "", review + ""};

        Cursor cursor = db.query("ITEM_REVIEW_COUNTERS", selectionColumns, whereClause, selectionArgs, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                count = cursor.getInt(0);
            }
        }
        return count;
    }

    private void insertInitialCounter(long itemId, int review) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues tag = new ContentValues();
            tag.put("ITEM_ID", itemId);
            tag.put("RATING", review);
            tag.put("COUNT", 1 + "");
            db.insert("ITEM_REVIEW_COUNTERS", null, tag);
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Reviews added to Order successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }


    public void updateCounterForItem(long itemId, int review, int score) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("COUNT", score);

            String whereClause = "ITEM_ID=  ?  AND RATING=?";
            String[] selectionArgs = {itemId + "", review + ""};


            db.update(
                    "ITEM_REVIEW_COUNTERS",
                    values,
                    whereClause,
                    selectionArgs);


            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Reviews added to Order successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

}



