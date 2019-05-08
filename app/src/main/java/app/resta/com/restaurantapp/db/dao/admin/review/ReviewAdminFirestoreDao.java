package app.resta.com.restaurantapp.db.dao.admin.review;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.FireStoreLocation;

/**
 * Created by Sriram on 06/03/2019.
 */

public class ReviewAdminFirestoreDao implements ReviewAdminDaoI {
    private final String TAG = "ReviewDao";
    private FirebaseFirestore db;

    public ReviewAdminFirestoreDao() {
        db = FirebaseAppInstance.getFireStoreInstance();
    }


    public void addReviews(List<ReviewForDish> reviewForDishes) {
        for (ReviewForDish reviewForDish : reviewForDishes) {
            if (reviewForDish.getReview() != null || (reviewForDish.getReviewText() != null && reviewForDish.getReviewText().length() > 0)) {
                addReview(reviewForDish, new OnResultListener<ReviewForDish>() {
                    @Override
                    public void onCallback(ReviewForDish reviewForDish1) {

                    }
                });
            }
        }
    }

    @Override
    public void addReview(final ReviewForDish reviewForDish, final OnResultListener<ReviewForDish> listener) {
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put(ReviewForDish.FIRESTORE_CREATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        reviewMap.put(ReviewForDish.FIRESTORE_CREATED_AT_KEY, FieldValue.serverTimestamp());
        reviewMap.put(ReviewForDish.FIRESTORE_UPDATED_BY_KEY, FireStoreLocation.getUserLoggedIn());
        reviewMap.put(ReviewForDish.FIRESTORE_UPDATED_AT_KEY, FieldValue.serverTimestamp());

        reviewMap.put(ReviewForDish.FIRESTORE_ITEM_ID, reviewForDish.getItem().getId());
        reviewMap.put(ReviewForDish.FIRESTORE_ITEM_NAME, reviewForDish.getItem().getName());
        reviewMap.put(ReviewForDish.FIRESTORE_ORDER_ID, reviewForDish.getOrderId());
        if (reviewForDish.getReview() != null) {
            reviewMap.put(ReviewForDish.FIRESTORE_RATING, reviewForDish.getReview().getValue());
        } else {
            reviewMap.put(ReviewForDish.FIRESTORE_RATING, ReviewEnum.NOREVIEW.getValue());
        }

        reviewMap.put(ReviewForDish.FIRESTORE_REVIEW_TEXT, reviewForDish.getReviewText());
        if(reviewForDish.getReviewText()!=null){
            reviewMap.put(ReviewForDish.FIRESTORE_REVIEW_TEXT_PRESENT, true);
        }else{
            reviewMap.put(ReviewForDish.FIRESTORE_REVIEW_TEXT_PRESENT, false);
        }
        reviewMap.put(ReviewForDish.FIRESTORE_REVIEW_TEXT, reviewForDish.getReviewText());
        DocumentReference newReviewReference = FireStoreLocation.getReviewsRootLocation(db).document();

        newReviewReference.set(reviewMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Review successfully created!");
                } else {
                    Log.d(TAG, "Error while creating order!");
                }
                listener.onCallback(reviewForDish);
            }
        });
    }

    @Override
    public void getLatestFiveComments(final OnResultListener<String> listener) {
        final StringBuilder reviewComments = new StringBuilder("");

        FireStoreLocation.getReviewsRootLocation(db).whereEqualTo(ReviewForDish.FIRESTORE_REVIEW_TEXT_PRESENT,true).orderBy(ReviewForDish.FIRESTORE_CREATED_AT_KEY).limit(5).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        ReviewForDish reviewForDish = ReviewForDish.prepare(document);
                        String name = reviewForDish.getItem().getName();
                        String comments = reviewForDish.getReviewText();
                        reviewComments.append(name + ": " + comments + "\n");
                    }
                    if (reviewComments.length() > 2) {
                        reviewComments.reverse().delete(0, 1).reverse();
                    }
                } else {
                    Log.e(TAG, "Error getting groups in meny type.", task.getException());
                }
                listener.onCallback(reviewComments.toString());
            }
        });
    }


}
