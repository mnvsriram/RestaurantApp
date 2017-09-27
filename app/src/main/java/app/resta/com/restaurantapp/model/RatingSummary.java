package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class RatingSummary implements Serializable {
    private long itemId;
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

    private int getRatingCountByType(ReviewEnum reviewEnum) {
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

    private String getCommentsByType(ReviewEnum reviewEnum) {
        String comments = getCommentsPerReviewType().get(reviewEnum);
        if (comments == null) {
            comments = "";
        } else {
            comments = comments + ", ";
        }
        return comments;
    }

    public void mergeFrom(RatingSummary source) {
        mergeRatingCounts(source);
        mergeComments(source);
    }

/*
    public void mergeFrom(RatingSummary source,RatingSummary destination) {
        mergeRatingCounts(source,destination);
        mergeComments(source);
    }
*/
    private void mergeRatingCounts(RatingSummary source) {
        Map<ReviewEnum, Integer> ratingsPerType = source.getRatingsCountPerType();
        if (ratingsPerType != null) {
            for (ReviewEnum reviewEnum : ratingsPerType.keySet()) {
                int countFromSource = ratingsPerType.get(reviewEnum);
                Integer count = getRatingsCountPerType().get(reviewEnum);
                if (count == null) {
                    count = 0;
                }
                count += countFromSource;
                getRatingsCountPerType().put(reviewEnum, count);
            }
        }
    }

    private void mergeComments(RatingSummary source) {
        Map<ReviewEnum, String> ratingsPerType = source.getCommentsPerReviewType();
        if (ratingsPerType != null) {
            for (ReviewEnum reviewEnum : ratingsPerType.keySet()) {
                String commentsFromSource = ratingsPerType.get(reviewEnum);
                String comments = this.getCommentsPerReviewType().get(reviewEnum);

                if (comments == null) {
                    comments = "";
                }
                comments += commentsFromSource;
                this.getCommentsPerReviewType().put(reviewEnum, comments);
            }
        }
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
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
}
