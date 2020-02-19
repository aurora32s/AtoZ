package com.haman.atoz.Networking;

import com.google.gson.annotations.SerializedName;
import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Data.UserData;

import java.util.ArrayList;

public class GetUserResponse {
    //Variable name should be same as in the json response from server
    @SerializedName("responseCode")
    int responseCode;
    @SerializedName("responseBody")
    UserData responseBody;

    public UserData getResponseBody(){return this.responseBody;}
    public int getResponseCode(){return this.responseCode;}
}

