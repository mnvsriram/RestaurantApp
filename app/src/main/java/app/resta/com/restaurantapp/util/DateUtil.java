package app.resta.com.restaurantapp.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDateString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

}
