package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum MenuCardButtonShapeEnum {
    RECTANGLE(1),
    SQUARE(2),
    ROUND(3),
    OVAL(4),
    STAR(5);

    private int value;

    private static final Map<Integer, MenuCardButtonShapeEnum> map = new HashMap<>(values().length, 1);

    static {
        for (MenuCardButtonShapeEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    MenuCardButtonShapeEnum(int value) {
        this.value = value;
    }

    public static MenuCardButtonShapeEnum of(int rating) {
        MenuCardButtonShapeEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }
}
