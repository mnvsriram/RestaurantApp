package app.resta.com.restaurantapp.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.db.DBHelper;
import app.resta.com.restaurantapp.model.OrderItem;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.MyApplication;

public class OrderItemDao {

    public long placeOrder(List<OrderItem> items) {
        long orderId = createOrder();
        mapItemsToOrder(items, orderId);
        return orderId;
    }

    private void mapItemsToOrder(List<OrderItem> items, long orderId) {
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            for (OrderItem item : items) {
                ContentValues tag = new ContentValues();
                tag.put("ITEM_ID", item.getRestaurantItem().getId());
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

    private long createOrder() {
        long id = -1;
        try {
            SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

}



