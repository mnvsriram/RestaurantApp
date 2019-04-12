package app.resta.com.restaurantapp.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDateString(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String formattedDate = sdf.format(date);
        return formattedDate;
    }

    public static Date getDateFromString(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date dateResult = null;
        try {
            dateResult = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateResult;
    }

    public static String getCurrentTimeStamp() {
        Date date = new Date();
        long time = date.getTime();
        Timestamp ts = new Timestamp(time);
        return ts.toString();
    }
}
