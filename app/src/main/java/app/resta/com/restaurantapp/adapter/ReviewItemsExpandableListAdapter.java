package app.resta.com.restaurantapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.GroupEditActivity;
import app.resta.com.restaurantapp.activity.ItemEditActivity;
import app.resta.com.restaurantapp.controller.ItemsOnPlate;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.dialog.MenuDeleteDialog;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class ReviewItemsExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater context;
    private Map<String, List<RestaurantItem>> dataCollection;
    private List<String> headerItems;
    private Activity activity;

    public ReviewItemsExpandableListAdapter(Activity activity, LayoutInflater context, Map<String, List<RestaurantItem>> dataCollection, List<String> headerItems) {
        this.activity = activity;
        this.context = context;
        this.dataCollection = dataCollection;
        this.headerItems = headerItems;
    }


    public Object getChild(int groupPosition, int childPosition) {
        return dataCollection.get(headerItems.get(groupPosition)).get(
                childPosition);
    }

    public RestaurantItem getChildMenuItem(int groupPosition, int childPosition) {
        return dataCollection.get(headerItems.get(groupPosition)).get(
                childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(final int groupPosition,
                             final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = context;
            convertView = infalInflater.inflate(R.layout.review_list_item, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title); // title
        TextView duration = (TextView) convertView.findViewById(R.id.duration); // duration
        RestaurantItem childItem = (RestaurantItem) getChild(groupPosition, childPosition);
        title.setText(childItem.getName());
        duration.setText(childItem.getPrice());



        //addButtonsToChildView(convertView, childItem, groupPosition, childPosition);
//        MenuDeleteDialog.show(R.id.deleteMenuButton, convertView, activity, childItem, groupPosition);
        return convertView;
    }


    public int getChildrenCount(int groupPosition) {
        return dataCollection.get(headerItems.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return headerItems.get(groupPosition);
    }

    public int getGroupCount() {
        return headerItems.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = context;
            convertView = infalInflater.inflate(R.layout.review_list_group, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.reviewItem);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(headerName);

        return convertView;
    }


    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}


