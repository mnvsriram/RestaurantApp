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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.GroupListAdapter;
import app.resta.com.restaurantapp.controller.StyleController;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuCardViewGroupListFragment extends Fragment {

    private View rootView;
    private GroupListAdapter listAdapter;
    private ListView lv;
    private Fragment container;
    private MenuTypeUserDaoI menuTypeUserDao;
    private Map<String, RestaurantItem> menuItemsForSelectedMenuType;
    private String groupMenuId = null;
    private StyleController styleController;

    public void setStyleController(StyleController styleController) {
        this.styleController = styleController;
    }

    public interface OnMenuCardGroupListWithIconsGroupClickListener {
        void onMenuCardGroupListWithIconsGroupClickListener(RestaurantItem item);
    }

    public MenuCardViewGroupListFragment() {
        // Required empty public constructor
    }


    private void loadMenuItems(String groupMenuId) {
        menuItemsForSelectedMenuType = new HashMap<>();
        menuTypeUserDao.getGroupsWithItemsInMenuType_u(groupMenuId, new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(List<RestaurantItem> groupsInMenuType) {
                if (groupsInMenuType != null) {
                    for (RestaurantItem group : groupsInMenuType) {
                        menuItemsForSelectedMenuType.put(group.getId(), group);
                    }
                }
                setAdapter();
                setOnGroupClickListener();
                selectAnItemOnLoad();
            }
        });
    }

    private void selectAnItemOnLoad() {
        if (listAdapter.getCount() > 0) {
            RestaurantItem selectedGroup = listAdapter.getItem(0);
            if (selectedGroup != null) {
                lv.setItemChecked(0, true);
                onGroupClickAction(0);
            }
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
        RestaurantItem selectedGroup = listAdapter.getItem(position);
        ((OnMenuCardGroupListWithIconsGroupClickListener) container).onMenuCardGroupListWithIconsGroupClickListener(selectedGroup);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = getActivity();
        menuTypeUserDao = new MenuTypeUserFireStoreDao();
        if (activity.getIntent().getExtras() != null) {
            if (groupMenuId == null) {
                groupMenuId = activity.getIntent().getStringExtra("groupMenuId");
            }
        }
        rootView = inflater.inflate(R.layout.menu_card_fragment_group_list, null);
        loadMenuItems(groupMenuId);
        return rootView;
    }

    private void setAdapter() {
        lv =
                rootView.findViewById(R.id.menuCardViewGroupList);
        listAdapter = new GroupListAdapter(new ArrayList<>(menuItemsForSelectedMenuType.values()), MyApplication.getAppContext(), styleController);
        lv.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    public void setGroupMenuId(String groupMenuId) {
        this.groupMenuId = groupMenuId;
    }

    public void setContainer(Fragment container) {
        this.container = container;
    }
}
