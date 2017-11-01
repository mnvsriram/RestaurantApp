package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum MenuCardLayoutEnum {
    Expandable_Menu_List(1);

    private int value;

    private static final Map<Integer, MenuCardLayoutEnum> map = new HashMap<>(values().length, 1);

    static {
        for (MenuCardLayoutEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    MenuCardLayoutEnum(int value) {
        this.value = value;
    }

    public static MenuCardLayoutEnum of(int rating) {
        MenuCardLayoutEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }

}
