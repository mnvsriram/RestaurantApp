package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum MenuCardButtonPropEnum {
    BUTTON_SHAPE(2),
    BUTTON_TEXT_COLOR(3),
    BUTTON_COLOR(4),
    BUTTON_TEXT_BLINK(5);

    private int value;

    private static final Map<Integer, MenuCardButtonPropEnum> map = new HashMap<>(values().length, 1);

    static {
        for (MenuCardButtonPropEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    MenuCardButtonPropEnum(int value) {
        this.value = value;
    }

    public static MenuCardButtonPropEnum of(int rating) {
        MenuCardButtonPropEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }
}
