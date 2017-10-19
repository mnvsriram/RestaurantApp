package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenuCard implements Serializable {
    private long id;
    private String name;
    private Map<MenuCardPropEnum, String> props = new HashMap<>();
    private Map<MenuCardButtonEnum, MenuCardButton> buttons = new HashMap<>();

    public Map<MenuCardPropEnum, String> getProps() {
        return props;
    }

    public void setProps(Map<MenuCardPropEnum, String> props) {
        this.props = props;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<MenuCardButtonEnum, MenuCardButton> getButtons() {
        return buttons;
    }

    public void setButtons(Map<MenuCardButtonEnum, MenuCardButton> buttons) {
        this.buttons = buttons;
    }

    public Map<MenuCardButtonEnum, MenuCardButton> getMainButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> mainButtons = new LinkedHashMap<>();
        mainButtons.put(MenuCardButtonEnum.MAIN_1, getDefaultIfNull(MenuCardButtonEnum.MAIN_1));
        mainButtons.put(MenuCardButtonEnum.MAIN_2, getDefaultIfNull(MenuCardButtonEnum.MAIN_2));
        mainButtons.put(MenuCardButtonEnum.MAIN_3, getDefaultIfNull(MenuCardButtonEnum.MAIN_3));
        mainButtons.put(MenuCardButtonEnum.MAIN_4, getDefaultIfNull(MenuCardButtonEnum.MAIN_4));
        return mainButtons;
    }


    public Map<MenuCardButtonEnum, MenuCardButton> getOtherButtons() {
        Map<MenuCardButtonEnum, MenuCardButton> otherButtons = new LinkedHashMap<>();

        otherButtons.put(MenuCardButtonEnum.TOP_LEFT, buttons.get(MenuCardButtonEnum.TOP_LEFT));
        otherButtons.put(MenuCardButtonEnum.TOP_CENTER, buttons.get(MenuCardButtonEnum.TOP_CENTER));
        otherButtons.put(MenuCardButtonEnum.TOP_RIGHT, buttons.get(MenuCardButtonEnum.TOP_RIGHT));

        otherButtons.put(MenuCardButtonEnum.MIDDLE_LEFT, buttons.get(MenuCardButtonEnum.MIDDLE_LEFT));
        otherButtons.put(MenuCardButtonEnum.MIDDLE_CENTER, buttons.get(MenuCardButtonEnum.MIDDLE_CENTER));
        otherButtons.put(MenuCardButtonEnum.MIDDLE_RIGHT, buttons.get(MenuCardButtonEnum.MIDDLE_RIGHT));

        otherButtons.put(MenuCardButtonEnum.BOTTOM_LEFT, buttons.get(MenuCardButtonEnum.BOTTOM_LEFT));
        otherButtons.put(MenuCardButtonEnum.BOTTOM_CENTER, buttons.get(MenuCardButtonEnum.BOTTOM_CENTER));
        otherButtons.put(MenuCardButtonEnum.BOTTOM_RIGHT, buttons.get(MenuCardButtonEnum.BOTTOM_RIGHT));

        return otherButtons;
    }

    private MenuCardButton getDefaultIfNull(MenuCardButtonEnum buttonEnum) {
        MenuCardButton button = buttons.get(buttonEnum);
        if (button == null) {
            button = new MenuCardButton();
            button.setLocation(buttonEnum);
        }
        return button;
    }
}
