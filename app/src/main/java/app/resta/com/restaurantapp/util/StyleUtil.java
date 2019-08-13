package app.resta.com.restaurantapp.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.controller.StyleController;
import app.resta.com.restaurantapp.db.DBHelper;

/**
 * Created by Sriram on 17/01/2017.
 */
public class StyleUtil {
    public static Map<String, Integer> colorMap = new HashMap<String, Integer>();
    public static Map<String, String> layOutMap = new HashMap<String, String>();
    public static Map<String, String> configMap = new HashMap<String, String>();
    public static Map<String, String> imageNameMap = new HashMap<String, String>();
    public static boolean dataFetched = false;

    static {
        loadDBProps();
    }


    private static void loadDBProps() {
        if (!StyleUtil.dataFetched) { // why is this condition required? forgot why I kept it

            try {
                SQLiteOpenHelper dbHelper = new DBHelper(MyApplication.getAppContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                Cursor cursor = db.query("SYS_PARAMS", new String[]{"_id", "TYPE", "VALUE"}, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    try {
                        String id = cursor.getString(0);
                        String name = cursor.getString(1);
                        String value = cursor.getString(2);
                        if (name != null) {
                            if (name.equalsIgnoreCase("color")) {
                                StyleUtil.colorMap.put(id, Color.parseColor(value));
                            } else if (name.equalsIgnoreCase("config")) {
                                StyleUtil.configMap.put(id, value);
                            } else if (name.equalsIgnoreCase("image")) {
                                StyleUtil.imageNameMap.put(id, value);
                            } else if (name.equalsIgnoreCase("layout")) {
                                StyleUtil.layOutMap.put(id, value);
                            }
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }
                cursor.close();
                db.close();
            } catch (Exception e) {
                Toast toast = Toast.makeText(MyApplication.getAppContext(), "Database unavailable", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }


    public static void setFont(ViewGroup group, Typeface lTypeface) {
        if (lTypeface != null) {
            int count = group.getChildCount();
            View v;
            for (int i = 0; i < count; i++) {
                v = group.getChildAt(i);
                if (v instanceof TextView) {
                    ((TextView) v).setTypeface(lTypeface);
                } else if (v instanceof EditText) {
                    ((EditText) v).setTypeface(lTypeface);
                } else if (v instanceof Button) {
                    ((Button) v).setTypeface(lTypeface);
                } else if (v instanceof TextInputLayout) {
                    ((TextInputLayout) v).setTypeface(lTypeface);
                } else if (v instanceof ViewGroup)
                    setFont((ViewGroup) v, lTypeface);
            }
        }
    }

    public static void setFont(View v, Typeface lTypeface) {
        if (lTypeface != null) {
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(lTypeface);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(lTypeface);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(lTypeface);
            } else if (v instanceof TextInputLayout) {
                ((TextInputLayout) v).setTypeface(lTypeface);
            } else if (v instanceof ViewGroup)
                setFont((ViewGroup) v, lTypeface);
        }

    }

    public static void setStyle(ViewGroup mainLayout, StyleController styleController) {
        if (styleController != null && styleController.getAppFontEnum() != null) {
            setFont(mainLayout, styleController.getAppFontEnum().getFont());
        }
    }

}
