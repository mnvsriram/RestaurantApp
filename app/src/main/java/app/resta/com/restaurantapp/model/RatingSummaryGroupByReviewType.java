package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.Date;

public class RatingSummaryGroupByReviewType implements Serializable {
    private Date date;
    private int noOfDaysOld;
    private long itemId;
    private String itemName;
    private ReviewEnum reviewEnum;
    private int count;
    private double score;
    String comments;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNoOfDaysOld() {
        return noOfDaysOld;
    }

    public void setNoOfDaysOld(int noOfDaysOld) {
        this.noOfDaysOld = noOfDaysOld;
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

    public ReviewEnum getReviewEnum() {
        return reviewEnum;
    }

    public void setReviewEnum(ReviewEnum reviewEnum) {
        this.reviewEnum = reviewEnum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


}
