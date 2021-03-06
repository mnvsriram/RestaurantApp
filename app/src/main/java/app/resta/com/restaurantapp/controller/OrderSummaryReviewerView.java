package app.resta.com.restaurantapp.controller;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.activity.SubmitReviewActivity;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.Order;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.ReviewForOrder;
import app.resta.com.restaurantapp.util.ComparatorUtils;
import app.resta.com.restaurantapp.util.DateUtil;

/**
 * Created by Sriram on 04/07/2017.
 */
public class OrderSummaryReviewerView extends OrderSummaryView {

    private MenuTypeAdminDaoI menuTypeAdminDao = new MenuTypeAdminFireStoreDao();

    public OrderSummaryReviewerView(Activity activity) {
        super(activity);
    }

    private final static int MAX_LENGTH_PER_LINE = 25;
    final Map<String, MenuType> menuTypeMap = new HashMap<>();

    public void fetchMenuTypesAndCreateTable(final Map<String, Order> orders) {
        menuTypeAdminDao.getAllMenuTypes(new OnResultListener<List<MenuType>>() {
            @Override
            public void onCallback(List<MenuType> menuTypes) {
                for (MenuType menuType : menuTypes) {
                    menuTypeMap.put(menuType.getId(), menuType);
                }
                createTable(orders);
            }
        });
    }


    private void createTable(Map<String, Order> ordersUnsorted) {
        Map<String, Order> orders = ComparatorUtils.sortByValuesInDescendingOrder(ordersUnsorted);
        TableLayout tl = getActivity().findViewById(R.id.ordersTable);
        tl.removeAllViews();
        TableRow headerRow = getHeaderRowForAdmin();
        tl.addView(headerRow, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        List<OrderedItem> orderedItems = null;
        for (Map.Entry<String, Order> entry : orders.entrySet()) {
            Order order = entry.getValue();
            String orderId = entry.getKey();
            orderedItems = order.getItems();
            String date = DateUtil.getDateString(order.getOrderCreatedAt(), "MMM-dd HH:mm");
            String orderComment = order.getTableNumber();
            String orderStatus = "";
            List<OrderedItem> items = order.getItems();
            String itemNames = "";
            Set<String> uniqueItemNamesWithSetMenus = new HashSet<>();
            String instruction = "";
            int oldValueItemsLength = 0;
            int oldValueInstructionsLength = 0;


            if (items != null) {
                if (items.size() > 20) {
                    items = items.subList(0, 20);
                }
                for (OrderedItem orderedItem : items) {
                    orderComment = orderedItem.getOrderComment();
                    orderStatus = orderedItem.getOrderStatus();
                    String itemName = orderedItem.getItemName();
                    if (orderedItem.getSetMenuGroup() > 0) {
                        itemName = menuTypeMap.get(orderedItem.getMenuTypeId()).getName() + "-" + orderedItem.getSetMenuGroup();
                    }

                    if (!uniqueItemNamesWithSetMenus.contains(itemName)) {
                        itemNames += itemName + ",";
                        uniqueItemNamesWithSetMenus.add(itemName);
                    }


                    if (orderedItem.getInstructions() != null && orderedItem.getInstructions().length() > 0) {
                        String instructionWithName = orderedItem.getItemName() + "-" + orderedItem.getInstructions();
                        if (instructionWithName.length() > MAX_LENGTH_PER_LINE) {
                            instructionWithName = instructionWithName.replaceAll("(.{" + MAX_LENGTH_PER_LINE + "})", "$1\n");
                        }
                        instruction += instructionWithName + ";";
                    }

                    int newValueInstructionsLength = instruction.length() % MAX_LENGTH_PER_LINE;
                    int newValueItemsLength = itemNames.length() % MAX_LENGTH_PER_LINE;
                    if (oldValueItemsLength > newValueItemsLength) {
                        itemNames += "\n";
                    }
                    if (oldValueInstructionsLength > newValueInstructionsLength) {
                        instruction += "\n";
                    }

                    oldValueItemsLength = itemNames.length() % MAX_LENGTH_PER_LINE;
                    oldValueInstructionsLength = instruction.length() % MAX_LENGTH_PER_LINE;
                }
            }
            TableRow tr = getRow(date, orderId, orderedItems, orderComment, itemNames, instruction, orderStatus);
            tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private TableRow getRow(String date, String orderId, List<OrderedItem> orderItems, String orderTable, String itemNames, String instructions, String orderStatus) {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_last_bg);
        tr.setPadding(5, 5, 5, 5);
        TextView dateCol = getColumnTextView(date, true, false);
        TextView comment = getColumnTextView(orderTable, false, false);
        TextView itemNamesTextView = getColumnTextView(itemNames, false, false);
        TextView instructionsView = getColumnTextView(instructions, false, false);
        View startReviewButton = getReviewView(orderId, orderStatus, orderItems);
        Button fullDetailsButton = getFullDetailsButton(date,orderItems, orderStatus, null);

        tr.addView(dateCol);
        tr.addView(comment);
        tr.addView(itemNamesTextView);
        tr.addView(instructionsView);
        tr.addView(startReviewButton);
        tr.addView(fullDetailsButton);
        return tr;
    }

    private TableRow getHeaderRowForAdmin() {
        TableRow tr = new TableRow(getActivity());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));
        tr.setBackgroundResource(R.drawable.table_row_bg);
        tr.setPadding(5, 5, 5, 5);

        TextView dateCol = getHeaderColumnTextView("Date", true, false);
        TextView comment = getHeaderColumnTextView("Comment", false, false);
        TextView itemsOrdered = getHeaderColumnTextView("Items Ordered", false, false);
        TextView instructions = getHeaderColumnTextView("Instructions", false, false);
        TextView reviewStatus = getHeaderColumnTextView("Review Status", false, false);
        TextView fullDetails = getHeaderColumnTextView("Full Details", false, true);

        tr.addView(dateCol);
        tr.addView(comment);
        tr.addView(itemsOrdered);
        tr.addView(instructions);
        tr.addView(reviewStatus);
        tr.addView(fullDetails);
        return tr;
    }

    private View getReviewView(String orderId, String orderActive, List<OrderedItem> items) {
        if (orderActive != null && orderActive.equals("Y")) {
            return getStartReviewButton(orderId, items);
        } else {
            return getColumnTextView("Review Complete", false, true);
        }
    }

    private Button getStartReviewButton(final String orderId, final List<OrderedItem> items) {
        Button b = new Button(getActivity());
        b.setText("Start Review");
        b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        b.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ReviewForOrder reviewForOrder = new ReviewForOrder(items, orderId);
                Intent intent = new Intent(getActivity(), SubmitReviewActivity.class);
                intent.putExtra("ordered_items", reviewForOrder);
                intent.putExtra("orderId", orderId);
                getActivity().startActivity(intent);

            }
        });
        return b;
    }

}
