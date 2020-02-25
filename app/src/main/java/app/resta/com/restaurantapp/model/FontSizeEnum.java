package app.resta.com.restaurantapp.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum FontSizeEnum {
    None(0),
    small(10),
    normal(20),
    large(30);

    private float value;

    private static final Map<Float, FontSizeEnum> map = new HashMap<>(values().length, 1);
    private static final Map<String, FontSizeEnum> mapByName = new HashMap<>(values().length, 1);

    static {
        for (FontSizeEnum fontSizeEnum : values()) map.put(fontSizeEnum.value, fontSizeEnum);
        for (FontSizeEnum reviewEnum : values()) mapByName.put(reviewEnum.name(), reviewEnum);
    }

    FontSizeEnum(int value) {
        this.value = value;
    }

    public static FontSizeEnum of(String rating) {
        FontSizeEnum result = map.get(rating);
        return result;
    }

    public static FontSizeEnum ofByName(String rating) {
        FontSizeEnum result = mapByName.get(rating);
        return result;
    }

    public Float getValue() {
        return value;
    }

}
