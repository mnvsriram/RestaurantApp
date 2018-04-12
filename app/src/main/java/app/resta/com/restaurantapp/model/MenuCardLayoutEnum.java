package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum MenuCardLayoutEnum {
    Expandable_Menu_With_Details(1),

    Item_Name_With_Description(2),
    Item_Name_With_Description_WithDetailPopup(3),

    Item_Without_description_in_single_row(4),
    Item_Without_description_in_single_row_WithDetailPopup(5),

    Item_Without_description_in_two_rows(6),
    Item_Without_description_in_two_rows_WithDetailPopup(7),

    Group_list_and_Items_With_Image_Icons(8);

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
