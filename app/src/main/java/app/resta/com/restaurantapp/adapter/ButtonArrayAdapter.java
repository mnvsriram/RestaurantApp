package app.resta.com.restaurantapp.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 29/07/2017.
 */

public class ButtonArrayAdapter extends ArrayAdapter<RestaurantItem> {

    RestaurantItem[] dataTemp;
    RestaurantItem[] data;
    Activity activity;
    private View.OnClickListener buttoOnClickListener;

    public ButtonArrayAdapter(Activity activity, int textViewResourceId, RestaurantItem[] itemArray, View.OnClickListener buttonOnClickListener) {
        super(activity, textViewResourceId, itemArray);
        this.activity = activity;
        this.dataTemp = itemArray;
        this.data = itemArray;
        this.buttoOnClickListener = buttonOnClickListener;
    }

    public void setData(RestaurantItem[] data) {
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (activity).getLayoutInflater();
            row = inflater.inflate(R.layout.item_button, parent, false);
            holder.button = (Button) row.findViewById(R.id.itemAddButton);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        RestaurantItem item = data[position];
        holder.button.setText(item.getName());
        holder.button.setTag(item);
        if (buttoOnClickListener != null) {
            holder.button.setOnClickListener(buttoOnClickListener);
        }
        return row;
    }

    public static class ViewHolder {
        Button button;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<RestaurantItem> tempList = new ArrayList<>();
                if (constraint != null && dataTemp != null) {
                    List<RestaurantItem> dataList = Arrays.asList(dataTemp);
                    for (RestaurantItem item : dataList) {
                        if (item.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            tempList.add(item);
                        }
                    }
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                List<RestaurantItem> filteredItems = (ArrayList<RestaurantItem>) results.values;
                Object[] itemObjectArr = filteredItems.toArray();
                data = Arrays.copyOf(itemObjectArr, itemObjectArr.length, RestaurantItem[].class);
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}