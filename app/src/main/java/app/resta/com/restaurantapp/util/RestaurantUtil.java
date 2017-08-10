package app.resta.com.restaurantapp.util;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.model.OrderedItem;
import app.resta.com.restaurantapp.model.RatingDurationEnum;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Nirmal Dhara on 12-07-2015.
 */
public class RestaurantUtil {

    public static void setImage(View view, int id, int width, int height) {
        view.setBackgroundResource(id);
        final float scale = MyApplication.getAppContext().getResources().getDisplayMetrics().density;
        int widthDp = (int) (width * scale + 0.5f);
        int heightDp = (int) (height * scale + 0.5f);
        view.getLayoutParams().width = widthDp;
        view.getLayoutParams().height = heightDp;
    }

    public static String join(List<String> strings, String delimeter) {
        String joinedString = "";
        if (strings != null) {
            for (String string : strings) {
                if (string != null) {
                    joinedString += delimeter;
                }
            }
        }
        return joinedString;
    }

    public static void setDurationSpinner(Activity activity, Spinner spinner) {
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, RatingDurationEnum.getEntries()); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

    }

    public static String getFormattedString(String name, int size) {
        if (name != null) {
            if (name.length() > size) {
                name = name.substring(0, size);
            } else if (name.length() < size) {
                int nameLength = name.length();
                for (int i = nameLength; i < size; i++) {
                    name = name + " ";
                }
            }
        }
        return name;
    }

    public static String insertPeriodically(
            String text, String insert, int period) {
        StringBuilder builder = new StringBuilder(text.length() + insert.length() * (text.length() / period) + 1);

        int index = 0;
        String prefix = "";
        while (index < text.length()) {
            // Don't put the insert in the very first iteration.
            // This is easier than appending it *after* each substring
            builder.append(prefix);
            prefix = insert;
            builder.append(text.substring(index,
                    Math.min(index + period, text.length())));
            index += period;
        }
        return builder.toString();
    }


    public static Map<Integer, List<OrderedItem>> mapItemsBySetMenuGroup(List<OrderedItem> items) {
        Map<Integer, List<OrderedItem>> itemsBySetMenuGroup = new HashMap<>();
        for (OrderedItem item : items) {
            List<OrderedItem> itemsOfSameSetMenu = itemsBySetMenuGroup.get(item.getSetMenuGroup());
            if (itemsOfSameSetMenu == null) {
                itemsOfSameSetMenu = new ArrayList<>();
            }
            itemsOfSameSetMenu.add(item);
            itemsBySetMenuGroup.put(item.getSetMenuGroup(), itemsOfSameSetMenu);
        }
        return itemsBySetMenuGroup;
    }
}