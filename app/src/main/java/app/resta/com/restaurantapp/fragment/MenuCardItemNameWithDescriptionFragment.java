package app.resta.com.restaurantapp.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuCardItemNameWithDescriptionFragment extends Fragment {
    private long menuTypeId;
    private View inflatedView;
    private MenuTypeDao menuTypeDao;
    private MenuItemDao menuItemDao;
    private MenuType menuType;
    Map<Long, RestaurantItem> groups;

    public MenuCardItemNameWithDescriptionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initialize() {
        menuTypeDao = new MenuTypeDao();
        menuItemDao = new MenuItemDao();
        menuType = menuTypeDao.getMenuGroupsById().get(menuTypeId);
        groups = menuItemDao.fetchMenuItems(menuTypeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_menu_card_items_with_description, container, false);
        initialize();
        setFields(inflater);
        return inflatedView;
    }

    private void setFields(LayoutInflater inflater) {
        setMenuTypeName();
        setMenuTypeDescription();
        setMenuItems(inflater);
    }


    private void setMenuTypeName() {
        TextView menuTypeName = (TextView) inflatedView.findViewById(R.id.menuCardViewMenuTypeName);
        menuTypeName.setText(menuType.getName());
    }

    private void setMenuTypeDescription() {
        TextView menuTypeDescription = (TextView) inflatedView.findViewById(R.id.menuCardViewMenuTypeDescription);
        menuTypeDescription.setText(menuType.getDescription());
    }

    private void setMenuItems(LayoutInflater inflater) {
        LinearLayout linearLayout = (LinearLayout) inflatedView.findViewById(R.id.itemsWithDescItemsList);
        if (groups != null) {
            for (RestaurantItem group : groups.values()) {
                linearLayout.addView(getViewForGroup(inflater, linearLayout, group));
                for (RestaurantItem item : group.getChildItems()) {
                    linearLayout.addView(getViewForItem(inflater, linearLayout, item));
                }
            }
        }
    }

    private View getViewForGroup(LayoutInflater inflater, LinearLayout parent, RestaurantItem group) {
        View v = inflater.inflate(R.layout.menu_card_view_group_details, parent, false);
        TextView groupName = (TextView) v.findViewById(R.id.menuCardViewGroupName);
        groupName.setText(group.getName());
        TextView groupDescription = (TextView) v.findViewById(R.id.menuCardViewGroupDescription);
        groupDescription.setText(group.getDescription());
        return v;
    }

    private View getViewForItem(LayoutInflater inflater, LinearLayout parent, RestaurantItem item) {
        View v = inflater.inflate(R.layout.menu_card_view_item_details, parent, false);
        TextView groupName = (TextView) v.findViewById(R.id.menuCardViewItemName);
        groupName.setText(item.getName());
        TextView groupDescription = (TextView) v.findViewById(R.id.menuCardViewItemDescription);
        groupDescription.setText(item.getDescription());
        return v;
    }

    public long getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(long menuTypeId) {
        this.menuTypeId = menuTypeId;
    }
}
