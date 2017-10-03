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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

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

    private static class ViewHolder {
        TextView txtName;
        ImageButton bad;
        ImageButton average;
        ImageButton good;
        EditText comment;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);

        ReviewForDish dataModel = (ReviewForDish) object;

    }

    private int lastPosition = -1;

    Map<Integer, View> holder = new HashMap<>();

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (holder.get(position) == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.review_submit_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.reviewItemTitle);
            viewHolder.good = (ImageButton) convertView.findViewById(R.id.reviewSubmitGood);
            viewHolder.average = (ImageButton) convertView.findViewById(R.id.reviewSubmitAverage);
            viewHolder.bad = (ImageButton) convertView.findViewById(R.id.reviewSubmitBad);
            viewHolder.comment = (EditText) convertView.findViewById((R.id.reviewComment));
            convertView.setTag(viewHolder);
            holder.put(position, convertView);
        } else {
            convertView = holder.get(position);
            viewHolder = (ViewHolder) convertView.getTag();
        }


        final ReviewForDish dataModel = getItem(position);

        lastPosition = position;


        viewHolder.txtName.setText(dataModel.getItem().getName());

        viewHolder.bad.setTag(dataModel);
        viewHolder.bad.setTag(R.string.tag_good_icon, convertView.findViewById(R.id.reviewSubmitGood));
        viewHolder.bad.setTag(R.string.tag_average_icon, convertView.findViewById(R.id.reviewSubmitAverage));
        viewHolder.bad.setOnClickListener(badReviewListener);

        viewHolder.good.setTag(dataModel);
        viewHolder.good.setTag(R.string.tag_bad_icon, convertView.findViewById(R.id.reviewSubmitBad));
        viewHolder.good.setTag(R.string.tag_average_icon, convertView.findViewById(R.id.reviewSubmitAverage));
        viewHolder.good.setOnClickListener(goodReviewListener);

        viewHolder.average.setTag(dataModel);
        viewHolder.average.setTag(R.string.tag_bad_icon, convertView.findViewById(R.id.reviewSubmitBad));
        viewHolder.average.setTag(R.string.tag_good_icon, convertView.findViewById(R.id.reviewSubmitGood));
        viewHolder.average.setOnClickListener(averageReviewListener);


        viewHolder.comment.getBackground().setColorFilter(editTextColor, PorterDuff.Mode.SRC_IN);
        viewHolder.comment.setHintTextColor(hintTextColor);
        viewHolder.comment.setTag(dataModel);
        viewHolder.comment.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                changeComment(viewHolder.comment);
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