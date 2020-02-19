package com.haman.atoz.Permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.haman.atoz.Data.Common;
import com.haman.atoz.R;

import java.util.ArrayList;

//request permission
public class PermissionsActivity {

    private static final String TAG = ".PermissionsActivity";

    //singleton
    private static final PermissionsActivity permissionActivity = new PermissionsActivity();
    public static PermissionsActivity getInstance(){return permissionActivity;}

    //request permission
    public void requestPermissions(final Activity callingActivity){

        //getTedPermission 요청 결과 listener
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(callingActivity, "Request Granted", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                new AlertDialog.Builder(callingActivity)
                        .setTitle(R.string.permission_requet_warning)
                        .setMessage(R.string.permission_request)
                        .setPositiveButton(R.string.permission_request_second, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(callingActivity);
                            }
                        })
                        .setNegativeButton(R.string.permission_request_deny, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(callingActivity, "Request Denied", Toast.LENGTH_LONG).show();
                                callingActivity.finish();
                            }
                        })
                        .show();
            }
        };

        //Ted Permission 요청
        TedPermission.with(callingActivity)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(R.string.permission_request)
                .setPermissions(Common.getInstance().PERMISSIOS)
                .check();
    }
}
