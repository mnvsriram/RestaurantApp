package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RatingSummary implements Serializable, Cloneable {
    public static final String FIRESTORE_NO_OF_3_RATING = "noOf3Ratings";
    public static final String FIRESTORE_NO_OF_2_RATING = "noOf2Ratings";
    public static final String FIRESTORE_NO_OF_1_RATING = "noOf1Ratings";
    public static final String FIRESTORE_REVIEW_TEXT_3_RATING = "reviewTextFor3Rating";
    public static final String FIRESTORE_REVIEW_TEXT_2_RATING = "reviewTextFor2Rating";
    public static final String FIRESTORE_REVIEW_TEXT_1_RATING = "reviewTextFor1Rating";
    public static final String FIRESTORE_RATINGSUMMARY_DATE = "ratingSummaryDate";

    public static final String FIRESTORE_REVIEW_SCORE = "reviewScore";

    public static final String FIRESTORE_ITEM_ID = "itemId";
    public static final String FIRESTORE_ITEM_NAME = "itemName";


    private String itemId;
    private String itemName;
    Map<ReviewEnum, Integer> ratingsCountPerType = new HashMap<>();
    private double score;
    Map<ReviewEnum, String> commentsPerReviewType = new HashMap<>();

    public int getGoodRatingCount() {
        return getRatingCountByType(ReviewEnum.GOOD);
    }

    public int getAverageRatingCount() {
        return getRatingCountByType(ReviewEnum.AVERAGE);
    }

    public int getBadRatingCount() {
        return getRatingCountByType(ReviewEnum.BAD);
    }

    public int getRatingCountByType(ReviewEnum reviewEnum) {
        Integer count = getRatingsCountPerType().get(reviewEnum);
        if (count == null) {
            count = 0;
        }
        return count;
    }


    public String getGoodRatingComments() {
        return getCommentsByType(ReviewEnum.GOOD);
    }

    public String getAverageRatingComments() {
        return getCommentsByType(ReviewEnum.AVERAGE);
    }


    public String getNoReviewComments() {
        return getCommentsByType(ReviewEnum.NOREVIEW);
    }

    public String getBadRatingComments() {
        return getCommentsByType(ReviewEnum.BAD);
    }

    public String getCommentsByType(ReviewEnum reviewEnum) {
        String comments = getCommentsPerReviewType().get(reviewEnum);
        if (comments == null) {
            comments = "";
        } else {
            comments = comments + ", ";
        }
        return comments;
    }


    public static RatingSummary merge(RatingSummary source, RatingSummary source2) {
        Map<ReviewEnum, Integer> ratingsPerType = source.getRatingsCountPerType();
        Map<ReviewEnum, Integer> ratingsPerType2 = source2.getRatingsCountPerType();

        Map<ReviewEnum, String> comments = source.getCommentsPerReviewType();
        Map<ReviewEnum, String> comments2 = source.getCommentsPerReviewType();


        RatingSummary cumulative = new RatingSummary();
        cumulative.setItemName(source.getItemName());
        cumulative.setItemId(source.getItemId());

        Map<ReviewEnum, Integer> cumulativeRating = new HashMap<>();
        cumulativeRating.put(ReviewEnum.GOOD, getRatingWithDefault(ratingsPerType.get(ReviewEnum.GOOD)) + getRatingWithDefault(ratingsPerType2.get(ReviewEnum.GOOD)));
        cumulativeRating.put(ReviewEnum.AVERAGE, getRatingWithDefault(ratingsPerType.get(ReviewEnum.AVERAGE)) + getRatingWithDefault(ratingsPerType2.get(ReviewEnum.AVERAGE)));
        cumulativeRating.put(ReviewEnum.BAD, getRatingWithDefault(ratingsPerType.get(ReviewEnum.BAD)) + getRatingWithDefault(ratingsPerType2.get(ReviewEnum.BAD)));
        cumulative.setRatingsCountPerType(cumulativeRating);


        Map<ReviewEnum, String> cumulativeComments = new HashMap<>();
        cumulativeComments.put(ReviewEnum.GOOD, getCommentsWithDefault(comments.get(ReviewEnum.GOOD)) + getCommentsWithDefault(comments2.get(ReviewEnum.GOOD)));
        cumulativeComments.put(ReviewEnum.AVERAGE, getCommentsWithDefault(comments.get(ReviewEnum.AVERAGE)) + getCommentsWithDefault(comments2.get(ReviewEnum.AVERAGE)));
        cumulativeComments.put(ReviewEnum.BAD, getCommentsWithDefault(comments.get(ReviewEnum.BAD)) + getCommentsWithDefault(comments2.get(ReviewEnum.BAD)));
        cumulative.setCommentsPerReviewType(cumulativeComments);

        return cumulative;
    }

    private static String getCommentsWithDefault(String comment) {
        if (comment == null) {
            comment = "";
        }
        return comment;
    }

    private static Integer getRatingWithDefault(Integer ratingCount) {
        if (ratingCount == null) {
            ratingCount = 0;
        }
        return ratingCount;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Map<ReviewEnum, Integer> getRatingsCountPerType() {
        return ratingsCountPerType;
    }

    public void setRatingsCountPerType(Map<ReviewEnum, Integer> ratingsCountPerType) {
        this.ratingsCountPerType = ratingsCountPerType;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Map<ReviewEnum, String> getCommentsPerReviewType() {
        return commentsPerReviewType;
    }

    public void setCommentsPerReviewType(Map<ReviewEnum, String> commentsPerReviewType) {
        this.commentsPerReviewType = commentsPerReviewType;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
