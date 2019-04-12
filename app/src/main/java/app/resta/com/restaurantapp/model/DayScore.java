package app.resta.com.restaurantapp.model;

import java.io.Serializable;

/**
 * Created by Sriram on 13/05/2017.
 */
public class DayScore implements Serializable, Cloneable {
    public static final String FIRESTORE_RATINGS_FOR_DATE = "ratingsForDate";

    public static final String FIRESTORE_NO_OF_3_RATING = "noOf3Ratings";
    public static final String FIRESTORE_NO_OF_2_RATING = "noOf2Ratings";
    public static final String FIRESTORE_NO_OF_1_RATING = "noOf1Ratings";

    public static final String FIRESTORE_REVIEW_SCORE = "reviewScore";

}
