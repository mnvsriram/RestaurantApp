package app.resta.com.restaurantapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.AddItemToGroupActivity;
import app.resta.com.restaurantapp.activity.MenuTypeAddActivity;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 02/08/2017.
 */

public class MenuTypeGroupListAdapter extends ArrayAdapter<RestaurantItem> implements View.OnClickListener {

    private List<RestaurantItem> dataSet;
    MenuTypeAddActivity activity;
    public boolean dataChanged = false;


    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageButton up;
        ImageButton down;
    }

    public MenuTypeGroupListAdapter(List<RestaurantItem> data, MenuTypeAddActivity activity) {
        super(activity, R.layout.item_in_group_list_item);
        this.dataSet = data;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    public void setData(List<RestaurantItem> data) {
        this.dataSet = data;
    }

    public List<RestaurantItem> getData() {
        return dataSet;
    }


    @Override
    public RestaurantItem getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        RestaurantItem dataModel = (RestaurantItem) object;

    }

    //private int lastPosition = -1;


    View.OnClickListener upArrowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataChanged = true;
            int index = (Integer) v.getTag();
            RestaurantItem itemToMoveUp = dataSet.get(index);
            if (index != 0) {
                RestaurantItem itemToMoveDown = dataSet.get(index - 1);
                dataSet.remove(index);
                dataSet.remove(index - 1);
                dataSet.add(index - 1, itemToMoveUp);
                dataSet.add(index, itemToMoveDown);
                notifyDataSetChanged();
            }
        }
    };

    View.OnClickListener downArrowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataChanged = true;
            int index = (Integer) v.getTag();
            RestaurantItem itemToMoveDown = dataSet.get(index);
            if (index != dataSet.size() - 1) {
                RestaurantItem itemToMoveUp = dataSet.get(index + 1);

                dataSet.remove(index + 1);
                dataSet.remove(index);

                dataSet.add(index, itemToMoveUp);
                dataSet.add(index + 1, itemToMoveDown);

                notifyDataSetChanged();
            }
        }
    };


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RestaurantItem dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_in_group_list_item, parent, false);
            viewHolder.txtName = (TextView) convertView.findViewById(R.id.itemsInGroupListTitle);
            viewHolder.up = (ImageButton) convertView.findViewById(R.id.itemsInGroupListUpButton);
            viewHolder.down = (ImageButton) convertView.findViewById(R.id.itemsInGroupListDownButton);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        ImageButton removeButton = (ImageButton) convertView.findViewById(R.id.itemsInGroupListRemoveButton);
        removeButton.setVisibility(View.INVISIBLE);

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.up.setTag(position);
        viewHolder.down.setTag(position);

        viewHolder.up.setOnClickListener(upArrowOnClickListener);
        viewHolder.down.setOnClickListener(downArrowOnClickListener);
        // Return the completed view to render on screen
        return convertView;
    }
}