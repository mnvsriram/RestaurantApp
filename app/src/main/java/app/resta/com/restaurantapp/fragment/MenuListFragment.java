package app.resta.com.restaurantapp.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuItem.MenuItemAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class MenuListFragment extends Fragment implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    private View rootView;
    private Map<String, List<RestaurantItem>> data = new LinkedHashMap<>();
    private Map<String, RestaurantItem> headerMap = new LinkedHashMap<>();
    private List<RestaurantItem> childList;
    private Map<String, List<RestaurantItem>> dataCollection;
    private MenuExpandableListAdapter listAdapter;
    private ExpandableListView elv;

    private int lastChildClicked;
    private int lastGroupClicked;

    private MenuTypeAdminDaoI menuTypeAdminDao;
    private MenuItemAdminDaoI menuItemAdminDao;
    private MenuGroupAdminDaoI menuGroupAdminDao;

    private Map<String, RestaurantItem> menuItemsForSelectedMenuType;

    final List<String> menuTypeNames = new ArrayList<>();
    final Map<String, MenuType> menuTypeNameTomenuType = new HashMap<>();
    final Map<String, MenuType> menuTypeIdTomenuType = new HashMap<>();
    ArrayAdapter<String> spinnerArrayAdapter;

    public interface OnMenuItemSelectedListener {
        void onRestaurantItemClicked(RestaurantItem item);
    }

    public interface OnMenuTypeChanged {
        void onMenuTypeChanged(String menuTypeId);
    }

    public MenuListFragment() {
        // Required empty public constructor
    }

    private void createCollection() {
        dataCollection = new LinkedHashMap<>();
        for (String parentItem : headerMap.keySet()) {
            List<RestaurantItem> l = data.get(parentItem);
            loadChild(l.toArray(new RestaurantItem[l.size()]));
            dataCollection.put(parentItem, childList);
        }
    }


    private void loadChild(RestaurantItem[] childModels) {
        childList = new ArrayList<>();
        Collections.addAll(childList, childModels);
    }

    private void addGroupAddButton(String groupMenuId) {
        ImageButton groupAddButton = rootView.findViewById(R.id.addMenuGroupButton);
        if (LoginController.getInstance().isAdminLoggedIn()) {
            groupAddButton.setVisibility(View.VISIBLE);
            groupAddButton.setFocusable(false);
            groupAddButton.setFocusableInTouchMode(false);
            if (groupMenuId == null) {
                groupAddButton.setOnClickListener(listAdapter.itemEditListener);
            } else {
                groupAddButton.setOnClickListener(groupAddListener);
                groupAddButton.setTag(R.string.tag_group_menu_id, groupMenuId);
            }
        } else {
            groupAddButton.setVisibility(View.GONE);
        }
    }


    private void addMenuToPlateButton(String selectedMenuTypeId) {
        MenuType selectedMenuType = menuTypeIdTomenuType.get(selectedMenuTypeId);


        ImageButton addMenuToPlateButton = rootView.findViewById(R.id.addMenuToPlateButton);
        if (LoginController.getInstance().isReviewAdminLoggedIn() && selectedMenuType != null && !selectedMenuType.isShowPriceOfChildren()) {
            addMenuToPlateButton.setVisibility(View.VISIBLE);
            addMenuToPlateButton.setFocusable(false);
            addMenuToPlateButton.setFocusableInTouchMode(false);
            addMenuToPlateButton.setOnClickListener(listAdapter.addSetMenuItemsToPlate);
        } else {
            addMenuToPlateButton.setVisibility(View.GONE);
        }
    }


    View.OnClickListener groupAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            listAdapter.showGroupEditPage(v);
        }
    };

    private void loadMenuItems(final String menuTypeId, final LayoutInflater inflater) {
        headerMap = new HashMap<>();
        data = new HashMap<>();
        menuItemsForSelectedMenuType = new HashMap<>();

        if (menuTypeId == null || menuTypeId.length() == 0) {
            menuItemAdminDao.getAllItems(new OnResultListener<List<RestaurantItem>>() {
                @Override
                public void onCallback(List<RestaurantItem> itemsInGroup) {
                    final RestaurantItem group = new RestaurantItem();
                    group.setName("All Items");
                    group.setChildItems(itemsInGroup);
                    group.setMenuTypeId(menuTypeId);
                    menuItemsForSelectedMenuType.put("ALL_ITEMS", group);
                    headerMap.put(group.getName(), group);
                    data.put(group.getName(), group.getChildItems());
                    setUp(inflater, menuTypeId);
                }
            });
//            setUp(inflater, menuTypeId);
        } else {
            menuTypeAdminDao.getGroupsInMenuType(menuTypeId + "", new OnResultListener<List<RestaurantItem>>() {
                @Override
                public void onCallback(final List<RestaurantItem> groups) {
                    final AtomicInteger index = new AtomicInteger(0);
                    if (groups != null && groups.size() > 0) {

                        for (final RestaurantItem group : groups) {
                            menuGroupAdminDao.getItemsInGroup(group.getId() + "", new OnResultListener<List<RestaurantItem>>() {
                                @Override
                                public void onCallback(List<RestaurantItem> itemsInGroup) {

                                    for (RestaurantItem itemInGroup : itemsInGroup) {
                                        itemInGroup.setMenuTypeId(menuTypeId);
                                        itemInGroup.setMenuTypeName(menuTypeIdTomenuType.get(menuTypeId).getName());
                                    }
                                    index.getAndIncrement();
                                    group.setChildItems(itemsInGroup);
                                    group.setMenuTypeId(menuTypeId);
                                    menuItemsForSelectedMenuType.put(group.getId(), group);
                                    headerMap.put(group.getName(), group);
                                    data.put(group.getName(), group.getChildItems());
                                    if (index.get() == groups.size()) {
                                        setUp(inflater, menuTypeId);
                                    }
                                }
                            });
                        }

                    } else {
                        setUp(inflater, menuTypeId);

                    }
                }
            });
        }

    }

    private void setUp(LayoutInflater inflater, String menuTypeId) {
        createCollection();
        setAdapter(inflater, menuTypeId);
        expandGroupOnLoad(groupToOpen);


        addGroupAddButton(menuTypeId);
        setOnGroupClickListener();
        setOnChildClickListener();
        setSearchView();
    }

    private void expandGroupOnLoad(String groupToOpen) {
        if (groupToOpen == null) {
            elv.expandGroup(0);
        } else {
            List<String> keyList = new ArrayList<>(menuItemsForSelectedMenuType.keySet());
            for (int i = 0; i < keyList.size(); i++) {
                String key = keyList.get(i);
                RestaurantItem value = menuItemsForSelectedMenuType.get(key);
                if (value != null && groupToOpen != null && value.getId() != null && value.getId().equalsIgnoreCase(groupToOpen)) {
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

    String groupToOpen = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = getActivity();
        String groupMenuId = null;
        menuTypeAdminDao = new MenuTypeAdminFireStoreDao();
        menuGroupAdminDao = new MenuGroupAdminFireStoreDao();
        menuItemAdminDao = new MenuItemAdminFireStoreDao();
        if (activity.getIntent().getExtras() != null) {
            groupToOpen = activity.getIntent().getStringExtra("groupToOpen");
            groupMenuId = activity.getIntent().getStringExtra("groupMenuId");
        }
        rootView = inflater.inflate(R.layout.fragment_menu_list, null);
        addMenuToPlateButton("0");
        setMenuTypeSpinner(groupMenuId, inflater);
        return rootView;
    }


    private void setMenuTypeSpinner(final String menuTypeId, final LayoutInflater inflater) {
        final Spinner menuTypeSpinner = rootView.findViewById(R.id.menuTypeSpinner);
        if (LoginController.getInstance().isReviewAdminLoggedIn() || (LoginController.getInstance().isAdminLoggedIn() && menuTypeId != null)) {
            menuTypeSpinner.setVisibility(View.VISIBLE);
            //setSpinner(inflater, menuTypeId);
            menuTypeAdminDao.getAllMenuTypes(new OnResultListener<List<MenuType>>() {
                @Override
                public void onCallback(List<MenuType> menuTypes) {
                    menuTypeNames.add("Please select");
                    for (MenuType type : menuTypes) {
                        menuTypeNameTomenuType.put(type.getName(), type);
                        menuTypeIdTomenuType.put(type.getId(), type);
                        menuTypeNames.add(type.getName());
                    }
                    spinnerArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, menuTypeNames);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    menuTypeSpinner.setAdapter(spinnerArrayAdapter);
                    menuTypeAdminDao.getMenuType(menuTypeId + "", new OnResultListener<MenuType>() {
                        @Override
                        public void onCallback(MenuType menuType) {
                            setSpinner(inflater, menuType.getId());
                            if (menuType != null) {
                                int spinnerPosition = spinnerArrayAdapter.getPosition(menuType.getName());
                                menuTypeSpinner.setSelection(spinnerPosition);
                            } else {
                                menuTypeSpinner.setSelection(0);
                            }
                        }
                    });


                }
            });


        } else {
            menuTypeSpinner.setVisibility(View.GONE);
            loadMenuItems(menuTypeId, inflater);
//            setHeading(selectedMenuTypeId);

        }
    }

    private void clearSearchView() {
        SearchView search = rootView.findViewById(R.id.searchMenu);
        search.setQuery("", true);
        search.clearFocus();
    }

    private void setSpinner(final LayoutInflater inflater, final String menuTypeId) {

        final Spinner menuTypeSpinner = rootView.findViewById(R.id.menuTypeSpinner);


        menuTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                ImageButton groupAddButton = rootView.findViewById(R.id.addMenuGroupButton);
                if (position == 0) {
                    setHeading("-1");
                    groupAddButton.setVisibility(View.INVISIBLE);
                    headerMap = new HashMap<>();
                    data = new HashMap<>();
                    menuItemsForSelectedMenuType = new HashMap<>();
                    setAdapter(inflater, null);
                    addMenuToPlateButton("0");
                } else {
                    groupAddButton.setVisibility(View.VISIBLE);
                    String selectedMenuTypeId = menuTypeNameTomenuType.get(spinnerArrayAdapter.getItem(position)).getId();
                    loadMenuItems(selectedMenuTypeId, inflater);
                    setHeading(selectedMenuTypeId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }


    private void setHeading(String groupMenuId) {
        TextView menuTypeHeading = rootView.findViewById(R.id.menuTypeHeading);
        if (LoginController.getInstance().isAdminLoggedIn()) {
            if (groupMenuId != null) {
                setHeadingForAdmin(groupMenuId, menuTypeHeading);
            }
        } else {
            menuTypeHeading.setVisibility(View.GONE);
        }
    }

    private void setHeadingForAdmin(String menuTypeId, TextView menuTypeHeading) {

        String heading;

        if (menuTypeId.equalsIgnoreCase("-1")) {
            heading = "Please select a menu type from the above dropdown to add a group.";
        } else {
            String menuTypeName = menuTypeIdTomenuType.get(menuTypeId).getName();

            if (data == null || data.size() == 0) {
                heading = "Please click + to add a group to " + menuTypeName;
            } else {
                heading = "Groups in " + menuTypeName;
            }
        }
        menuTypeHeading.setText(heading);
        menuTypeHeading.setVisibility(View.VISIBLE);
    }

    private void setHeadingForWaiter() {
        if (LoginController.getInstance().isReviewAdminLoggedIn()) {

            TextView menuTypeHeading = rootView.findViewById(R.id.menuTypeHeading);
            if (data == null || data.size() == 0) {
                menuTypeHeading.setText("Please select a different Menu from the above dropdown.");
                menuTypeHeading.setVisibility(View.VISIBLE);
            } else {
                menuTypeHeading.setVisibility(View.GONE);
            }
        }
    }

    private void setAdapter(LayoutInflater inflater, String menuTypeId) {
        MenuType menuType = menuTypeIdTomenuType.get(menuTypeId);
        elv =
                rootView.findViewById(R.id.list);
        listAdapter = new MenuExpandableListAdapter
                (getActivity(), inflater, headerMap, dataCollection, menuType);
        elv.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
        addMenuToPlateButton(menuTypeId);
    }

    private void setSearchView() {
        Activity activity = getActivity();
        SearchManager searchManager = (SearchManager) activity.getSystemService(MyApplication.getAppContext().SEARCH_SERVICE);
        SearchView search = rootView.findViewById(R.id.searchMenu);
        search.setSearchableInfo(searchManager.getSearchableInfo(activity.getComponentName()));
        search.setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setOnCloseListener(this);
    }

    private boolean onChildClickAction(ExpandableListView expandableList,
                                       int groupPosition, int childPosition) {
        lastChildClicked = childPosition;
        lastGroupClicked = groupPosition;

        int index = groupPosition;
        if (childPosition >= 0) {
            index = expandableList.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));

        }
        expandableList.setItemChecked(index, true);
        RestaurantItem item = listAdapter.getChildMenuItem(groupPosition, childPosition);
        try {
            ((OnMenuItemSelectedListener) getActivity()).onRestaurantItemClicked(item);
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
