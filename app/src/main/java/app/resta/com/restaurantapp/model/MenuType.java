package app.resta.com.restaurantapp.model;

import java.io.Serializable;

public class MenuType implements Serializable {
    private long id;
    private String name;
    private String price;
    private String showPriceOfChildren;

    public String getShowPriceOfChildren() {
        return showPriceOfChildren;
    }

    public void setShowPriceOfChildren(String showPriceOfChildren) {
        this.showPriceOfChildren = showPriceOfChildren;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
