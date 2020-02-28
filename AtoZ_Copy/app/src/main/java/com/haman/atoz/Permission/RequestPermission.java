package com.haman.atoz.Permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.haman.atoz.R;
import com.haman.atoz.Data.Common;
import java.util.ArrayList;

//REQUEST PERMISSIONS ARE DENIED
public class RequestPermission {

    private static final String TAG = ".RequestPermission";
    private final Activity activity;
    public RequestPermission(Activity activity){this.activity = activity;}

    public void requestPermission(){

        //LISTENER FOR TED PERMISSION
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Common.getInstance().showMessage(activity, R.string.PERMISSION_GRANTED);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {

                new AlertDialog.Builder(activity)
                        .setTitle(R.string.PERMISSION_WARN)
                        .setMessage(R.string.PERMISSION_ASK)
                        .setPositiveButton(R.string.PERMISSION_GRANT, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermission();
                            }
                        })
                        .setNegativeButton(R.string.PERMISSION_DENY, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Common.getInstance().showMessage(activity,R.string.PERMISSION_DENIED);
                                activity.finish();
                            }
                        })
                        .show();
            }
        };

        //REQUEST TED PERMISSION
        TedPermission.with(activity)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(R.string.PERMISSION_WARN)
                .setPermissions(Common.getInstance().PERMISSIONS)
                .check();
    }
}
