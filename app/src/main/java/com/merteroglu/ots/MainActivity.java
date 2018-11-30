package com.merteroglu.ots;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String permissions[] = {Manifest.permission.BLUETOOTH,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_PRIVILEGED};
        requestAppPermissions(permissions,R.string.app_name,10);

    }


    public void requestAppPermissions(final String[]requestedPermissions, final int stringId, final int requestCode) {


        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        boolean showRequestPermissions = false;
        for(String permission: requestedPermissions) {
            permissionCheck = permissionCheck + ContextCompat.checkSelfPermission(this, permission);
            showRequestPermissions = showRequestPermissions || ActivityCompat.shouldShowRequestPermissionRationale(this, permission);
        }

        if (permissionCheck!=PackageManager.PERMISSION_GRANTED) {
            if(showRequestPermissions) {
                 ActivityCompat.requestPermissions(MainActivity.this, requestedPermissions, requestCode);
            } else {
                ActivityCompat.requestPermissions(this, requestedPermissions, requestCode);
            }
        } else {

        }
    }

}
