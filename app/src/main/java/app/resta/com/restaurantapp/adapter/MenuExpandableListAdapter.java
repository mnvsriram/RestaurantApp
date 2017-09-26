package app.resta.com.restaurantapp.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.GroupEditActivity;
import app.resta.com.restaurantapp.activity.ItemEditActivity;
import app.resta.com.restaurantapp.activity.OrderActivity;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.ItemsOnPlate;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.dialog.MenuGroupDeleteDialog;
import app.resta.com.restaurantapp.dialog.MenuItemDeleteDialog;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuExpandableListAdapter extends BaseExpandableListAdapter {
    public static int setMenuCounter = 1;
    private LayoutInflater context;
    private static Map<String, List<RestaurantItem>> dataCollection;
    private static List<String> headerItems;
    private static Map<String, RestaurantItem> headerMap;
    private Activity activity;
    private ItemsOnPlate itemsOnPlate = new ItemsOnPlate();
    private GridLayout plateGrid;
    private static Map<String, List<RestaurantItem>> originalDataCollection;
    private static List<String> originalHeaderItems;
    //private MenuItemDao menuItemDao;
    private MenuTypeDao menuTypeDao;
    private AuthenticationController authenticationController;
    List<RestaurantItem> setMenuItems;

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
        initialize(activity);
    }

    private void initialize(Activity activity) {
        menuTypeDao = new MenuTypeDao();
        authenticationController = new AuthenticationController(activity);
        setMenuItems = new ArrayList<RestaurantItem>();
    }

    public Object getChild(int groupPosition, int childPosition) {
        return dataCollection.get(headerItems.get(groupPosition)).get(
                childPosition);
    }

    public static RestaurantItem getChildMenuItem(int groupPosition, int childPosition) {
        if (dataCollection != null && dataCollection.size() > 0 && headerItems != null && headerItems.size() > 0) {
            List<RestaurantItem> childItems = dataCollection.get(headerItems.get(groupPosition));
            if (childItems != null && childItems.size() > 0) {
                return childItems.get(childPosition);
            }
        }
        return null;
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    private void setChild(View convertView, RestaurantItem childItem) {
        TextView title = (TextView) convertView.findViewById(R.id.title); // title
        TextView artist = (TextView) convertView.findViewById(R.id.artist); // artist name
        TextView duration = (TextView) convertView.findViewById(R.id.duration); // duration
        title.setText(childItem.getName());
        artist.setText("Text");

        MenuType menuType = menuTypeDao.getMenuGroupsById().get(childItem.getMenuTypeId());
        if (menuType == null || menuType.getShowPriceOfChildren().equalsIgnoreCase("Y")) {
            duration.setText(childItem.getPrice());
        }

        if (childItem.getActive() != null && childItem.getActive().equalsIgnoreCase("N")) {
            RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.menuListItem);
            layout.setBackgroundColor(Color.BLUE);
        }

    }

    public View getChildView(final int groupPosition,
                             final int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {

        LayoutInflater infalInflater = context;
        convertView = infalInflater.inflate(R.layout.menu_list_item, null);

        RestaurantItem childItem = (RestaurantItem) getChild(groupPosition, childPosition);
        setChild(convertView, childItem);


        addButtonsToChildView(convertView, childItem, groupPosition, childPosition);
        MenuItemDeleteDialog deleteDialog = new MenuItemDeleteDialog();
        deleteDialog.show(R.id.deleteMenuButton, convertView, activity, childItem, groupPosition);
        return convertView;
    }

    private ImageButton createEditButton(View view, RestaurantItem childItem, int groupPosition, int childPosition) {
        ImageButton itemEditButton = (ImageButton) view.findViewById(R.id.editMenuButton);
        itemEditButton.setOnClickListener(itemEditListener);
        itemEditButton.setTag(R.string.tag_item_obj, childItem);
        itemEditButton.setTag(R.string.tag_item_group_position, groupPosition);
        itemEditButton.setTag(R.string.tag_item_child_position, childPosition);
        return itemEditButton;
    }

    private CheckBox createAddItemCheckBox(View view, RestaurantItem childItem, int groupPosition, int childPosition) {
        CheckBox itemSelectInMenuCheckBox = (CheckBox) view.findViewById(R.id.itemSelectInMenuCheckBox);
        itemSelectInMenuCheckBox.setOnClickListener(addSetMenuItemToTempPlate);
        itemSelectInMenuCheckBox.setTag(R.string.tag_item_obj, childItem);
        itemSelectInMenuCheckBox.setTag(R.string.tag_item_group_position, groupPosition);
        itemSelectInMenuCheckBox.setTag(R.string.tag_item_child_position, childPosition);
        if (setMenuItems != null && setMenuItems.contains(childItem)) {
            itemSelectInMenuCheckBox.setChecked(true);
        } else {
            itemSelectInMenuCheckBox.setChecked(false);
        }
        return itemSelectInMenuCheckBox;
    }


    private ImageButton addToPlateButton(View view, RestaurantItem childItem, int groupPosition, int childPosition) {
        ImageButton addToPlateButton = (ImageButton) view.findViewById(R.id.addToPlateButton);
        addToPlateButton.setOnClickListener(addToPlateListener);
        addToPlateButton.setTag(childItem);
        return addToPlateButton;
    }

    private void addButtonsForAdmin(ImageButton itemEditButton, ImageButton addToPlateButton, CheckBox itemSelectInMenuCheckBox) {
        itemEditButton.setVisibility(View.VISIBLE);
        itemEditButton.setFocusable(false);
        itemEditButton.setFocusableInTouchMode(false);
        addToPlateButton.setVisibility(View.GONE);
        itemSelectInMenuCheckBox.setVisibility(View.GONE);
    }

    private void addButtonsForWaiter(RestaurantItem childItem, ImageButton itemEditButton, ImageButton addToPlateButton, CheckBox itemSelectInMenuCheckBox) {
        itemEditButton.setVisibility(View.GONE);
        MenuType menuType = menuTypeDao.getMenuGroupsById().get(childItem.getMenuTypeId());
        if (menuType != null && menuType.getShowPriceOfChildren().equals("N")) {
            itemSelectInMenuCheckBox.setVisibility(View.VISIBLE);
            itemSelectInMenuCheckBox.setFocusable(false);
            itemSelectInMenuCheckBox.setFocusableInTouchMode(false);
            addToPlateButton.setVisibility(View.GONE);
        } else {
            addToPlateButton.setVisibility(View.VISIBLE);
            addToPlateButton.setFocusable(false);
            addToPlateButton.setFocusableInTouchMode(false);
            itemSelectInMenuCheckBox.setVisibility(View.GONE);
        }
    }

    private void addButtonsForUser(ImageButton itemEditButton, ImageButton addToPlateButton, CheckBox itemSelectInMenuCheckBox) {
        itemEditButton.setVisibility(View.GONE);
        addToPlateButton.setVisibility(View.GONE);
        itemSelectInMenuCheckBox.setVisibility(View.GONE);
    }

    private void addButtonsToChildView(View view, RestaurantItem childItem, int groupPosition, int childPosition) {
        ImageButton itemEditButton = createEditButton(view, childItem, groupPosition, childPosition);
        ImageButton addToPlateButton = addToPlateButton(view, childItem, groupPosition, childPosition);
        CheckBox itemSelectInMenuCheckBox = createAddItemCheckBox(view, childItem, groupPosition, childPosition);

        if (LoginController.getInstance().isAdminLoggedIn()) {
            addButtonsForAdmin(itemEditButton, addToPlateButton, itemSelectInMenuCheckBox);
        } else if (LoginController.getInstance().isReviewAdminLoggedIn()) {
            addButtonsForWaiter(childItem, itemEditButton, addToPlateButton, itemSelectInMenuCheckBox);
        } else {
            addButtonsForUser(itemEditButton, addToPlateButton, itemSelectInMenuCheckBox);
        }
    }

    public int getChildrenCount(int groupPosition) {
        if (dataCollection != null && headerItems != null && dataCollection.size() > 0 && headerItems.size() > 0) {


            return dataCollection.get(headerItems.get(groupPosition)).size();
        }
        return 0;
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

    private void setGroup(View convertView, String headerName, int groupPosition) {
        TextView item = (TextView) convertView.findViewById(R.id.item);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(headerName);
        RestaurantItem groupObj = headerMap.get(headerName);
        if (groupObj.getActive() != null && groupObj.getActive().equalsIgnoreCase("N")) {
            RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.menuListGroup);
            layout.setBackgroundColor(Color.GRAY);
        }
        addButtonsToGroup(convertView, groupObj, groupPosition);
    }

    private void addButtonsToGroup(View convertView, RestaurantItem groupObj, int groupPosition) {
        addGroupEditButton(convertView, groupObj, groupPosition);
        addMenuItemAddButton(convertView, groupObj);
        addDeleteButton(groupObj, convertView, groupPosition);
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerName = (String) getGroup(groupPosition);
        LayoutInflater inflater = context;
        convertView = inflater.inflate(R.layout.menu_list_group, null);
        setGroup(convertView, headerName, groupPosition);
        return convertView;
    }

    private void addDeleteButton(RestaurantItem groupObj, View convertView, int groupPosition) {
        if (groupObj.getId() != -1 && groupObj.getChildItems() != null && groupObj.getChildItems().size() == 0) {
            MenuGroupDeleteDialog deleteDialog = new MenuGroupDeleteDialog();
            deleteDialog.show(R.id.deleteMenuGroupButton, convertView, activity, groupObj, groupPosition);
        } else {
            ImageButton deleteButton = (ImageButton) convertView.findViewById(R.id.deleteMenuGroupButton);
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void addGroupEditButton(View view, RestaurantItem groupObj, int groupPosition) {
        ImageButton groupEditButton = (ImageButton) view.findViewById(R.id.editGroupButton);
        groupEditButton.setOnClickListener(groupEditListener);
        groupEditButton.setTag(R.string.tag_group_obj, groupObj);
        groupEditButton.setTag(R.string.tag_group_position, groupPosition);

        if (groupObj.getId() != -1 && LoginController.getInstance().isAdminLoggedIn()) {
            groupEditButton.setVisibility(View.VISIBLE);
            groupEditButton.setFocusable(false);
            groupEditButton.setFocusableInTouchMode(false);
        } else {
            groupEditButton.setVisibility(View.GONE);
        }

    }


    private void addMenuItemAddButton(View view, RestaurantItem groupObj) {
        ImageButton addMenuItemButton = (ImageButton) view.findViewById(R.id.addMenuItemButton);
        addMenuItemButton.setOnClickListener(itemAddListener);
        addMenuItemButton.setTag(R.string.tag_item_obj, groupObj);
        if (groupObj.getId() != -1 && LoginController.getInstance().isAdminLoggedIn()) {
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

    public View.OnClickListener itemAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showItemAddPage(v);
        }
    };


    public View.OnClickListener itemEditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            showItemEditPage(v);
        }
    };


    View.OnClickListener addToPlateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addItemToPlate((RestaurantItem) v.getTag());
        }
    };

    public View.OnClickListener addSetMenuItemsToPlate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (setMenuItems != null && setMenuItems.size() > 0) {
                for (RestaurantItem setMenuItem : setMenuItems) {
                    if (setMenuItem != null) {
                        setMenuItem.setSetMenuGroup(setMenuCounter);
                        addItemToPlate(setMenuItem);
                    }
                }
                Toast.makeText(activity, "Items Added to the Order.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "Please select an item before adding.", Toast.LENGTH_SHORT).show();
            }
            setMenuCounter++;
            setMenuItems = new ArrayList<>();
            notifyDataSetChanged();
        }
    };


    View.OnClickListener addSetMenuItemToTempPlate = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CheckBox checkBox = (CheckBox) view;
            if (checkBox.isChecked()) {
                setMenuItems.add((RestaurantItem) view.getTag(R.string.tag_item_obj));
            } else {
                setMenuItems.remove((RestaurantItem) view.getTag(R.string.tag_item_obj));
            }
        }
    };

    public void addItemToPlate(RestaurantItem item) {
        OrderActivity orderActivity = (OrderActivity) activity;
        orderActivity.onRestaurantItemClicked(item);
    }

    public void showItemEditPage(View view) {
        Intent intent = null;
        intent = new Intent(activity, ItemEditActivity.class);

        Object tagItemObject = view.getTag(R.string.tag_item_obj);
        Object tagItemGroupPosition = view.getTag(R.string.tag_item_group_position);
        Object tagItemChildPosition = view.getTag(R.string.tag_item_child_position);
        RestaurantItem item = null;
        if (tagItemObject == null) {
            item = new RestaurantItem();
        } else {
            item = (RestaurantItem) tagItemObject;
        }
        intent.putExtra("item_obj", item);
        if (tagItemGroupPosition != null) {
            int groupPosition = (Integer) tagItemGroupPosition;
            intent.putExtra("item_group_position", groupPosition);
        }

        if (tagItemChildPosition != null) {
            int childPosition = (Integer) tagItemChildPosition;
            intent.putExtra("item_child_position", childPosition);
        }

        activity.startActivity(intent);
    }


    public void showItemAddPage(View view) {
        RestaurantItem item = (RestaurantItem) view.getTag(R.string.tag_item_obj);
        Map<String, Object> params = new HashMap<>();
        params.put("addItemActivity_group_id", item.getId());
        authenticationController.goToAddItemToGroupActivityPage(params);

    }

    private RestaurantItem getDefaultItem() {
        RestaurantItem item = new RestaurantItem();
        item.setPrice("0");
        item.setDescription("dummyDescription");
        return item;
    }

    public void showGroupEditPage(View view) {
        Intent intent = new Intent(activity, GroupEditActivity.class);
        Object tagObj = view.getTag(R.string.tag_group_obj);
        RestaurantItem item = getDefaultItem();
        if (tagObj != null) {
            item = (RestaurantItem) tagObj;
        }

        Object groupMenuIdObj = view.getTag(R.string.tag_group_menu_id);
        if (groupMenuIdObj != null) {
            item.setMenuTypeId((long) groupMenuIdObj);

        }


        Object groupPositionObj = view.getTag(R.string.tag_group_position);
        int groupPosition = -1;
        if (groupPositionObj != null) {
            groupPosition = (Integer) groupPositionObj;
        }

        intent.putExtra("groupEditActivity_parent_obj", item);
        intent.putExtra("groupEditActivity_group_position", groupPosition);


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
        dataCollection = new HashMap<>();
        headerItems = new ArrayList<>();
        Map<String, List<RestaurantItem>> newDataCollection = new HashMap<>();
        if (query.isEmpty()) {
            dataCollection.putAll(originalDataCollection);
            headerItems.addAll(originalHeaderItems);
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
                if (newList.size() > 0) {
                    headerItems.add(groupName);
                    newDataCollection.put(groupName, newList);
                }
            }

        }


        dataCollection.putAll(newDataCollection);
        notifyDataSetChanged();
    }


}


