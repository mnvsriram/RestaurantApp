package app.resta.com.restaurantapp.adapter;


import android.app.Activity;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.model.OrderedItem;

public class OrderItemsExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater context;
    private Map<String, List<OrderedItem>> dataCollection;
    private List<String> headerItems;
    private Activity activity;

    public OrderItemsExpandableListAdapter(Activity activity, LayoutInflater context, Map<String, List<OrderedItem>> dataCollection, List<String> headerItems) {
        this.activity = activity;
        this.context = context;
        this.dataCollection = dataCollection;
        this.headerItems = headerItems;
    }


    public Object getChild(int groupPosition, int childPosition) {
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
            convertView = infalInflater.inflate(R.layout.order_list_item, null);
        }

        TextView title = (TextView) convertView.findViewById(R.id.orderItemTitle);
        TextView quantity = (TextView) convertView.findViewById(R.id.orderItemQuantity);
        TextView instructions = (TextView) convertView.findViewById(R.id.instructions);
        TextView price = (TextView) convertView.findViewById(R.id.orderItemPrice);


        OrderedItem childItem = (OrderedItem) getChild(groupPosition, childPosition);


        ImageButton addQuantity = (ImageButton) convertView.findViewById(R.id.addQuanitty);
        addQuantity.setTag(childItem);

        ImageButton removeQuantity = (ImageButton) convertView.findViewById(R.id.removeQuanitty);
        removeQuantity.setTag(childItem);

        ImageButton instructionsButton = (ImageButton) convertView.findViewById(R.id.editInstructionsButton);
        instructionsButton.setTag(childItem);

        title.setText(childItem.getItemName());

        double totalPriceForThisItem = 0;
        try {
            Double priceForSingleUnit = childItem.getPrice();
            if (priceForSingleUnit != null) {
                totalPriceForThisItem = childItem.getQuantity() * priceForSingleUnit;
            }
        } catch (NumberFormatException nfe) {
            // convert the price in restaurant item to double and then remove this try and catch block
        }
        price.setText(totalPriceForThisItem + "");
        quantity.setText(childItem.getQuantity() + "");
        instructions.setText(childItem.getInstructions());

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
            convertView = infalInflater.inflate(R.layout.order_list_group, null);
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


