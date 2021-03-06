package app.resta.com.restaurantapp.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ItemIconAdapter;
import app.resta.com.restaurantapp.controller.StyleController;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class MenuCardViewItemIconListFragment extends Fragment {

    private RestaurantItem selectedGroup;
    private View rootView;
    private ItemIconAdapter itemsAdapter;
    private ListView lv;
    private Activity container;
    private List<RestaurantItem> itemsInGroup;
    private StyleController styleController;

    public void setStyleController(StyleController styleController) {
        this.styleController = styleController;
    }

    public void setSelectedGroup(RestaurantItem selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public MenuCardViewItemIconListFragment() {
    }

    private void loadMenuItems() {
        itemsInGroup = selectedGroup.getChildItems();
        setAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.menu_card_fragment_item_icon_list, null);
        loadMenuItems();
        return rootView;
    }

    private void setAdapter() {
        GridView gridview = rootView.findViewById(R.id.menuCardItemIconsGridView);
        itemsAdapter = new ItemIconAdapter(new ArrayList<>(itemsInGroup), container, styleController);
        gridview.setAdapter(itemsAdapter);
        itemsAdapter.notifyDataSetChanged();
    }

    public void setContainer(Activity activity) {
        this.container = activity;
    }
}
