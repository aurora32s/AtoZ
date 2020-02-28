package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

//RESPONSE DATA FROM SERVER
public class PostResponseData {

    @SerializedName("responseCode") int responseCode;
    @SerializedName("responseBody") String responseBody;

    public int getResponseCode(){return this.responseCode;}
    public String getResponseBody(){return this.responseBody;}
}
