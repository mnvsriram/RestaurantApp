package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MenuCardButton implements Serializable {
    private long id;
    private MenuCardButtonEnum location;
    private Map<MenuCardButtonPropEnum, String> props = new HashMap<>();
    private boolean enabled = true;
    private String name;

    private long cardId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public MenuCardButtonEnum getLocation() {
        return location;
    }

    public void setLocation(MenuCardButtonEnum location) {
        this.location = location;
    }

    public Map<MenuCardButtonPropEnum, String> getProps() {
        return props;
    }

    public void setProps(Map<MenuCardButtonPropEnum, String> props) {
        this.props = props;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

}
