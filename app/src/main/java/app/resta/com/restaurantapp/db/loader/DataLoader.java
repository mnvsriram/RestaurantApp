package app.resta.com.restaurantapp.db.loader;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import java.util.List;

import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuCard.MenuCardAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.menuType.MenuTypeAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.MenuCard;
import app.resta.com.restaurantapp.model.MenuType;
import app.resta.com.restaurantapp.model.RestaurantImage;
import app.resta.com.restaurantapp.model.RestaurantItem;
import app.resta.com.restaurantapp.util.GlideApp;
import app.resta.com.restaurantapp.util.MyApplication;

/**
 * Created by Sriram on 05/04/2019.
 */

public class DataLoader {
    private MenuCardAdminDaoI menuCardAdminDao;
    private MenuTypeAdminDaoI menuTypeAdminDao;

    private final static String NO_IMAGE = "noImage.jpg";
    private boolean noImageDownloaded = false;
    private StorageReference storageRef;

    public DataLoader() {
        menuCardAdminDao = new MenuCardAdminFireStoreDao();
        menuTypeAdminDao = new MenuTypeAdminFireStoreDao();
        storageRef = FirebaseAppInstance.getStorageReferenceInstance();
    }

    public void loadData(TextView statusTextView, final OnResultListener<String> listener) {
        setTextViewStatus(statusTextView, "Loading Default Menu Card....");
        loadDefaultMenuCard(statusTextView, listener);
    }

    private void loadMenuTypesAndItems(final TextView statusTextView, final OnResultListener<String> listener) {
        menuTypeAdminDao.getAllMenuTypes(new OnResultListener<List<MenuType>>() {
            @Override
            public void onCallback(List<MenuType> menuTypes) {
                setTextViewStatus(statusTextView, "Loaded Menu Types. Loading items in Menu Types...");
                for (MenuType menuType : menuTypes) {
                    menuTypeAdminDao.getGroupsWithItemsInMenuType(menuType.getId(), new OnResultListener<List<RestaurantItem>>() {
                        @Override
                        public void onCallback(List<RestaurantItem> groups) {
                            setTextViewStatus(statusTextView, "Loaded groups with items");
                            downloadImages(statusTextView, groups);
                            listener.onCallback("success");
                        }
                    });

                }
            }
        });
    }

    private void loadDefaultMenuCard(final TextView statusTextView, final OnResultListener<String> listener) {
        menuCardAdminDao.getDefaultCardWithButtonsAndActions(new OnResultListener<MenuCard>() {
            @Override
            public void onCallback(MenuCard menuCard) {
                setTextViewStatus(statusTextView, "Default Menu Card Loaded");
                setTextViewStatus(statusTextView, "Loading Menu Types and Items....");
                loadMenuTypesAndItems(statusTextView, listener);
            }
        });
    }


    private void setTextViewStatus(TextView statusTextView, String status) {
        if (statusTextView != null && status != null) {
            statusTextView.setText(status);
        }
    }

    private void downloadImages(final TextView statusTextView, List<RestaurantItem> groups) {
        setTextViewStatus(statusTextView, "Loaded images..");

        for (RestaurantItem group : groups) {
            List<RestaurantItem> childItems = group.getChildItems();
            for (RestaurantItem childItem : childItems) {
                downloadImagesForItem(statusTextView, childItem);
            }
        }
    }

    private void downloadImagesForItem(final TextView statusTextView, RestaurantItem item) {
        setTextViewStatus(statusTextView, "Loaded images for " + item.getName() + "...");
        downloadItemImage(item.getItemImage1());
        downloadItemImage(item.getItemImage2());
        downloadItemImage(item.getItemImage3());
    }

    private void downloadItemImage(RestaurantImage restaurantImage) {
        if (restaurantImage != null && restaurantImage.getStorageUrl() != null && restaurantImage.getStorageUrl().length() > 0) {
            downloadImage(restaurantImage.getStorageUrl());
        }
    }

    private void downloadImage(final String url) {
        if (url != null && url.length() > 0) {
            final StorageReference imageRef = storageRef.child(url);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (url.contains(NO_IMAGE)) {
                            if (!noImageDownloaded) {
                                GlideApp.with(MyApplication.getAppContext()).downloadOnly()
                                        .load(imageRef).submit().get();
                                if (url.contains(NO_IMAGE)) {
                                    noImageDownloaded = true;
                                }
                            }
                        } else {
                            GlideApp.with(MyApplication.getAppContext()).downloadOnly()
                                    .load(imageRef).submit().get();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

}
