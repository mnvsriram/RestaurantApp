package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.MenuItemDao;
import app.resta.com.restaurantapp.db.dao.MenuTypeDao;
import app.resta.com.restaurantapp.db.dao.OrderItemDao;
import app.resta.com.restaurantapp.fragment.OrderListFragment;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewForOrder;

public class OrderActivity extends BaseActivity implements OrderListFragment.OnReviewMenuItemSelectedListener {

    private Map<String, List<OrderedItem>> dataCollection;
    private List<String> headerItems;
    private OrderItemDao orderDao;
    long orderId = 0;
    private MenuTypeDao menuTypeDao;
    AuthenticationController authenticationController;

    private void addItemToReview(RestaurantItem restaurantItem, OrderedItem item) {
        int setMenuGroup = 0;
        String mapKey = restaurantItem.getMenuTypeName();

        if (item != null) {
            setMenuGroup = item.getSetMenuGroup();
            mapKey = menuTypeDao.getMenuGroupsById().get(item.getMenuTypeId()).getName();
        }

        if (setMenuGroup <= 0) {
            setMenuGroup = restaurantItem.getSetMenuGroup();
        }

        if (setMenuGroup > 0) {
            mapKey = mapKey + "-" + setMenuGroup;
        }
        List<OrderedItem> existingItems = dataCollection.get(mapKey);
        if (existingItems == null) {
            existingItems = new ArrayList<>();
        }
        if (item == null) {
            item = new OrderedItem(restaurantItem);
        }


        MenuType menuType = menuTypeDao.getMenuGroupsById().get(item.getMenuTypeId());
        if (menuType.getShowPriceOfChildren() != null && menuType.getShowPriceOfChildren().equals("N")) {
            item.setPrice(Double.parseDouble(menuType.getPrice()));
        }

        if (item != null) {
            item.setQuantity(item.getQuantity());
            item.setInstructions(item.getInstructions());
        }
        existingItems.add(item);
        restaurantItem.setSetMenuGroup(0);
        dataCollection.put(mapKey, existingItems);
        headerItems = new ArrayList<>();
        headerItems.addAll(dataCollection.keySet());
    }


    private void removeItemFromReview(OrderedItem item) {
        String menuTypeName = menuTypeDao.getMenuGroupsById().get(item.getMenuTypeId()).getName();
        if (item.getSetMenuGroup() > 0) {
            menuTypeName = menuTypeName + "-" + item.getSetMenuGroup();
        }

        List<OrderedItem> existingItems = dataCollection.get(menuTypeName);
        if (existingItems != null) {
            existingItems.remove(item);
        }
        if (existingItems == null || existingItems.size() == 0) {
            dataCollection.remove(menuTypeName);
        } else {
            dataCollection.put(menuTypeName, existingItems);
        }

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
    public void onRestaurantItemClicked(RestaurantItem restaurantItem) {
        addItemToReview(restaurantItem, null);
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
        //ft.commit();
        ft.commitAllowingStateLoss();
        getFragmentManager().executePendingTransactions();
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
        menuTypeDao = new MenuTypeDao();
        setToolbar();
        reset();
        RelativeLayout summaryRow = (RelativeLayout) findViewById(R.id.summaryRow);

        Intent intent = getIntent();
        List<OrderedItem> orderedItems = null;
        String orderComment = "";
        if (intent.hasExtra("orderActivity_orderItems")) {
            orderedItems = (ArrayList<OrderedItem>) intent.getSerializableExtra("orderActivity_orderItems");
        }
        if (intent.hasExtra("orderActivity_orderId")) {
            orderId = intent.getLongExtra("orderActivity_orderId", 0);
        }
        if (intent.hasExtra("orderActivity_orderComment")) {
            orderComment = intent.getStringExtra("orderActivity_orderComment");
        }

        if (dataCollection.size() == 0) {
            summaryRow.setVisibility(View.GONE);
        } else {
            summaryRow.setVisibility(View.VISIBLE);
        }

        if (orderedItems != null) {
            Map<Long, RestaurantItem> itemsByIdMap = MenuItemDao.getAllItemsById();

            int numberOfSetMenu = 0;
            for (OrderedItem item : orderedItems) {
                if (item.getSetMenuGroup() > numberOfSetMenu) {
                    numberOfSetMenu = item.getSetMenuGroup();
                }
                addItemToReview(itemsByIdMap.get(item.getItemId()), item);
            }
            MenuExpandableListAdapter.setMenuCounter = numberOfSetMenu + 1;
            refreshList();
        }
        authenticationController = new AuthenticationController(this);
        modifyButtons();
        setComment(orderComment);
    }

    private void modifyButtons() {
        Button placeOrderButton = (Button) findViewById(R.id.placeOrderButton);
        Button placeOrderAndStartReviewButton = (Button) findViewById(R.id.placeOrderAndStartReviewButton);
        if (orderId > 0) {
            placeOrderButton.setText("Modify Order");
            placeOrderAndStartReviewButton.setText("Modify Order and Start Review");
        } else {
            placeOrderButton.setText("Place Order");
            placeOrderAndStartReviewButton.setText("Place Order and Start Review");
        }
    }


    private void setComment(String orderComment) {
        TextView tableNoOrNotes = (TextView) findViewById(R.id.tableNoOrNotesText);
        tableNoOrNotes.setText(orderComment);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToReviewerLaunchPage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
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
        if (items != null && items.size() > 0) {
            long createdOrderId = placeOrder(items);
            ReviewForOrder reviewForOrder = new ReviewForOrder(items, createdOrderId);
            Intent intent = new Intent(this, SubmitReviewActivity.class);
            intent.putExtra("ordered_items", reviewForOrder);
            intent.putExtra("orderId", createdOrderId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please select any item.", Toast.LENGTH_SHORT).show();
        }
    }


    private void reset() {
        dataCollection = new LinkedHashMap<>();
        headerItems = new ArrayList<>();
        orderId = 0;
        modifyButtons();
        clearComments();
        refreshList();
    }

    private void clearComments() {
        TextView tableNoOrNotes = (TextView) findViewById(R.id.tableNoOrNotesText);
        tableNoOrNotes.setText("");
    }

    public void showOrderSummaryForReviewer(View view) {
        authenticationController.goToOrderSummaryPage();
    }

    public void createOrder(View view) {
        List<OrderedItem> orderedItems = getOrderedItems();
        if (orderedItems != null && orderedItems.size() > 0) {
            placeOrder(orderedItems);
            reset();
        } else {
            Toast.makeText(this, "Please select any item.", Toast.LENGTH_SHORT).show();
        }
    }

    private long placeOrder(List<OrderedItem> orderedItems) {
        long orderCreatedId = 0;
        TextView tableNoOrNotes = (TextView) findViewById(R.id.tableNoOrNotesText);
        String comment = tableNoOrNotes.getText().toString();
        if (orderId > 0) {
            orderCreatedId = orderDao.modifyOrder(orderedItems, orderId, comment);
        } else {
            orderCreatedId = orderDao.placeOrder(orderedItems, comment);
        }
        MenuExpandableListAdapter.setMenuCounter = 1;
        return orderCreatedId;
    }
}
