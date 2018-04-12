package app.resta.com.restaurantapp.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.TextUtils;

public class MenuCardItemWithoutDescriptionMultiColumnFragment extends Fragment {
    private long menuTypeId;
    private View inflatedView;
    private MenuTypeDao menuTypeDao;
    private MenuItemDao menuItemDao;
    private MenuType menuType;
    Map<Long, RestaurantItem> groups;
    private boolean showDetailsPopup;

    public MenuCardItemWithoutDescriptionMultiColumnFragment() {
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
        if (menuType != null) {
            setMenuTypeName();
            setMenuTypeDescription();
            setMenuItems(inflater);
        }
    }


    private void setMenuTypeName() {
        TextView menuTypeName = (TextView) inflatedView.findViewById(R.id.menuCardViewMenuTypeName);
        menuTypeName.setText(TextUtils.getUnderlinesString(menuType.getName()));
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
                int noOfChilds = group.getChildItems().size();

                double noOfRows = noOfChilds / 2.0;
                int noOfRowsCeil = new Double(Math.ceil(noOfRows)).intValue();
                int midNumber = noOfRowsCeil;
                for (int i = 0; i < midNumber; ) {
                    RestaurantItem leftItem = group.getChildItems().get(i++);
                    RestaurantItem rightItem = null;
                    if (noOfRowsCeil < noOfChilds) {
                        rightItem = group.getChildItems().get(noOfRowsCeil++);
                    }
                    linearLayout.addView(getViewForItem(inflater, linearLayout, leftItem, rightItem));
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

    private View getViewForItem(LayoutInflater inflater, LinearLayout parent, RestaurantItem leftItem, RestaurantItem rightItem) {
        View v = inflater.inflate(R.layout.menu_card_view_item_multi_row, parent, false);
        if (leftItem != null) {
            TextView itemName = (TextView) v.findViewById(R.id.menuCardViewItemNameLeft);
            itemName.setText(leftItem.getName());

            TextView price = (TextView) v.findViewById(R.id.menuCardViewPriceLeft);
            price.setText(leftItem.getPrice());

            ImageButton showDetailsIconLeft = (ImageButton) v.findViewById(R.id.showDetailsPopupLeft);
            if (showDetailsPopup) {
                showDetailsIconLeft.setTag(leftItem);
                showDetailsIconLeft.setVisibility(View.VISIBLE);
            } else {
                showDetailsIconLeft.setVisibility(View.GONE);
            }

        }
        if (rightItem != null) {
            TextView itemName = (TextView) v.findViewById(R.id.menuCardViewItemNameRight);
            itemName.setText(rightItem.getName());

            TextView price = (TextView) v.findViewById(R.id.menuCardViewPriceRight);
            price.setText(rightItem.getPrice());

            ImageButton showDetailsIconRight = (ImageButton) v.findViewById(R.id.showDetailsPopupRight);
            if (showDetailsPopup) {
                showDetailsIconRight.setTag(leftItem);
                showDetailsIconRight.setVisibility(View.VISIBLE);
            } else {
                showDetailsIconRight.setVisibility(View.GONE);
            }
        }


        return v;
    }

    public long getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(long menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public void setShowDetailsPopup(boolean showDetailsPopup) {
        this.showDetailsPopup = showDetailsPopup;
    }
}
