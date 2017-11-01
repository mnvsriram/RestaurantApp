package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum ColorCodeEnum {
    Red("#ff0000"),
    Blue("#0000ff"),
    Green("#008000"),
    Black("#000000"),
    White("#ffffff"),
    Gray("#808080"),
    Cyan("#00FFFF"),
    Magenta("#ff00ff"),
    Yellow("#FFFF00"),
    Lightgray("#696969"),
    Darkgray("#A9A9A9"),
    Lime("#00FF00"),
    Maroon("#800000"),
    Navy("#000080"),
    Olive("#556b2f"),
    Purple("#800080"),
    Silver("#C0C0C0");

    private String value;

    private static final Map<String, ColorCodeEnum> map = new HashMap<>(values().length, 1);

    static {
        for (ColorCodeEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    ColorCodeEnum(String value) {
        this.value = value;
    }

    public static ColorCodeEnum of(String rating) {
        ColorCodeEnum result = map.get(rating);
        return result;
    }

    public String getValue() {
        return value;
    }

}
