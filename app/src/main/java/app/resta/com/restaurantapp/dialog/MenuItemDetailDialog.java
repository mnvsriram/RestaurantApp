package app.resta.com.restaurantapp.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.service.MenuDetailService;

/**
 * Created by Sriram on 23/01/2018.
 */

public class MenuItemDetailDialog extends Dialog implements
        android.view.View.OnClickListener {
    public Activity c;
    public Dialog d;
    public Button yes, no;
    private RestaurantItem dataObject;

    private TextView reviewGoodCount;
    private TextView reviewAverageCount;
    private TextView reviewBadCount;

    private ImageButton reviewGood;
    private ImageButton reviewAverage;
    private ImageButton reviewBad;


    public MenuItemDetailDialog(Activity activity, RestaurantItem item) {
        super(activity);
        this.c = activity;
        this.dataObject = item;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_menu_detail);
        setName((TextView) findViewById(R.id.nameHeader), dataObject);
        setDescription((TextView) findViewById(R.id.itemDescripton), dataObject);

        reviewGoodCount = findViewById(R.id.reviewGoodCount);
        reviewAverageCount = findViewById(R.id.reviewAverageCount);
        reviewBadCount = findViewById(R.id.reviewBadCount);

        reviewGood = findViewById(R.id.reviewGood);
        reviewAverage = findViewById(R.id.reviewAverage);
        reviewBad = findViewById(R.id.reviewBad);

        setReviews(dataObject);
        setImage(dataObject);
    }

    private void setImage(RestaurantItem item) {
        ViewPager mViewPager = findViewById(R.id.pagerForImages);
        MenuDetailService.setImageFromCache(item, mViewPager);
    }

    private void setName(TextView view, RestaurantItem item) {
        view.setText(item.getName());
    }

    private void setDescription(TextView view, RestaurantItem item) {
        view.setText(item.getDescription());
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case 1:
                c.finish();
                break;
            case 2:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void setReviewScores(Map<ReviewEnum, Long> scores) {
        TextView reviewGoodCount = findViewById(R.id.reviewGoodCount);
        TextView reviewAverageCount = findViewById(R.id.reviewAverageCount);
        TextView reviewBadCount = findViewById(R.id.reviewBadCount);
        MenuDetailService.setReviewScores(scores, reviewGoodCount, reviewAverageCount, reviewBadCount);
    }

    private void setReviews(RestaurantItem item) {
        Map<ReviewEnum, Long> scores = item.getRatingCountMap();
        setReviewScores(scores);
        setReviewImages(scores);
    }

    private void setReviewImages(Map<ReviewEnum, Long> scores) {
        ImageButton reviewGood = findViewById(R.id.reviewGood);
        ImageButton reviewAverage = findViewById(R.id.reviewAverage);
        ImageButton reviewBad = findViewById(R.id.reviewBad);
        MenuDetailService.setReviewImages(scores, reviewGood, reviewAverage, reviewBad);
    }

}
