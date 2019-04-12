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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.MenuExpandableListAdapter;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.admin.order.OrderAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.order.OrderAdminFirestoreDao;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuItem.MenuItemUserFireStoreDao;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserDaoI;
import app.resta.com.restaurantapp.db.dao.user.menuType.MenuTypeUserFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.fragment.OrderListFragment;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.Order;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.model.ReviewForOrder;

public class OrderActivity extends BaseActivity implements OrderListFragment.OnReviewMenuItemSelectedListener {
    private String TAG = "OrderActivity";
    private Map<String, List<OrderedItem>> dataCollection;
    private List<String> headerItems;
    private OrderAdminDaoI orderAdminDao;
    String orderId = null;
    private MenuTypeUserDaoI menuTypeUserDao = new MenuTypeUserFireStoreDao();
    private MenuItemUserDaoI menuItemUserDao = new MenuItemUserFireStoreDao();
    AuthenticationController authenticationController;

    private void addItemToReview(final RestaurantItem restaurantItem, final OrderedItem item) {
        if (item != null) {
            menuTypeUserDao.getMenuType_u(item.getMenuTypeId(), new OnResultListener<MenuType>() {

                @Override
                public void onCallback(MenuType menuType) {
                    String keyForMap = menuType.getName();
                    int groupForSetMenu = item.getSetMenuGroup();

                    if (groupForSetMenu <= 0) {
                        groupForSetMenu = restaurantItem.getSetMenuGroup();
                    }

                    if (groupForSetMenu > 0) {
                        keyForMap = keyForMap + "-" + groupForSetMenu;
                    }
                    List<OrderedItem> existingItems = dataCollection.get(keyForMap);
                    if (existingItems == null) {
                        existingItems = new ArrayList<>();
                    }

                    OrderedItem i = item;
                    if (item == null) {
                        i = new OrderedItem(restaurantItem);
                    }

                    if (!menuType.isShowPriceOfChildren()) {
                        i.setPrice(Double.parseDouble(menuType.getPrice()));
                    }

                    if (i != null) {
                        i.setQuantity(i.getQuantity());
                        i.setInstructions(i.getInstructions());
                    }
                    existingItems.add(i);
                    restaurantItem.setSetMenuGroup(0);
                    dataCollection.put(keyForMap, existingItems);
                    headerItems = new ArrayList<>();
                    headerItems.addAll(dataCollection.keySet());

                }
            });
        }
    }


