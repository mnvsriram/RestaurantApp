package app.resta.com.restaurantapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class ReviewAdapter extends ArrayAdapter<RestaurantItem> implements View.OnClickListener {

    private List<RestaurantItem> dataSet;
    Context mContext;


    public ReviewAdapter(List<RestaurantItem> data, Context context) {
        super(context, R.layout.review_submit_list_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object object = getItem(position);

        RestaurantItem dataModel = (RestaurantItem) object;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        RestaurantItem dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view


        LayoutInflater inflater = LayoutInflater.from(getContext());
        convertView = inflater.inflate(R.layout.review_submit_list_item, parent, false);

        TextView txtName = (TextView) convertView.findViewById(R.id.reviewItemTitle);
        lastPosition = position;

        txtName.setText(dataModel.getName());
        return convertView;
    }
}