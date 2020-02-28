package com.haman.atoz.Permission;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

//CHECK WHETHER EACH PERMISSION IS ACCEPTED OR DENIED
public class CheckPermission {

    private static final String TAG = ".CheckPermission";
    private final Context context;

    public CheckPermission(Context context){this.context = context;}

    public boolean lacksPermissions(String ... permissions){

        for(String permission : permissions){

            if(ContextCompat.checkSelfPermission(context,permission) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }
}
