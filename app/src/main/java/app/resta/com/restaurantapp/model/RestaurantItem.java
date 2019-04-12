package app.resta.com.restaurantapp.model;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.resta.com.restaurantapp.util.FireStoreUtil;

/**
 * Created by Sriram on 13/05/2017.
 */
public class RestaurantItem implements Serializable, Cloneable {
    public static final String FIRESTORE_NAME_KEY = "name";
    public static final String FIRESTORE_DESC_KEY = "description";
    public static final String FIRESTORE_PRICE_KEY = "price";
    public static final String FIRESTORE_ACTIVE_KEY = "active";
    public static final String FIRESTORE_IMAGE1_URL = "itemImageUrl_1";
    public static final String FIRESTORE_IMAGE2_URL = "itemImageUrl_2";
    public static final String FIRESTORE_IMAGE3_URL = "itemImageUrl_3";
    public static final String FIRESTORE_IMAGE1_DESC = "itemImageDesc_1";
    public static final String FIRESTORE_IMAGE2_DESC = "itemImageDesc_2";
    public static final String FIRESTORE_IMAGE3_DESC = "itemImageDesc_3";

    public static final String FIRESTORE_CREATED_BY_KEY = "createdBy";
    public static final String FIRESTORE_CREATED_AT_KEY = "createdAt";
    public static final String FIRESTORE_UPDATED_BY_KEY = "lastModifiedBy";
    public static final String FIRESTORE_UPDATED_AT_KEY = "lastModifiedAt";


    public static final String FIRESTORE_NO_OF_3_RATING = "noOf3Ratings";
    public static final String FIRESTORE_NO_OF_2_RATING = "noOf2Ratings";
    public static final String FIRESTORE_NO_OF_1_RATING = "noOf1Ratings";

    public static final String FIRESTORE_NO_OF_3_RATING_FOR_TODAY = "noOf3RatingsForToday";
    public static final String FIRESTORE_NO_OF_2_RATING_FOR_TODAY = "noOf2RatingsForToday";
    public static final String FIRESTORE_NO_OF_1_RATING_FOR_TODAY = "noOf1RatingsForToday";

    public static final String FIRESTORE_REVIEW_SCORE_TODAY = "todayReviewScore";
    public static final String FIRESTORE_REVIEW_SCORE = "overallReviewScore";

    private String id;
    private long position;
    private String name;
    private String price;
    private RestaurantItem parent;
    private String menuTypeId;
    private String menuTypeName;
    private List<RestaurantItem> ggwItems;
    private RestaurantImage itemImage1;
    private RestaurantImage itemImage2;
    private RestaurantImage itemImage3;

    private RestaurantImage[] images = new RestaurantImage[3];
    private String active;
    private String description;
    private List<RestaurantItem> childItems = new ArrayList<>();
    private Map<Long, ItemParentMapping> itemToParentMappings;
    private int setMenuGroup;
    private Map<ReviewEnum, Long> ratingCountMap = new HashMap<>();

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


    public List<RestaurantImage> getFireStoreImages() {
        List<RestaurantImage> images = new ArrayList<>();
        images.add(itemImage1);
        images.add(itemImage2);
        images.add(itemImage3);
        return images;
    }


    public void setImages(RestaurantImage[] images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public RestaurantImage getItemImage1() {
        return itemImage1;
    }

    public void setItemImage1(RestaurantImage itemImage1) {
        this.itemImage1 = itemImage1;
    }

    public RestaurantImage getItemImage2() {
        return itemImage2;
    }

    public void setItemImage2(RestaurantImage itemImage2) {
        this.itemImage2 = itemImage2;
    }

    public RestaurantImage getItemImage3() {
        return itemImage3;
    }

    public void setItemImage3(RestaurantImage itemImage3) {
        this.itemImage3 = itemImage3;
    }

    public void setImage(String imageNum, String imageUrl, String imageDesc) {
        RestaurantImage image = new RestaurantImage();
        image.setStorageUrl(imageUrl);
        image.setDescription(imageDesc);
        if (imageNum.equalsIgnoreCase("1")) {
            setItemImage1(image);
        } else if (imageNum.equalsIgnoreCase("2")) {
            setItemImage2(image);
        } else if (imageNum.equalsIgnoreCase("3")) {
            setItemImage3(image);
        }
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

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public String getMenuTypeId() {
        return menuTypeId;
    }

    public void setMenuTypeId(String menuTypeId) {
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

    public ItemParentMapping getMappingForParent(String id) {
        if (itemToParentMappings != null) {
            return itemToParentMappings.get(id);
        }
        return null;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
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

    public Map<ReviewEnum, Long> getRatingCountMap() {
        return ratingCountMap;
    }

    public void setRatingCountMap(Map<ReviewEnum, Long> ratingCountMap) {
        this.ratingCountMap = ratingCountMap;
    }

    public static RestaurantItem prepareRestaurantItem(QueryDocumentSnapshot documentSnapshot) {
        return getRestaurantItem(documentSnapshot);
    }


    public static RestaurantItem prepareRestaurantItem(DocumentSnapshot documentSnapshot) {
        return getRestaurantItem(documentSnapshot);
    }

    private static RestaurantImage image(Map<String, Object> keyValueMap, String imageKey, String descKey) {
        String image1Url = FireStoreUtil.getImageUrl(keyValueMap, imageKey);
        RestaurantImage image = null;
        if (image1Url != null && image1Url.length() > 0) {
            image = new RestaurantImage();
            image.setStorageUrl(image1Url);
            image.setDescription(FireStoreUtil.getString(keyValueMap, descKey));
        }
        return image;
    }

    @NonNull
    private static RestaurantItem getRestaurantItem(DocumentSnapshot documentSnapshot) {
        Map<String, Object> keyValueMap = documentSnapshot.getData();
        RestaurantItem restaurantItem = new RestaurantItem();
        restaurantItem.setId(documentSnapshot.getId());
        restaurantItem.setName(FireStoreUtil.getString(keyValueMap, FIRESTORE_NAME_KEY));
        restaurantItem.setDescription(FireStoreUtil.getString(keyValueMap, FIRESTORE_DESC_KEY));
        restaurantItem.setPrice(FireStoreUtil.getString(keyValueMap, FIRESTORE_PRICE_KEY));
        restaurantItem.setActive(FireStoreUtil.getString(keyValueMap, FIRESTORE_ACTIVE_KEY));

        restaurantItem.setItemImage1(image(keyValueMap, FIRESTORE_IMAGE1_URL, FIRESTORE_IMAGE1_DESC));
        restaurantItem.setItemImage2(image(keyValueMap, FIRESTORE_IMAGE2_URL, FIRESTORE_IMAGE2_DESC));
        restaurantItem.setItemImage3(image(keyValueMap, FIRESTORE_IMAGE3_URL, FIRESTORE_IMAGE3_DESC));

        HashMap<ReviewEnum, Long> ratingMap = new HashMap<>();
        ratingMap.put(ReviewEnum.BAD, FireStoreUtil.getLong(keyValueMap, FIRESTORE_NO_OF_1_RATING, 0l));
        ratingMap.put(ReviewEnum.AVERAGE, FireStoreUtil.getLong(keyValueMap, FIRESTORE_NO_OF_2_RATING, 0l));
        ratingMap.put(ReviewEnum.GOOD, FireStoreUtil.getLong(keyValueMap, FIRESTORE_NO_OF_3_RATING, 0l));

        restaurantItem.setRatingCountMap(ratingMap);
        return restaurantItem;
    }

}