    private void removeItemFromReview(final OrderedItem item) {

        menuTypeUserDao.getMenuType_u(item.getMenuTypeId(), new OnResultListener<MenuType>() {

            @Override
            public void onCallback(MenuType menuType) {
                String menuTypeName = menuType.getName();
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
        });

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

        final EditText instructions = instructionsView.findViewById(R.id.orderItemInstructions);
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
                findViewById(R.id.totalquantity);
        TextView totalPrice =
                findViewById(R.id.totalPrice);

        RelativeLayout summaryRow = findViewById(R.id.summaryRow);

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
        orderAdminDao = new OrderAdminFirestoreDao();
        setToolbar();
        reset();
        RelativeLayout summaryRow = findViewById(R.id.summaryRow);

        Intent intent = getIntent();
        List<OrderedItem> orderedItems = null;
        String orderComment = "";
        if (intent.hasExtra("orderActivity_orderItems")) {
            orderedItems = (ArrayList<OrderedItem>) intent.getSerializableExtra("orderActivity_orderItems");
        }
        if (intent.hasExtra("orderActivity_orderId")) {
            orderId = intent.getStringExtra("orderActivity_orderId");
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
            final List<OrderedItem> itemOrdered = (ArrayList<OrderedItem>) intent.getSerializableExtra("orderActivity_orderItems");

            menuItemUserDao.getAllItems_u(new OnResultListener<List<RestaurantItem>>() {
                @Override
                public void onCallback(List<RestaurantItem> items) {
                    Map<String, RestaurantItem> itemsByIdMap = new HashMap<>();

                    for (RestaurantItem item : items) {
                        itemsByIdMap.put(item.getId(), item);
                    }

                    int numberOfSetMenu = 0;
                    for (OrderedItem item : itemOrdered) {
                        if (item.getSetMenuGroup() > numberOfSetMenu) {
                            numberOfSetMenu = item.getSetMenuGroup();
                        }
                        addItemToReview(itemsByIdMap.get(item.getItemId()), item);
                    }
                    MenuExpandableListAdapter.setMenuCounter = numberOfSetMenu + 1;
                    refreshList();

                }
            });

        }
        authenticationController = new AuthenticationController(this);
        modifyButtons();
        setComment(orderComment);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void modifyButtons() {
        Button placeOrderButton = findViewById(R.id.placeOrderButton);
        Button placeOrderAndStartReviewButton = findViewById(R.id.placeOrderAndStartReviewButton);
        if (orderId != null) {
            placeOrderButton.setText("Modify Order");
            placeOrderAndStartReviewButton.setText("Modify Order and Start Review");
        } else {
            placeOrderButton.setText("Place Order");
            placeOrderAndStartReviewButton.setText("Place Order and Start Review");
        }
    }


    private void setComment(String orderComment) {
        TextView tableNoOrNotes = findViewById(R.id.tableNoOrNotesText);
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
        final List<OrderedItem> items = getOrderedItems();
        if (items != null && items.size() > 0) {
            placeOrder(items, new OnResultListener<Order>() {
                @Override
                public void onCallback(Order order) {
                    ReviewForOrder reviewForOrder = new ReviewForOrder(items, order.getOrderId());
                    Intent intent = new Intent(OrderActivity.this, SubmitReviewActivity.class);
                    intent.putExtra("ordered_items", reviewForOrder);
                    intent.putExtra("orderId", order.getOrderId());
                    startActivity(intent);
                }
            });

        } else {
            Toast.makeText(this, "Please select any item.", Toast.LENGTH_SHORT).show();
        }
    }


    private void reset() {
        dataCollection = new LinkedHashMap<>();
        headerItems = new ArrayList<>();
        orderId = null;
        modifyButtons();
        clearComments();
        refreshList();
    }

    private void clearComments() {
        TextView tableNoOrNotes = findViewById(R.id.tableNoOrNotesText);
        tableNoOrNotes.setText("");
    }

    public void showOrderSummaryForReviewer(View view) {
        authenticationController.goToOrderSummaryPage();
    }

    public void createOrder(View view) {
        List<OrderedItem> orderedItems = getOrderedItems();
        if (orderedItems != null && orderedItems.size() > 0) {
            placeOrder(orderedItems, new OnResultListener<Order>() {
                @Override
                public void onCallback(Order order) {

                }
            });
        } else {
            Toast.makeText(this, "Please select any item.", Toast.LENGTH_SHORT).show();
        }
    }

    private void placeOrder(List<OrderedItem> orderedItems, final OnResultListener<Order> listener) {
        TextView tableNoOrNotes = findViewById(R.id.tableNoOrNotesText);
        String comment = tableNoOrNotes.getText().toString();
        if (orderId != null) {
            orderAdminDao.modifyOrder(orderedItems, orderId, comment, comment, new OnResultListener<Order>() {
                @Override
                public void onCallback(Order order) {
                    reset();
                    MenuExpandableListAdapter.setMenuCounter = 1;
                    listener.onCallback(order);
                }
            });
        } else {
            orderAdminDao.placeOrder(orderedItems, comment, comment, new OnResultListener<Order>() {
                @Override
                public void onCallback(Order order) {
                    reset();
                    MenuExpandableListAdapter.setMenuCounter = 1;
                    listener.onCallback(order);
                }
            });
        }
    }
}
