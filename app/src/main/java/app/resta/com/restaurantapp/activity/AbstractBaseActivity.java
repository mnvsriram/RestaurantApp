package app.resta.com.restaurantapp.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.util.FilePicker;
import app.resta.com.restaurantapp.util.ImageUtil;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

public abstract class AbstractBaseActivity extends AppCompatActivity {
    private static final String TAG = "AbstractBaseActivity";
    //public static boolean isAdmin = false;
    final LoginController loginController = LoginController.getInstance();
    AuthenticationController authenticationController;
    public final static int LOAD_IMAGE_FROM_GALLERY = 1;
    public final static int LOAD_IMAGE_FROM_CAMERA = 0;
    public final static int LOAD_IMAGE_FROM_APP_IMAGES = 2;
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtil util = new StyleUtil();
        authenticationController = new AuthenticationController(this);

        ActivityManager am = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);

        if (am.getLockTaskModeState() !=
                ActivityManager.LOCK_TASK_MODE_LOCKED) {
            LoginController.getInstance().logout();
            Toast.makeText(MyApplication.getAppContext(), "Not locked. Shutting down.", Toast.LENGTH_LONG).show();
            finish();
            System.exit(0);
        }


    }

    public void login(final MenuItem item) {
        authenticationController.login(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem loginIcon = menu.findItem(R.id.loginIcon);
        if (loginController.isAdminLoggedIn()) {
            loginIcon.setIcon(R.drawable.admin);
        } else if (loginController.isReviewAdminLoggedIn()) {
            loginIcon.setIcon(R.drawable.waiter);
        } else {
            loginIcon.setIcon(R.drawable.login);
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }


    public void showSelectImageFromDialog(final View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setIcon(R.drawable.edit);
        builderSingle.setTitle("Select Image From:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Gallery");
        arrayAdapter.add("Camera");
        arrayAdapter.add("All Folders");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                if (strName.equalsIgnoreCase("camera")) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Picture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    startActivityForResult(intent, LOAD_IMAGE_FROM_CAMERA);
//
//                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    takePicture.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                    startActivityForResult(takePicture, LOAD_IMAGE_FROM_CAMERA);//zero can be replaced with any action code
                } else if (strName.equals("Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhoto.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    startActivityForResult(pickPhoto, LOAD_IMAGE_FROM_GALLERY);//one can be replaced with any action code
                } else if (strName.equals("All Folders")) {
                    Intent intent = new Intent(MyApplication.getAppContext(), FilePicker.class);
                    intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    intent.putExtra(FilePicker.IMAGE_ONLY_PICKER, "true");
                    startActivityForResult(intent, LOAD_IMAGE_FROM_APP_IMAGES);
                }
            }
        });
        builderSingle.show();
    }

    public abstract void setNewImagePath(Intent imageIntent, String picturePath);

    public abstract void setNewImagePath(Uri uri, String picturePath);

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case LOAD_IMAGE_FROM_CAMERA:
                if (resultCode == RESULT_OK) {
//                    Log.i(TAG, "Selected the image from Camera");
//                    Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
//                    Uri tempUri = ImageUtil.getImageUri(getApplicationContext(), photo);
//                    File finalFile = new File(ImageUtil.getRealPathFromURI(tempUri, getContentResolver()));
//                    setNewImagePath(imageReturnedIntent, finalFile.getPath());
//                    setNewImagePath(tempUri, finalFile.getPath());

                    Log.i(TAG, "Selected the image from Camera");
                    File finalFile = new File(ImageUtil.getRealPathFromURI(imageUri, getContentResolver()));
                    setNewImagePath(imageUri, finalFile.getPath());

                }
                break;
            case LOAD_IMAGE_FROM_GALLERY:

                Log.i(TAG, "Selected the image from Gallery");
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    setNewImagePath(imageReturnedIntent, picturePath);
                    setNewImagePath(selectedImage, picturePath);

                }
                break;
            case LOAD_IMAGE_FROM_APP_IMAGES:
                Log.i(TAG, "Selected the image from All Folders");
                if (resultCode == RESULT_OK) {
                    if (imageReturnedIntent.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        File selectedFile = new File
                                (imageReturnedIntent.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        setNewImagePath(imageReturnedIntent, selectedFile.getAbsolutePath());
                    }
                }
                break;
        }
    }

    protected void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
