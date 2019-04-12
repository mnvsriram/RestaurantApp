package app.resta.com.restaurantapp.model;

import java.io.Serializable;

public class ReviewCount implements Serializable {
    private long badReviewCount;
    private long goodReviewCount;
    private long averageReviewCount;

    public ReviewCount(long goodReviewCount, long averageReviewCount, long badReviewCount) {
        this.badReviewCount = badReviewCount;
        this.goodReviewCount = goodReviewCount;
        this.averageReviewCount = averageReviewCount;
    }

    public void increment(ReviewEnum reviewEnum) {
        increment(reviewEnum, 1);
    }


    public void increment(ReviewEnum reviewEnum, int incrementBy) {
        if (reviewEnum != null) {
            if (reviewEnum == ReviewEnum.GOOD) {
                setGoodReviewCount(getGoodReviewCount() + incrementBy);
            } else if (reviewEnum == ReviewEnum.AVERAGE) {
                setAverageReviewCount(getAverageReviewCount() + incrementBy);
            } else if (reviewEnum == ReviewEnum.BAD) {
                setBadReviewCount(getBadReviewCount() + incrementBy);
            }
        }
    }

    public long getBadReviewCount() {
        return badReviewCount;
    }

    public void setBadReviewCount(long badReviewCount) {
        this.badReviewCount = badReviewCount;
    }

    public long getGoodReviewCount() {
        return goodReviewCount;
    }

    public void setGoodReviewCount(long goodReviewCount) {
        this.goodReviewCount = goodReviewCount;
    }

    public long getAverageReviewCount() {
        return averageReviewCount;
    }

    public void setAverageReviewCount(long averageReviewCount) {
        this.averageReviewCount = averageReviewCount;
    }
}
