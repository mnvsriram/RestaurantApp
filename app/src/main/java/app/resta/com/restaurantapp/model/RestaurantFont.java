package app.resta.com.restaurantapp.model;

import java.io.Serializable;

/**
 * Created by Sriram on 13/05/2017.
 */
public class RestaurantFont implements Serializable, Cloneable {
    private long id;
    private String name;

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
