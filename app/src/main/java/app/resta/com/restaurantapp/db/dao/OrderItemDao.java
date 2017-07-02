package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class OrderItemDao {

    public long placeOrder(List<OrderedItem> items) {
        long orderId = createOrder();
        mapItemsToOrder(items, orderId);
        return orderId;
    }

    private long createOrder() {
        long id = -1;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = sdf.format(new Date());

            ContentValues order = new ContentValues();
            order.put("CREATIONDATE", date);
            id = db.insert("ORDER_ITEMS", null, order);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Order Created successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
        return id;
    }

    private void mapItemsToOrder(List<OrderedItem> items, long orderId) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (OrderedItem item : items) {
                ContentValues tag = new ContentValues();
                tag.put("ITEM_ID", item.getItemId());
                tag.put("QUANTITY", item.getQuantity());
                tag.put("INSTRUCTIONS", item.getInstructions());
                tag.put("ORDER_ID", orderId);
                db.insert("ORDER_ITEM_MAPPING", null, tag);
            }
            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item added to Order successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public Map<Long, List<OrderedItem>> getOrders(Date fromDate, Date toDate) {
        //1==1 dates are NOT YET used in the query
        Map<Long, List<OrderedItem>> orderData = new LinkedHashMap<>();
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = "\tselect orders._id, orderItems.ITEM_ID,orderItems.QUANTITY, items.NAME, orders.CREATIONDATE  from ORDER_ITEMS orders, ORDER_ITEM_MAPPING orderItems , MENU_ITEM items where  orders._id = orderItems.ORDER_ID and items._ID = orderItems.ITEM_ID and orders.CREATIONDATE BETWEEN '2017-07-01 20:30:00' AND '2017-07-01 20:40:00'  ORDER BY CREATIONDATE desc";
            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    long orderId = cursor.getLong(0);
                    long itemId = cursor.getLong(1);
                    int quantity = cursor.getInt(2);
                    String itemName = cursor.getString(3);
                    String dateTime = cursor.getString(4);

                    OrderedItem orderedItem = new OrderedItem();
                    orderedItem.setOrderId(orderId);
                    orderedItem.setItemId(itemId);
                    orderedItem.setQuantity(quantity);
                    orderedItem.setItemName(itemName);
                    orderedItem.setOrderDate(dateTime);

                    List<OrderedItem> itemsForThisOrder = orderData.get(orderId);

                    if (itemsForThisOrder == null) {
                        itemsForThisOrder = new ArrayList<>();
                    }
                    itemsForThisOrder.add(orderedItem);

                    orderData.put(orderId, itemsForThisOrder);

                } catch (Exception e) {
                    continue;
                }
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable1", Toast.LENGTH_LONG);
            toast.show();
        }
        return orderData;
    }

}



