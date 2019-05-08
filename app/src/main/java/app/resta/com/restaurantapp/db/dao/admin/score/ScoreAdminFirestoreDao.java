package app.resta.com.restaurantapp.db.dao.admin.score;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DayScore;
import app.resta.com.restaurantapp.model.ReviewCount;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.FireStoreLocation;
import app.resta.com.restaurantapp.util.FireStoreUtil;
import app.resta.com.restaurantapp.util.PerformanceUtils;

/**
 * Created by Sriram on 06/03/2019.
 */

public class ScoreAdminFirestoreDao implements ScoreAdminDaoI {
    private final String TAG = "ScoreDao";
    private FirebaseFirestore db;

    public ScoreAdminFirestoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    private String getFieldForRating(ReviewEnum reviewEnum) {
        if (reviewEnum != null) {
            if (reviewEnum == ReviewEnum.BAD) {
                return DayScore.FIRESTORE_NO_OF_1_RATING;
            } else if (reviewEnum == ReviewEnum.AVERAGE) {
                return DayScore.FIRESTORE_NO_OF_2_RATING;
            } else if (reviewEnum == ReviewEnum.GOOD) {
                return DayScore.FIRESTORE_NO_OF_3_RATING;
            }
        }
        return null;
    }

    public void modifyScores(List<ReviewForDish> reviewForDishes) {
        HashMap<ReviewEnum, Integer> countMap = new HashMap<>();
        for (ReviewForDish reviewForDish : reviewForDishes) {
            Integer count = countMap.get(reviewForDish.getReview());
            if (count == null) {
                count = new Integer(0);
            }
            count++;
            countMap.put(reviewForDish.getReview(), count);
        }

        for (ReviewEnum reviewEnum : countMap.keySet()) {
            modifyScore(reviewEnum, countMap.get(reviewEnum), new OnResultListener<String>() {
                @Override
                public void onCallback(String status) {

                }
            });
        }

    }

    @Override
    public void modifyScore(final ReviewEnum reviewEnum, final int incrementBy, final OnResultListener<String> listener) {
        final String fieldForRating = getFieldForRating(reviewEnum);
        if (fieldForRating != null) {
            final DocumentReference todayScore = FireStoreLocation.getScoresRootLocation(db).document("today");
            db.runTransaction(new Transaction.Function<Void>() {
                @Override
                public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                    DocumentSnapshot snapshot = transaction.get(todayScore);
                    Map<String, Object> data = snapshot.getData();
                    long newNoOfRating = FireStoreUtil.getLong(data,fieldForRating,0l) + incrementBy;
                    ReviewCount reviewCount = new ReviewCount(FireStoreUtil.getLong(data,DayScore.FIRESTORE_NO_OF_3_RATING,0l), FireStoreUtil.getLong(data,DayScore.FIRESTORE_NO_OF_2_RATING,0l), FireStoreUtil.getLong(data,DayScore.FIRESTORE_NO_OF_1_RATING,0l));
                    reviewCount.increment(reviewEnum, incrementBy);

                    Double updatedTodaysScore = PerformanceUtils.getDayPerformanceScore(reviewCount);

                    Map<String, Object> itemValueMap = new HashMap<>();
                    itemValueMap.put(fieldForRating, newNoOfRating);
                    itemValueMap.put(DayScore.FIRESTORE_REVIEW_SCORE, updatedTodaysScore);

                    transaction.set(todayScore, itemValueMap, SetOptions.merge());
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
}
