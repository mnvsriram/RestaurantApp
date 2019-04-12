package app.resta.com.restaurantapp.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.TextUtils;

public class MenuCardItemWithoutDescriptionMultiColumnFragment extends Fragment {
    private String menuTypeId;
    private View inflatedView;
    private MenuType menuType;
    private boolean showDetailsPopup;

    public MenuCardItemWithoutDescriptionMultiColumnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initialize(final LayoutInflater inflater) {
        MenuTypeUserDaoI menuTypeUserDao = new MenuTypeUserFireStoreDao();

        menuTypeUserDao.getMenuType_u(menuTypeId, new OnResultListener<MenuType>() {
            @Override
            public void onCallback(MenuType menuTypeFromDB) {
                menuType = menuTypeFromDB;
                setMenuTypeName();
                setMenuTypeDescription();
            }
        });


        menuTypeUserDao.getGroupsWithItemsInMenuType_u(menuTypeId, new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> groupsInMenuType) {
                setMenuItems(groupsInMenuType, inflater);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_menu_card_items_with_description, container, false);
        initialize(inflater);
        return inflatedView;
    }

    private void setMenuTypeName() {
        TextView menuTypeName = inflatedView.findViewById(R.id.menuCardViewMenuTypeName);
        menuTypeName.setText(TextUtils.getUnderlinesString(menuType.getName()));
    }

    private void setMenuTypeDescription() {
        TextView menuTypeDescription = inflatedView.findViewById(R.id.menuCardViewMenuTypeDescription);
        menuTypeDescription.setText(menuType.getDescription());
    }

    private void setMenuItems(List<RestaurantItem> groupsInMenuType, LayoutInflater inflater) {
        LinearLayout linearLayout = inflatedView.findViewById(R.id.itemsWithDescItemsList);
        if (groupsInMenuType!= null) {
            for (RestaurantItem group : groupsInMenuType) {
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
        TextView groupName = v.findViewById(R.id.menuCardViewGroupName);
        groupName.setText(group.getName());
        TextView groupDescription = v.findViewById(R.id.menuCardViewGroupDescription);
        groupDescription.setText(group.getDescription());
        return v;
    }

    private View getViewForItem(LayoutInflater inflater, LinearLayout parent, RestaurantItem leftItem, RestaurantItem rightItem) {
        View v = inflater.inflate(R.layout.menu_card_view_item_multi_row, parent, false);
        if (leftItem != null) {
            TextView itemName = v.findViewById(R.id.menuCardViewItemNameLeft);
            itemName.setText(leftItem.getName());

            TextView price;
            price = v.findViewById(R.id.menuCardViewPriceLeft);
            price.setText(leftItem.getPrice());

            ImageButton showDetailsIconLeft = v.findViewById(R.id.showDetailsPopupLeft);
            if (showDetailsPopup) {
                showDetailsIconLeft.setTag(leftItem);
                showDetailsIconLeft.setVisibility(View.VISIBLE);
            } else {
                showDetailsIconLeft.setVisibility(View.GONE);
            }

        }
        if (rightItem != null) {
            TextView itemName;
            itemName = v.findViewById(R.id.menuCardViewItemNameRight);
            itemName.setText(rightItem.getName());

            TextView price = v.findViewById(R.id.menuCardViewPriceRight);
            price.setText(rightItem.getPrice());

            ImageButton showDetailsIconRight = v.findViewById(R.id.showDetailsPopupRight);
            if (showDetailsPopup) {
                showDetailsIconRight.setTag(leftItem);
                showDetailsIconRight.setVisibility(View.VISIBLE);
            } else {
                showDetailsIconRight.setVisibility(View.GONE);
            }
        }


        return v;
    }

    public String getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(String menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public void setShowDetailsPopup(boolean showDetailsPopup) {
        this.showDetailsPopup = showDetailsPopup;
    }
}
