package app.resta.com.restaurantapp.db.dao.admin.ratingSummary;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

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
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(final Transaction transaction) throws FirebaseFirestoreException {
                    todaySummaryForItem.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot snapshot = task.getResult();
                                if (snapshot.exists()) {
                                    Log.d(TAG, "DocumentSnapshot data: " + snapshot.getData());

                                    long newNoOfRating = snapshot.getLong(fieldForRating) + incrementBy;
                                    ReviewCount reviewCount = new ReviewCount(snapshot.getLong(RatingSummary.FIRESTORE_NO_OF_3_RATING), snapshot.getLong(RatingSummary.FIRESTORE_NO_OF_2_RATING), snapshot.getLong(RatingSummary.FIRESTORE_NO_OF_1_RATING));
                                    reviewCount.increment(reviewForDish.getReview(), incrementBy);

                                    Double updatedTodaysScore = PerformanceUtils.getDayPerformanceScore(reviewCount);


                                    String updatedReviewText = snapshot.getLong(fieldForReviewText) + ";" + reviewForDish.getReviewText();

                                    Map<String, Object> itemValueMap = new HashMap<>();
                                    itemValueMap.put(fieldForRating, newNoOfRating);
                                    itemValueMap.put(RatingSummary.FIRESTORE_REVIEW_SCORE, updatedTodaysScore);
                                    itemValueMap.put(fieldForReviewText, updatedReviewText);
                                    transaction.set(todaySummaryForItem, itemValueMap, SetOptions.merge());
                                } else {
                                    Log.d(TAG, "No such document");
                                    long newNoOfRating = incrementBy;
                                    ReviewCount reviewCount = new ReviewCount(0, 0, 0);
                                    reviewCount.increment(reviewForDish.getReview(), incrementBy);

                                    Double updatedTodaysScore = PerformanceUtils.getDayPerformanceScore(reviewCount);

                                    Map<String, Object> itemValueMap = new HashMap<>();
                                    itemValueMap.put(fieldForRating, newNoOfRating);
                                    itemValueMap.put(RatingSummary.FIRESTORE_REVIEW_SCORE, updatedTodaysScore);
                                    itemValueMap.put(RatingSummary.FIRESTORE_ITEM_ID, reviewForDish.getItem().getId());
                                    itemValueMap.put(RatingSummary.FIRESTORE_ITEM_NAME, reviewForDish.getItem().getName());
                                    itemValueMap.put(fieldForReviewText, reviewForDish.getReviewText());
                                    transaction.set(todaySummaryForItem, itemValueMap, SetOptions.merge());
                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                    return null;
                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    listener.onCallback("Updated");
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
        Date toDate = cal.getTime();

        cal.add(Calendar.DATE, -noOfDaysOld);
        Date fromDate = cal.getTime();

        FireStoreLocation.getRatingSummaryRootLocation(db).whereGreaterThanOrEqualTo(RatingSummary.FIRESTORE_RATINGSUMMARY_DATE, fromDate)
                .whereLessThanOrEqualTo(RatingSummary.FIRESTORE_RATINGSUMMARY_DATE, toDate).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            AtomicInteger integer = new AtomicInteger(0);
                            for (QueryDocumentSnapshot documentForDate : task.getResult()) {
                                getItemSummariesForDate(documentForDate, ratingsPerDayPerItem);
                                int andIncrement = integer.getAndIncrement();
                                if (andIncrement == task.getResult().size()) {
                                    listener.onCallback(ratingsPerDayPerItem);
                                }
                            }
                        } else {
                            Log.e(TAG, "Error getting groups in meny type.", task.getException());
                        }
                    }
                });
    }


    private void getItemSummariesForDate(QueryDocumentSnapshot ratingSummaryForDate, final Map<Long, Map<String, RatingSummary>> resultMap) {
        String dateStr = ratingSummaryForDate.getId();
        Date dateFromDocument = DateUtil.getDateFromString(dateStr, "yyyyMMdd");
        long diff = Calendar.getInstance().getTime().getTime() - dateFromDocument.getTime();
        final long diffDays = diff / (24 * 60 * 60 * 1000);
        final Map<String, RatingSummary> summaryForADate = resultMap.get(diffDays);

        FireStoreLocation.getRatingSummaryLocationForADate(db, ratingSummaryForDate.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentForItem : task.getResult()) {
                        Map<String, Object> keyValueMap = documentForItem.getData();
                        int noOf1Ratings = FireStoreUtil.getInt(keyValueMap, RatingSummary.FIRESTORE_NO_OF_1_RATING);
                        int noOf2Ratings = FireStoreUtil.getInt(keyValueMap, RatingSummary.FIRESTORE_NO_OF_2_RATING);
                        int noOf3Ratings = FireStoreUtil.getInt(keyValueMap, RatingSummary.FIRESTORE_NO_OF_3_RATING);

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
            }
        });
    }

}
