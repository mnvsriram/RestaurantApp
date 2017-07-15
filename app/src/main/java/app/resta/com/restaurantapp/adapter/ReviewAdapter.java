package app.resta.com.restaurantapp.adapter;


import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.ReviewEnum;
import app.resta.com.restaurantapp.model.ReviewForDish;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantUtil;

public class ReviewAdapter extends ArrayAdapter<ReviewForDish> implements View.OnClickListener {
    private List<ReviewForDish> dataSet;
    Context mContext;
    private Activity activity;
    int editTextColor = MyApplication.getAppContext().getResources().getColor(R.color.black);
    int hintTextColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);

    public ReviewAdapter(Activity activity, List<ReviewForDish> data, Context context) {
        super(context, R.layout.review_submit_list_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);

        ReviewForDish dataModel = (ReviewForDish) object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ReviewForDish dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.review_submit_list_item, parent, false);

        TextView txtName = (TextView) convertView.findViewById(R.id.reviewItemTitle);
        lastPosition = position;

        txtName.setText(dataModel.getItem().getName());


        ImageButton badButton = (ImageButton) convertView.findViewById(R.id.reviewSubmitBad);
        badButton.setTag(dataModel);
        badButton.setTag(R.string.tag_good_icon, convertView.findViewById(R.id.reviewSubmitGood));
        badButton.setTag(R.string.tag_average_icon, convertView.findViewById(R.id.reviewSubmitAverage));
        badButton.setOnClickListener(badReviewListener);

        ImageButton goodButton = (ImageButton) convertView.findViewById(R.id.reviewSubmitGood);
        goodButton.setTag(dataModel);
        goodButton.setTag(R.string.tag_bad_icon, convertView.findViewById(R.id.reviewSubmitBad));
        goodButton.setTag(R.string.tag_average_icon, convertView.findViewById(R.id.reviewSubmitAverage));
        goodButton.setOnClickListener(goodReviewListener);

        ImageButton averageButton = (ImageButton) convertView.findViewById(R.id.reviewSubmitAverage);
        averageButton.setTag(dataModel);
        averageButton.setTag(R.string.tag_bad_icon, convertView.findViewById(R.id.reviewSubmitBad));
        averageButton.setTag(R.string.tag_good_icon, convertView.findViewById(R.id.reviewSubmitGood));
        averageButton.setOnClickListener(averageReviewListener);

        final EditText editText = (EditText) convertView.findViewById(R.id.reviewComment);
        editText.getBackground().setColorFilter(editTextColor, PorterDuff.Mode.SRC_IN);
        editText.setHintTextColor(hintTextColor);
        editText.setTag(dataModel);

        editText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeComment(editText);
            }
        });


        return convertView;
    }


    View.OnClickListener badReviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reviewBad(v);
        }
    };
    View.OnClickListener goodReviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reviewGood(v);
        }
    };
    View.OnClickListener averageReviewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            reviewAverage(v);
        }
    };


    public void reviewBad(View view) {
        ReviewForDish reviewForDish = (ReviewForDish) view.getTag();

        View badIcon = view;
        View averageIcon = (View) view.getTag(R.string.tag_average_icon);
        View goodIcon = (View) view.getTag(R.string.tag_good_icon);
        RestaurantUtil.setImage(averageIcon, R.drawable.reviewaveragebw, 40, 40);
        RestaurantUtil.setImage(goodIcon, R.drawable.reviewgoodbw, 40, 40);

        ReviewEnum review = reviewForDish.getReview();
        if (review != null && review.equals(ReviewEnum.BAD)) {
            RestaurantUtil.setImage(badIcon, R.drawable.reviewbadbw, 40, 40);
            reviewForDish.setReview(null);
        } else {
            RestaurantUtil.setImage(badIcon, R.drawable.reviewbadcolor, 50, 50);
            reviewForDish.setReview(ReviewEnum.BAD);
        }
    }


    public void reviewGood(View view) {
        ReviewForDish reviewForDish = (ReviewForDish) view.getTag();

        View goodIcon = view;
        View averageIcon = (View) view.getTag(R.string.tag_average_icon);
        View badIcon = (View) view.getTag(R.string.tag_bad_icon);

        ReviewEnum review = reviewForDish.getReview();
        RestaurantUtil.setImage(averageIcon, R.drawable.reviewaveragebw, 40, 40);
        RestaurantUtil.setImage(badIcon, R.drawable.reviewbadbw, 40, 40);

        if (review != null && review.equals(ReviewEnum.GOOD)) {
            RestaurantUtil.setImage(goodIcon, R.drawable.reviewgoodbw, 40, 40);
            reviewForDish.setReview(null);
        } else {
            RestaurantUtil.setImage(goodIcon, R.drawable.reviewgoodcolor, 50, 50);
            reviewForDish.setReview(ReviewEnum.GOOD);
        }
    }


    public void changeComment(View view) {
        EditText commentsView = (EditText) view;
        if (commentsView.getText() != null) {
            ReviewForDish reviewForDish = (ReviewForDish) view.getTag();
            if (reviewForDish != null && commentsView.getText() != null) {
                reviewForDish.setReviewText(commentsView.getText().toString());
            }
        }
    }

    public void reviewAverage(View view) {
        ReviewForDish reviewForDish = (ReviewForDish) view.getTag();

        View averageIcon = view;
        View goodIcon = (View) view.getTag(R.string.tag_good_icon);
        View badIcon = (View) view.getTag(R.string.tag_bad_icon);

        ReviewEnum review = reviewForDish.getReview();
        RestaurantUtil.setImage(goodIcon, R.drawable.reviewgoodbw, 40, 40);
        RestaurantUtil.setImage(badIcon, R.drawable.reviewbadbw, 40, 40);

        if (review != null && review.equals(ReviewEnum.AVERAGE)) {
            RestaurantUtil.setImage(averageIcon, R.drawable.reviewaveragebw, 40, 40);
            reviewForDish.setReview(null);
        } else {
            RestaurantUtil.setImage(averageIcon, R.drawable.reviewaveragecolor, 50, 50);
            reviewForDish.setReview(ReviewEnum.AVERAGE);
        }
    }
}