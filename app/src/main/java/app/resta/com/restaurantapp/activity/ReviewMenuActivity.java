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
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.fragment.ReviewListFragment;
import app.resta.com.restaurantapp.model.OrderItem;
import app.resta.com.restaurantapp.model.RestaurantItem;

public class ReviewMenuActivity extends BaseActivity implements ReviewListFragment.OnReviewMenuItemSelectedListener {

    ReviewListFragment frag = new ReviewListFragment();

    private Map<String, List<OrderItem>> dataCollection;
    private List<String> headerItems;
    private OrderItemDao orderDao;

    private void addItemToReview(RestaurantItem item) {
        List<OrderItem> existingItems = dataCollection.get(item.getParentItem().getName());
        if (existingItems == null) {
            existingItems = new ArrayList<>();
        }
        OrderItem orderItem = new OrderItem(item);

        existingItems.add(orderItem);

        dataCollection.put(item.getParentItem().getName(), existingItems);
        headerItems = new ArrayList<>();
        headerItems.addAll(dataCollection.keySet());
    }


    private void removeItemFromReview(OrderItem item) {
        List<OrderItem> existingItems = dataCollection.get(item.getRestaurantItem().getParentItem().getName());
        if (existingItems != null) {
            existingItems.remove(item);
        }
        dataCollection.put(item.getRestaurantItem().getParentItem().getName(), existingItems);
        headerItems = new ArrayList<>();
        headerItems.addAll(dataCollection.keySet());
    }


    public void increaseQuantity(View view) {
        OrderItem orderItem = (OrderItem) view.getTag();
        orderItem.increaseQuantity();
        refreshList();
    }

    public void addInstructions(View view) {
        final OrderItem orderItem = (OrderItem) view.getTag();

        LayoutInflater li = LayoutInflater.from(this);
        final View instructionsView = li.inflate(R.layout.order_item_instructions_dialog, null);

        final EditText instructions = (EditText) instructionsView.findViewById(R.id.orderItemInstructions);
        instructions.setText(orderItem.getInstructions());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setView(instructionsView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String adminUserName = instructions.getText().toString();
                                orderItem.setInstructions(adminUserName);

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
        OrderItem orderItem = (OrderItem) view.getTag();
        orderItem.reduceQuantity();
        if (orderItem.getQuantity() == 0) {
            removeItemFromReview(orderItem);
        }
        refreshList();
    }

    @Override
    public void onRestaurantItemClicked(RestaurantItem item) {
        addItemToReview(item);
        refreshList();
    }

    private void refreshList() {
        OrderItem orderSummary = getOrderSummary();

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


        ReviewListFragment frag = new ReviewListFragment();
        frag.setDataCollection(dataCollection);
        frag.setHeaderItems(headerItems);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.review_fragment_container, frag);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }


    private OrderItem getOrderSummary() {
        int totalQuantity = 0;
        double price = 0;
        for (String parent : dataCollection.keySet()) {
            List<OrderItem> items = dataCollection.get(parent);
            for (OrderItem item : items) {
                try {
                    double priceForSingleUnit = Double.parseDouble(item.getRestaurantItem().getPrice());
                    double subTotal = item.getQuantity() * priceForSingleUnit;
                    price += subTotal;
                } catch (NumberFormatException nfe) {
                    // convert the price variable to double in restaurantitem class and remove this try catch block
                }
                totalQuantity += item.getQuantity();
            }
        }
        OrderItem orderSummary = new OrderItem();
        orderSummary.setQuantity(totalQuantity);
        orderSummary.setTotalPrice(price);
        return orderSummary;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_menu);
        orderDao = new OrderItemDao();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        reset();
        RelativeLayout summaryRow = (RelativeLayout) findViewById(R.id.summaryRow);

        if (dataCollection.size() == 0) {
            summaryRow.setVisibility(View.GONE);
        } else {
            summaryRow.setVisibility(View.VISIBLE);
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
        List<OrderItem> orderedItems = getOrderedItems();
        placeOrder(orderedItems);
        reset();
    }

    private long placeOrder(List<OrderItem> orderedItems) {
        return orderDao.placeOrder(orderedItems);
    }

    private List<OrderItem> getOrderedItems() {
        List<OrderItem> orderedItems = new ArrayList<>();
        for (String group : dataCollection.keySet()) {
            List<OrderItem> items = dataCollection.get(group);
            orderedItems.addAll(items);
        }
        return orderedItems;
    }


    private Set<RestaurantItem> getOrderedDishes(List<OrderItem> items) {
        Set<RestaurantItem> dishes = new HashSet<>();
        for (OrderItem item : items) {
            dishes.add(item.getRestaurantItem());
        }
        return dishes;
    }

    public void startReview(View view) {
        List<OrderItem> items = getOrderedItems();
        Set<RestaurantItem> dishes = getOrderedDishes(items);

        long orderId = placeOrder(items);

        Intent intent = new Intent(this, SubmitReviewActivity.class);
        startActivity(intent);

    }


    private void reset() {
        dataCollection = new LinkedHashMap<>();
        headerItems = new ArrayList<>();
        refreshList();
    }
}
