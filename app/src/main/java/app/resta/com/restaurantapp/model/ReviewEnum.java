package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum ReviewEnum {
    GOOD(3), AVERAGE(2), BAD(1), NOREVIEW(-1);
    private int value;

    private static final Map<Integer, ReviewEnum> map = new HashMap<>(values().length, 1);

    static {
        for (ReviewEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    ReviewEnum(int value) {
        this.value = value;
    }

    public static ReviewEnum of(int rating) {
        ReviewEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }
}
