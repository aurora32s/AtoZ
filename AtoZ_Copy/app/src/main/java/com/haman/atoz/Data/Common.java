package com.haman.atoz.Data;

import android.Manifest;
import android.content.Context;
import android.widget.Toast;

//manage common data or final data
public class Common {

    private static final Common common = new Common();
    public static Common getInstance(){return common;}

    public final int SIGN_IN_REQUEST_CODE = 1; //GOOGLE LOGIN REQUEST CODE
    public final String TEST_AUTH = "TOKEN"; //TEST AUTHORIZATION TOKEN CODE

    //NETWORKING
    public final int RESPONSE_SUCCESS = 200;
    public final int RESPONSE_EXTRA_SUCCESS = 202;
    public final int RESPONSE_FAIL = 400;

    //USER DATA
    private UserData userData = null;

    public UserData getUserData(){return this.userData;}
    public void setUserData(UserData userData){this.userData = userData;}

    //SHOW FAIL/ERROR MESSAGE TO CLIENT
    public void showMessage(Context context, int message){
        Toast.makeText(context, context.getString(message) ,Toast.LENGTH_LONG).show();
    }

    //MANAGE PERMISSION
    public final String[] PERMISSIONS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    //POST TYPE
    public final int VIDEO = 0;
    public final int AUDIO = 1;
}
