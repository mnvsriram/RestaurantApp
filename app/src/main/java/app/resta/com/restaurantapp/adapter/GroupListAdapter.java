package app.resta.com.restaurantapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 15/11/2017.
 */
public class GroupListAdapter extends ArrayAdapter<RestaurantItem> {

    private List<RestaurantItem> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView groupName;
    }

    public GroupListAdapter(List<RestaurantItem> data, Context context) {
        super(context, R.layout.menu_list_item, data);
        this.dataSet = data;
        this.mContext = context;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RestaurantItem dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.group_list_item, parent, false);
            viewHolder.groupName = (TextView) convertView.findViewById(R.id.groupListGroupName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.groupName.setText(dataModel.getName());

//
//        RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.menuListItem);
//        layout.setBackgroundColor(Color.BLUE);

        return convertView;
    }
}
