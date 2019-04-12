package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

/**
 * Created by Sriram on 23/06/2017.
 */
public class ReviewForDish implements Serializable {
    private RestaurantItem item;
    private ReviewEnum review;
    private String orderId;
    private String reviewText;

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    public static final String FIRESTORE_RATING = "rating";
    public static final String FIRESTORE_REVIEW_TEXT = "reviewText";
    public static final String FIRESTORE_ORDER_ID = "orderId";
    public static final String FIRESTORE_ITEM_ID = "itemId";


    public ReviewForDish() {

    }

    public ReviewForDish(RestaurantItem item, String orderId) {
        this.item = item;
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public RestaurantItem getItem() {
        return item;
    }

    public void setItem(RestaurantItem item) {
        this.item = item;
    }

    public ReviewEnum getReview() {
        return review;
    }

    public void setReview(ReviewEnum review) {
        this.review = review;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ReviewForDish that = (ReviewForDish) o;

        if (orderId != that.orderId) return false;
        return item != null ? item.equals(that.item) : that.item == null;

    }

    @Override
    public int hashCode() {
        int result = item != null ? item.hashCode() : 0;
        result = 31 * result + (orderId != null ? orderId.hashCode() : 0);
        return result;
    }


    public static ReviewForDish prepare(QueryDocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }


    public static ReviewForDish prepare(DocumentSnapshot documentSnapshot) {
        return get(documentSnapshot);
    }

    @NonNull
    private static ReviewForDish get(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        ReviewForDish reviewForDish = new ReviewForDish();
        reviewForDish.setReviewText(FireStoreUtil.getString(keyValueMap, FIRESTORE_REVIEW_TEXT));
        reviewForDish.setReview(ReviewEnum.of(FireStoreUtil.getInt(keyValueMap, FIRESTORE_RATING)));
        reviewForDish.setOrderId(FireStoreUtil.getString(keyValueMap, FIRESTORE_ORDER_ID));
        RestaurantItem item = new RestaurantItem();
        item.setId(FireStoreUtil.getString(keyValueMap, FIRESTORE_ITEM_ID));
        reviewForDish.setItem(item);
        return reviewForDish;
    }
}
