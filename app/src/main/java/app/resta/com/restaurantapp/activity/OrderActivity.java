package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.fragment.OrderListFragment;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewForOrder;

public class OrderActivity extends BaseActivity implements OrderListFragment.OnReviewMenuItemSelectedListener {

    OrderListFragment frag = new OrderListFragment();

    private Map<String, List<OrderedItem>> dataCollection;
    private List<String> headerItems;
    private OrderItemDao orderDao;

    private void addItemToReview(RestaurantItem item) {
        List<OrderedItem> existingItems = dataCollection.get(item.getParentItem().getName());
        if (existingItems == null) {
            existingItems = new ArrayList<>();
        }
        OrderedItem orderedItem = new OrderedItem(item);

        existingItems.add(orderedItem);

        dataCollection.put(item.getParentItem().getName(), existingItems);
        headerItems = new ArrayList<>();
        headerItems.addAll(dataCollection.keySet());
    }


    private void removeItemFromReview(OrderedItem item) {
        List<OrderedItem> existingItems = dataCollection.get(item.getParentName());
        if (existingItems != null) {
            existingItems.remove(item);
        }
        dataCollection.put(item.getParentName(), existingItems);
        headerItems = new ArrayList<>();
        headerItems.addAll(dataCollection.keySet());
    }


    public void increaseQuantity(View view) {
        OrderedItem orderedItem = (OrderedItem) view.getTag();
        orderedItem.increaseQuantity();
        refreshList();
    }

    public void addInstructions(View view) {
        final OrderedItem orderedItem = (OrderedItem) view.getTag();

        LayoutInflater li = LayoutInflater.from(this);
        final View instructionsView = li.inflate(R.layout.order_item_instructions_dialog, null);

        final EditText instructions = (EditText) instructionsView.findViewById(R.id.orderItemInstructions);
        instructions.setText(orderedItem.getInstructions());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(instructionsView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String adminUserName = instructions.getText().toString();
                                orderedItem.setInstructions(adminUserName);

                                refreshList();
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(instructions.getWindowToken(), 0);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void reduceQuantity(View view) {
        OrderedItem orderedItem = (OrderedItem) view.getTag();
        orderedItem.reduceQuantity();
        if (orderedItem.getQuantity() == 0) {
            removeItemFromReview(orderedItem);
        }
        refreshList();
    }

    @Override
    public void onRestaurantItemClicked(RestaurantItem item) {
        addItemToReview(item);
        refreshList();
    }

    private void refreshList() {
        OrderedItem orderSummary = getOrderSummary();

        TextView totalQuantity =
                (TextView) findViewById(R.id.totalquantity);
        TextView totalPrice =
                (TextView) findViewById(R.id.totalPrice);

        RelativeLayout summaryRow = (RelativeLayout) findViewById(R.id.summaryRow);

        totalQuantity.setText(orderSummary.getQuantity() + "");
        totalPrice.setText(orderSummary.getTotalPrice() + "");

        if (dataCollection.size() == 0) {
            summaryRow.setVisibility(View.GONE);
        } else {
            summaryRow.setVisibility(View.VISIBLE);
        }


        OrderListFragment frag = new OrderListFragment();
        frag.setDataCollection(dataCollection);
        frag.setHeaderItems(headerItems);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.review_fragment_container, frag);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }


    private OrderedItem getOrderSummary() {
        int totalQuantity = 0;
        double price = 0;
        for (String parent : dataCollection.keySet()) {
            List<OrderedItem> items = dataCollection.get(parent);
            for (OrderedItem item : items) {
                try {
                    double priceForSingleUnit = item.getPrice();
                    double subTotal = item.getQuantity() * priceForSingleUnit;
                    price += subTotal;
                } catch (NumberFormatException nfe) {
                    // convert the price variable to double in restaurantitem class and remove this try catch block
                }
                totalQuantity += item.getQuantity();
            }
        }
        OrderedItem orderSummary = new OrderedItem();
        orderSummary.setQuantity(totalQuantity);
        orderSummary.setTotalPrice(price);
        return orderSummary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_menu);
        orderDao = new OrderItemDao();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        reset();
        RelativeLayout summaryRow = (RelativeLayout) findViewById(R.id.summaryRow);

        Intent intent = getIntent();
        List<OrderedItem> orderedItems = null;
        if (intent.hasExtra("orderActivity_orderItems")) {
            orderedItems = (ArrayList<OrderedItem>) intent.getSerializableExtra("orderActivity_orderItems");
        }

        if (dataCollection.size() == 0) {
            summaryRow.setVisibility(View.GONE);
        } else {
            summaryRow.setVisibility(View.VISIBLE);
        }

        if (orderedItems != null) {
            Map<Long, RestaurantItem> itemsByIdMap = MenuItemDao.getAllItemsById();
            for (OrderedItem item : orderedItems) {
                addItemToReview(itemsByIdMap.get(item.getItemId()));
            }
            refreshList();
        }


    }

    @Override
    public void onBackPressed() {
        authenticationController.goToHomePage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void createOrder(View view) {
        List<OrderedItem> orderedItems = getOrderedItems();
        placeOrder(orderedItems);
        reset();
    }

    private long placeOrder(List<OrderedItem> orderedItems) {
        return orderDao.placeOrder(orderedItems);
    }

    private List<OrderedItem> getOrderedItems() {
        List<OrderedItem> orderedItems = new ArrayList<>();
        for (String group : dataCollection.keySet()) {
            List<OrderedItem> items = dataCollection.get(group);
            orderedItems.addAll(items);
        }
        return orderedItems;
    }


    public void startReview(View view) {
        List<OrderedItem> items = getOrderedItems();
        long orderId = placeOrder(items);
        ReviewForOrder reviewForOrder = new ReviewForOrder(items, orderId);
        Intent intent = new Intent(this, SubmitReviewActivity.class);
        intent.putExtra("ordered_items", reviewForOrder);
        intent.putExtra("orderId", orderId);
        startActivity(intent);
    }


    private void reset() {
        dataCollection = new LinkedHashMap<>();
        headerItems = new ArrayList<>();
        refreshList();
    }

    public void showOrderSummaryForReviewer(View view) {
        authenticationController.goToOrderSummaryPage();
    }
}
