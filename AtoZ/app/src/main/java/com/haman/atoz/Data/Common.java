package com.haman.atoz.Data;

import android.Manifest;
import android.media.MediaSession2Service;

public class Common {

    //singleton
    private static final Common common = new Common();

    public static Common getInstance(){return common;}

    //Request Permission
    public final String[] PERMISSIOS = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_PHONE_STATE
    };

    //response Code
    public final int REQUEST_SUCCESS = 200;
    public final int REQUEST_ERROR = 404;
    public final int PUT_ALREADY_EXIST = 201;

    //user data
    private UserData userData;
    //set User Data
    public void setUserData(UserData userData){this.userData = userData;}
    //get User Data
    public UserData getUserData(){return this.userData;}
}
