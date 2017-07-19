package app.resta.com.restaurantapp.model;

import java.io.Serializable;

public class ReviewCount implements Serializable {
    private int badReviewCount;
    private int goodReviewCount;
    private int averageReviewCount;

    public ReviewCount(int goodReviewCount, int averageReviewCount, int badReviewCount) {
        this.badReviewCount = badReviewCount;
        this.goodReviewCount = goodReviewCount;
        this.averageReviewCount = averageReviewCount;
    }

    public int getBadReviewCount() {
        return badReviewCount;
    }

    public void setBadReviewCount(int badReviewCount) {
        this.badReviewCount = badReviewCount;
    }

    public int getGoodReviewCount() {
        return goodReviewCount;
    }

    public void setGoodReviewCount(int goodReviewCount) {
        this.goodReviewCount = goodReviewCount;
    }

    public int getAverageReviewCount() {
        return averageReviewCount;
    }

    public void setAverageReviewCount(int averageReviewCount) {
        this.averageReviewCount = averageReviewCount;
    }
}
