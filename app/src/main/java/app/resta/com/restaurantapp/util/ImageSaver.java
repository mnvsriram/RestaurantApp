package app.resta.com.restaurantapp.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Sriram on 10/06/2017.
 */
public class ImageSaver {

    private Activity activity;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE

    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private final static String DEFAULT_SAVE_TO_DIRECTORY = Environment.getExternalStorageDirectory() + "/restaurantAppImages";

    public ImageSaver(Activity activity) {
        this.activity = activity;
    }

    public void saveImageFromDrawableToLocal(int id, String newFileName) {
        Bitmap bm = BitmapFactory.decodeResource(activity.getResources(), id);
        verifyStoragePermissions(activity);
        createDirectoryAndSaveFile(bm, newFileName);
    }


    public void saveImageToAppFolder(Bitmap bm, String newFileName) {
        verifyStoragePermissions(activity);
        createDirectoryAndSaveFile(bm, newFileName);
    }


    public void deleteImage(String fileName) {
        verifyStoragePermissions(activity);
        deleteFile(fileName);
    }

    private void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void deleteFile(String fileName) {
        File file = new File(DEFAULT_SAVE_TO_DIRECTORY + "/" + fileName + ".jpeg");
        if (file.exists()) {
            file.delete();
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        File direct = new File(DEFAULT_SAVE_TO_DIRECTORY);
        if (!direct.exists()) {
            File wallpaperDirectory = new File(DEFAULT_SAVE_TO_DIRECTORY);
            wallpaperDirectory.mkdirs();
        }

        File file = new File(DEFAULT_SAVE_TO_DIRECTORY + "/" + fileName + ".jpeg");
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
