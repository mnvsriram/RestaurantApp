package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum MenuCardButtonEnum {
    MAIN_1(1),
    MAIN_2(2),
    MAIN_3(3),
    MAIN_4(4),
    TOP_LEFT(5),
    TOP_CENTER(6),
    TOP_RIGHT(7),
    MIDDLE_LEFT(8),
    MIDDLE_CENTER(9),
    MIDDLE_RIGHT(10),
    BOTTOM_LEFT(11),
    BOTTOM_CENTER(12),
    BOTTOM_RIGHT(13);

    private int value;

    private static final Map<Integer, MenuCardButtonEnum> map = new HashMap<>(values().length, 1);

    static {
        for (MenuCardButtonEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    MenuCardButtonEnum(int value) {
        this.value = value;
    }

    public static MenuCardButtonEnum of(int rating) {
        MenuCardButtonEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }


    public static boolean isMainButton(MenuCardButtonEnum menuCardButtonEnum) {
        boolean isMainButton = false;
        if (MAIN_1 == menuCardButtonEnum || MAIN_2 == menuCardButtonEnum || MAIN_3 == menuCardButtonEnum || MAIN_4 == menuCardButtonEnum) {
            isMainButton = true;
        }
        return isMainButton;
    }

    public static boolean isMainButton(int buttonId) {
        boolean isMainButton = false;
        if (MAIN_1.getValue() == buttonId || MAIN_2.getValue() == buttonId || MAIN_3.getValue() == buttonId || MAIN_4.getValue() == buttonId) {
            isMainButton = true;
        }
        return isMainButton;
    }
}
