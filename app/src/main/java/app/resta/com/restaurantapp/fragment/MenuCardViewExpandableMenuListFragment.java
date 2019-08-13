package app.resta.com.restaurantapp.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuCardExpandableMenuListAdapter;
import app.resta.com.restaurantapp.controller.StyleController;
import app.resta.com.restaurantapp.db.dao.user.menuGroup.MenuGroupUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuGroup.MenuGroupUserFireStoreDao;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.ListViewUtils;
import app.resta.com.restaurantapp.util.StyleUtil;

public class MenuCardViewExpandableMenuListFragment extends Fragment {

    private View rootView;
    private Map<String, List<RestaurantItem>> data = new LinkedHashMap<>();
    private Map<String, RestaurantItem> headerMap = new LinkedHashMap<>();
    private List<RestaurantItem> childList;
    private Map<String, List<RestaurantItem>> dataCollection;
    private MenuCardExpandableMenuListAdapter listAdapter;
    private ExpandableListView elv;
    private Fragment container;
    private MenuTypeUserDaoI menuTypeUserDao;
    private MenuGroupUserDaoI menuGroupUserDao;
    private Map<String, RestaurantItem> menuItemsForSelectedMenuType;
    private String groupMenuId = null;
    private StyleController styleController;

    public interface OnMenuCardExpandableItemSelectedListener {
        void onMenuCardExpandableItemSelectedListener(RestaurantItem item);
    }

    public MenuCardViewExpandableMenuListFragment() {
        // Required empty public constructor
    }

    public void setStyleController(StyleController styleController) {
        this.styleController = styleController;
    }

    private void createCollection() {
        dataCollection = new LinkedHashMap<String, List<RestaurantItem>>();
        for (String parentItem : headerMap.keySet()) {
            List<RestaurantItem> l = data.get(parentItem);
            loadChild(l.toArray(new RestaurantItem[l.size()]));
            dataCollection.put(parentItem, childList);
        }
    }


    private void loadChild(RestaurantItem[] childModels) {
        childList = new ArrayList<RestaurantItem>();
        for (RestaurantItem model : childModels)
            childList.add(model);
    }


    private void loadMenuItems(final String menuTypeId, final LayoutInflater inflater) {
        headerMap = new HashMap<>();
        data = new HashMap<>();
        menuItemsForSelectedMenuType = new HashMap<>();

        menuTypeUserDao.getGroupsInMenuType_u(menuTypeId + "", new OnResultListener<List<RestaurantItem>>() {
            @Override
            public void onCallback(final List<RestaurantItem> groups) {
                final AtomicInteger index = new AtomicInteger(0);
                if (groups != null && groups.size() > 0) {
                    for (final RestaurantItem group : groups) {
                        menuGroupUserDao.getItemsInGroup_u(group.getId() + "", new OnResultListener<List<RestaurantItem>>() {
                            @Override
                            public void onCallback(List<RestaurantItem> itemsInGroup) {
                                index.getAndIncrement();
                                group.setChildItems(itemsInGroup);
                                group.setMenuTypeId(menuTypeId);
                                menuItemsForSelectedMenuType.put(group.getId(), group);
                                headerMap.put(group.getName(), group);
                                data.put(group.getName(), group.getChildItems());
                                if (index.get() == groups.size()) {
                                    setUp(inflater);
                                }
                            }
                        });
                    }
                } else {
                    setUp(inflater);
                }
            }
        });


    }

    private void setUp(LayoutInflater inflater) {
        createCollection();
        setAdapter(inflater);
        expandFirst();
        selectAnItemOnLoad();

        ListViewUtils.setListViewHeight(elv);
        setOnGroupClickListener();
        setOnChildClickListener();

    }

    private void selectAnItemOnLoad() {
        int index = 0;
        try {
            index = elv.getFlatListPosition(ExpandableListView.getPackedPositionForChild(0, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (index >= 0) {
            elv.setItemChecked(index, true);
            onChildClickAction(elv, 0, 0);
        }

    }

    private void setOnGroupClickListener() {
        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (elv.isGroupExpanded(groupPosition)) {
                    elv.collapseGroup(groupPosition);
                } else {
                    elv.expandGroup(groupPosition);
                    for (int i = 0; i < headerMap.size(); i++) {
                        if (i != groupPosition) {
                            elv.collapseGroup(i);
                        }
                    }
                }
                ListViewUtils.setListViewHeight(elv);
                return true;
            }
        });
    }

    private void setOnChildClickListener() {
        elv.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableList, View v,
                                        int groupPosition, int childPosition, long id) {
                return onChildClickAction(expandableList, groupPosition, childPosition);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = getActivity();
        menuTypeUserDao = new MenuTypeUserFireStoreDao();
        menuGroupUserDao = new MenuGroupUserFireStoreDao();
        if (activity.getIntent().getExtras() != null) {
            if (groupMenuId == null) {
                groupMenuId = activity.getIntent().getStringExtra("groupMenuId");
            }
        }
        rootView = inflater.inflate(R.layout.menu_card_fragment_menu_expandable_list, null);
        loadMenuItems(groupMenuId, inflater);


        ViewGroup mainLayout = rootView.findViewById(R.id.menuCardViewExpandableMenuList);
        StyleUtil.setStyle(mainLayout, styleController);

        return rootView;
    }

    private void setAdapter(LayoutInflater inflater) {
        elv =
                (ExpandableListView) rootView.findViewById(R.id.menuCardViewExpandableMenuList);
        listAdapter = new MenuCardExpandableMenuListAdapter
                (getActivity(), inflater, headerMap, dataCollection, styleController);
        elv.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    private boolean onChildClickAction(ExpandableListView expandableList,
                                       int groupPosition, int childPosition) {
        int index = groupPosition;
        if (childPosition >= 0) {
            index = expandableList.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
        }
        expandableList.setItemChecked(index, true);
        try {
            RestaurantItem item = listAdapter.getChildMenuItem(groupPosition, childPosition);
            ((OnMenuCardExpandableItemSelectedListener) container).onMenuCardExpandableItemSelectedListener(item);
        } catch (ClassCastException cce) {
            cce.printStackTrace();
            //the container class should implement this interface.. otherwise there iwill be casting exception
        }
        return false;
    }


    private void expandFirst() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            elv.expandGroup(i);
            break;
        }
    }


    public void setGroupMenuId(String groupMenuId) {
        this.groupMenuId = groupMenuId;
    }

    public void setContainer(Fragment container) {
        this.container = container;
    }
}
