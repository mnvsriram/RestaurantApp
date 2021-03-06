package app.resta.com.restaurantapp.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import app.resta.com.restaurantapp.R;
import app.resta.com.restaurantapp.dataCreator.ExcelImporter;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminDaoI;
import app.resta.com.restaurantapp.db.dao.admin.device.DeviceAdminFireStoreDao;
import app.resta.com.restaurantapp.db.dao.admin.publisher.PublisherDaoI;
import app.resta.com.restaurantapp.db.dao.admin.publisher.PublisherDaoImpl;
import app.resta.com.restaurantapp.db.listener.OnResultListener;
import app.resta.com.restaurantapp.model.DeviceInfo;
import app.resta.com.restaurantapp.util.MyApplication;
import app.resta.com.restaurantapp.util.RestaurantMetadata;

public class UpdateClusterSettingsActivity extends BaseActivity {
    DeviceAdminDaoI deviceAdminDaoI;
    private DeviceInfo thisDeviceDetails = new DeviceInfo();
    private PublisherDaoI publisherDao;
    ExcelImporter importer = new ExcelImporter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_updates_cluster);
        initialize();
        setToolbar();
        loadIntentParams();
        setFields();
    }

    private void initialize() {
        deviceAdminDaoI = new DeviceAdminFireStoreDao();
        publisherDao = new PublisherDaoImpl();
    }


    private void setFields() {
        setUsername();
        setRestaurantName();
        setAddress();
        setRestaurantID();
        setDeviceId();
        setLastSyncTime();
    }

    public void setLastSyncTime() {
        TextView syncTime = findViewById(R.id.clusterPage_LastDataRefreshTime);
        thisDeviceDetails.setLastSyncedTimeStamp(RestaurantMetadata.getLastSyncTime());
        syncTime.setText("Data was synced on: " + thisDeviceDetails.getLastSyncedTimeStamp());
    }

    private void setRestaurantName() {
        TextView restaurantName = (TextView) findViewById(R.id.clusterPage_restaurantText);
        restaurantName.setText(RestaurantMetadata.getRestaurantName());
        thisDeviceDetails.setRestaurantName(RestaurantMetadata.getRestaurantName());
    }


    private void setAddress() {
        TextView address = findViewById(R.id.clusterPage_addressLineOneText);
        address.setText(RestaurantMetadata.getRestaurantAddress());
        thisDeviceDetails.setAddress(RestaurantMetadata.getRestaurantAddress());
    }

    private void setRestaurantID() {
        TextView restaurantId = findViewById(R.id.clusterPage_restaurantIdText);
        restaurantId.setText(RestaurantMetadata.getRestaurantId());
        thisDeviceDetails.setRestaurantId(RestaurantMetadata.getRestaurantId());
    }

    private void setDeviceId() {
        TextView clusterPage_deviceNameText = findViewById(R.id.clusterPage_deviceNameText);
        clusterPage_deviceNameText.setText(RestaurantMetadata.getDeviceId());
        thisDeviceDetails.setDeviceId(RestaurantMetadata.getDeviceId());
    }

    //
    private void setUsername() {
        TextView username = (TextView) findViewById(R.id.clusterPage_usernameText);
        username.setText(RestaurantMetadata.getUsername());
        thisDeviceDetails.setUsername(RestaurantMetadata.getUsername());
    }


    public void editDeviceDetails(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("thisDevice_details", thisDeviceDetails);
        authenticationController.goToDeviceUpdatePage(params);
    }


    public void refreshData(View view) {
        Map<String, Object> params = new HashMap<>();
        params.put("thisDevice_details", thisDeviceDetails);
        authenticationController.goToRefreshDataPage(params);
    }

    public void publishData(View view) {
        publisherDao.publishData(thisDeviceDetails);
    }

    public void importIngredientsData(View view) {
        importer.importIngredientsData();
    }


    public void importTagsData(View view) {
        importer.importTagsData();
    }


    public void importItemsData(View view) {
        Button button = findViewById(R.id.clusterPage_importItemsDataButton);
        button.setEnabled(false);
        button.setText("Please wait");
        importer.importItemsData();
        button.setEnabled(true);
        button.setText("Import Items Data");
    }

    public void exitLockMode(View view) {
        LayoutInflater li = LayoutInflater.from(this);
        final View promptsView = li.inflate(R.layout.exit_lock_mode_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Exit Lock Mode",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText reason = (EditText) promptsView.findViewById(R.id.exitLockModeReason);
                                if (reason != null && reason.getText() != null && reason.getText().toString() != null && reason.getText().length() > 0) {
                                    deviceAdminDaoI.addCommentForExitLockMode(reason.getText().toString(), new OnResultListener<String>() {
                                        @Override
                                        public void onCallback(String status) {

                                        }
                                    });
                                    exitLockMode();
                                } else {
                                    Toast.makeText(MyApplication.getAppContext(), "Please enter a reason for exiting the lock mode.", Toast.LENGTH_SHORT).show();
                                }
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

    public void exitLockMode() {
        Intent lockIntent = new Intent(getApplicationContext(),
                LockedActivity.class);
        lockIntent.putExtra("EXIT_LOCK_MODE", "true");
        startActivity(lockIntent);
    }

    private void loadIntentParams() {
        Intent intent = getIntent();
    }

    @Override
    public void onBackPressed() {
        authenticationController.goToSettingsPage();
    }

    public void gotoWifiActivity(View view) {
        //1==1
        //authenticationController.goToWifiDirectActivity(null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //  NearByService.copyFileToGroupOwner(data);
    }

//
//    public void selectImageForIngredient(View view) {
//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
//        builderSingle.setIcon(R.drawable.edit);
//        builderSingle.setTitle("Select Image From:-");
//
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
//        arrayAdapter.add("Gallery");
//        arrayAdapter.add("Camera");
//        arrayAdapter.add("All Folders");
//
//        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//
//        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String strName = arrayAdapter.getItem(which);
//
//                if (strName.equalsIgnoreCase("camera")) {
//                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(takePicture, 0);//zero can be replaced with any action code
//                } else if (strName.equals("Gallery")) {
//                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
//                }
//            }
//        });
//        builderSingle.show();
//    }

}
