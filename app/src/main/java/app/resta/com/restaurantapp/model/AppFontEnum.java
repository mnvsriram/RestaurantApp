package app.resta.com.restaurantapp.model;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum AppFontEnum {


    None(2),
    Paciffo(1);

    private int value;

    private static final Map<Integer, AppFontEnum> map = new HashMap<>(values().length, 1);

    static {
        for (AppFontEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    AppFontEnum(int value) {
        this.value = value;
    }

    public static AppFontEnum of(int rating) {
        AppFontEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }

    public Typeface getFont() {
        if (this == AppFontEnum.Paciffo) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.pacifico);
        }
        return null;
    }
}
