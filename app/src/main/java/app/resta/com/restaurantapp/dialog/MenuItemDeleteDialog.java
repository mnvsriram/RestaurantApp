package app.resta.com.restaurantapp.dialog;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuGroup.MenuGroupAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantItem;

/**
 * Created by Sriram on 26/05/2017.
 */
public class MenuItemDeleteDialog extends MenuDeleteDialog {

    private MenuGroupAdminDaoI menuGroupAdminDao = new MenuGroupAdminFireStoreDao();

    public void delete(final Activity activity, final RestaurantItem item, final int groupPosition, final MenuType menuType) {
        if (menuType != null && menuType.getId().length() > 0) {
            menuGroupAdminDao.deleteItemFromGroup(item.getId(), item.getParent().getId(), new OnResultListener<String>() {
                @Override
                public void onCallback(String message) {
                    reset();
                    Map<String, Object> params = new HashMap<>();
                    params.put("groupMenuId", menuType.getId());
                    authenticationController.goToMenuPage(params);
                }
            });
        } else {
            deleteItem(activity, item, groupPosition);
        }
    }

    private void deleteItem(final Activity activity, final RestaurantItem item, final int groupPosition) {
        menuGroupAdminDao.deleteItemFromAllGroups(item.getId(), new OnResultListener<String>() {
            @Override
            public void onCallback(String message) {
                menuItemAdminDao.deleteItem(item.getId(), new OnResultListener<String>() {
                    @Override
                    public void onCallback(String message) {
                        menuItemAdminDao.deleteGGWReferencesFromMenuItems(item.getId(), new OnResultListener<String>() {
                            @Override
                            public void onCallback(String status) {
                                deleteImages(item);
                                reset();
                                dispatchToMenuPage(activity, item, groupPosition);
                            }
                        });

                    }
                });
            }
        });
    }

//
//    private void deleteImagesFromPhone(Activity activity, RestaurantImage[] images) {
//        if (images != null) {
//            ImageSaver saver = new ImageSaver(activity);
//            for (RestaurantImage restaurantImage : images) {
//                if (restaurantImage != null && restaurantImage.getName() != null) {
//                    saver.deleteImage(restaurantImage.getName());
//                }
//            }
//        }
//    }


    public String buildDeleteConfirmationString(RestaurantItem item) {
        String message;

        if (item.getParent() != null && item.getParent().getId() != null) {
            message = "This action will remove '" + item.getName() + "' from the selected group.";
        } else {
            message = "This action will remove '" + item.getName() + " from all the groups.";
        }

        message += "\nAre you sure you want to delete? ";
        return message;
    }


    public void dispatchToMenuPage(final Activity activity, final RestaurantItem item, final int groupPosition) {
        reset();
        Map<String, Object> params = new HashMap<>();
        params.put("modifiedItemId", item.getId());
        params.put("modifiedItemGroupPosition", groupPosition);
        params.put("modifiedItemChildPosition", -1);


        if (item.getParent() == null || item.getParent().getId() == null) {
//            params.put("groupToOpen", 0l);
//            params.put("groupMenuId", null);
        } else {
            params.put("groupMenuId", item.getParent().getMenuTypeId());
            params.put("groupToOpen", item.getParent().getId());
        }


        authenticationController.goToMenuPage(params);
    }


}