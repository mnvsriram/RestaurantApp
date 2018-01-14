package app.resta.com.restaurantapp.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.GroupListAdapter;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ListViewUtils;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardViewGroupListFragment extends Fragment {

    private View rootView;
    private GroupListAdapter listAdapter;
    private ListView lv;
    private Fragment container;
    private MenuItemDao menuItemDao;
    private Map<Long, RestaurantItem> menuItemsForSelectedMenuType;
    private long groupMenuId = 0;

    public interface OnMenuCardGroupListWithIconsGroupClickListener {
        void onMenuCardGroupListWithIconsGroupClickListener(RestaurantItem item);
    }

    public MenuCardViewGroupListFragment() {
        // Required empty public constructor
    }


    private void loadMenuItems(long groupMenuId, LayoutInflater inflater) {
        menuItemsForSelectedMenuType = menuItemDao.fetchMenuItems(groupMenuId);
        setAdapter();
    }

    private void selectAnItemOnLoad() {
        RestaurantItem selectedGroup = (RestaurantItem) listAdapter.getItem(0);
        if (selectedGroup != null) {
            lv.setItemChecked(0, true);
            onGroupClickAction(0);
        }
    }

    private void setOnGroupClickListener() {
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3) {
                onGroupClickAction(position);
            }
        });
    }


    private void onGroupClickAction(int position) {
        RestaurantItem selectedGroup = (RestaurantItem) listAdapter.getItem(position);
        ((OnMenuCardGroupListWithIconsGroupClickListener) container).onMenuCardGroupListWithIconsGroupClickListener(selectedGroup);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = getActivity();
        menuItemDao = new MenuItemDao();
        if (activity.getIntent().getExtras() != null) {
            if (groupMenuId <= 0) {
                groupMenuId = activity.getIntent().getLongExtra("groupMenuId", 0);
            }
        }
        rootView = inflater.inflate(R.layout.menu_card_fragment_group_list, null);
        loadMenuItems(groupMenuId, inflater);
        setOnGroupClickListener();
        selectAnItemOnLoad();
        return rootView;
    }

    private void setAdapter() {
        lv =
                (ListView) rootView.findViewById(R.id.menuCardViewGroupList);
        listAdapter = new GroupListAdapter(new ArrayList<>(menuItemsForSelectedMenuType.values()), MyApplication.getAppContext());
        lv.setAdapter(listAdapter);
        //ListViewUtils.setListViewHeightBasedOnChildren(lv);
        listAdapter.notifyDataSetChanged();
    }

    public void setGroupMenuId(long groupMenuId) {
        this.groupMenuId = groupMenuId;
    }

    public void setContainer(Fragment container) {
        this.container = container;
    }
}
