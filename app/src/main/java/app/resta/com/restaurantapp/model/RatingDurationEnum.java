package app.resta.com.restaurantapp.model;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Sriram on 24/06/2017.
 */
public enum RatingDurationEnum {
    Today("Today", 0, 0), sevendays("Last 7 days", 1, 7), thirtyDays("Last 30 days", 2, 30), sixtyDays("Last 60 days", 3, 60),
    oneTwentyDays("Last 120 days", 4, 120), oneEightyDays("Last 180 days", 5, 180);

    private String name;
    private int position;
    private int value;

    private static final Map<Integer, RatingDurationEnum> map = new TreeMap<>();

    static {
        for (RatingDurationEnum ratingDurationEnum : values())
            map.put(ratingDurationEnum.position, ratingDurationEnum);
    }

    RatingDurationEnum(String name, int position, int value) {
        this.name = name;
        this.position = position;
        this.value = value;
    }

    public static RatingDurationEnum of(int position) {
        RatingDurationEnum result = map.get(position);
        return result;
    }

    public int getValue() {
        return value;
    }

    public int getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public static String[] getEntries() {
        String[] names = new String[map.size()];
        int index = 0;
        for (Integer position : map.keySet()) {
            names[index++] = map.get(position).getName();
        }
        return names;
    }
}
