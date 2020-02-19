package com.haman.atoz.Networking;

import com.google.gson.annotations.SerializedName;

public class PostResponse {

    @SerializedName("responseCode")
    int responseCode;
    @SerializedName("responseBody")
    String responseBody;

    public String getResponseBody(){return this.responseBody;}
    public int getResponseCode(){return this.responseCode;}
}
