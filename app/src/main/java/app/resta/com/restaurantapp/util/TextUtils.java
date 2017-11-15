package app.resta.com.restaurantapp.util;

import android.text.SpannableString;
import android.text.style.UnderlineSpan;

/**
 * Created by Sriram on 13/11/2017.
 */
public class TextUtils {

    public static final SpannableString getUnderlinesString(String text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }
}
