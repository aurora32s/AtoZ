package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

//GOOGLE SIGN IN RESPONSE
public class GetResponseUserData {
    @SerializedName("responseCode") int responseCode;
    @SerializedName("responseBody") UserData responseBody;

    public int getResponseCode(){return this.responseCode;}
    public UserData getResponseBody(){return this.responseBody;}
}
