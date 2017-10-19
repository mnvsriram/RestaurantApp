package app.resta.com.restaurantapp.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.MenuTypeSettingsActivity;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.model.MenuType;

/**
 * Created by Sriram on 02/08/2017.
 */

public class MenuTypeListAdapter extends ArrayAdapter<MenuType> implements View.OnClickListener {

    private List<MenuType> dataSet;
    MenuTypeSettingsActivity activity;
    private AuthenticationController authenticationController;

    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView price;
        ImageButton edit;
        Button addRemoveItemButton;
    }

    public MenuTypeListAdapter(List<MenuType> data, MenuTypeSettingsActivity activity) {
        super(activity, R.layout.item_in_group_list_item);
        this.dataSet = data;
        this.activity = activity;
        authenticationController = new AuthenticationController(activity);
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    public void setData(List<MenuType> data) {
        this.dataSet = data;
    }

    public List<MenuType> getData() {
        return dataSet;
    }


    @Override
    public MenuType getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public void onClick(View v) {

        int position = (Integer) v.getTag();
        Object object = getItem(position);
        MenuType dataModel = (MenuType) object;

    }

    View.OnClickListener buttonOnClickEditListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            MenuType itemToEdit = dataSet.get(index);

            Map<String, Object> params = new HashMap<>();
            params.put("activityMenuTypeAdd_menuTypeId", itemToEdit.getId());
            authenticationController.goToMenuTypeAddPage(params);
        }
    };


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuType dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.menu_type_list_item, parent, false);
            viewHolder.title = (TextView) convertView.findViewById(R.id.menuTypeTitle);
            viewHolder.price = (TextView) convertView.findViewById(R.id.menuTypePrice);
            viewHolder.edit = (ImageButton) convertView.findViewById(R.id.menuTypeEdit);

            //viewHolder.addRemoveItemButton = (Button) convertView.findViewById(R.id.menuTypeAddRemoveButton);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        viewHolder.title.setText(dataModel.getName());
        viewHolder.edit.setTag(position);
        //viewHolder.addRemoveItemButton.setTag(position);
        //viewHolder.addRemoveItemButton.setOnClickListener(addRemoveItemButtonOnClickEditListener);
        viewHolder.edit.setOnClickListener(buttonOnClickEditListener);
        if (dataModel.getPrice() != null) {
            viewHolder.price.setText("£ " + dataModel.getPrice());
        }
        return convertView;
    }

}