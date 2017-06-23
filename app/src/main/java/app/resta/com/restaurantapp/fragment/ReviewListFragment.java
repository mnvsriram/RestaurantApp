package app.resta.com.restaurantapp.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.ReviewItemsExpandableListAdapter;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class ReviewListFragment extends Fragment {

    private View rootView;
    private List<RestaurantItem> childList;
    private ReviewItemsExpandableListAdapter listAdapter;
    private ExpandableListView elv;

    private Map<String, List<RestaurantItem>> dataCollection;
    private List<String> headerItems;

    public void setHeaderItems(List<String> headerItems) {
        this.headerItems = headerItems;
    }

    public void setDataCollection(Map<String, List<RestaurantItem>> dataCollection) {
        this.dataCollection = dataCollection;
    }

    public ReviewListFragment() {
    }


    private void loadChild(RestaurantItem[] childModels) {
        childList = new ArrayList<RestaurantItem>();
        for (RestaurantItem model : childModels)
            childList.add(model);
    }


    public static interface OnReviewMenuItemSelectedListener {
        public void onRestaurantItemClicked(RestaurantItem item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Activity activity = getActivity();

        rootView = inflater.inflate(R.layout.fragment_review_item_list, null);
        elv =
                (ExpandableListView) rootView.findViewById(R.id.reviewList);


        elv.setGroupIndicator(null);
        elv.setChildIndicator(null);
        elv.setChildDivider(getResources().getDrawable(R.color.white));
        elv.setDivider(getResources().getDrawable(R.color.black));
        elv.setDividerHeight(2);


//        elv.setTranscriptMode(ExpandableListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        elv.setStackFromBottom(true);

        listAdapter = new ReviewItemsExpandableListAdapter(getActivity(), inflater, dataCollection, headerItems);


        elv.setAdapter(listAdapter);


        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (elv.isGroupExpanded(groupPosition)) {
                    elv.expandGroup(groupPosition);
                } else {
                    elv.expandGroup(groupPosition);
                }
                return true;
            }
        });


        expandAll();

        return rootView;
    }

    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            elv.expandGroup(i);
        }
    }


}
