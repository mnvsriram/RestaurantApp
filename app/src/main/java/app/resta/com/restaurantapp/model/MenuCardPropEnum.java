package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum MenuCardPropEnum {
    BGCOLOR(1), FONT_STYLE(2);
    private int value;

    private static final Map<Integer, MenuCardPropEnum> map = new HashMap<>(values().length, 1);

    static {
        for (MenuCardPropEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    MenuCardPropEnum(int value) {
        this.value = value;
    }

    public static MenuCardPropEnum of(int rating) {
        MenuCardPropEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }
}
