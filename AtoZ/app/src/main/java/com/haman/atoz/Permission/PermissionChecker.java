package com.haman.atoz.Permission;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

//permission 여부 check
public class PermissionChecker {

    private static final String TAG = ".PermissionChecker";
    private final Context context;

    public PermissionChecker(Context context){
        this.context = context;
    }

    //원하는 permission 들이 accept 되어 있는지 확인
    public boolean lacksPermissions(String ... permissions){
        for(String permission : permissions){
            if(lackPermission(permission)){
                return true;
            }
        }
        return false;
    }

    //permission 검사
    private boolean lackPermission(String permission){
        return ContextCompat.checkSelfPermission(context,permission) == PackageManager.PERMISSION_GRANTED;
    }
}
