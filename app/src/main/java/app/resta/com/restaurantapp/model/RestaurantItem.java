package app.resta.com.restaurantapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Sriram on 13/05/2017.
 */
public class RestaurantItem implements Serializable, Cloneable {
    private long id;
    private int position;
    private String name;
    private String price;
    private RestaurantItem parent;
    private long menuTypeId;
    private String menuTypeName;
    private List<RestaurantItem> ggwItems;
    private RestaurantImage[] images = new RestaurantImage[3];
    private String active;
    private String description;
    private List<RestaurantItem> childItems = new ArrayList<>();
    private Map<Long, ItemParentMapping> itemToParentMappings;
    private int setMenuGroup;


    public RestaurantItem() {
    }

    public int getSetMenuGroup() {
        return setMenuGroup;
    }

    public void setSetMenuGroup(int setMenuGroup) {
        this.setMenuGroup = setMenuGroup;
    }

    public RestaurantItem(OrderedItem item) {
        this.id = item.getItemId();
        this.name = item.getItemName();
        this.price = item.getPrice() + "";
    }

    public Map<Long, ItemParentMapping> getItemToParentMappings() {
        return itemToParentMappings;
    }

    public void setItemToParentMappings(Map<Long, ItemParentMapping> itemToParentMappings) {
        this.itemToParentMappings = itemToParentMappings;
    }

    public List<RestaurantItem> getGgwItems() {
        return ggwItems;
    }

    public void setGgwItems(List<RestaurantItem> ggwItems) {
        this.ggwItems = ggwItems;
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

    public long getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(long menuTypeId) {
        this.menuTypeId = menuTypeId;
    }

    public String getMenuTypeName() {
        return menuTypeName;
    }

    public void setMenuTypeName(String menuTypeName) {
        this.menuTypeName = menuTypeName;
    }

    public RestaurantItem getParent() {
        return parent;
    }

    public void setParent(RestaurantItem parent) {
        this.parent = parent;
    }

    public ItemParentMapping getMappingForParent(long id) {
        if (itemToParentMappings != null) {
            return itemToParentMappings.get(id);
        }
        return null;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    @Override
    public RestaurantItem clone() {
        RestaurantItem clonedItem = null;
        try {
            clonedItem = (RestaurantItem) super.clone();
        } catch (CloneNotSupportedException cnse) {
            clonedItem = this;
        }
        return clonedItem;
    }


    public RestaurantItem clone(ItemParentMapping mapping) {
        RestaurantItem clonedItem = clone();
        if (mapping.getPrice() != null) {
            clonedItem.setPrice(mapping.getPrice());
        }
        return clonedItem;
    }
}
