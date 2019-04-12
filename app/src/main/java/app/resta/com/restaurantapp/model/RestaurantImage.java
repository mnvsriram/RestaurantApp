package app.resta.com.restaurantapp.model;

import java.io.Serializable;

/**
 * Created by Sriram on 13/05/2017.
 */
public class RestaurantImage implements Serializable {
    private long id;
    private String itemId;
    private String name;
    private String description;
    private String storageUrl;

    public RestaurantImage(String itemId, String name) {
        this.name = name;
        this.itemId = itemId;
    }

    public RestaurantImage(String itemId, String name, String description) {
        this.itemId = itemId;
        this.name = name;
        this.description = description;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public RestaurantImage() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
