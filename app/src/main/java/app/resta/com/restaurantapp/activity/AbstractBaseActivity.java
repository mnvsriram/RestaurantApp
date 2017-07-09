package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.controller.LoginController;
import app.resta.com.restaurantapp.util.FilePicker;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.StyleUtil;

public abstract class AbstractBaseActivity extends AppCompatActivity {
    //public static boolean isAdmin = false;
    final LoginController loginController = LoginController.getInstance();
    AuthenticationController authenticationController;

    public final static int LOAD_IMAGE_FROM_GALLERY = 1;
    public final static int LOAD_IMAGE_FROM_CAMERA = 2;
    public final static int LOAD_IMAGE_FROM_APP_IMAGES = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StyleUtil util = new StyleUtil();
        authenticationController = new AuthenticationController(this);
    }

    public void loginForAdmin(final MenuItem item) {
        authenticationController.loginForAdmin(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem compose = menu.findItem(R.id.miCompose);
        if (loginController.isAdminLoggedIn()) {
            compose.setIcon(R.drawable.admin);
        } else if (loginController.isReviewAdminLoggedIn()) {
            compose.setIcon(R.drawable.admin);
        } else {
            compose.setIcon(R.drawable.login);
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
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, LOAD_IMAGE_FROM_CAMERA);//zero can be replaced with any action code
                } else if (strName.equals("Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, LOAD_IMAGE_FROM_GALLERY);//one can be replaced with any action code
                } else if (strName.equals("All Folders")) {
                    Intent intent = new Intent(MyApplication.getAppContext(), FilePicker.class);
                    intent.putExtra(FilePicker.IMAGE_ONLY_PICKER, "true");
                    startActivityForResult(intent, LOAD_IMAGE_FROM_APP_IMAGES);
                }
            }
        });
        builderSingle.show();
    }

    public abstract void setNewImagePath(Intent intent, String path);

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case LOAD_IMAGE_FROM_CAMERA:
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
                }
                break;
            case LOAD_IMAGE_FROM_GALLERY:
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

                }
                break;
            case LOAD_IMAGE_FROM_APP_IMAGES:
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
}
