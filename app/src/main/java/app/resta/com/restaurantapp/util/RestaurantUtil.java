package app.resta.com.restaurantapp.util;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import app.resta.com.restaurantapp.model.RatingDurationEnum;

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
}