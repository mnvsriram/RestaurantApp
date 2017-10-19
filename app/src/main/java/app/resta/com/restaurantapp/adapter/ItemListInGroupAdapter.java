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
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 02/08/2017.
 */

public class ItemListInGroupAdapter extends ArrayAdapter<RestaurantItem> implements View.OnClickListener {

    private List<RestaurantItem> dataSet;
    AddItemToGroupActivity activity;

    // View lookup cache
    private static class ViewHolder {
        TextView txtName;
        ImageButton up;
        ImageButton down;
        ImageButton delete;
    }

    public ItemListInGroupAdapter(List<RestaurantItem> data, AddItemToGroupActivity activity) {
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

    View.OnClickListener buttonOnClickRemoveFromGroupListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            RestaurantItem itemToRemove = dataSet.get(index);
            dataSet.remove(itemToRemove);
            notifyDataSetChanged();

            activity.addToGrid(itemToRemove);
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
            viewHolder.delete = (ImageButton) convertView.findViewById(R.id.itemsInGroupListRemoveButton);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        //Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //result.startAnimation(animation);
        //lastPosition = position;

        viewHolder.txtName.setText(dataModel.getName());
        viewHolder.delete.setTag(position);
        viewHolder.up.setTag(position);
        viewHolder.down.setTag(position);

        viewHolder.delete.setOnClickListener(buttonOnClickRemoveFromGroupListener);
        viewHolder.up.setOnClickListener(upArrowOnClickListener);
        viewHolder.down.setOnClickListener(downArrowOnClickListener);
        // Return the completed view to render on screen
        return convertView;
    }
}