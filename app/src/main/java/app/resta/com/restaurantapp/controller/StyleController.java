package app.resta.com.restaurantapp.controller;

import java.io.Serializable;

import app.resta.com.restaurantapp.model.AppFontEnum;
import app.resta.com.restaurantapp.model.ColorCodeEnum;

/**
 * Created by Sriram on 13/08/2019.
 */

public class StyleController implements Serializable {
    AppFontEnum appFontEnum;
    String contentColor;
    String backgroundColor;

    public String getContentColor() {
        return contentColor;
    }

    public void setContentColor(String contentColor) {
        this.contentColor = contentColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public AppFontEnum getAppFontEnum() {
        return appFontEnum;
    }

    public void setAppFontEnum(AppFontEnum appFontEnum) {
        this.appFontEnum = appFontEnum;
    }
}
