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
import app.resta.com.restaurantapp.db.dao.ReviewDao;
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
    private ReviewDao reviewDao;

    TextView reviewGoodCount;
    TextView reviewAverageCount;
    TextView reviewBadCount;

    ImageButton reviewGood;
    ImageButton reviewAverage;
    ImageButton reviewBad;


    public MenuItemDetailDialog(Activity activity, RestaurantItem item, ReviewDao reviewDao) {
        super(activity);
        // TODO Auto-generated constructor stub
        this.c = activity;
        this.dataObject = item;
        this.reviewDao = reviewDao;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_menu_detail);
        setName((TextView) findViewById(R.id.nameHeader), dataObject);
        setDescription((TextView) findViewById(R.id.itemDescripton), dataObject);

        reviewGoodCount = (TextView) findViewById(R.id.reviewGoodCount);
        reviewAverageCount = (TextView) findViewById(R.id.reviewAverageCount);
        reviewBadCount = (TextView) findViewById(R.id.reviewBadCount);

        reviewGood = (ImageButton) findViewById(R.id.reviewGood);
        reviewAverage = (ImageButton) findViewById(R.id.reviewAverage);
        reviewBad = (ImageButton) findViewById(R.id.reviewBad);

        setReviews(dataObject);
        setImage(dataObject);
    }

    private void setImage(RestaurantItem item) {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pagerForImages);
        MenuDetailService.setImage(item, mViewPager, c);
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

    private void setReviewScores(Map<ReviewEnum, Integer> scores) {
        TextView reviewGoodCount = (TextView) findViewById(R.id.reviewGoodCount);
        TextView reviewAverageCount = (TextView) findViewById(R.id.reviewAverageCount);
        TextView reviewBadCount = (TextView) findViewById(R.id.reviewBadCount);
        MenuDetailService.setReviewScores(scores, reviewGoodCount, reviewAverageCount, reviewBadCount);
    }

    private void setReviews(RestaurantItem item) {
        Map<ReviewEnum, Integer> scores = reviewDao.getScores(item.getId());
        setReviewScores(scores);
        setReviewImages(scores);
    }

    private void setReviewImages(Map<ReviewEnum, Integer> scores) {
        ImageButton reviewGood = (ImageButton) findViewById(R.id.reviewGood);
        ImageButton reviewAverage = (ImageButton) findViewById(R.id.reviewAverage);
        ImageButton reviewBad = (ImageButton) findViewById(R.id.reviewBad);
        MenuDetailService.setReviewImages(scores, reviewGood, reviewAverage, reviewBad);
    }

}
