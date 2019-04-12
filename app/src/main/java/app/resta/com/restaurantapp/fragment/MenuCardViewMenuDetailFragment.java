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
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.service.MenuDetailService;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardViewMenuDetailFragment extends Fragment {
    private List<Tag> tagList;
    private RestaurantItem selectedItem;
    private View inflatedView;

    public void setTagList(List<Tag> tagList) {
        this.tagList = tagList;
    }

    public MenuCardViewMenuDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_menu_detail, container, false);
        return inflatedView;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null && selectedItem != null) {
            setFields(view, selectedItem);
        }
    }

    private void setName(View view, RestaurantItem item) {
        TextView title = view.findViewById(R.id.nameHeader);
        title.setText(item.getName());
    }

    private void setDescription(View view, RestaurantItem item) {
        TextView itemDescription = view.findViewById(R.id.itemDescripton);
        itemDescription.setText(item.getDescription());

    }

    private void setGGWImage(View view, RestaurantItem item) {
        ImageView ggwImage = view.findViewById(R.id.goesGreatWithImage);
        ggwImage.setTag(item.getId());
        ggwImage.setVisibility(View.VISIBLE);
    }

    private void setReviewScores(Map<ReviewEnum, Long> scores, View view) {
        TextView reviewGoodCount = view.findViewById(R.id.reviewGoodCount);
        TextView reviewAverageCount = view.findViewById(R.id.reviewAverageCount);
        TextView reviewBadCount = view.findViewById(R.id.reviewBadCount);
        MenuDetailService.setReviewScores(scores, reviewGoodCount, reviewAverageCount, reviewBadCount);
    }

    private void setReviews(View view, RestaurantItem item) {
        Map<ReviewEnum, Long> scores = item.getRatingCountMap();
        setReviewScores(scores, view);
        setReviewImages(scores, view);
    }

    private void setReviewImages(Map<ReviewEnum, Long> scores, View view) {
        ImageButton reviewGood = view.findViewById(R.id.reviewGood);
        ImageButton reviewAverage = view.findViewById(R.id.reviewAverage);
        ImageButton reviewBad = view.findViewById(R.id.reviewBad);
        MenuDetailService.setReviewImages(scores, reviewGood, reviewAverage, reviewBad);
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
        LinearLayout layout = view.findViewById(R.id.tagIcons);
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


    private void setImage(RestaurantItem item) {
        ViewPager mViewPager = inflatedView.findViewById(R.id.pagerForImages);
        MenuDetailService.setImage(item, mViewPager, false);
    }

    public void setSelectedItem(RestaurantItem selectedItem) {
        this.selectedItem = selectedItem;
    }
}
