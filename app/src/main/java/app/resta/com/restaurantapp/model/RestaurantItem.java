package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sriram on 13/05/2017.
 */
public class RestaurantItem implements Serializable {
    private long id;
    private String name;
    private String price;
    private long parentId;
    private RestaurantItem parentItem;
    private List<RestaurantItem> ggwItems;
    private RestaurantImage[] images = new RestaurantImage[3];
    private String active;
    private String description;
    private List<RestaurantItem> childItems = new ArrayList<>();

    public List<RestaurantItem> getGgwItems() {
        return ggwItems;
    }

    public void setGgwItems(List<RestaurantItem> ggwItems) {
        this.ggwItems = ggwItems;
    }


    public RestaurantItem() {
    }

    public RestaurantItem(OrderedItem item) {
        this.id = item.getItemId();
        this.name = item.getItemName();
        this.price = item.getPrice() + "";
    }

    public RestaurantImage[] getImages() {
        return images;
    }

    public void setImages(RestaurantImage[] images) {
        this.images = images;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getImage(int index) {
        if (images != null) {
            RestaurantImage image = images[index];
            if (image != null) {
                return image.getName();
            }
        }
        return null;
    }

    public void setImage(int index, RestaurantImage image) {

        if (images == null) {
            images = new RestaurantImage[3];
        }
        images[index] = image;
    }

    public void addChildItem(RestaurantItem item) {
        this.getChildItems().add(item);
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

    public List<RestaurantItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<RestaurantItem> childItems) {
        this.childItems = childItems;
    }

    public RestaurantItem getParentItem() {
        return parentItem;
    }

    public void setParentItem(RestaurantItem parentItem) {
        this.parentItem = parentItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RestaurantItem that = (RestaurantItem) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
