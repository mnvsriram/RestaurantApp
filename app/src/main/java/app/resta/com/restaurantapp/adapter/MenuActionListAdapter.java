package app.resta.com.restaurantapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.MenuButtonEditActivity;
import app.resta.com.restaurantapp.model.MenuCardAction;
import app.resta.com.restaurantapp.model.MenuType;

/**
 * Created by Sriram on 02/08/2017.
 */

public class MenuActionListAdapter extends ArrayAdapter<MenuCardAction> implements View.OnClickListener {

    private List<MenuCardAction> dataSet;
    MenuButtonEditActivity activity;
    public boolean dataChanged = false;
    Map<String, MenuType> menuTypeById;

    // View lookup cache
    private static class ViewHolder {
        TextView menuTypeName;
        TextView layoutName;
        ImageButton up;
        ImageButton down;
        ImageButton delete;
    }

    public MenuActionListAdapter(List<MenuCardAction> actions, Map<String, MenuType> menuTypeById, MenuButtonEditActivity activity) {
        super(activity, R.layout.action_in_menu_list);
        this.dataSet = actions;
        this.activity = activity;
        this.menuTypeById = menuTypeById;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    public void setData(List<MenuCardAction> data) {
        this.dataSet = data;
    }

    public List<MenuCardAction> getData() {
        return dataSet;
    }


    @Override
    public MenuCardAction getItem(int position) {
        return dataSet.get(position);
    }

    @Override
    public void onClick(View v) {
//        int position = (Integer) v.getTag();
        //      Object object = getItem(position);
        //    RestaurantItem dataModel = (RestaurantItem) object;

    }

    View.OnClickListener upArrowOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataChanged = true;
            int index = (Integer) v.getTag();
            MenuCardAction itemToMoveUp = dataSet.get(index);
            if (index != 0) {
                MenuCardAction itemToMoveDown = dataSet.get(index - 1);
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
            MenuCardAction itemToMoveDown = dataSet.get(index);
            if (index != dataSet.size() - 1) {
                MenuCardAction itemToMoveUp = dataSet.get(index + 1);

                dataSet.remove(index + 1);
                dataSet.remove(index);

                dataSet.add(index, itemToMoveUp);
                dataSet.add(index + 1, itemToMoveDown);

                notifyDataSetChanged();
            }
        }
    };


    View.OnClickListener deleteOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dataChanged = true;
            int index = (Integer) v.getTag();
            MenuCardAction itemToDelete = dataSet.get(index);
            dataSet.remove(itemToDelete);
            activity.setActions(dataSet);
            notifyDataSetChanged();
        }
    };

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MenuCardAction dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.action_in_menu_list, parent, false);

            viewHolder.menuTypeName = (TextView) convertView.findViewById(R.id.actionMenuTypeName);
            viewHolder.layoutName = (TextView) convertView.findViewById(R.id.actionMenuLayoutName);

            viewHolder.up = (ImageButton) convertView.findViewById(R.id.actionsInMenuUpButton);
            viewHolder.down = (ImageButton) convertView.findViewById(R.id.actionsInMenuDownButton);
            viewHolder.delete = (ImageButton) convertView.findViewById(R.id.actionsInMenuRemoveButton);
            result = convertView;
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        if (dataModel.getMenuTypeName() == null && menuTypeById.get(dataModel.getMenuTypeId()) != null) {
            dataModel.setMenuTypeName(menuTypeById.get(dataModel.getMenuTypeId()).getName());
        }

        viewHolder.menuTypeName.setText(dataModel.getMenuTypeName());
        viewHolder.layoutName.setText(dataModel.getLayoutName());

        viewHolder.up.setTag(position);
        viewHolder.down.setTag(position);
        viewHolder.delete.setTag(position);

        viewHolder.up.setOnClickListener(upArrowOnClickListener);
        viewHolder.down.setOnClickListener(downArrowOnClickListener);
        viewHolder.delete.setOnClickListener(deleteOnClickListener);
        return convertView;
    }
}