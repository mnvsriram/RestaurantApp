package app.resta.com.restaurantapp.controller;

import java.io.Serializable;

import app.resta.com.restaurantapp.model.AppFontEnum;
import app.resta.com.restaurantapp.model.MenuCardActionStyle;

/**
 * Created by Sriram on 13/08/2019.
 */

public class StyleController implements Serializable {
    AppFontEnum appFontEnum;
    String contentColor;
    String backgroundColor;

    MenuCardActionStyle itemStyle;
    MenuCardActionStyle itemDescStyle;
    MenuCardActionStyle groupNameStyle;
    MenuCardActionStyle menuNameStyle;
    MenuCardActionStyle menuDescStyle;


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

    public MenuCardActionStyle getItemStyle() {
        return itemStyle;
    }

    public void setItemStyle(MenuCardActionStyle itemStyle) {
        this.itemStyle = itemStyle;
    }

    public MenuCardActionStyle getItemDescStyle() {
        return itemDescStyle;
    }

    public void setItemDescStyle(MenuCardActionStyle itemDescStyle) {
        this.itemDescStyle = itemDescStyle;
    }

    public MenuCardActionStyle getGroupNameStyle() {
        return groupNameStyle;
    }

    public void setGroupNameStyle(MenuCardActionStyle groupNameStyle) {
        this.groupNameStyle = groupNameStyle;
    }

    public MenuCardActionStyle getMenuNameStyle() {
        return menuNameStyle;
    }

    public void setMenuNameStyle(MenuCardActionStyle menuNameStyle) {
        this.menuNameStyle = menuNameStyle;
    }

    public MenuCardActionStyle getMenuDescStyle() {
        return menuDescStyle;
    }

    public void setMenuDescStyle(MenuCardActionStyle menuDescStyle) {
        this.menuDescStyle = menuDescStyle;
    }
}
