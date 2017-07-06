package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.dao.TagsDao;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.FilePicker;
import app.resta.com.restaurantapp.util.ImageSaver;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.validator.TagsValidator;

public class TagsActivity extends BaseActivity {
    String newImagePath = "";
    AuthenticationController authenticationController;
    TagsDao tagsDao;
    GridLayout tagsGrid = null;
    TagsValidator tagsValidator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_tags);
        tagsValidator = null;
        newImagePath = "";
        authenticationController = new AuthenticationController(this);
        tagsDao = new TagsDao();
        tagsGrid = (GridLayout) findViewById(R.id.tagsItemsGrid);
        tagsGrid.removeAllViews();
        tagsGrid.setColumnCount(3);

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
        setTagsGrid();
    }

    private void setTagsGrid() {
        List<Tag> tags = tagsDao.getTagsRefData();
        for (Tag tag : tags) {
            addTagsRow(tag);
        }
    }

    private void addTagsRow(Tag tag) {
        showTagNameInGrid(tag.getName());
        showTagsImageInGrid(tag.getImage());
        showDeleteTagsButtonInImageInGrid(tag);
    }

    private void showTagNameInGrid(String item) {
        TextView tagName = new TextView(MyApplication.getAppContext());
        tagName.setText(item);
        tagName.setWidth(100);
        tagName.setTextColor(Color.BLACK);
        tagName.setTextSize(20);
        //ggwItemButton.setTag(item);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tagsGrid.addView(tagName, lp);
    }


    private void showTagsImageInGrid(String imageName) {
        ImageButton ggwItemButton = new ImageButton(MyApplication.getAppContext());
        if (imageName == null || imageName.length() == 0 || imageName.equalsIgnoreCase("noImage")) {
            ggwItemButton.setBackgroundResource(R.drawable.noimage);
        } else {
            String path = Environment.getExternalStorageDirectory() + "/restaurantAppImages/";
            String filePath = path + imageName + ".jpeg";
            Bitmap bmp = BitmapFactory.decodeFile(filePath);
            Drawable d = new BitmapDrawable(getResources(), bmp);
            ggwItemButton.setBackground(d);
        }
        ggwItemButton.setClickable(true);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(60, 60);
        tagsGrid.addView(ggwItemButton, lp);
    }

    private void showDeleteTagsButtonInImageInGrid(final Tag tag) {
        ImageButton ggwItemButton = new ImageButton(MyApplication.getAppContext());
        ggwItemButton.setBackgroundResource(R.drawable.deletered);
        ggwItemButton.setClickable(true);
        //ggwItemButton.setTag(tag.getId());

        ggwItemButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        TagsActivity.this);
                buildAlertWindow(alertDialogBuilder, tag);
            }
        });


        // 1==1 dialog box to confirm deletion is required here
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(60, 60);
        tagsGrid.addView(ggwItemButton, lp);
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void selectImageForTag(View view) {
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
                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
                } else if (strName.equals("Gallery")) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
                } else if (strName.equals("All Folders")) {
                    Intent intent = new Intent(TagsActivity.this, FilePicker.class);
                    intent.putExtra(FilePicker.IMAGE_ONLY_PICKER, "true");
                    startActivityForResult(intent, 2);
                }
            }
        });
        builderSingle.show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        ImageView tagsImageView = (ImageView) findViewById(R.id.tagsSettingsImage);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    tagsImageView.setImageURI(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    newImagePath = picturePath;
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    tagsImageView.setImageURI(selectedImage);
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    newImagePath = picturePath;
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {

                    if (imageReturnedIntent.hasExtra(FilePicker.EXTRA_FILE_PATH)) {
                        File selectedFile = new File
                                (imageReturnedIntent.getStringExtra(FilePicker.EXTRA_FILE_PATH));
                        newImagePath = selectedFile.getAbsolutePath();
                        tagsImageView.setImageBitmap(BitmapFactory.decodeFile(newImagePath));
                    }
                }
                break;
        }
    }

    private void getTagName(Tag tag) {
        EditText userInput = (EditText) findViewById(R.id.tagNameSettings);
        String modifiedName = userInput.getText().toString();
        tag.setName(modifiedName);
    }


    private void getTagImage(Tag tag) {
        String newImageName = tag.getName() + "tag";
        String oldImageName = tag.getImage();
        ImageSaver imageSaver = new ImageSaver(this);
        if (newImagePath != null && newImagePath.length() > 0) {
            tag.setImage(newImageName);
            Bitmap mp = BitmapFactory.decodeFile(newImagePath);
            imageSaver.deleteImage(oldImageName);
            imageSaver.saveImageToAppFolder(mp, tag.getImage());
        } else {
            tag.setImage("noImage");
        }
    }

    public void save(View view) {
        Tag newTag = new Tag();
        getTagName(newTag);
        getTagImage(newTag);
        tagsValidator = new TagsValidator(this, newTag);
        if (tagsValidator.validate()) {
            tagsDao.insertTagsRefData(newTag);
            authenticationController.goToTagsSettingsPage();
        }
    }

    private void buildAlertWindow(AlertDialog.Builder alertDialogBuilder, final Tag tag) {
        alertDialogBuilder.setTitle("Delete confirmation");
        alertDialogBuilder.setMessage("Do you really want to delete the Tag - " + tag.getName() + "?");
        alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);

        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                tagsDao.deleteTagRefData(tag.getId());
                                authenticationController.goToTagsSettingsPage();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });


        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}