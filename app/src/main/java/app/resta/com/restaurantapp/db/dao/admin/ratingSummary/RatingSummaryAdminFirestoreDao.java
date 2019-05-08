package app.resta.com.restaurantapp.db.dao.admin.ratingSummary;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RatingSummary;
import app.resta.com.restaurantapp.model.ReviewCount;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.FireStoreUtil;
import app.resta.com.restaurantapp.util.PerformanceUtils;

/**
 * Created by Sriram on 06/03/2019.
 */

public class RatingSummaryAdminFirestoreDao implements RatingSummaryAdminDaoI {
    private final String TAG = "ScoreDao";
    private FirebaseFirestore db;

    public RatingSummaryAdminFirestoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }

    @Override
    public void modifyRatingSummary(List<ReviewForDish> reviewForDishes) {
        createTodaysNode(reviewForDishes);
    }

    private String getFieldForRating(ReviewEnum reviewEnum) {
        if (reviewEnum != null) {
            if (reviewEnum == ReviewEnum.BAD) {
                return RatingSummary.FIRESTORE_NO_OF_1_RATING;
            } else if (reviewEnum == ReviewEnum.AVERAGE) {
                return RatingSummary.FIRESTORE_NO_OF_2_RATING;
            } else if (reviewEnum == ReviewEnum.GOOD) {
                return RatingSummary.FIRESTORE_NO_OF_3_RATING;
            }
        }
        return null;
    }


    private String getFieldForReviewText(ReviewEnum reviewEnum) {
        if (reviewEnum != null) {
            if (reviewEnum == ReviewEnum.BAD) {
                return RatingSummary.FIRESTORE_REVIEW_TEXT_1_RATING;
            } else if (reviewEnum == ReviewEnum.AVERAGE) {
                return RatingSummary.FIRESTORE_REVIEW_TEXT_2_RATING;
            } else if (reviewEnum == ReviewEnum.GOOD) {
                return RatingSummary.FIRESTORE_REVIEW_TEXT_3_RATING;
            }
        }
        return null;
    }


    private String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        return dateFormat.format(date);
    }


    private void createTodaysNode(final List<ReviewForDish> reviewForDishes) {
        final String date = getTodaysDate();
        final DocumentReference todaySummaryForItem = FireStoreLocation.getRatingSummaryRootLocation(db).document(date);
        Map<String, Object> itemValueMap = new HashMap<>();
        itemValueMap.put(RatingSummary.FIRESTORE_RATINGSUMMARY_DATE, FieldValue.serverTimestamp());
        todaySummaryForItem.set(itemValueMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                for (ReviewForDish reviewForDish : reviewForDishes) {
                    modifyRatingSummary(date, reviewForDish, 1, new OnResultListener<String>() {
                        @Override
                        public void onCallback(String status) {

                        }
                    });
                }
            }
        });
    }

    public void modifyRatingSummary(String date, final ReviewForDish reviewForDish, final int incrementBy, final OnResultListener<String> listener) {

        final String fieldForRating = getFieldForRating(reviewForDish.getReview());
        final String fieldForReviewText = getFieldForReviewText(reviewForDish.getReview());
        if (fieldForRating != null) {
            final DocumentReference todaySummaryForItem = FireStoreLocation.getRatingSummaryLocationForADate(db, date).document(reviewForDish.getItem().getId());
            todaySummaryForItem.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot snapshot = task.getResult();
                        Map<String, Object> itemValueMap = new HashMap<>();
                        if (snapshot.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + snapshot.getData());
                            Map<String, Object> data = snapshot.getData();
                            long newNoOfRating = FireStoreUtil.getLong(data, fieldForRating, 0l) + incrementBy;
                            ReviewCount reviewCount = new ReviewCount(FireStoreUtil.getLong(data, RatingSummary.FIRESTORE_NO_OF_3_RATING, 0l), FireStoreUtil.getLong(data, RatingSummary.FIRESTORE_NO_OF_2_RATING, 0l), FireStoreUtil.getLong(data, RatingSummary.FIRESTORE_NO_OF_1_RATING, 0l));
                            reviewCount.increment(reviewForDish.getReview(), incrementBy);
                            Double updatedTodaysScore = PerformanceUtils.getDayPerformanceScore(reviewCount);
                            if (reviewForDish.getReviewText() != null) {
                                if (FireStoreUtil.getString(data, fieldForReviewText) != null) {
                                    String updatedReviewText = FireStoreUtil.getString(data, fieldForReviewText) + ";" + reviewForDish.getReviewText();
                                    itemValueMap.put(fieldForReviewText, updatedReviewText);
                                } else {
                                    String updatedReviewText = reviewForDish.getReviewText();
                                    itemValueMap.put(fieldForReviewText, updatedReviewText);
                                }

                            }
                            itemValueMap.put(fieldForRating, newNoOfRating);
                            itemValueMap.put(RatingSummary.FIRESTORE_REVIEW_SCORE, updatedTodaysScore);
                        } else {
                            Log.d(TAG, "No such document");
                            long newNoOfRating = incrementBy;
                            ReviewCount reviewCount = new ReviewCount(0, 0, 0);
                            reviewCount.increment(reviewForDish.getReview(), incrementBy);
                            Double updatedTodaysScore = PerformanceUtils.getDayPerformanceScore(reviewCount);

                            itemValueMap.put(fieldForRating, newNoOfRating);
                            itemValueMap.put(RatingSummary.FIRESTORE_REVIEW_SCORE, updatedTodaysScore);
                            itemValueMap.put(RatingSummary.FIRESTORE_ITEM_ID, reviewForDish.getItem().getId());
                            itemValueMap.put(RatingSummary.FIRESTORE_ITEM_NAME, reviewForDish.getItem().getName());
                            if(reviewForDish.getReviewText()!=null){
                                itemValueMap.put(fieldForReviewText, reviewForDish.getReviewText());
                            }

                        }

                        todaySummaryForItem.set(itemValueMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                listener.onCallback("completed");
                            }
                        });

                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                        listener.onCallback("completed with error");
                    }
                }
            });
        }
    }


    public void getRatingsPerDayPerItem(int noOfDaysOld, final OnResultListener<Map<Long, Map<String, RatingSummary>>> listener) {
        final Map<Long, Map<String, RatingSummary>> ratingsPerDayPerItem = new HashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        cal.add(Calendar.DATE, -noOfDaysOld);
        Date fromDate = cal.getTime();


        Calendar toCal = Calendar.getInstance();
        toCal.set(Calendar.HOUR_OF_DAY, 23);
        toCal.set(Calendar.MINUTE, 59);
        toCal.set(Calendar.SECOND, 59);
        Date toDate = toCal.getTime();


        FireStoreLocation.getRatingSummaryRootLocation(db).whereGreaterThanOrEqualTo(RatingSummary.FIRESTORE_RATINGSUMMARY_DATE, fromDate)
                .whereLessThanOrEqualTo(RatingSummary.FIRESTORE_RATINGSUMMARY_DATE, toDate).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final AtomicInteger integer = new AtomicInteger(0);
                            for (QueryDocumentSnapshot documentForDate : task.getResult()) {
                                getItemSummariesForDate(documentForDate, ratingsPerDayPerItem, new OnResultListener<String>() {
                                    @Override
                                    public void onCallback(String status) {
                                        integer.getAndIncrement();
                                        if (integer.get() == task.getResult().size()) {
                                            listener.onCallback(ratingsPerDayPerItem);
                                        }
                                    }
                                });

                            }
                        } else {
                            Log.e(TAG, "Error getting groups in meny type.", task.getException());
                            listener.onCallback(ratingsPerDayPerItem);
                        }
                    }
                });
    }


    private void getItemSummariesForDate(QueryDocumentSnapshot ratingSummaryForDate, final Map<Long, Map<String, RatingSummary>> resultMap, final OnResultListener<String> listener) {
        String dateStr = ratingSummaryForDate.getId();
        Date dateFromDocument = DateUtil.getDateFromString(dateStr, "yyyyMMdd");
        long diff = Calendar.getInstance().getTime().getTime() - dateFromDocument.getTime();
        final long diffDays = diff / (24 * 60 * 60 * 1000);
        final Map<String, RatingSummary> summaryForADate = new HashMap<>();
        if (resultMap.get(diffDays) != null) {
            summaryForADate.putAll(resultMap.get(diffDays));
        }

        FireStoreLocation.getRatingSummaryLocationForADate(db, ratingSummaryForDate.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentForItem : task.getResult()) {
                        Map<String, Object> keyValueMap = documentForItem.getData();
                        int noOf1Ratings = FireStoreUtil.getInt(keyValueMap, RatingSummary.FIRESTORE_NO_OF_1_RATING, 0);
                        int noOf2Ratings = FireStoreUtil.getInt(keyValueMap, RatingSummary.FIRESTORE_NO_OF_2_RATING, 0);
                        int noOf3Ratings = FireStoreUtil.getInt(keyValueMap, RatingSummary.FIRESTORE_NO_OF_3_RATING, 0);

                        String reviewsFor1Rating = FireStoreUtil.getString(keyValueMap, RatingSummary.FIRESTORE_REVIEW_TEXT_1_RATING);
                        String reviewsFor2Rating = FireStoreUtil.getString(keyValueMap, RatingSummary.FIRESTORE_REVIEW_TEXT_2_RATING);
                        String reviewsFor3Rating = FireStoreUtil.getString(keyValueMap, RatingSummary.FIRESTORE_REVIEW_TEXT_3_RATING);


                        RatingSummary summary = new RatingSummary();
                        summary.getRatingsCountPerType().put(ReviewEnum.BAD, noOf1Ratings);
                        summary.getRatingsCountPerType().put(ReviewEnum.AVERAGE, noOf2Ratings);
                        summary.getRatingsCountPerType().put(ReviewEnum.GOOD, noOf3Ratings);

                        summary.getCommentsPerReviewType().put(ReviewEnum.BAD, reviewsFor1Rating);
                        summary.getCommentsPerReviewType().put(ReviewEnum.AVERAGE, reviewsFor2Rating);
                        summary.getCommentsPerReviewType().put(ReviewEnum.GOOD, reviewsFor3Rating);

                        summary.setItemId(FireStoreUtil.getString(keyValueMap, RatingSummary.FIRESTORE_ITEM_ID));
                        summary.setItemName(FireStoreUtil.getString(keyValueMap, RatingSummary.FIRESTORE_ITEM_NAME));

                        summaryForADate.put(summary.getItemId(), summary);
                    }

                    resultMap.put(diffDays, summaryForADate);
                } else {
                    Log.e(TAG, "Error getting groups in meny type.", task.getException());
                }
                listener.onCallback("success");
            }
        });
    }

}
