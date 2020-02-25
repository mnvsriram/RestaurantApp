package app.resta.com.restaurantapp.model;

import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum FontFaceEnum {
    None(Typeface.NORMAL),
    Bold(Typeface.BOLD),
    Normal(Typeface.NORMAL),
    Italic(Typeface.ITALIC),
    BoldAndItalic(Typeface.BOLD_ITALIC);


    private int value;

    private static final Map<Integer, FontFaceEnum> map = new HashMap<>(values().length, 1);
    private static final Map<String, FontFaceEnum> mapByName = new HashMap<>(values().length, 1);

    static {
        for (FontFaceEnum fontFaceEnum : values()) map.put(fontFaceEnum.value, fontFaceEnum);
        for (FontFaceEnum fontFaceEnum : values()) mapByName.put(fontFaceEnum.name(), fontFaceEnum);
    }

    FontFaceEnum(int value) {
        this.value = value;
    }

    public static FontFaceEnum of(String rating) {
        FontFaceEnum result = map.get(rating);
        return result;
    }

    public static FontFaceEnum ofByName(String rating) {
        FontFaceEnum result = mapByName.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }

}
