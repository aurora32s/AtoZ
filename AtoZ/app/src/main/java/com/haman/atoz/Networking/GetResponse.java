package com.haman.atoz.Networking;

import com.google.gson.annotations.SerializedName;
import com.haman.atoz.Data.MediaData;

import java.util.ArrayList;

public class GetResponse {

    //Variable name should be same as in the json response from server
    @SerializedName("responseCode")
    int responseCode;
    @SerializedName("responseBody")
    ArrayList<MediaData> responseBody;

    public ArrayList<MediaData> getResponseBody(){return this.responseBody;}
    public int getResponseCode(){return this.responseCode;}
}

