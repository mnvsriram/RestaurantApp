package app.resta.com.restaurantapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.GroupEditActivity;
import app.resta.com.restaurantapp.activity.ItemEditActivity;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.dialog.MenuDeleteDialog;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater context;
    private static Map<String, List<RestaurantItem>> dataCollection;
    private static List<String> headerItems;
    private static Map<String, RestaurantItem> headerMap;
    private Activity activity;

    private static Map<String, List<RestaurantItem>> originalDataCollection;
    private static List<String> originalHeaderItems;

    public static List<String> getHeaderItems() {
        return headerItems;
    }

    public static Map<String, RestaurantItem> getHeaderMap() {
        return headerMap;
    }


    public MenuExpandableListAdapter(Activity activity, LayoutInflater context,
                                     Map<String, RestaurantItem> headerMap,
                                     Map<String, List<RestaurantItem>> dataCollection) {
        this.activity = activity;
        this.context = context;
        this.dataCollection = dataCollection;
        this.headerMap = headerMap;
        headerItems = new ArrayList<>();
        if (headerMap != null) {
            for (String headerName : headerMap.keySet()) {
                headerItems.add(headerName);
            }
        }

        originalDataCollection = dataCollection;
        originalHeaderItems = headerItems;
    }


    public Object getChild(int groupPosition, int childPosition) {
        return dataCollection.get(headerItems.get(groupPosition)).get(
                childPosition);
    }

    public static RestaurantItem getChildMenuItem(int groupPosition, int childPosition) {
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
            convertView = infalInflater.inflate(R.layout.menu_list_item, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.title); // title
        TextView artist = (TextView) convertView.findViewById(R.id.artist); // artist name
        TextView duration = (TextView) convertView.findViewById(R.id.duration); // duration
        RestaurantItem childItem = (RestaurantItem) getChild(groupPosition, childPosition);
        title.setText(childItem.getName());
        artist.setText("Text");
        duration.setText(childItem.getPrice());
//        MenuEditDialog.show(R.id.editMenuButton, convertView, activity, childItem, groupPosition, childPosition);

        addItemEditButton(convertView, childItem, groupPosition, childPosition);

        MenuDeleteDialog.show(R.id.deleteMenuButton, convertView, activity, childItem, groupPosition);

        return convertView;
    }

    private void addItemEditButton(View view, RestaurantItem childItem, int groupPosition, int childPosition) {

        ImageButton itemEditButton = (ImageButton) view.findViewById(R.id.editMenuButton);
        itemEditButton.setOnClickListener(itemEditListener);
        itemEditButton.setTag(R.string.tag_item_obj, childItem);
        itemEditButton.setTag(R.string.tag_item_group_position, groupPosition);
        itemEditButton.setTag(R.string.tag_item_child_position, childPosition);

        if (LoginController.getInstance().isAdminLoggedIn()) {
            itemEditButton.setVisibility(View.VISIBLE);
            itemEditButton.setFocusable(false);
            itemEditButton.setFocusableInTouchMode(false);
        } else {
            itemEditButton.setVisibility(View.GONE);
        }

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
            convertView = infalInflater.inflate(R.layout.menu_list_group, null);
        }

        TextView item = (TextView) convertView.findViewById(R.id.item);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(headerName);

        RestaurantItem groupObj = MenuExpandableListAdapter.getHeaderMap().get(headerName);

        addGroupEditButton(convertView, groupObj, groupPosition);
        addMenuItemAddButton(convertView, groupObj, groupPosition);

        return convertView;
    }

    private void addGroupEditButton(View view, RestaurantItem groupObj, int groupPosition) {

        ImageButton groupEditButton = (ImageButton) view.findViewById(R.id.editGroupButton);
        groupEditButton.setOnClickListener(groupEditListener);
        groupEditButton.setTag(R.string.tag_group_obj, groupObj);
        groupEditButton.setTag(R.string.tag_group_position, groupPosition);

        if (LoginController.getInstance().isAdminLoggedIn()) {
            groupEditButton.setVisibility(View.VISIBLE);
            groupEditButton.setFocusable(false);
            groupEditButton.setFocusableInTouchMode(false);
        } else {
            groupEditButton.setVisibility(View.GONE);
        }

    }

    private void addMenuItemAddButton(View view, RestaurantItem groupObj, int groupPosition) {

        RestaurantItem newRestaurantItem = new RestaurantItem();
        newRestaurantItem.setParentItem(groupObj);

        ImageButton addMenuItemButton = (ImageButton) view.findViewById(R.id.addMenuItemButton);
        addMenuItemButton.setOnClickListener(itemEditListener);

        addMenuItemButton.setTag(R.string.tag_item_obj, newRestaurantItem);
        addMenuItemButton.setTag(R.string.tag_item_group_position, groupPosition);
        addMenuItemButton.setTag(R.string.tag_item_child_position, -1);

        if (LoginController.getInstance().isAdminLoggedIn()) {
            addMenuItemButton.setVisibility(View.VISIBLE);
            addMenuItemButton.setFocusable(false);
            addMenuItemButton.setFocusableInTouchMode(false);
        } else {
            addMenuItemButton.setVisibility(View.GONE);
        }

    }

    View.OnClickListener groupEditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showGroupEditPage(v);
        }
    };
    View.OnClickListener itemEditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showItemEditPage(v);
        }
    };

    public void showItemEditPage(View view) {
        Intent intent = null;
        intent = new Intent(activity, ItemEditActivity.class);

        RestaurantItem item = (RestaurantItem) view.getTag(R.string.tag_item_obj);
        int groupPosition = (Integer) view.getTag(R.string.tag_item_group_position);
        int childPosition = (Integer) view.getTag(R.string.tag_item_child_position);

        intent.putExtra("item_obj", item);
        intent.putExtra("item_group_position", groupPosition);
        intent.putExtra("item_child_position", childPosition);
        activity.startActivity(intent);
    }


    public void showGroupEditPage(View view) {
        Intent intent = null;
        intent = new Intent(activity, GroupEditActivity.class);

        RestaurantItem item = (RestaurantItem) view.getTag(R.string.tag_group_obj);
        int groupPosition = (Integer) view.getTag(R.string.tag_group_position);

        intent.putExtra("group_obj", item);
        intent.putExtra("group_position", groupPosition);

        activity.startActivity(intent);
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void filterData(String query) {
        query = query.trim().toLowerCase();

        //headerItems = new ArrayList<>();
        dataCollection = new HashMap<>();
        //continentList.clear();

        //List<String> newHeaders = new ArrayList<>();
        Map<String, List<RestaurantItem>> newDataCollection = new HashMap<>();

        if (query.isEmpty()) {
            //     headerItems.addAll(originalHeaderItems);
            dataCollection.putAll(originalDataCollection);
            //continentList.addAll(originalList);
        } else {


            for (String groupName : originalDataCollection.keySet()) {
                List<RestaurantItem> items = originalDataCollection.get(groupName);
                List<RestaurantItem> newList = new ArrayList<>();
                for (RestaurantItem item : items) {
                    if (item.getName().toLowerCase().contains(query) ||
                            groupName.toLowerCase().contains(query)) {
                        newList.add(item);
                    }
                }
                newDataCollection.put(groupName, newList);
            }

        }


        dataCollection.putAll(newDataCollection);
        notifyDataSetChanged();
    }


}


