package app.resta.com.restaurantapp.model;

import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum AppFontEnum implements Serializable {


    None(1),
    AlexBrush(2),
    AmaticSC(3),
    Blackjack(4),
    GreatVibes(5),
    KaushanScript(6),
    OpenSans_Light(7),
    OpenSans_Regular(8),
    OpenSans_Semibold(9),
    Ostrich(10),
    Pacifico(11),
    Raleway(12);

    private int value;

    private static final Map<Integer, AppFontEnum> map = new HashMap<>(values().length, 1);

    static {
        for (AppFontEnum reviewEnum : values()) map.put(reviewEnum.value, reviewEnum);
    }

    AppFontEnum(int value) {
        this.value = value;
    }

    public static AppFontEnum of(int rating) {
        AppFontEnum result = map.get(rating);
        return result;
    }

    public int getValue() {
        return value;
    }

    public Typeface getFont() {
        if (this == AppFontEnum.Pacifico) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.pacifico);
        } else if (this == AppFontEnum.AlexBrush) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.alex_brush_regular);
        } else if (this == AppFontEnum.AmaticSC) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.amatic_regular);
        } else if (this == AppFontEnum.Blackjack) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.blackjack);
        } else if (this == AppFontEnum.GreatVibes) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.great_vibes_regular);
        } else if (this == AppFontEnum.KaushanScript) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.kaushan_script_regular);
        } else if (this == AppFontEnum.OpenSans_Light) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.opensans_light);
        } else if (this == AppFontEnum.OpenSans_Regular) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.opensans_regular);
        } else if (this == AppFontEnum.OpenSans_Semibold) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.opensans_semibold);
        } else if (this == AppFontEnum.Raleway) {
            return ResourcesCompat.getFont(MyApplication.getAppContext(), R.font.raleway_regular);
        }
        return null;
    }
}
