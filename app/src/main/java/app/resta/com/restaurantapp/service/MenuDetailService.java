package app.resta.com.restaurantapp.service;

import android.support.v4.view.ViewPager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.CustomPageAdapter;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.util.FireBaseStorageLocation;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantUtil;

/**
 * Created by Sriram on 10/02/2018.
 */

public class MenuDetailService {

    public static void setImage(RestaurantItem item, ViewPager mViewPager, boolean skipCache) {
        List<RestaurantImage> images = item.getFireStoreImages();
        List<String> imageUrls = new ArrayList<>();
        if (images != null) {
            for (RestaurantImage restaurantImage : images) {
                if (restaurantImage != null && restaurantImage.getStorageUrl() != null && !restaurantImage.getStorageUrl().equalsIgnoreCase(FireBaseStorageLocation.getNoImageLocation())) {
                    imageUrls.add(restaurantImage.getStorageUrl());
                }
            }
        }
        if (imageUrls.size() == 0) {
            imageUrls.add(FireBaseStorageLocation.getNoImageLocation());
        }
        CustomPageAdapter mCustomPagerAdapter = new CustomPageAdapter(MyApplication.getAppContext(), imageUrls, skipCache);
        mViewPager.setAdapter(mCustomPagerAdapter);
    }


    public static void setReviewImages(Map<ReviewEnum, Long> scores, ImageButton reviewGood, ImageButton reviewAverage, ImageButton reviewBad) {
        ReviewEnum highestReview = getHighestScoreReview(scores);
        if (highestReview.equals(ReviewEnum.GOOD)) {
            RestaurantUtil.setImage(reviewGood, R.drawable.reviewgoodcolor, 50, 50);
        } else if (highestReview.equals(ReviewEnum.AVERAGE)) {
            RestaurantUtil.setImage(reviewAverage, R.drawable.reviewaveragecolor, 50, 50);
        } else if (highestReview.equals(ReviewEnum.BAD)) {
            RestaurantUtil.setImage(reviewBad, R.drawable.reviewbadcolor, 50, 50);
        }

    }

    private static ReviewEnum getHighestScoreReview(Map<ReviewEnum, Long> scores) {
        ReviewEnum highest;
        if (scores.get(ReviewEnum.BAD) > scores.get(ReviewEnum.GOOD)) {
            if (scores.get(ReviewEnum.BAD) > scores.get(ReviewEnum.AVERAGE)) {
                highest = ReviewEnum.BAD;
            } else {
                highest = ReviewEnum.AVERAGE;
            }
        } else {
            if (scores.get(ReviewEnum.GOOD) > scores.get(ReviewEnum.AVERAGE)) {
                highest = ReviewEnum.GOOD;
            } else {
                highest = ReviewEnum.AVERAGE;
            }
        }
        return highest;
    }

    public static void setReviewScores(Map<ReviewEnum, Long> scores, TextView reviewGoodCount, TextView reviewAverageCount, TextView reviewBadCount) {
        reviewGoodCount.setText(scores.get(ReviewEnum.GOOD) + "");
        reviewAverageCount.setText(scores.get(ReviewEnum.AVERAGE) + "");
        reviewBadCount.setText(scores.get(ReviewEnum.BAD) + "");
    }

}
