package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.util.DateUtil;
import app.resta.com.restaurantapp.util.MyApplication;

public class OrderItemDao {

    public long placeOrder(List<OrderedItem> items) {
        long orderId = createOrder();
        mapItemsToOrder(items, orderId);
        return orderId;
    }


    public long modifyOrder(List<OrderedItem> items, long orderId) {
        deleteAllItemsForOrder(orderId);
        mapItemsToOrder(items, orderId);
        return orderId;
    }


    private long createOrder() {
        long id = -1;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues order = new ContentValues();
            order.put("CREATIONDATE", DateUtil.getDateString(new Date(), "yyyy-MM-dd HH:mm:ss"));
            order.put("ACTIVE", "Y");

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


    private void deleteAllItemsForOrder(long orderId) {
        SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String whereClause = "ORDER_ID= ?";
        String[] whereArgs = {orderId + ""};
        db.delete("ORDER_ITEM_MAPPING", whereClause, whereArgs);

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
            String sql = "select orders._id, orderItems.ITEM_ID,orderItems.QUANTITY, items.NAME, orders.CREATIONDATE, orders.ACTIVE , orderItems.INSTRUCTIONS from ORDER_ITEMS orders, ORDER_ITEM_MAPPING orderItems , MENU_ITEM items where  orders._id = orderItems.ORDER_ID and items._ID = orderItems.ITEM_ID ";
            if (fromDate != null && toDate == null) {
                sql += "and orders.CREATIONDATE >= '" + DateUtil.getDateString(fromDate, "yyyy-MM-dd HH:mm:ss") + "' ORDER BY CREATIONDATE desc";
            } else if (fromDate != null && toDate != null) {
                sql += "and orders.CREATIONDATE BETWEEN '" + DateUtil.getDateString(fromDate, "yyyy-MM-dd HH:mm:ss") + "' AND '" + DateUtil.getDateString(toDate, "yyyy-MM-dd HH:mm:ss") + "'  ORDER BY CREATIONDATE desc";
            } else {
                return orderData;
            }

            Cursor cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                try {
                    long orderId = cursor.getLong(0);
                    long itemId = cursor.getLong(1);
                    int quantity = cursor.getInt(2);
                    String itemName = cursor.getString(3);
                    String dateTime = cursor.getString(4);
                    String active = cursor.getString(5);
                    String instructions = cursor.getString(6);

                    OrderedItem orderedItem = new OrderedItem();
                    orderedItem.setOrderId(orderId);
                    orderedItem.setItemId(itemId);
                    orderedItem.setQuantity(quantity);
                    orderedItem.setItemName(itemName);
                    orderedItem.setOrderDate(dateTime);
                    orderedItem.setOrderStatus(active);
                    orderedItem.setInstructions(instructions);
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

    public void markOrderAsComplete(long orderId) {
        try {
            String whereClause = "_id = ?";
            String[] selectionArgs = {String.valueOf(orderId)};

            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("ACTIVE", "N");

            db.update(
                    "ORDER_ITEMS",
                    values,
                    whereClause,
                    selectionArgs);

            db.close();
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Menu Item Updated successfully", Toast.LENGTH_LONG);
            toast.show();
        } catch (Exception e) {
            Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
            toast.show();
        }
    }

}



