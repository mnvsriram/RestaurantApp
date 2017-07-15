package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.MyApplication;

public class ReviewDao {

    public void saveReviews(List<ReviewForDish> items) {
        removeEmptyReviews(items);
        if (items.size() > 0) {
            mapReviewsToOrder(items);
            updateCounters(items);
        }
    }

    private void removeEmptyReviews(List<ReviewForDish> items) {
        Iterator<ReviewForDish> reviewForDishIterator = items.iterator();
        for (; reviewForDishIterator.hasNext(); ) {
            ReviewForDish reviewForDish = reviewForDishIterator.next();
            if (!((reviewForDish.getReview() != null && reviewForDish.getReview().getValue() > 0) || (reviewForDish.getReviewText() != null && reviewForDish.getReviewText().length() > 0))) {
                reviewForDishIterator.remove();
            }
        }
    }

    private void mapReviewsToOrder(List<ReviewForDish> items) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (ReviewForDish item : items) {
                if (item.getReview() == null) {
                    item.setReview(ReviewEnum.NOREVIEW);
                }
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
            if (item.getReview() != null && !item.getReview().equals(ReviewEnum.NOREVIEW)) {
                int score = getScore(item.getItem().getId(), item.getReview().getValue());
                score++;
                if (score == 1) {
                    insertInitialCounter(item.getItem().getId(), item.getReview().getValue());
                } else {
                    updateCounterForItem(item.getItem().getId(), item.getReview().getValue(), score);
                }
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

    public Map<Long, List<ReviewForDish>> getReviews(Set<Long> orderIds) {
        Map<Long, List<ReviewForDish>> reviewsForOrders = new HashMap<>();
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Map<ReviewEnum, Integer> scoreMap = new HashMap<>();


        String sql = "select reviews.ORDER_ID,reviews.ITEM_ID,reviews.RATING,reviews.REVIEW, menuItem.NAME from ORDER_ITEM_REVIEWS reviews, MENU_ITEM menuItem where reviews.ITEM_ID = menuItem._ID ";
        sql += " and ORDER_ID IN (" + TextUtils.join(",", Collections.nCopies(orderIds.size(), "?")) + ")";

        Long[] longArr = orderIds.toArray(new Long[orderIds.size()]);

        String[] arr = new String[longArr.length];
        for (int i = 0; i < longArr.length; i++) {
            arr[i] = String.valueOf(longArr[i]);
        }


        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, arr);
            //cursor = db.query("ORDER_ITEM_REVIEWS", selectionColumns, where, arr, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long orderId = cursor.getLong(0);
                long itemId = cursor.getLong(1);
                int rating = cursor.getInt(2);
                String review = cursor.getString(3);
                String itemName = cursor.getString(4);
                if (rating == 0) {
                    continue;
                }
                ReviewForDish reviewForDish = new ReviewForDish();
                reviewForDish.setOrderId(orderId);
                reviewForDish.setReview(ReviewEnum.of(rating));

                RestaurantItem item = new RestaurantItem();
                item.setId(itemId);
                item.setName(itemName);

                reviewForDish.setItem(item);
                List<ReviewForDish> reviews = reviewsForOrders.get(orderId);
                if (reviews == null) {
                    reviews = new ArrayList<>();
                }
                reviews.add(reviewForDish);
                reviewsForOrders.put(orderId, reviews);
            }
        }
        return reviewsForOrders;
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
/*
    public Map<Long, List<RatingSummaryGroupByReviewType>> reviewsForToday() {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Map<Long, List<RatingSummaryGroupByReviewType>> ratingsMap = new HashMap<>();
        String sql = "select reviews.ITEM_ID,reviews.RATING,menuItem.NAME, count(*) as noOfReviews, GROUP_CONCAT(reviews.REVIEW) as comments from ORDER_ITEM_REVIEWS reviews, MENU_ITEM menuItem ,ORDER_ITEMS orderItems where reviews.ITEM_ID = menuItem._ID  and orderItems._id = reviews.ORDER_ID and orderItems.CREATIONDATE > ?" +
                "group by reviews.ITEM_ID,reviews.RATING,menuItem.NAME";
        String[] params = new String[1];
        params[0] = DateUtil.getDateString(new Date(), "yyyy-MM-dd");
        Cursor cursor = db.rawQuery(sql, params);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(0);
                int ratingType = cursor.getInt(1);
                String name = cursor.getString(2);
                int noOfReviews = cursor.getInt(3);
                String comments = cursor.getString(4);

                RatingSummaryGroupByReviewType ratingGroupByItemAndRatingType = new RatingSummaryGroupByReviewType();
                ratingGroupByItemAndRatingType.setReviewEnum(ReviewEnum.of(ratingType));
                ratingGroupByItemAndRatingType.setCount(noOfReviews);
                ratingGroupByItemAndRatingType.setItemName(name);
                ratingGroupByItemAndRatingType.setItemId(itemId);
                ratingGroupByItemAndRatingType.setNoOfDaysOld(0);
                ratingGroupByItemAndRatingType.setComments(comments);
                List<RatingSummaryGroupByReviewType> ratingsForThisItem = ratingsMap.get(itemId);
                if (ratingsForThisItem == null) {
                    ratingsForThisItem = new ArrayList<>();
                }
                ratingsForThisItem.add(ratingGroupByItemAndRatingType);
                ratingsMap.put(itemId, ratingsForThisItem);
            }
        }
        return ratingsMap;
    }
*/

    public Map<Long, RatingSummary> reviewsForToday() {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Map<Long, RatingSummary> ratingsSummaryMap = new HashMap<>();
        String sql = "select reviews.ITEM_ID,reviews.RATING,menuItem.NAME, count(*) as noOfReviews, GROUP_CONCAT(reviews.REVIEW) as comments from ORDER_ITEM_REVIEWS reviews, MENU_ITEM menuItem ,ORDER_ITEMS orderItems where reviews.ITEM_ID = menuItem._ID  and orderItems._id = reviews.ORDER_ID and orderItems.CREATIONDATE > ?" +
                "group by reviews.ITEM_ID,reviews.RATING,menuItem.NAME";
        String[] params = new String[1];
        params[0] = DateUtil.getDateString(new Date(), "yyyy-MM-dd");
        Cursor cursor = db.rawQuery(sql, params);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(0);
                int ratingType = cursor.getInt(1);
                String name = cursor.getString(2);
                int noOfReviews = cursor.getInt(3);
                String comments = cursor.getString(4);

                RatingSummary summary = ratingsSummaryMap.get(itemId);
                if (summary == null) {
                    summary = new RatingSummary();
                    summary.setItemId(itemId);
                    summary.setItemName(name);
                }
                summary.getRatingsCountPerType().put(ReviewEnum.of(ratingType), noOfReviews);
                summary.getCommentsPerReviewType().put(ReviewEnum.of(ratingType), comments);
                ratingsSummaryMap.put(itemId, summary);
            }
        }
        return ratingsSummaryMap;
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



