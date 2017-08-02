package app.resta.com.restaurantapp.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuListFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private View rootView;
    private Map<String, List<RestaurantItem>> data = new LinkedHashMap<>();
    private Map<String, RestaurantItem> headerMap = new LinkedHashMap<>();
    //private List<String> headerList;
    private List<RestaurantItem> childList;
    private Map<String, List<RestaurantItem>> dataCollection;
    private MenuExpandableListAdapter listAdapter;
    private ExpandableListView elv;

    private int lastChildClicked;
    private int lastGroupClicked;
    private MenuItemDao menuItemDao;

    public static interface OnMenuItemSelectedListener {
        public void onRestaurantItemClicked(int groupPosition, int childPosition);
    }

    private OnMenuItemSelectedListener listener;

    public MenuListFragment() {
        // Required empty public constructor
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

    private void addGroupAddButton(long groupMenuId) {
        ImageButton groupAddButton = (ImageButton) rootView.findViewById(R.id.addMenuGroupButton);

        if (LoginController.getInstance().isAdminLoggedIn()) {
            groupAddButton.setVisibility(View.VISIBLE);
            groupAddButton.setFocusable(false);
            groupAddButton.setFocusableInTouchMode(false);
            groupAddButton.setOnClickListener(groupAddListener);
            if (groupMenuId == -1) {
                groupAddButton.setOnClickListener(listAdapter.itemEditListener);
            }
        } else {
            groupAddButton.setVisibility(View.GONE);
        }
    }


    View.OnClickListener groupAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listAdapter.showGroupEditPage(v);
        }
    };

    private Map<Long, RestaurantItem> getMenuItems(long groupMenuId) {
        Map<Long, RestaurantItem> items = new HashMap<>();
        items = menuItemDao.fetchMenuItems(groupMenuId);
        for (RestaurantItem parent : items.values()) {
            headerMap.put(parent.getName(), parent);
            data.put(parent.getName(), parent.getChildItems());
        }
        createCollection();
        return items;
    }

    private void expandGroupOnLoad(long groupToOpen, Map<Long, RestaurantItem> items) {
        if (groupToOpen == 0) {
            elv.expandGroup(0);
        } else {
            List<Long> keyList = new ArrayList<Long>(items.keySet());
            for (int i = 0; i < keyList.size(); i++) {
                Long key = keyList.get(i);
                RestaurantItem value = items.get(key);
                if (value.getId() == groupToOpen) {
                    elv.expandGroup(i);
                }
            }
        }
    }

    private void selectAnItemOnLoad(long modifiedItemId, int groupPosition, int childPosition) {
        if (modifiedItemId > 0) {
            int index = groupPosition;
            if (groupPosition >= 0 && childPosition >= 0) {
                try {
                    index = elv.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (index >= 0) {
                elv.setItemChecked(index, true);
                onChildClickAction(elv, groupPosition, childPosition);
            }
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
        long groupToOpen = 0;
        long modifiedItemId = 0;
        int groupPosition = 0;
        int childPosition = 0;
        long groupMenuId = 0;
        menuItemDao = new MenuItemDao();
        if (activity.getIntent().getExtras() != null) {
            groupToOpen = activity.getIntent().getLongExtra("groupToOpen", 0l);
            modifiedItemId = activity.getIntent().getLongExtra("modifiedItemId", -1);
            groupPosition = activity.getIntent().getIntExtra("modifiedItemGroupPosition", 0);
            childPosition = activity.getIntent().getIntExtra("modifiedItemChildPosition", 0);
            groupMenuId = activity.getIntent().getLongExtra("groupMenuId", 0);
        }
        rootView = inflater.inflate(R.layout.fragment_menu_list, null);
        Map<Long, RestaurantItem> items = getMenuItems(groupMenuId);

        setAdapter(inflater);
        addGroupAddButton(groupMenuId);
        expandGroupOnLoad(groupToOpen, items);
        selectAnItemOnLoad(modifiedItemId, groupPosition, childPosition);
        setOnGroupClickListener();
        setOnChildClickListener();
        setSearchView(activity);
        return rootView;
    }

    private void setAdapter(LayoutInflater inflater) {
        elv =
                (ExpandableListView) rootView.findViewById(R.id.list);
        listAdapter = new MenuExpandableListAdapter
                (getActivity(), inflater, headerMap, dataCollection);
        elv.setAdapter(listAdapter);

    }

    private void setSearchView(Activity activity) {
        SearchManager searchManager = (SearchManager) activity.getSystemService(MyApplication.getAppContext().SEARCH_SERVICE);
        SearchView search = (SearchView) rootView.findViewById(R.id.searchMenu);
        search.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
    }

    private boolean onChildClickAction(ExpandableListView expandableList,
                                       int groupPosition, int childPosition) {
        lastChildClicked = childPosition;
        lastGroupClicked = groupPosition;

        Toast toast = Toast.makeText(MyApplication.getAppContext(), childPosition + "", Toast.LENGTH_LONG);
        toast.show();

        int index = groupPosition;
        if (childPosition >= 0) {
            index = expandableList.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));

        }
        expandableList.setItemChecked(index, true);
        try {
            ((OnMenuItemSelectedListener) getActivity()).onRestaurantItemClicked(groupPosition, childPosition);
        } catch (ClassCastException cce) {

        }
        return false;

    }

    @Override
    public boolean onClose() {
        listAdapter.filterData("");
        //expandAll();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        listAdapter.filterData(query);
        if (query.equalsIgnoreCase("")) {
            collapseAll();
            expandPreviouslySelectedGroup();
        } else {
            expandAll();
        }

        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        listAdapter.filterData(query);

        if (query.equalsIgnoreCase("")) {
            collapseAll();
            expandPreviouslySelectedGroup();
        } else {
            expandAll();
        }

        return false;
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            elv.expandGroup(i);
        }
    }

    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            elv.collapseGroup(i);
        }
    }

    private void expandPreviouslySelectedGroup() {
        int index = lastGroupClicked;
        if (lastChildClicked >= 0) {
            index = elv.getFlatListPosition(ExpandableListView.getPackedPositionForChild(lastGroupClicked, lastChildClicked));

        }
        elv.expandGroup(lastGroupClicked);
//        elv.setItemChecked(index, true);

    }

}
