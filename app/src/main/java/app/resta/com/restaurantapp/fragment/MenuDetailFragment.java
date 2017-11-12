package app.resta.com.restaurantapp.fragment;


import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.CustomPageAdapter;
import app.resta.com.restaurantapp.db.dao.ReviewDao;
import app.resta.com.restaurantapp.model.Ingredient;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class MenuDetailFragment extends Fragment {
    private ReviewDao reviewDao;
    private List<Tag> tagList;
    private RestaurantItem selectedItem;
    private View inflatedView;

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public MenuDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_menu_detail, container, false);
        return inflatedView;
    }

    private ReviewEnum getHighestScoreReview(Map<ReviewEnum, Integer> scores) {
        ReviewEnum highest = null;
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

    @Override
    public void onStart() {
        super.onStart();
        reviewDao = new ReviewDao();
        View view = getView();
        if (view != null && selectedItem != null) {
            setFields(view, selectedItem);
        }
    }

    private void setName(View view, RestaurantItem item) {
        TextView title = (TextView) view.findViewById(R.id.nameHeader);
        title.setText(item.getName());
    }

    private void setDescription(View view, RestaurantItem item) {
        TextView itemDescription = (TextView) view.findViewById(R.id.itemDescripton);
        itemDescription.setText(item.getDescription());

    }

    private void setGGWImage(View view, RestaurantItem item) {
        ImageView ggwImage = (ImageView) view.findViewById(R.id.goesGreatWithImage);
        ggwImage.setTag(item.getId());
    }

    private void setReviewScores(Map<ReviewEnum, Integer> scores, View view) {
        TextView reviewGoodCount = (TextView) view.findViewById(R.id.reviewGoodCount);
        reviewGoodCount.setText(scores.get(ReviewEnum.GOOD) + "");


        TextView reviewAverageCount = (TextView) view.findViewById(R.id.reviewAverageCount);
        reviewAverageCount.setText(scores.get(ReviewEnum.AVERAGE) + "");


        TextView reviewBadCount = (TextView) view.findViewById(R.id.reviewBadCount);
        reviewBadCount.setText(scores.get(ReviewEnum.BAD) + "");

    }

    private void setReviews(View view, RestaurantItem item) {
        Map<ReviewEnum, Integer> scores = reviewDao.getScores(item.getId());
        setReviewScores(scores, view);
        setReviewImages(scores, view);
    }

    private void setReviewImages(Map<ReviewEnum, Integer> scores, View view) {
        ReviewEnum highestReview = getHighestScoreReview(scores);
        if (highestReview.equals(ReviewEnum.GOOD)) {
            ImageButton reviewGood = (ImageButton) view.findViewById(R.id.reviewGood);
            RestaurantUtil.setImage(reviewGood, R.drawable.reviewgoodcolor, 50, 50);
        } else if (highestReview.equals(ReviewEnum.AVERAGE)) {
            ImageButton reviewAverage = (ImageButton) view.findViewById(R.id.reviewAverage);
            RestaurantUtil.setImage(reviewAverage, R.drawable.reviewaveragecolor, 50, 50);
        } else if (highestReview.equals(ReviewEnum.BAD)) {
            ImageButton reviewBad = (ImageButton) view.findViewById(R.id.reviewBad);
            RestaurantUtil.setImage(reviewBad, R.drawable.reviewbadcolor, 50, 50);
        }

    }

    private void setFields(View view, RestaurantItem item) {
        setName(view, item);
        setDescription(view, item);
        setGGWImage(view, item);
        setReviews(view, item);
        setImage(item);
        setTags(view);
        //setIngredients(view);
    }

    private void setTags(View view) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.tagIcons);
        if (tagList != null) {
            for (Tag tag : tagList) {
                addTagButton(tag, layout);
            }
        }
    }


    private void addTagButton(Tag tag, LinearLayout tagsLayout) {
        ImageButton tagButton = new ImageButton(MyApplication.getAppContext());
        String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
        String filePath = path + tag.getImage() + ".jpeg";
        File file = new File(filePath);
        if (file.exists()) {
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            Drawable d = new BitmapDrawable(getResources(), bmp);
            tagButton.setBackground(d);
        } else {
            tagButton.setBackgroundResource(R.drawable.noimage);
        }
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(40, 40);

        tagsLayout.addView(tagButton, lp);
    }


    private void addIngredientsButton(Ingredient ingredient, LinearLayout tagsLayout) {
        ImageButton ingredientButton = new ImageButton(MyApplication.getAppContext());
        ingredientButton.setBackgroundResource(R.drawable.deletered);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(20, 20);
        tagsLayout.addView(ingredientButton, lp);
    }


    private void setImage(RestaurantItem item) {
        RestaurantImage[] images = item.getImages();
        List<String> is = new ArrayList<>();

        if (images != null) {
            for (RestaurantImage restaurantImage : images) {
                if (restaurantImage != null && restaurantImage.getName() != null && !restaurantImage.getName().equalsIgnoreCase("noImage")) {
                    is.add(restaurantImage.getName());
                }
            }
        }
        if (is.size() == 0) {
            is.add("noImage");
        }

        List<Bitmap> bitMapImages = new ArrayList<>();
        for (String imageToDisplay : is) {
            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
            if (imageToDisplay.equalsIgnoreCase("noImage")) {
                Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.noimage);
                bitMapImages.add(bm);
            } else {
                String filePath = path + imageToDisplay + ".jpeg";
                File file = new File(filePath);
                if (file.exists()) {
                    Bitmap bmp = BitmapFactory.decodeFile(filePath);
                    bitMapImages.add(bmp);
                }
            }

        }
        CustomPageAdapter mCustomPagerAdapter = new CustomPageAdapter(MyApplication.getAppContext(), bitMapImages);
        ViewPager mViewPager = (ViewPager) inflatedView.findViewById(R.id.pagerForImages);
        mViewPager.setAdapter(mCustomPagerAdapter);

    }

    public RestaurantItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(RestaurantItem selectedItem) {
        this.selectedItem = selectedItem;
    }
}
