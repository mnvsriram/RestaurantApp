package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.adapter.TagArrayAdapter;
import app.resta.com.restaurantapp.controller.AuthenticationController;
import app.resta.com.restaurantapp.db.FirebaseAppInstance;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.tag.TagAdminFireStoreDao;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.Tag;
import app.resta.com.restaurantapp.util.FireBaseStorageLocation;
import app.resta.com.restaurantapp.util.ImageUtil;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.TagNameComparator;

public class TagsActivity extends BaseActivity {
    private final String TAG = "TagsActivity";
    String newImagePath = "";
    AuthenticationController authenticationController;
    TagAdminDaoI tagAdminDao;
    GridView tagsGrid = null;
    StorageReference storageRef;
    TagArrayAdapter adapter;
    List<Tag> tagsData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_tags);
        newImagePath = "";
        authenticationController = new AuthenticationController(this);
        tagAdminDao = new TagAdminFireStoreDao();
        tagsGrid = findViewById(R.id.tagsItemsGrid);
        storageRef = FirebaseAppInstance.getStorageReferenceInstance();

        adapter = new TagArrayAdapter(TagsActivity.this, android.R.layout.simple_list_item_1, convertListToArray(tagsData), buttonOnClickDelete);
        tagsGrid.setAdapter(adapter);
        setToolbar();
        setTagsGrid();
    }

    private void setTagsGrid() {
        tagAdminDao.getTags(new OnResultListener<List<Tag>>() {
            @Override
            public void onCallback(List<Tag> tags) {
                findViewById(R.id.tagsGridOnLoadProgressBar).setVisibility(View.GONE);
                for (Tag tag : tags) {
                    addToGrid(tag);
                }
            }
        });
    }


    public Tag[] convertListToArray(List<Tag> tagList) {
        Collections.sort(tagList, new TagNameComparator());
        Object[] itemObjectArr = tagList.toArray();
        return Arrays.copyOf(itemObjectArr, itemObjectArr.length, Tag[].class);
    }

    public void addToGrid(Tag item) {
        tagsData.add(item);
        adapter.setData(convertListToArray(tagsData));
        adapter.notifyDataSetChanged();
    }

    private void deleteImage(final Tag tag) {
        Log.i(TAG, "Deleting image for tag" + tag.getName());
        StorageReference tagImageRef = storageRef.child(FireBaseStorageLocation.getTagImagesLocation() + tag.getId() + ".jpg");
        tagImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // File deleted successfully
                Log.i(TAG, "Successfully deleted image for  " + tag.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e(TAG, "onFailure: Not able to delete the image for " + tag.getName());
            }
        });
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }

    public void goBack(View view) {
        onBackPressed();
    }


    private void getTagName(Tag tag) {
        EditText userInput = findViewById(R.id.tagNameSettings);
        String modifiedName = userInput.getText().toString();
        tag.setName(modifiedName);
    }


    public void save(View view) {
        Log.i(TAG, "Saving Tag");
        final Tag newTag = new Tag();
        getTagName(newTag);
        if (newTag.getName() == null || newTag.getName().trim().length() == 0) {
            String errorMessage = "Please enter a name for the Tag.";
            markNameFieldAsError(errorMessage);
        } else {
            tagAdminDao.getTags(new OnResultListener<List<Tag>>() {
                @Override
                public void onCallback(List<Tag> tags) {
                    boolean exists = false;
                    for (Tag tag : tags) {
                        if (tag.getName().equalsIgnoreCase(newTag.getName())) {
                            exists = true;
                        }
                    }
                    if (exists) {
                        String error = "A Tag already exists with this name. Please choose a different name.";
                        markNameFieldAsError(error);
                    } else {
                        markNameFieldAsSuccess();
                        Log.i(TAG, "Saving tag " + newTag.getName());
                        tagAdminDao.insertTag(newTag, new OnResultListener<Tag>() {
                            @Override
                            public void onCallback(Tag tag) {
                                saveImage(tag);
                            }
                        });
                    }
                }
            });

        }

    }

    private void markNameFieldAsSuccess() {
        final TextView nameLabel = findViewById(R.id.editTagsNameLabel);
        final EditText userInput = findViewById(R.id.tagNameSettings);
        final int greenColor = MyApplication.getAppContext().getResources().getColor(R.color.green);
        final int greyColor = MyApplication.getAppContext().getResources().getColor(R.color.grey);
        final TextView errorBlock = findViewById(R.id.tagNameValidationBlock);

        nameLabel.setTextColor(greenColor);
        userInput.getBackground().setColorFilter(greyColor, PorterDuff.Mode.SRC_ATOP);
        errorBlock.setText("");
        errorBlock.setVisibility(View.GONE);
    }

    private void markNameFieldAsError(String error) {
        final TextView nameLabel = findViewById(R.id.editTagsNameLabel);
        final EditText userInput = findViewById(R.id.tagNameSettings);
        final int errorColor = MyApplication.getAppContext().getResources().getColor(R.color.red);
        final TextView errorBlock = findViewById(R.id.tagNameValidationBlock);
        errorBlock.setText(error);
        errorBlock.setVisibility(View.VISIBLE);
        nameLabel.setTextColor(errorColor);
        userInput.getBackground().setColorFilter(errorColor, PorterDuff.Mode.SRC_ATOP);
    }

    void saveImage(final Tag tag) {
        Log.i(TAG, "Saving image for tag " + tag.getName() + " and id " + tag.getId());
        if (tag.getId() != null && tag.getId().length() > 0 && newImagePath != null && newImagePath.length() > 0) {
            Uri uri = Uri.fromFile(new File(newImagePath));
            StorageReference storageReference = storageRef.child(FireBaseStorageLocation.getTagImagesLocation() + tag.getId() + ".jpg");
            ImageUtil.createImage(storageReference, uri, Tag.FIRESTORE_IMAGE_URL, new OnResultListener<String>() {
                @Override
                public void onCallback(String path) {
                    tagAdminDao.updateImageUrl(tag, path, new OnResultListener<String>() {
                        @Override
                        public void onCallback(String path) {
                            authenticationController.goToTagsSettingsPage();
                        }
                    });
                }
            });
        } else {
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
                                Log.i(TAG, "Deleting Tag " + tag.getName());
                                tagAdminDao.deleteTag(tag.getId());
                                deleteImage(tag);
                                Toast.makeText(TagsActivity.this, "Tag " + tag.getName() + " deleted.", Toast.LENGTH_LONG).show();
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


    public void setNewImagePath(Uri uri, String path) {
        newImagePath = path;
        ImageView tagsImageView;
        tagsImageView = findViewById(R.id.tagsSettingsImage);
        Bitmap bitmapImage = BitmapFactory.decodeFile(path);
        tagsImageView.setImageBitmap(bitmapImage);
    }

//
//    public void setNewImagePath(Intent intent, String path) {
//        newImagePath = path;
//        ImageView tagsImageView;
//        tagsImageView = findViewById(R.id.tagsSettingsImage);
//        Bitmap bitmapImage = BitmapFactory.decodeFile(path);
//        tagsImageView.setImageBitmap(bitmapImage);
//    }

    View.OnClickListener buttonOnClickDelete = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Tag tag = (Tag) v.getTag();
            if (tag != null) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        TagsActivity.this);
                buildAlertWindow(alertDialogBuilder, tag);
            }
        }
    };
}
