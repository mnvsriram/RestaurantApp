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
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.model.MenuType;
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
    private MenuTypeDao menuTypeDao;
    private Map<Long, RestaurantItem> menuItemsForSelectedMenuType;
    private long groupMenuId = 0;
    
    public void setGroupMenuId(long groupMenuId){
    this.groupMenuId = groupMenuId;
    }
    public long getGroupMenuId(){
        return groupMenuId;
    }
    public static interface OnMenuItemSelectedListener {
        public void onRestaurantItemClicked(int groupPosition, int childPosition);
    }

    public static interface OnMenuTypeChanged {
        public void onMenuTypeChanged(long groupMenuId);
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
            if (groupMenuId == -1) {
                groupAddButton.setOnClickListener(listAdapter.itemEditListener);
            } else {
                groupAddButton.setOnClickListener(groupAddListener);
                groupAddButton.setTag(R.string.tag_group_menu_id, groupMenuId);
            }
        } else {
            groupAddButton.setVisibility(View.GONE);
        }
    }


    private void addMenuToPlateButton(long selectedMenuTypeId) {
        MenuType selectedMenuType = menuTypeDao.getMenuGroupsById().get(selectedMenuTypeId);

        ImageButton addMenuToPlateButton = (ImageButton) rootView.findViewById(R.id.addMenuToPlateButton);
        if (LoginController.getInstance().isReviewAdminLoggedIn() && selectedMenuType != null && selectedMenuType.getShowPriceOfChildren() != null && selectedMenuType.getShowPriceOfChildren().equals("N")) {
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

    private void loadMenuItems(long groupMenuId, LayoutInflater inflater) {
        headerMap = new HashMap<>();
        data = new HashMap<>();
        menuItemsForSelectedMenuType = new HashMap<>();
        menuItemsForSelectedMenuType = menuItemDao.fetchMenuItems(groupMenuId);
        for (RestaurantItem parent : menuItemsForSelectedMenuType.values()) {
            headerMap.put(parent.getName(), parent);
            data.put(parent.getName(), parent.getChildItems());
        }
        createCollection();
        setAdapter(inflater);
        //listAdapter.notifyDataSetChanged();
    }

    private void expandGroupOnLoad(long groupToOpen) {
        if (groupToOpen == 0) {
            elv.expandGroup(0);
        } else {
            List<Long> keyList = new ArrayList<Long>(menuItemsForSelectedMenuType.keySet());
            for (int i = 0; i < keyList.size(); i++) {
                Long key = keyList.get(i);
                RestaurantItem value = menuItemsForSelectedMenuType.get(key);
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
        
        menuItemDao = new MenuItemDao();
        menuTypeDao = new MenuTypeDao();
        if (activity.getIntent().getExtras() != null) {
            groupToOpen = activity.getIntent().getLongExtra("groupToOpen", 0l);
            modifiedItemId = activity.getIntent().getLongExtra("modifiedItemId", -1);
            groupPosition = activity.getIntent().getIntExtra("modifiedItemGroupPosition", 0);
            childPosition = activity.getIntent().getIntExtra("modifiedItemChildPosition", 0);
            if(groupMenuId<=0){
                groupMenuId = activity.getIntent().getLongExtra("groupMenuId", 0);
            }
                
            
        }
        rootView = inflater.inflate(R.layout.fragment_menu_list, null);
        addMenuToPlateButton(0);
        loadMenuItems(groupMenuId, inflater);
        setMenuTypeSpinner(groupMenuId, inflater);
        setHeading(groupMenuId);
        //setAdapter();
        addGroupAddButton(groupMenuId);
        expandGroupOnLoad(groupToOpen);
        selectAnItemOnLoad(modifiedItemId, groupPosition, childPosition);
        setOnGroupClickListener();
        setOnChildClickListener();
        setSearchView(activity);
        return rootView;
    }


    private void setMenuTypeSpinner(final long groupMenuId, final LayoutInflater inflater) {
        Spinner menuTypeSpinner = (Spinner) rootView.findViewById(R.id.menuTypeSpinner);
        if (LoginController.getInstance().isReviewAdminLoggedIn() || (LoginController.getInstance().isAdminLoggedIn() && groupMenuId != -1)) {
            setSpinner(inflater);
            MenuType menuType = menuTypeDao.getMenuGroupsById().get(groupMenuId);
            if (menuType != null) {
                int spinnerPosition = spinnerArrayAdapter.getPosition(menuType.getName());
                menuTypeSpinner.setSelection(spinnerPosition);
            } else {
                menuTypeSpinner.setSelection(0);
            }
            menuTypeSpinner.setVisibility(View.VISIBLE);
        } else {
            menuTypeSpinner.setVisibility(View.GONE);
        }
    }

    private void clearSearchView() {
        SearchView search = (SearchView) rootView.findViewById(R.id.searchMenu);
        search.setQuery("", true);
        search.clearFocus();
    }

    ArrayAdapter<String> spinnerArrayAdapter = null;

    private void setSpinner(final LayoutInflater inflater) {
        Spinner menuTypeSpinner = (Spinner) rootView.findViewById(R.id.menuTypeSpinner);
        List<String> menuTypes = new ArrayList<>(menuTypeDao.getMenuGroupsByName().keySet());

        spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, menuTypes);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuTypeSpinner.setAdapter(spinnerArrayAdapter);
        menuTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                long selectedMenuTypeId = menuTypeDao.getMenuGroupsByName().get(spinnerArrayAdapter.getItem(position));
                loadMenuItems(selectedMenuTypeId, inflater);
                setHeadingForWaiter();
                clearSearchView();
                addMenuToPlateButton(selectedMenuTypeId);
                try {
                    ((OnMenuTypeChanged) getActivity()).onMenuTypeChanged(selectedMenuTypeId);
                } catch (ClassCastException cce) {

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
        if (spinnerArrayAdapter.getCount() > 0) {
            menuTypeSpinner.setSelection(0);
        }
    }


    private void setHeading(long groupMenuId) {
        TextView menuTypeHeading = (TextView) rootView.findViewById(R.id.menuTypeHeading);
        if (LoginController.getInstance().isAdminLoggedIn()) {
            if (groupMenuId > 0) {
                setHeadingForAdmin(groupMenuId, menuTypeHeading);
            }
        } else {
            menuTypeHeading.setVisibility(View.GONE);
        }
    }

    private void setHeadingForAdmin(long groupMenuId, TextView menuTypeHeading) {
        String menuTypeName = menuTypeDao.getMenuGroupsById().get(groupMenuId).getName();
        String heading = "";

        if (data == null || data.size() == 0) {
            heading = "Please click + to add a group to " + menuTypeName;
        } else {
            heading = "Groups in " + menuTypeName;
        }
        menuTypeHeading.setText(heading);
        menuTypeHeading.setVisibility(View.VISIBLE);

    }

    private void setHeadingForWaiter() {
        TextView menuTypeHeading = (TextView) rootView.findViewById(R.id.menuTypeHeading);
        if (data == null || data.size() == 0) {
            menuTypeHeading.setText("Please select a different Menu from the above dropdown.");
            menuTypeHeading.setVisibility(View.VISIBLE);
        } else {
            menuTypeHeading.setVisibility(View.GONE);
        }
    }

    private void setAdapter(LayoutInflater inflater) {
        elv =
                (ExpandableListView) rootView.findViewById(R.id.list);
        listAdapter = new MenuExpandableListAdapter
                (getActivity(), inflater, headerMap, dataCollection);
        elv.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
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
