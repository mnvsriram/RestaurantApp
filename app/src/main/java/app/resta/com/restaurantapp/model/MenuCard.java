package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class MenuCard implements Serializable {
    private long id;
    private String name;
    private Map<MenuCardPropEnum, String> props = new HashMap<>();

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
}
