package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.RatingSummaryGroupByReviewType;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.MyApplication;

public class RatingSummaryDao {
    private static Map<Integer, Map<Long, RatingSummary>> ratingsPerDayPerItem = new HashMap<>();
    private static int dataFetchedForDays = -1;
    private ReviewDao reviewDao = new ReviewDao();

    public void clearReviewCache() {
        ratingsPerDayPerItem = new HashMap<>();        //fetchDataFromDB = true;
        dataFetchedForDays = -1;
    }

    public void saveReviews(List<RatingSummaryGroupByReviewType> ratingSummaryGroupByReviewType) {
        if (ratingSummaryGroupByReviewType != null) {
            for (RatingSummaryGroupByReviewType ratingSummary : ratingSummaryGroupByReviewType) {
                insertRatingSummary(ratingSummary);
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
            tag.put("ITEM_NAME", ratingSummary.getItemId());
            tag.put("RATING_TYPE", ratingSummary.getReviewEnum().getValue() + "");
            tag.put("COUNT", ratingSummary.getCount());
            tag.put("COMMENTS", ratingSummary.getComments());
            db.insert("RATING_DAILY_SUMMARY", null, tag);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addToMapPerItemId(RatingSummary summary, int noOfDaysOld) {
        Map<Long, RatingSummary> ratingsForADay = ratingsPerDayPerItem.get(noOfDaysOld);
        if (ratingsForADay == null) {
            ratingsForADay = new HashMap<>();
        }
        ratingsForADay.put(summary.getItemId(), summary);
        ratingsPerDayPerItem.put(noOfDaysOld, ratingsForADay);
    }

    public Map<Integer, Map<Long, RatingSummary>> getRatingsPerDayPerItem(int noOfDaysOld) {
        getRatingSummaries(noOfDaysOld);
        Map<Integer, Map<Long, RatingSummary>> summaryForTheSpecifiedDuration = new TreeMap<>();
        if (ratingsPerDayPerItem != null) {
            for (Integer day : ratingsPerDayPerItem.keySet()) {
                if (day <= noOfDaysOld) {
                    summaryForTheSpecifiedDuration.put(day, ratingsPerDayPerItem.get(day));
                }
            }
        }
        return summaryForTheSpecifiedDuration;
    }

    private void getRatingSummaries(int noOfDaysOlder) {
        ratingsPerDayPerItem.put(0, reviewDao.reviewsForToday());
        if (dataFetchedForDays == -1) {
            dataFetchedForDays = 0;
        }
        if (noOfDaysOlder > dataFetchedForDays) {
            loadRatingSummaries(noOfDaysOlder, dataFetchedForDays);
            dataFetchedForDays = noOfDaysOlder;
        }
    }

    private void loadRatingSummaries(int from, int to) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Map<Integer, List<RatingSummaryGroupByReviewType>> ratingSummaryMap = new HashMap<>();

            //String sql = "select (datetime() - DATE_OF_REVIEW) as daysOlder, ITEM_ID, ITEM_NAME, RATING_TYPE, COUNT, COMMENTS from RATING_DAILY_SUMMARY where daysOlder<=?  and daysOlder>=?";
            String sql = "select julianday(date()) - julianday(date(DATE_OF_REVIEW))  as daysOlder, ITEM_ID, ITEM_NAME, RATING_TYPE, COUNT, COMMENTS  from RATING_DAILY_SUMMARY where daysOlder<=" + from + "  and daysOlder>=" + to;
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    int daysOlder = cursor.getInt(0);
                    long item_id = cursor.getLong(1);
                    String itemName = cursor.getString(2);
                    int ratingType = cursor.getInt(3);
                    int count = cursor.getInt(4);
                    String comments = cursor.getString(5);


                    RatingSummary summary = new RatingSummary();
                    summary.getRatingsCountPerType().put(ReviewEnum.of(ratingType), count);
                    summary.getCommentsPerReviewType().put(ReviewEnum.of(ratingType), comments);
                    summary.setItemId(item_id);
                    summary.setItemName(itemName);
                    addToMapPerItemId(summary, daysOlder);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}