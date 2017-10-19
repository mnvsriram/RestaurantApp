package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum MenuCardButtonPropEnum {
    MENU_DATA(1),
    BUTTON_SHAPE(2);


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
