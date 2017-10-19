package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.RatingSummaryGroupByReviewType;
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
                reviewForDish.setReviewText(review);
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


    public Map<Long, RatingSummary> getReviewsForToday() {
        Map<Integer, Map<Long, RatingSummary>> reviews = reviews();
        if (reviews.size() > 0) {
            saveReviewSummaries(reviews);
        }
        return reviews.get(0);
    }


    private List<RatingSummaryGroupByReviewType> getReviewsByTypeItemAndDate(Map<Long, RatingSummary> summaryForItems, int noOfDaysOld) {
        List<RatingSummaryGroupByReviewType> summaries = new ArrayList<>();
        if (summaryForItems != null) {
            for (long itemId : summaryForItems.keySet()) {
                RatingSummaryGroupByReviewType ratingSummaryGroupByReviewType = new RatingSummaryGroupByReviewType();
                RatingSummary itemSummary = summaryForItems.get(itemId);
                summaries.addAll(getObjectsByReviewType(itemSummary, noOfDaysOld));
            }
        }
        return summaries;
    }


    private List<RatingSummaryGroupByReviewType> getObjectsByReviewType(RatingSummary itemSummary, int noOfDaysOld) {
        List<RatingSummaryGroupByReviewType> summariesByType = new ArrayList<>();
        summariesByType.add(getRatingSummaryGroupByReviewType(itemSummary, noOfDaysOld, ReviewEnum.GOOD));
        summariesByType.add(getRatingSummaryGroupByReviewType(itemSummary, noOfDaysOld, ReviewEnum.AVERAGE));
        summariesByType.add(getRatingSummaryGroupByReviewType(itemSummary, noOfDaysOld, ReviewEnum.BAD));
        return summariesByType;
    }

    private RatingSummaryGroupByReviewType getRatingSummaryGroupByReviewType(RatingSummary itemSummary, int noOfDaysOld, ReviewEnum reviewType) {
        RatingSummaryGroupByReviewType ratingSummaryGroupByReviewType = null;
        int count = itemSummary.getRatingCountByType(reviewType);
        String comments = itemSummary.getCommentsByType(reviewType);
        if (count > 0 || comments.length() > 0) {
            ratingSummaryGroupByReviewType = new RatingSummaryGroupByReviewType();
            Calendar todayCal = Calendar.getInstance();
            todayCal.add(Calendar.DATE, -noOfDaysOld);
            ratingSummaryGroupByReviewType.setDate(todayCal.getTime());
            ratingSummaryGroupByReviewType.setItemId(itemSummary.getItemId());
            ratingSummaryGroupByReviewType.setItemName(itemSummary.getItemName());
            ratingSummaryGroupByReviewType.setReviewEnum(reviewType);
            ratingSummaryGroupByReviewType.setCount(count);
            ratingSummaryGroupByReviewType.setComments(comments);
        }
        return ratingSummaryGroupByReviewType;
    }

    private void saveReviewSummaries(Map<Integer, Map<Long, RatingSummary>> ratingsPerDayPerItem) {
        for (int daysOlder : ratingsPerDayPerItem.keySet()) {
            if (daysOlder > 0) {
                List<RatingSummaryGroupByReviewType> summaries = getReviewsByTypeItemAndDate(ratingsPerDayPerItem.get(daysOlder), daysOlder);
                saveReviewSummaryRow(summaries);
                deleteFromReviewTable(daysOlder);
            }
        }
    }

    private void deleteFromReviewTable(int daysOlder) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            String deleteQuery = "DELETE FROM ORDER_ITEM_REVIEWS\n" +
                    "        WHERE _id in (\n" +
                    "                select reviews._id\n" +
                    "                FROM ORDER_ITEM_REVIEWS reviews\n" +
                    "                INNER JOIN ORDER_ITEMS orderItems ON orderItems._id = reviews.ORDER_ID\n" +
                    "                where\n" +
                    "                julianday(date()) - julianday(date(orderItems.CREATIONDATE)) = ?" +
                    "        )";

            db.execSQL(deleteQuery, new Integer[]{daysOlder});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveReviewSummaryRow(List<RatingSummaryGroupByReviewType> ratingSummaryGroupByReviewType) {
        if (ratingSummaryGroupByReviewType != null) {
            for (RatingSummaryGroupByReviewType ratingSummary : ratingSummaryGroupByReviewType) {
                if (ratingSummary != null) {
                    insertRatingSummary(ratingSummary);
                }
            }
        }
    }

    private void insertRatingSummary(RatingSummaryGroupByReviewType ratingSummary) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues tag = new ContentValues();

            tag.put("DATE_OF_REVIEW", DateUtil.getDateString(ratingSummary.getDate(), "yyyy-MM-dd HH:mm:ss"));
            tag.put("ITEM_ID", ratingSummary.getItemId());
            tag.put("ITEM_NAME", ratingSummary.getItemName());
            tag.put("RATING_TYPE", ratingSummary.getReviewEnum().getValue() + "");
            tag.put("COUNT", ratingSummary.getCount());
            tag.put("COMMENTS", ratingSummary.getComments());
            db.insert("RATING_DAILY_SUMMARY", null, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private Map<Integer, Map<Long, RatingSummary>> reviews() {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Map<Long, RatingSummary> ratingsSummaryMap = new LinkedHashMap<>();

        Map<Integer, Map<Long, RatingSummary>> ratingSummaryPerDayPerItem = new HashMap<>();

        String sql = "select reviews.ITEM_ID,reviews.RATING,menuItem.NAME, count(*) as noOfReviews, GROUP_CONCAT(reviews.REVIEW) as comments, julianday(date()) - julianday(date(CREATIONDATE))  as daysOlder from ORDER_ITEM_REVIEWS reviews, MENU_ITEM menuItem ,ORDER_ITEMS orderItems where reviews.ITEM_ID = menuItem._ID  and orderItems._id = reviews.ORDER_ID " +
                "group by reviews.ITEM_ID,reviews.RATING,menuItem.NAME, daysOlder";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                long itemId = cursor.getLong(0);
                int ratingType = cursor.getInt(1);
                String name = cursor.getString(2);
                int noOfReviews = cursor.getInt(3);
                String comments = cursor.getString(4);
                int daysOlder = cursor.getInt(5);


                Map<Long, RatingSummary> ratingsSummaryMapPerItem = ratingSummaryPerDayPerItem.get(daysOlder);

                if (ratingsSummaryMapPerItem == null) {
                    ratingsSummaryMapPerItem = new HashMap<>();
                }

                RatingSummary summary = ratingsSummaryMapPerItem.get(itemId);
                if (summary == null) {
                    summary = new RatingSummary();
                    summary.setItemId(itemId);
                    summary.setItemName(name);
                }
                summary.getRatingsCountPerType().put(ReviewEnum.of(ratingType), noOfReviews);
                summary.getCommentsPerReviewType().put(ReviewEnum.of(ratingType), comments);
                ratingsSummaryMapPerItem.put(itemId, summary);

                ratingSummaryPerDayPerItem.put(daysOlder, ratingsSummaryMapPerItem);
            }
        }
        return ratingSummaryPerDayPerItem;
    }


    public String getLatestFiveComments() {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        StringBuffer reviewComments = new StringBuffer("");
        String sql = "select menuItem.NAME, reviews.REVIEW from ORDER_ITEM_REVIEWS reviews, MENU_ITEM menuItem ,ORDER_ITEMS orderItems where reviews.ITEM_ID = menuItem._ID  and orderItems._id = reviews.ORDER_ID and reviews.REVIEW!='' " +
                "order by orderItems.CREATIONDATE desc limit 5";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                String comments = cursor.getString(1);
                reviewComments.append(name + ": " + comments + "\n");
            }
        }
        if (reviewComments.length() > 2) {
            reviewComments.reverse().delete(0, 1).reverse();
        }
        return reviewComments.toString();
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



